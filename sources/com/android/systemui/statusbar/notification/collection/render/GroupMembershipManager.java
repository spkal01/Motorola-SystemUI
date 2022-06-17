package com.android.systemui.statusbar.notification.collection.render;

import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.List;

public interface GroupMembershipManager {
    List<NotificationEntry> getChildren(ListEntry listEntry);

    NotificationEntry getGroupSummary(NotificationEntry notificationEntry);

    boolean isChildInGroup(NotificationEntry notificationEntry);

    boolean isGroupSummary(NotificationEntry notificationEntry);

    boolean isOnlyChildInGroup(NotificationEntry notificationEntry);

    NotificationEntry getLogicalGroupSummary(NotificationEntry notificationEntry) {
        return getGroupSummary(notificationEntry);
    }
}
