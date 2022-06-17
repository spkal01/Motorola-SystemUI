package com.android.systemui.p008tv;

import dagger.internal.Factory;

/* renamed from: com.android.systemui.tv.TvSystemUIModule_ProvideLeakReportEmailFactory */
public final class TvSystemUIModule_ProvideLeakReportEmailFactory implements Factory<String> {

    /* renamed from: com.android.systemui.tv.TvSystemUIModule_ProvideLeakReportEmailFactory$InstanceHolder */
    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final TvSystemUIModule_ProvideLeakReportEmailFactory INSTANCE = new TvSystemUIModule_ProvideLeakReportEmailFactory();
    }

    public String get() {
        return provideLeakReportEmail();
    }

    public static TvSystemUIModule_ProvideLeakReportEmailFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static String provideLeakReportEmail() {
        return TvSystemUIModule.provideLeakReportEmail();
    }
}
