package com.android.systemui.dagger;

import com.android.systemui.shared.system.smartspace.SmartspaceTransitionController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class SystemUIModule_ProvideSmartspaceTransitionControllerFactory implements Factory<SmartspaceTransitionController> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SystemUIModule_ProvideSmartspaceTransitionControllerFactory INSTANCE = new SystemUIModule_ProvideSmartspaceTransitionControllerFactory();
    }

    public SmartspaceTransitionController get() {
        return provideSmartspaceTransitionController();
    }

    public static SystemUIModule_ProvideSmartspaceTransitionControllerFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static SmartspaceTransitionController provideSmartspaceTransitionController() {
        return (SmartspaceTransitionController) Preconditions.checkNotNullFromProvides(SystemUIModule.provideSmartspaceTransitionController());
    }
}
