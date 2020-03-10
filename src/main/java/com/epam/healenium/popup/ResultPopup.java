package com.epam.healenium.popup;

import com.epam.healenium.model.Locator;
import com.epam.healenium.util.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;

public class ResultPopup {

    private AnActionEvent event;
    private ListPopup listPopup;
    private JBPopupFactory.ActionSelectionAid aid;

    public ResultPopup(AnActionEvent event) {
        this.event = event;
        this.aid = JBPopupFactory.ActionSelectionAid.NUMBERING;
    }

    public void createList(String title, Locator[] data, OnItemClickListener listener) {
        DefaultActionGroup group = new DefaultActionGroup();
        String type = null;
        if (data != null && data.length > 0) {
            for (int i = 0; i < data.length; i++) {
                if(Utils.isEmpty(type) || !data[i].getType().equals(type)){
                    type = data[i].getType();
                    group.addSeparator(" " + type + " ");
                }
                group.add(new ListItemAction(i, data[i].getValue(), listener));
            }
        }
        listPopup = JBPopupFactory.getInstance().createActionGroupPopup(title, group,
            event.getDataContext(), aid, true, null, -1, null, "unknown");
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
        void OnItemClick(int position, String value);
    }

    private static class ListItemAction extends AnAction {

        private int index;
        private String value;
        private OnItemClickListener clickListener;

        public ListItemAction(int index, String value, OnItemClickListener clickListener) {
            super(value);
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
