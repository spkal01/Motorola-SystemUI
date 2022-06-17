package com.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.doze.MotoDisplayManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class DependencyProvider_ProvideMotoDisplayManagerFactory implements Factory<MotoDisplayManager> {
    private final Provider<Context> contextProvider;
    private final Provider<DozeHost> dozeHostProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideMotoDisplayManagerFactory(DependencyProvider dependencyProvider, Provider<Context> provider, Provider<DozeHost> provider2) {
        this.module = dependencyProvider;
        this.contextProvider = provider;
        this.dozeHostProvider = provider2;
    }

    public MotoDisplayManager get() {
        return provideMotoDisplayManager(this.module, this.contextProvider.get(), this.dozeHostProvider.get());
    }

    public static DependencyProvider_ProvideMotoDisplayManagerFactory create(DependencyProvider dependencyProvider, Provider<Context> provider, Provider<DozeHost> provider2) {
        return new DependencyProvider_ProvideMotoDisplayManagerFactory(dependencyProvider, provider, provider2);
    }

    public static MotoDisplayManager provideMotoDisplayManager(DependencyProvider dependencyProvider, Context context, DozeHost dozeHost) {
        return (MotoDisplayManager) Preconditions.checkNotNullFromProvides(dependencyProvider.provideMotoDisplayManager(context, dozeHost));
    }
}
