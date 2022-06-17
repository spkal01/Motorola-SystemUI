package com.android.systemui.dagger;

import android.content.Context;
import android.os.PowerManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvidePowerManagerFactory implements Factory<PowerManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvidePowerManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public PowerManager get() {
        return providePowerManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvidePowerManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvidePowerManagerFactory(provider);
    }

    public static PowerManager providePowerManager(Context context) {
        return (PowerManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.providePowerManager(context));
    }
}
