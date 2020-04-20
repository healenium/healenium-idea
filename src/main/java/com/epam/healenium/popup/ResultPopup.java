package com.epam.healenium.popup;

import com.epam.healenium.model.HealingResultDto;
import com.epam.healenium.util.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

public class ResultPopup {

    private AnActionEvent event;
    private ListPopup listPopup;
    private JBPopupFactory.ActionSelectionAid aid;

    public ResultPopup(AnActionEvent event) {
        this.event = event;
        this.aid = JBPopupFactory.ActionSelectionAid.NUMBERING;
    }

    public void createList(String title, HealingResultDto[] data, OnItemClickListener listener) {
        DefaultActionGroup group = new DefaultActionGroup();
        String type = null;
        if (data != null && data.length > 0) {
            int maxLength = Arrays.stream(data).map(it-> it.getLocator().getValue()).max(Comparator.comparingInt(String::length)).get().length();
            for (int i = 0; i < data.length; i++) {
                if(Utils.isEmpty(type) || !data[i].getLocator().getType().equals(type)){
                    type = data[i].getLocator().getType();
                    group.addSeparator(" " + type + " ");
                }
                String locator = data[i].getLocator().getValue();
                String locatorValue = StringUtils.rightPad(locator, 100 + (maxLength - locator.length()), "");
                String scoreValue = String.format("score = %.2f", data[i].getScore());
                String actionTitle = String.format("%1$s %2$s", locatorValue, scoreValue);
                group.add(new ListItemAction(i, data[i], actionTitle, listener));
            }
        }
        listPopup = JBPopupFactory.getInstance().createActionGroupPopup(title, group, event.getDataContext(), aid, false);
        show();
    }

    public void dispose() {
        if (listPopup != null) {
            listPopup.dispose();
        }
    }

    private void show() {
        if (listPopup == null || event == null) {
            return;
        }
        Project project = event.getProject();
        if (project != null) {
            listPopup.showCenteredInCurrentWindow(project);
        } else {
            listPopup.showInBestPositionFor(event.getDataContext());
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position, HealingResultDto value);
    }

    private static class ListItemAction extends AnAction {

        private int index;
        private HealingResultDto value;
        private OnItemClickListener clickListener;

        public ListItemAction(int index, HealingResultDto value, String title, OnItemClickListener clickListener) {
            super(title);
            this.index = index;
            this.value = value;
            this.clickListener = clickListener;
        }

        @Override
        public void actionPerformed(AnActionEvent anActionEvent) {
            if (clickListener != null) {
                clickListener.OnItemClick(this.index, this.value);
            }
        }
    }
}
