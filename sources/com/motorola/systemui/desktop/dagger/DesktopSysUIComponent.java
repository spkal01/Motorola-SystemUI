package com.motorola.systemui.desktop.dagger;

import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopStatusBarKeyguardViewManager;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopStatusBarNotificationPresenter;

public interface DesktopSysUIComponent extends SysUIComponent {

    public interface Builder extends SysUIComponent.Builder {
        DesktopSysUIComponent build();
    }

    StatusBar getStatusBar();

    void inject(DesktopStatusBarNotificationPresenter desktopStatusBarNotificationPresenter);

    public interface DesktopModule {
        static StatusBarKeyguardViewManager provideStatusBarKeyguardViewManager() {
            return new DesktopStatusBarKeyguardViewManager();
        }
    }
}
