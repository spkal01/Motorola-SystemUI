package com.android.systemui.dagger;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideDisplayIdFactory implements Factory<Integer> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideDisplayIdFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Integer get() {
        return Integer.valueOf(provideDisplayId(this.contextProvider.get()));
    }

    public static FrameworkServicesModule_ProvideDisplayIdFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideDisplayIdFactory(provider);
    }

    public static int provideDisplayId(Context context) {
        return FrameworkServicesModule.provideDisplayId(context);
    }
}
