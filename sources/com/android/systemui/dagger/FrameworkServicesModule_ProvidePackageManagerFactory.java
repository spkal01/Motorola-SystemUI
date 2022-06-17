package com.android.systemui.dagger;

import android.content.Context;
import android.content.pm.PackageManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class FrameworkServicesModule_ProvidePackageManagerFactory implements Factory<PackageManager> {
    private final Provider<Context> contextProvider;

    public FrameworkServicesModule_ProvidePackageManagerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public PackageManager get() {
        return providePackageManager(this.contextProvider.get());
    }

    public static FrameworkServicesModule_ProvidePackageManagerFactory create(Provider<Context> provider) {
        return new FrameworkServicesModule_ProvidePackageManagerFactory(provider);
    }

    public static PackageManager providePackageManager(Context context) {
        return (PackageManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.providePackageManager(context));
    }
}
