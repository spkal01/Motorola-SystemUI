package com.android.systemui.dagger;

import android.content.pm.IPackageManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class FrameworkServicesModule_ProvideIPackageManagerFactory implements Factory<IPackageManager> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final FrameworkServicesModule_ProvideIPackageManagerFactory INSTANCE = new FrameworkServicesModule_ProvideIPackageManagerFactory();
    }

    public IPackageManager get() {
        return provideIPackageManager();
    }

    public static FrameworkServicesModule_ProvideIPackageManagerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static IPackageManager provideIPackageManager() {
        return (IPackageManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideIPackageManager());
    }
}
