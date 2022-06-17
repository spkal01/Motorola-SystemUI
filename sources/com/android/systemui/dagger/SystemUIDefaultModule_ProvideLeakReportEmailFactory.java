package com.android.systemui.dagger;

import dagger.internal.Factory;

public final class SystemUIDefaultModule_ProvideLeakReportEmailFactory implements Factory<String> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SystemUIDefaultModule_ProvideLeakReportEmailFactory INSTANCE = new SystemUIDefaultModule_ProvideLeakReportEmailFactory();
    }

    public String get() {
        return provideLeakReportEmail();
    }

    public static SystemUIDefaultModule_ProvideLeakReportEmailFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static String provideLeakReportEmail() {
        return SystemUIDefaultModule.provideLeakReportEmail();
    }
}
