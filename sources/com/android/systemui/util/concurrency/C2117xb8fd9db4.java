package com.android.systemui.util.concurrency;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideBackgroundRepeatableExecutorFactory */
public final class C2117xb8fd9db4 implements Factory<RepeatableExecutor> {
    private final Provider<DelayableExecutor> execProvider;

    public C2117xb8fd9db4(Provider<DelayableExecutor> provider) {
        this.execProvider = provider;
    }

    public RepeatableExecutor get() {
        return provideBackgroundRepeatableExecutor(this.execProvider.get());
    }

    public static C2117xb8fd9db4 create(Provider<DelayableExecutor> provider) {
        return new C2117xb8fd9db4(provider);
    }

    public static RepeatableExecutor provideBackgroundRepeatableExecutor(DelayableExecutor delayableExecutor) {
        return (RepeatableExecutor) Preconditions.checkNotNullFromProvides(SysUIConcurrencyModule.provideBackgroundRepeatableExecutor(delayableExecutor));
    }
}
