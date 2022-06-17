package com.android.systemui.dagger;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvidesFingerprintManagerFactory implements Factory<FingerprintManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvidesFingerprintManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public FingerprintManager get() {
        return providesFingerprintManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvidesFingerprintManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvidesFingerprintManagerFactory(provider);
    }

    public static FingerprintManager providesFingerprintManager(Context context) {
        return FrameworkServicesModule.providesFingerprintManager(context);
    }
}
