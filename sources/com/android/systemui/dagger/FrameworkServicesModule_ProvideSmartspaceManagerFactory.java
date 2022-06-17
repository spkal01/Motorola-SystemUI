package com.android.systemui.dagger;

import android.app.smartspace.SmartspaceManager;
import android.content.Context;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvideSmartspaceManagerFactory implements Factory<SmartspaceManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvideSmartspaceManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public SmartspaceManager get() {
        return provideSmartspaceManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvideSmartspaceManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvideSmartspaceManagerFactory(provider);
    }

    public static SmartspaceManager provideSmartspaceManager(Context context) {
        return (SmartspaceManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideSmartspaceManager(context));
    }
}
