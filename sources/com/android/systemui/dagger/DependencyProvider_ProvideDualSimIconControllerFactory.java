package com.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.moto.DualSimIconController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class DependencyProvider_ProvideDualSimIconControllerFactory implements Factory<DualSimIconController> {
    private final Provider<Context> contextProvider;
    private final DependencyProvider module;

    public DependencyProvider_ProvideDualSimIconControllerFactory(DependencyProvider dependencyProvider, Provider<Context> provider) {
        this.module = dependencyProvider;
        this.contextProvider = provider;
    }

    public DualSimIconController get() {
        return provideDualSimIconController(this.module, this.contextProvider.get());
    }

    public static DependencyProvider_ProvideDualSimIconControllerFactory create(DependencyProvider dependencyProvider, Provider<Context> provider) {
        return new DependencyProvider_ProvideDualSimIconControllerFactory(dependencyProvider, provider);
    }

    public static DualSimIconController provideDualSimIconController(DependencyProvider dependencyProvider, Context context) {
        return (DualSimIconController) Preconditions.checkNotNullFromProvides(dependencyProvider.provideDualSimIconController(context));
    }
}
