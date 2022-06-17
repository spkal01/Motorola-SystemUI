package com.android.systemui.dagger;

import android.content.Context;
import android.os.Vibrator;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideVibratorFactory implements Factory<Vibrator> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideVibratorFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Vibrator get() {
        return provideVibrator(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideVibratorFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideVibratorFactory(provider);
    }

    public static Vibrator provideVibrator(Context context) {
        return FrameworkServicesModule.provideVibrator(context);
    }
}
