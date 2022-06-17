package com.android.systemui.statusbar.notification.collection.listbuilder.pluggable;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public abstract class NotifStabilityManager extends Pluggable<NotifStabilityManager> {
    public abstract boolean isGroupChangeAllowed(NotificationEntry notificationEntry);

    public abstract boolean isSectionChangeAllowed(NotificationEntry notificationEntry);

    public abstract void onBeginRun();

    protected NotifStabilityManager(String str) {
        super(str);
    }
}
