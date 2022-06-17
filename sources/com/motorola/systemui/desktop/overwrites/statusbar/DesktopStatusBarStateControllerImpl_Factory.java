package com.motorola.systemui.desktop.overwrites.statusbar;

import dagger.internal.Factory;

public final class DesktopStatusBarStateControllerImpl_Factory implements Factory<DesktopStatusBarStateControllerImpl> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final DesktopStatusBarStateControllerImpl_Factory INSTANCE = new DesktopStatusBarStateControllerImpl_Factory();
    }

    public DesktopStatusBarStateControllerImpl get() {
        return newInstance();
    }

    public static DesktopStatusBarStateControllerImpl_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static DesktopStatusBarStateControllerImpl newInstance() {
        return new DesktopStatusBarStateControllerImpl();
    }
}
