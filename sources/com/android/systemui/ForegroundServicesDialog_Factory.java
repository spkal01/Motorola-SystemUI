package com.android.systemui;

import dagger.internal.Factory;

public final class ForegroundServicesDialog_Factory implements Factory<ForegroundServicesDialog> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final ForegroundServicesDialog_Factory INSTANCE = new ForegroundServicesDialog_Factory();
    }

    public ForegroundServicesDialog get() {
        return newInstance();
    }

    public static ForegroundServicesDialog_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static ForegroundServicesDialog newInstance() {
        return new ForegroundServicesDialog();
    }
}
