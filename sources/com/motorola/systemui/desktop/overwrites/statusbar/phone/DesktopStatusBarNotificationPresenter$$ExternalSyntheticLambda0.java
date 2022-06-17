package com.motorola.systemui.desktop.overwrites.statusbar.phone;

import java.util.function.BooleanSupplier;

public final /* synthetic */ class DesktopStatusBarNotificationPresenter$$ExternalSyntheticLambda0 implements BooleanSupplier {
    public final /* synthetic */ DesktopStatusBarNotificationPresenter f$0;

    public /* synthetic */ DesktopStatusBarNotificationPresenter$$ExternalSyntheticLambda0(DesktopStatusBarNotificationPresenter desktopStatusBarNotificationPresenter) {
        this.f$0 = desktopStatusBarNotificationPresenter;
    }

    public final boolean getAsBoolean() {
        return this.f$0.canDismissLockScreen();
    }
}
