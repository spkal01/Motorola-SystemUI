package com.android.systemui.util.concurrency;

import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class GlobalConcurrencyModule_ProvideMainExecutorFactory implements Factory<Executor> {
    private final Provider<Context> contextProvider;

    public GlobalConcurrencyModule_ProvideMainExecutorFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Executor get() {
        return provideMainExecutor(this.contextProvider.get());
    }

    public static GlobalConcurrencyModule_ProvideMainExecutorFactory create(Provider<Context> provider) {
        return new GlobalConcurrencyModule_ProvideMainExecutorFactory(provider);
    }

    public static Executor provideMainExecutor(Context context) {
        return (Executor) Preconditions.checkNotNullFromProvides(GlobalConcurrencyModule.provideMainExecutor(context));
    }
}
