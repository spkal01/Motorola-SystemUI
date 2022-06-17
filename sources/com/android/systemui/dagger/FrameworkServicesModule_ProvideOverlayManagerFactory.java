package com.android.systemui.dagger;

import android.content.Context;
import android.content.om.OverlayManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideOverlayManagerFactory implements Factory<OverlayManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideOverlayManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public OverlayManager get() {
        return provideOverlayManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideOverlayManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideOverlayManagerFactory(provider);
    }

    public static OverlayManager provideOverlayManager(Context context) {
        return (OverlayManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideOverlayManager(context));
    }
}
