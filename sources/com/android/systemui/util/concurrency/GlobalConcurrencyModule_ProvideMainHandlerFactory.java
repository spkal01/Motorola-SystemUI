package com.android.systemui.util.concurrency;

import android.os.Handler;
import android.os.Looper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class GlobalConcurrencyModule_ProvideMainHandlerFactory implements Factory<Handler> {
    private final Provider<Looper> mainLooperProvider;

    public GlobalConcurrencyModule_ProvideMainHandlerFactory(Provider<Looper> provider) {
        this.mainLooperProvider = provider;
    }

    public Handler get() {
        return provideMainHandler(this.mainLooperProvider.get());
    }

    public static GlobalConcurrencyModule_ProvideMainHandlerFactory create(Provider<Looper> provider) {
        return new GlobalConcurrencyModule_ProvideMainHandlerFactory(provider);
    }

    public static Handler provideMainHandler(Looper looper) {
        return (Handler) Preconditions.checkNotNullFromProvides(GlobalConcurrencyModule.provideMainHandler(looper));
    }
}
