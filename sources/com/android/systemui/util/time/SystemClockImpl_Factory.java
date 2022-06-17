package com.android.systemui.util.time;

import dagger.internal.Factory;

public final class SystemClockImpl_Factory implements Factory<SystemClockImpl> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SystemClockImpl_Factory INSTANCE = new SystemClockImpl_Factory();
    }

    public SystemClockImpl get() {
        return newInstance();
    }

    public static SystemClockImpl_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static SystemClockImpl newInstance() {
        return new SystemClockImpl();
    }
}
