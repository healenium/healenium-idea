package com.epam.healenium;

import com.epam.healenium.model.HealingDto;
import com.epam.healenium.model.HealingResultDto;
import com.epam.healenium.popup.SinglHealingResultPopup;
import com.epam.healenium.util.Predicates;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiFile;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
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

        HealingDto healingDto = null;
        if (Predicates.isPsiJavaTocken().test(element)) {
            healingDto = actionPerformedPerElement(e, element);
        }
        if (healingDto != null) {
            showResultsAndUpdate(e, healingDto);
        }
        healingResultNotification();
    }

    private void showResultsAndUpdate(AnActionEvent e, HealingDto healingDto) {
        new SinglHealingResultPopup(e).createList("Healing Results", healingDto.getResults().toArray(
                new HealingResultDto[0]), (position, value) -> {
            updateLocators(value, healingDto.getMethodCall());
        });
    }

    protected void updateLocators(HealingResultDto resultDto, PsiElement methodCall) {
        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(() -> {
                    PsiExpression locatorExpression = factory.createExpressionFromText("\""
                            + resultDto.getLocator().getValue() + "\"", null);
                    updateLocatorValue(methodCall, locatorExpression);
                }), "Updated locator", null);
    }

}
