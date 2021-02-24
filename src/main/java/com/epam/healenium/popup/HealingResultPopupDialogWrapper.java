package com.epam.healenium.popup;

import com.epam.healenium.model.HealingDto;
import com.epam.healenium.model.HealingResultDto;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HealingResultPopupDialogWrapper extends DialogWrapper {

    private OnIOkClickListener okClickListener;
    private Set<HealingDto> healingDtoSet;
    private JComponent jComponent;

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
            checkBox.setName(data.getLocator());
            checkBox.setSelected(true);
            dialogPanel.add(checkBox);
        });

        this.jComponent = dialogPanel;
        return dialogPanel;
    }

    public interface OnIOkClickListener {
        void onOkClick(Set<HealingDto> allData);
    }

    @Override
    public void doOKAction() {
        filterUnselectedElements(healingDtoSet);
        okClickListener.onOkClick(healingDtoSet);
        super.doOKAction();
    }

    private void filterUnselectedElements(Set<HealingDto> healingDtoSet) {
        List<String> unselectedLocators = Arrays.stream(jComponent.getComponents())
                .map(JCheckBox.class::cast)
                .filter(c -> !c.isSelected())
                .map(Component::getName)
                .collect(Collectors.toList());
        healingDtoSet.removeIf(dto -> unselectedLocators.contains(dto.getLocator()));
    }
}
