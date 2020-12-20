package com.epam.healenium;

import com.epam.healenium.model.HealingResultDto;
import com.epam.healenium.util.Predicates;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GetClassHealingAction extends AbstractHealingAction {

    public GetClassHealingAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        List<PsiElement> allElementList = iterableTree(file.getChildren());
        allElementList.forEach(element -> actionPerformedPerElement(e, element));
        healingResultNotification();
    }

    protected void showResultsAndUpdate(@NotNull AnActionEvent e, Set<HealingResultDto> locators) {
        updateLocators(locators.iterator().next());
    }

    @Override
    protected void healingResultNotification() {
        if (count > 0) {
            notifier.notify(count + " Locators was healing successfully");
        }
        super.healingResultNotification();
    }

    private List<PsiElement> iterableTree(PsiElement[] children) {
        List<PsiElement> psiElementList = new ArrayList();
        if (children == null || children.length == 0) {
            return psiElementList;
        }

        Arrays.stream(children).
                forEach(element -> {
                    if (Predicates.isPsiJavaTocken().test(element)) {
                        psiElementList.add(element);
                    }
                    psiElementList.addAll(iterableTree(element.getChildren()));

                });

        return psiElementList;
    }
}
