package com.android.systemui.dagger;

import dagger.internal.Factory;

public final class GlobalModule_ProvideIsTestHarnessFactory implements Factory<Boolean> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final GlobalModule_ProvideIsTestHarnessFactory INSTANCE = new GlobalModule_ProvideIsTestHarnessFactory();
    }

    public Boolean get() {
        return Boolean.valueOf(provideIsTestHarness());
    }

    public static GlobalModule_ProvideIsTestHarnessFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static boolean provideIsTestHarness() {
        return GlobalModule.provideIsTestHarness();
    }
}
