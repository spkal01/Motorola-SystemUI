package com.motorola.systemui.desktop.dagger;

import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.motorola.systemui.desktop.dagger.DesktopSysUIComponent;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

/* renamed from: com.motorola.systemui.desktop.dagger.DesktopSysUIComponent_DesktopModule_ProvideStatusBarKeyguardViewManagerFactory */
public final class C2759xaf8d20c6 implements Factory<StatusBarKeyguardViewManager> {

    /* renamed from: com.motorola.systemui.desktop.dagger.DesktopSysUIComponent_DesktopModule_ProvideStatusBarKeyguardViewManagerFactory$InstanceHolder */
    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final C2759xaf8d20c6 INSTANCE = new C2759xaf8d20c6();
    }

    public StatusBarKeyguardViewManager get() {
        return provideStatusBarKeyguardViewManager();
    }

    public static C2759xaf8d20c6 create() {
        return InstanceHolder.INSTANCE;
    }

    public static StatusBarKeyguardViewManager provideStatusBarKeyguardViewManager() {
        return (StatusBarKeyguardViewManager) Preconditions.checkNotNullFromProvides(DesktopSysUIComponent.DesktopModule.provideStatusBarKeyguardViewManager());
    }
}
