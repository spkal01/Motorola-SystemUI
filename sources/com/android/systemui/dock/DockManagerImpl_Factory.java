package com.android.systemui.dock;

import dagger.internal.Factory;

public final class DockManagerImpl_Factory implements Factory<DockManagerImpl> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final DockManagerImpl_Factory INSTANCE = new DockManagerImpl_Factory();
    }

    public DockManagerImpl get() {
        return newInstance();
    }

    public static DockManagerImpl_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static DockManagerImpl newInstance() {
        return new DockManagerImpl();
    }
}
