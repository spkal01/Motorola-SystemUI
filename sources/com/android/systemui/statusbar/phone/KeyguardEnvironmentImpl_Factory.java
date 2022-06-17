package com.android.systemui.statusbar.phone;

import dagger.internal.Factory;

public final class KeyguardEnvironmentImpl_Factory implements Factory<KeyguardEnvironmentImpl> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final KeyguardEnvironmentImpl_Factory INSTANCE = new KeyguardEnvironmentImpl_Factory();
    }

    public KeyguardEnvironmentImpl get() {
        return newInstance();
    }

    public static KeyguardEnvironmentImpl_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static KeyguardEnvironmentImpl newInstance() {
        return new KeyguardEnvironmentImpl();
    }
}
