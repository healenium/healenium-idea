package com.epam.healenium;

import com.epam.healenium.model.HealingDto;
import com.epam.healenium.model.HealingResultDto;
import com.epam.healenium.popup.HealingResultPopupDialogWrapper;
import com.epam.healenium.util.Predicates;
import com.epam.healenium.util.Utils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GetClassHealingAction extends AbstractHealingAction {

    public GetClassHealingAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        List<PsiElement> allClassElementList = Utils.iterableTree(file.getChildren(), Predicates.isPsiJavaTocken());
        Set<HealingDto> healingDtoSet = new HashSet<>();
        allClassElementList.stream()
                .map(element -> actionPerformedPerElement(e, element))
                .filter(Objects::nonNull)
                .forEach(healingDtoSet::add);
        if (!healingDtoSet.isEmpty()) {
            showResultsAndUpdate(healingDtoSet);
        }
        healingResultNotification();
    }

    protected void showResultsAndUpdate(Set<HealingDto> healingDtoSet) {
        new HealingResultPopupDialogWrapper(healingDtoSet,
                this::updateLocators).show();
    }

    protected void updateLocators(Set<HealingDto> healingDtoSet) {
        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(() -> {
                    for (HealingDto healingDto : healingDtoSet) {
                        String locatorValue = healingDto.getResults().stream()
                                .filter(r -> r.getCreateDate() != null)
                                .max(Comparator.comparing(HealingResultDto::getCreateDate))
                                .orElse(healingDto.getResults().iterator().next())
                                .getLocator()
                                .getValue();
                        PsiExpression locatorExpression = factory.createExpressionFromText(
                                "\"" + locatorValue + "\"", null);
                        updateLocatorValue(healingDto.getMethodCall(), locatorExpression);
                    }
                }), "Updated Locator", null);
    }

    @Override
    protected void healingResultNotification() {
        if (count > 0) {
            notifier.notify(count + " Locators was healing successfully");
        }
        super.healingResultNotification();
    }

}
