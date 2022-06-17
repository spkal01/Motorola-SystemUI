package com.android.systemui.dump;

import dagger.internal.Factory;

public final class DumpManager_Factory implements Factory<DumpManager> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final DumpManager_Factory INSTANCE = new DumpManager_Factory();
    }

    public DumpManager get() {
        return newInstance();
    }

    public static DumpManager_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static DumpManager newInstance() {
        return new DumpManager();
    }
}
