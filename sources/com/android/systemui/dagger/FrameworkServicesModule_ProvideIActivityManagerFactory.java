package com.android.systemui.dagger;

import android.app.IActivityManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class FrameworkServicesModule_ProvideIActivityManagerFactory implements Factory<IActivityManager> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final FrameworkServicesModule_ProvideIActivityManagerFactory INSTANCE = new FrameworkServicesModule_ProvideIActivityManagerFactory();
    }

    public IActivityManager get() {
        return provideIActivityManager();
    }

    public static FrameworkServicesModule_ProvideIActivityManagerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static IActivityManager provideIActivityManager() {
        return (IActivityManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideIActivityManager());
    }
}
