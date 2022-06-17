package com.android.systemui.dagger;

import android.app.trust.TrustManager;
import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideTrustManagerFactory implements Factory<TrustManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideTrustManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public TrustManager get() {
        return provideTrustManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideTrustManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideTrustManagerFactory(provider);
    }

    public static TrustManager provideTrustManager(Context context) {
        return (TrustManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideTrustManager(context));
    }
}
