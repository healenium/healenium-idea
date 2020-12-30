package com.epam.healenium.popup;

import com.epam.healenium.model.HealingDto;
import com.epam.healenium.model.HealingResultDto;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import java.util.Comparator;
import java.util.Set;

public class HealingResultPopupDialogWrapper extends DialogWrapper {

    private OnIOkClickListener okClickListener;
    private Set<HealingDto> healingDtoSet;

    public HealingResultPopupDialogWrapper(Set<HealingDto> healingDtoSet,
                                           OnIOkClickListener okClickListener) {
        super(true); // use current window as parent
        this.healingDtoSet = healingDtoSet;
        this.okClickListener = okClickListener;
        init();
        setTitle("Healing Results");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));

        healingDtoSet.forEach(data -> {
            String locatorValue = data.getResults().stream()
                    .filter(r -> r.getCreateDate() != null)
                    .max(Comparator.comparing(HealingResultDto::getCreateDate))
                    .orElse(data.getResults().iterator().next())
                    .getLocator().getValue();
            JCheckBox checkBox = new JCheckBox(data.getLocator() + "  ->  " + locatorValue);
            checkBox.setSelected(true);
            dialogPanel.add(checkBox);
        });

        return dialogPanel;
    }

    public interface OnIOkClickListener {
        void onOkClick(Set<HealingDto> allData);
    }

    @Override
    public void doOKAction() {
        okClickListener.onOkClick(healingDtoSet);
        super.doOKAction();
    }
}
