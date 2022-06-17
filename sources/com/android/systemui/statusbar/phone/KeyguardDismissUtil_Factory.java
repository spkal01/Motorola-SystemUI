package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;

public final class KeyguardDismissUtil_Factory implements Factory<KeyguardDismissUtil> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final KeyguardDismissUtil_Factory INSTANCE = new KeyguardDismissUtil_Factory();
    }

    public KeyguardDismissUtil get() {
        return newInstance();
    }

    public static KeyguardDismissUtil_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static KeyguardDismissUtil newInstance() {
        return new KeyguardDismissUtil();
    }
}
