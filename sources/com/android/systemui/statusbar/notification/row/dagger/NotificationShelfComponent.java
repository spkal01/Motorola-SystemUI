package com.android.systemui.statusbar.notification.row.dagger;

import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.NotificationShelfController;

public interface NotificationShelfComponent {

    public interface Builder {
        NotificationShelfComponent build();

        Builder notificationShelf(NotificationShelf notificationShelf);
    }

    NotificationShelfController getNotificationShelfController();
}
