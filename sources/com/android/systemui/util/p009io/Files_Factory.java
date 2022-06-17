package com.android.systemui.util.p009io;

import dagger.internal.Factory;

/* renamed from: com.android.systemui.util.io.Files_Factory */
public final class Files_Factory implements Factory<Files> {

    /* renamed from: com.android.systemui.util.io.Files_Factory$InstanceHolder */
    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final Files_Factory INSTANCE = new Files_Factory();
    }

    public Files get() {
        return newInstance();
    }

    public static Files_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static Files newInstance() {
        return new Files();
    }
}
