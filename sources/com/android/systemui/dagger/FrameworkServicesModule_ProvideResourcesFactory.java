package com.android.systemui.dagger;

import android.content.Context;
import android.content.res.Resources;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideResourcesFactory implements Factory<Resources> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideResourcesFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Resources get() {
        return provideResources(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideResourcesFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideResourcesFactory(provider);
    }

    public static Resources provideResources(Context context) {
        return (Resources) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideResources(context));
    }
}
