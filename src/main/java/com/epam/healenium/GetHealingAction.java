package com.epam.healenium;

import com.epam.healenium.model.HealingResultDto;
import com.epam.healenium.popup.ResultPopup;
import com.epam.healenium.util.Predicates;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Slf4j
public class GetHealingAction extends AbstractHealingAction {

    public GetHealingAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(LangDataKeys.EDITOR);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();

        // элемент на котором находится курсор
        PsiElement element = file.findElementAt(caretOffset);

        if (Predicates.isPsiJavaTocken().test(element)) {
            actionPerformedPerElement(e, element);
        }
        healingResultNotification();
    }

    protected void showResultsAndUpdate(@NotNull AnActionEvent e, Set<HealingResultDto> locators) {
        new ResultPopup(e).createList("Healing Results", locators.toArray(
                new HealingResultDto[0]), (position, value) -> {
            updateLocators(value);
        });
    }

}
