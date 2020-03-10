package com.epam.healenium;

import com.epam.healenium.client.HealingClient;
import com.epam.healenium.model.Locator;
import com.epam.healenium.popup.ResultPopup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class GetHealingAction extends AnAction {

    private PsiElementFactory factory;
    private PsiMethodCallExpression methodCall;
    private Project project;
    private List<Locator> data = new ArrayList<>();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Editor editor = e.getData(LangDataKeys.EDITOR);
        project = e.getData(CommonDataKeys.PROJECT);
        CaretModel caretModel = editor.getCaretModel();
        Caret caret = caretModel.getCurrentCaret();
        //TODO: PsiElement element = file.findElementAt(caretOffset);
        // must be -> PsiJavaToken:STRING_LITERAL, also restrict by qualifierName==By

        int caretOffset = caretModel.getOffset();
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        PsiElement element = file.findElementAt(caretOffset);

        // метод в котором выбрали поиск результатов
        PsiMethod method = findProperMethod(PsiTreeUtil.getParentOfType( element, PsiMethod.class));
        if (method != null && element instanceof PsiJavaToken) {
            String locator = (String) PsiTreeUtil.getParentOfType(element, PsiLiteralExpression.class).getValue();
            String className = method.getContainingClass().getQualifiedName();
            String methodName = method.getName();
            log.info("CurrentClass -> {} and method -> {}", className, methodName);
            data = new HealingClient().makeCall(locator, className, methodName);
        }

        // получим выражение точки выбора
        methodCall = PsiTreeUtil.getParentOfType( element, PsiMethodCallExpression.class);
        if (methodCall != null) {
            factory = JavaPsiFacade.getInstance(project).getElementFactory();
        }


        if(!data.isEmpty()){
            new ResultPopup(e).createList("Healing Results", data.toArray(new Locator[0]), (position, value) -> {
                CommandProcessor.getInstance().executeCommand(project,
                    () -> ApplicationManager.getApplication().runWriteAction(() -> {
                        PsiExpression locatorExpression = factory.createExpressionFromText("\""+ value + "\"", null);
                        methodCall.getArgumentList().getExpressions()[0].replace(locatorExpression);
                        PsiIdentifier cssArgument = factory.createIdentifier("cssSelector");
                        methodCall.getMethodExpression().getReferenceNameElement().replace(cssArgument);
                    }), "Updated locator", null);
            });
        }
    }


    private PsiMethod findProperMethod(PsiMethod referenceMethod){
        PsiMethod result = null;
        if(hasAnnotation(referenceMethod)) return referenceMethod;
        SearchScope searchScope = ProjectScope.getContentScope(project);
        Collection<PsiReference> refs = MethodReferencesSearch.search(referenceMethod, searchScope, true).findAll();
        for(PsiReference reference : refs){
            // выражение, где используется наш метод поиска
            PsiMethodCallExpression expression = PsiTreeUtil.getParentOfType(reference.getElement(), PsiMethodCallExpression.class);
            if(expression!= null){
                result = PsiTreeUtil.getParentOfType(expression.getMethodExpression(), PsiMethod.class);
                if(result != null) {
                    if (hasAnnotation(result)) {
                        //TODO: может использоваться более чем в одном тесте. Нужно собрать все методы и сделать несколько запросов в будущем
                        break;
                    } else {
                        result = findProperMethod(result);
                        //TODO: может использоваться более чем в одном тесте. Нужно собрать все методы и сделать несколько запросов в будущем
                        if(result!=null) break;
                    }
                }
            }
        }
        return result;
    }

    private boolean hasAnnotation(PsiMethod method){
        return Arrays.stream(method.getAnnotations()).anyMatch(it-> it.getText().equals("@Restore"));
    }

}
