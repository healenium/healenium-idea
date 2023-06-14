package com.epam.healenium;

import com.epam.healenium.client.HealingClient;
import com.epam.healenium.model.ByArgumentEnum;
import com.epam.healenium.model.HealingDto;
import com.epam.healenium.model.HealingResultDto;
import com.epam.healenium.popup.HealingNotifier;
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
        // receive method
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        // receive locator
        PsiLiteralExpression literalExpression = PsiTreeUtil.getParentOfType(element, PsiLiteralExpression.class);
        if (literalExpression == null) return null;
        String locator = (String) literalExpression.getValue();
        String className = method != null
                // receive full class name
                ? method.getContainingClass().getQualifiedName()
                // receive full class name. because method is null, so we on the top level and no need run the tree.
                : PsiTreeUtil.getParentOfType(element, PsiMember.class).getContainingClass().getQualifiedName();

        // get Healed locators
        Set<HealingDto> data = new HealingClient().makeCall(locator, null);
        filterNonSuccessHealingResults(data);

        if (data.isEmpty()) return null;

        HealingDto healingDto = data.iterator().next();
        if (method != null && data.size() != 1) {
            healingDto.setResults(findProperMethod(data, method));
        }

        PsiElement methodCall = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (methodCall == null) {
            methodCall = PsiTreeUtil.getParentOfType(element, PsiAnnotation.class);
        }

        if (methodCall != null) {
            factory = JavaPsiFacade.getInstance(project).getElementFactory();
        }

        count++;
        return healingDto.setMethodCall(methodCall);
    }

    private void filterNonSuccessHealingResults(Set<HealingDto> data) {
        if (!data.isEmpty()) {
            data.forEach(dto -> dto.getResults().removeIf(r -> !r.isSuccessHealing()));
            data.removeIf(d -> d.getResults().isEmpty());
        }
    }

    protected Set<HealingResultDto> findProperMethod(Set<HealingDto> data, PsiMethod referenceMethod) {
        Set<HealingResultDto> result = findResults(data, referenceMethod);
        if (!result.isEmpty()) return result;
        SearchScope searchScope = ProjectScope.getContentScope(project);
        Collection<PsiReference> refs = MethodReferencesSearch
                .search(referenceMethod, searchScope, true)
                .findAll();
        for (PsiReference reference : refs) {
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

    protected void updateLocatorValue(PsiElement methodCall, PsiExpression locatorExpression, String type) {
        if (methodCall instanceof PsiMethodCallExpression) {
            String text = methodCall.getFirstChild().getLastChild().getText();
            updateMethodLocatorValue((PsiMethodCallExpression) methodCall, locatorExpression, factory.createIdentifier(text));
        } else {
            String annotationValue = ByArgumentEnum.getAnnotationValue(type);
            updateAnnotationLocatorValue((PsiAnnotation) methodCall, locatorExpression, factory.createIdentifier(annotationValue));
        }
    }

    protected void updateMethodLocatorValue(PsiMethodCallExpression methodCall, PsiExpression locatorExpression, PsiElement argument) {
        methodCall.getArgumentList().getExpressions()[0].replace(locatorExpression);
        methodCall.getMethodExpression().getReferenceNameElement().replace(argument);
    }

    protected void updateAnnotationLocatorValue(PsiAnnotation methodCall, PsiExpression locatorExpression, PsiElement argument) {
        ((PsiNameValuePair) methodCall.getAttributes().get(0)).setValue(locatorExpression);
        ((PsiNameValuePair) methodCall.getAttributes().get(0)).getNameIdentifier().replace(argument);
    }

    protected void healingResultNotification() {
        if (count == 0) {
            notifier.notify("No healing results are available");
        }
        count = 0;
    }
}
