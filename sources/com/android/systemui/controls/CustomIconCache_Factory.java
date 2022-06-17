package com.android.systemui.controls;

import dagger.internal.Factory;

public final class CustomIconCache_Factory implements Factory<CustomIconCache> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final CustomIconCache_Factory INSTANCE = new CustomIconCache_Factory();
    }

    public CustomIconCache get() {
        return newInstance();
    }

    public static CustomIconCache_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static CustomIconCache newInstance() {
        return new CustomIconCache();
    }
}
