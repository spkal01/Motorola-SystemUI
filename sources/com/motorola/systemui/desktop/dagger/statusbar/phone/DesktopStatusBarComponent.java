package com.motorola.systemui.desktop.dagger.statusbar.phone;

import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController;
import com.motorola.systemui.desktop.overwrites.statusbar.notification.DesktopHeadsUpController;

public interface DesktopStatusBarComponent {

    public interface Builder {
        DesktopStatusBarComponent build();
    }

    DesktopHeadsUpController getHeadsUpController();

    DesktopNotificationStackScrollLayoutController getNotificationStackScrollLayoutController();
}
