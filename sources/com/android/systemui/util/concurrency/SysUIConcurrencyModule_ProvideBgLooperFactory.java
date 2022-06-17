package com.android.systemui.util.concurrency;

import android.os.Looper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class SysUIConcurrencyModule_ProvideBgLooperFactory implements Factory<Looper> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SysUIConcurrencyModule_ProvideBgLooperFactory INSTANCE = new SysUIConcurrencyModule_ProvideBgLooperFactory();
    }

    public Looper get() {
        return provideBgLooper();
    }

    public static SysUIConcurrencyModule_ProvideBgLooperFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static Looper provideBgLooper() {
        return (Looper) Preconditions.checkNotNullFromProvides(SysUIConcurrencyModule.provideBgLooper());
    }
}
