package com.android.systemui.dagger;

import android.service.dreams.IDreamManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class FrameworkServicesModule_ProvideIDreamManagerFactory implements Factory<IDreamManager> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final FrameworkServicesModule_ProvideIDreamManagerFactory INSTANCE = new FrameworkServicesModule_ProvideIDreamManagerFactory();
    }

    public IDreamManager get() {
        return provideIDreamManager();
    }

    public static FrameworkServicesModule_ProvideIDreamManagerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static IDreamManager provideIDreamManager() {
        return (IDreamManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideIDreamManager());
    }
}
