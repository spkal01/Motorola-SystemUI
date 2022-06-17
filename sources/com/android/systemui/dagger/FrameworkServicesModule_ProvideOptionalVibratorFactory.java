package com.android.systemui.dagger;

import android.content.Context;
import android.os.Vibrator;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Optional;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideOptionalVibratorFactory implements Factory<Optional<Vibrator>> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideOptionalVibratorFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Optional<Vibrator> get() {
        return provideOptionalVibrator(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideOptionalVibratorFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideOptionalVibratorFactory(provider);
    }

    public static Optional<Vibrator> provideOptionalVibrator(Context context) {
        return (Optional) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideOptionalVibrator(context));
    }
}
