package com.android.systemui.dagger;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideColorDisplayManagerFactory implements Factory<ColorDisplayManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideColorDisplayManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public ColorDisplayManager get() {
        return provideColorDisplayManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideColorDisplayManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideColorDisplayManagerFactory(provider);
    }

    public static ColorDisplayManager provideColorDisplayManager(Context context) {
        return (ColorDisplayManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideColorDisplayManager(context));
    }
}
