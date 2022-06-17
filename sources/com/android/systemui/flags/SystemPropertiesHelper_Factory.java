package com.android.systemui.flags;

import dagger.internal.Factory;

public final class SystemPropertiesHelper_Factory implements Factory<SystemPropertiesHelper> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SystemPropertiesHelper_Factory INSTANCE = new SystemPropertiesHelper_Factory();
    }

    public SystemPropertiesHelper get() {
        return newInstance();
    }

    public static SystemPropertiesHelper_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static SystemPropertiesHelper newInstance() {
        return new SystemPropertiesHelper();
    }
}
