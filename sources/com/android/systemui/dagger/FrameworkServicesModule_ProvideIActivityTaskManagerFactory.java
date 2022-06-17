package com.android.systemui.dagger;

import android.app.IActivityTaskManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class FrameworkServicesModule_ProvideIActivityTaskManagerFactory implements Factory<IActivityTaskManager> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final FrameworkServicesModule_ProvideIActivityTaskManagerFactory INSTANCE = new FrameworkServicesModule_ProvideIActivityTaskManagerFactory();
    }

    public IActivityTaskManager get() {
        return provideIActivityTaskManager();
    }

    public static FrameworkServicesModule_ProvideIActivityTaskManagerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static IActivityTaskManager provideIActivityTaskManager() {
        return (IActivityTaskManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideIActivityTaskManager());
    }
}
