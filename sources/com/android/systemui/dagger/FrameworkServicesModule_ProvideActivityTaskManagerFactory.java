package com.android.systemui.dagger;

import android.app.ActivityTaskManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class FrameworkServicesModule_ProvideActivityTaskManagerFactory implements Factory<ActivityTaskManager> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final FrameworkServicesModule_ProvideActivityTaskManagerFactory INSTANCE = new FrameworkServicesModule_ProvideActivityTaskManagerFactory();
    }

    public ActivityTaskManager get() {
        return provideActivityTaskManager();
    }

    public static FrameworkServicesModule_ProvideActivityTaskManagerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static ActivityTaskManager provideActivityTaskManager() {
        return (ActivityTaskManager) Preconditions.checkNotNullFromProvides(FrameworkServicesModule.provideActivityTaskManager());
    }
}
