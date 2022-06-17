package com.android.systemui.dagger;

import com.android.systemui.model.SysUiState;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class SystemUIModule_ProvideSysUiStateFactory implements Factory<SysUiState> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SystemUIModule_ProvideSysUiStateFactory INSTANCE = new SystemUIModule_ProvideSysUiStateFactory();
    }

    public SysUiState get() {
        return provideSysUiState();
    }

    public static SystemUIModule_ProvideSysUiStateFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static SysUiState provideSysUiState() {
        return (SysUiState) Preconditions.checkNotNullFromProvides(SystemUIModule.provideSysUiState());
    }
}
