package com.android.systemui.keyguard;

import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class DismissCallbackRegistry_Factory implements Factory<DismissCallbackRegistry> {
    private final Provider<Executor> uiBgExecutorProvider;

    public DismissCallbackRegistry_Factory(Provider<Executor> provider) {
        this.uiBgExecutorProvider = provider;
    }

    public DismissCallbackRegistry get() {
        return newInstance(this.uiBgExecutorProvider.get());
    }

    public static DismissCallbackRegistry_Factory create(Provider<Executor> provider) {
        return new DismissCallbackRegistry_Factory(provider);
    }

    public static DismissCallbackRegistry newInstance(Executor executor) {
        return new DismissCallbackRegistry(executor);
    }
}
