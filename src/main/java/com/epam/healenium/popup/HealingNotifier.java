package com.epam.healenium.popup;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class HealingNotifier {

//    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("Healing notifications", NotificationDisplayType.BALLOON, true);

    public Notification notify(String content) {
        return notify(null, content, NotificationType.INFORMATION);
    }

    public Notification notify(String content, NotificationType type) {
        return notify(null, content, type);
    }

    public Notification notify(Project project, String content, NotificationType type) {
//        final Notification notification = NOTIFICATION_GROUP.createNotification(content, type);
        final Notification notification = new Notification(new String("Healing notifications"), "", content, type);
        notification.notify(project);
        return notification;
    }

}
