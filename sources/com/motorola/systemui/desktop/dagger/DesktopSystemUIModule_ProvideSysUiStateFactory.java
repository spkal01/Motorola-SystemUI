package com.motorola.systemui.desktop.dagger;

import com.android.systemui.model.SysUiState;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class DesktopSystemUIModule_ProvideSysUiStateFactory implements Factory<SysUiState> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final DesktopSystemUIModule_ProvideSysUiStateFactory INSTANCE = new DesktopSystemUIModule_ProvideSysUiStateFactory();
    }

    public SysUiState get() {
        return provideSysUiState();
    }

    public static DesktopSystemUIModule_ProvideSysUiStateFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static SysUiState provideSysUiState() {
        return (SysUiState) Preconditions.checkNotNullFromProvides(DesktopSystemUIModule.provideSysUiState());
    }
}
