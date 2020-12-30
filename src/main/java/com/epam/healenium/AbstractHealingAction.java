package com.epam.healenium;

import com.epam.healenium.client.HealingClient;
import com.epam.healenium.model.HealingDto;
import com.epam.healenium.model.HealingResultDto;
import com.epam.healenium.popup.HealingNotifier;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractHealingAction extends AnAction {

    protected PsiElementFactory factory;
    protected Project project;
    protected HealingNotifier notifier;
    protected int count = 0;

    public AbstractHealingAction() {
        super(StringUtils.EMPTY, StringUtils.EMPTY,
                new ImageIcon(AbstractHealingAction.class.getClassLoader().getResource("icon/healenium.png")));
        notifier = new HealingNotifier();
    }

    protected HealingDto actionPerformedPerElement(@NotNull AnActionEvent e, PsiElement element) {
        project = e.getData(CommonDataKeys.PROJECT);
        // метод в котором инициировали поиск результатов хилинга
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        // получаем локатор
        PsiLiteralExpression literalExpression = PsiTreeUtil.getParentOfType(element, PsiLiteralExpression.class);
        if (literalExpression == null) return null;
        String locator = (String) literalExpression.getValue();
        String className = method != null
                // получаем полное имя класса
                ? method.getContainingClass().getQualifiedName()
                // получаем полное имя класса. Т.к метода нет, значит мы нв верхнеуровневом свойстве, и проходить по дереву не нужно
                : PsiTreeUtil.getParentOfType(element, PsiMember.class).getContainingClass().getQualifiedName();

        // запрашиваем исправленные локаторы
        Set<HealingDto> data = new HealingClient().makeCall(locator, className);
        if (data.isEmpty()) return null;

        if (method == null && data.size() > 1) {
            notifier.notify("Can't detect healing results", NotificationType.ERROR);
            return null;
        }

        if (method != null && data.size() != 1) {
            // проходим по классу вверх, в поисках нужного метода
            data.iterator().next().setResults(findProperMethod(data, PsiTreeUtil.getParentOfType(element, PsiMethod.class)));
        }

        // получим выражение точки выбора
        PsiElement methodCall = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (methodCall == null) {
            methodCall = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
        }

        if (methodCall != null) {
            factory = JavaPsiFacade.getInstance(project).getElementFactory();
        }

        count++;
        return data.iterator().next().setMethodCall(methodCall);
    }

    protected Set<HealingResultDto> findProperMethod(Set<HealingDto> data, PsiMethod referenceMethod) {
        Set<HealingResultDto> result = findResults(data, referenceMethod);
        if (!result.isEmpty()) return result;
        SearchScope searchScope = ProjectScope.getContentScope(project);
        Collection<PsiReference> refs = MethodReferencesSearch
                .search(referenceMethod, searchScope, true)
                .findAll();
        for (PsiReference reference : refs) {
            // выражение, где используется наш метод поиска
            PsiMethodCallExpression expression = PsiTreeUtil.getParentOfType(reference.getElement(), PsiMethodCallExpression.class);
            if (expression != null) {
                PsiMethod targetMethod = PsiTreeUtil.getParentOfType(expression.getMethodExpression(), PsiMethod.class);
                return findProperMethod(data, targetMethod);
            }
        }
        return result;
    }

    protected Set<HealingResultDto> findResults(Set<HealingDto> data, PsiMethod method) {
        return data.stream()
                .filter(it -> it.getMethodName().equals(method.getName()))
                .flatMap(it -> it.getResults().stream())
                .collect(Collectors.toSet());
    }

    protected void updateLocatorValue(PsiElement methodCall, PsiExpression locatorExpression) {
        if (methodCall instanceof PsiMethodCallExpression) {
            updateMethodLocatorValue((PsiMethodCallExpression) methodCall, locatorExpression, factory.createIdentifier("cssSelector"));
        } else {
            updateAnnotationLocatorValue((PsiAnnotation) methodCall, locatorExpression, factory.createIdentifier("css"));
        }
    }

    protected void updateMethodLocatorValue(PsiMethodCallExpression methodCall, PsiExpression locatorExpression, PsiElement cssArgument) {
        methodCall.getArgumentList().getExpressions()[0].replace(locatorExpression);
        methodCall.getMethodExpression().getReferenceNameElement().replace(cssArgument);
    }

    protected void updateAnnotationLocatorValue(PsiAnnotation methodCall, PsiExpression locatorExpression, PsiElement cssArgument) {
        ((PsiNameValuePair) methodCall.getAttributes().get(0)).setValue(locatorExpression);
        ((PsiNameValuePair) methodCall.getAttributes().get(0)).getNameIdentifier().replace(cssArgument);
    }

    protected void healingResultNotification() {
        if (count == 0) {
            notifier.notify("No healing results are available");
        }
        count = 0;
    }
}
