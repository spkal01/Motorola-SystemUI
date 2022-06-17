package com.motorola.systemui.desktop.dagger;

import com.android.systemui.shared.system.smartspace.SmartspaceTransitionController;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* renamed from: com.motorola.systemui.desktop.dagger.DesktopSystemUIModule_ProvideSmartspaceTransitionControllerFactory */
public final class C2760xc242d32f implements Factory<SmartspaceTransitionController> {

    /* renamed from: com.motorola.systemui.desktop.dagger.DesktopSystemUIModule_ProvideSmartspaceTransitionControllerFactory$InstanceHolder */
    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final C2760xc242d32f INSTANCE = new C2760xc242d32f();
    }

    public SmartspaceTransitionController get() {
        return provideSmartspaceTransitionController();
    }

    public static C2760xc242d32f create() {
        return InstanceHolder.INSTANCE;
    }

    public static SmartspaceTransitionController provideSmartspaceTransitionController() {
        return (SmartspaceTransitionController) Preconditions.checkNotNullFromProvides(DesktopSystemUIModule.provideSmartspaceTransitionController());
    }
}
