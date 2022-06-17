package com.android.systemui.util.concurrency;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.concurrent.Executor;

public final class SysUIConcurrencyModule_ProvideUiBackgroundExecutorFactory implements Factory<Executor> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SysUIConcurrencyModule_ProvideUiBackgroundExecutorFactory INSTANCE = new SysUIConcurrencyModule_ProvideUiBackgroundExecutorFactory();
    }

    public Executor get() {
        return provideUiBackgroundExecutor();
    }

    public static SysUIConcurrencyModule_ProvideUiBackgroundExecutorFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static Executor provideUiBackgroundExecutor() {
        return (Executor) Preconditions.checkNotNullFromProvides(SysUIConcurrencyModule.provideUiBackgroundExecutor());
    }
}
