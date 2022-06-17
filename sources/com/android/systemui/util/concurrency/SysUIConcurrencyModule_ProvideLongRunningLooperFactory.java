package com.android.systemui.util.concurrency;

import android.os.Looper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class SysUIConcurrencyModule_ProvideLongRunningLooperFactory implements Factory<Looper> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SysUIConcurrencyModule_ProvideLongRunningLooperFactory INSTANCE = new SysUIConcurrencyModule_ProvideLongRunningLooperFactory();
    }

    public Looper get() {
        return provideLongRunningLooper();
    }

    public static SysUIConcurrencyModule_ProvideLongRunningLooperFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static Looper provideLongRunningLooper() {
        return (Looper) Preconditions.checkNotNullFromProvides(SysUIConcurrencyModule.provideLongRunningLooper());
    }
}
