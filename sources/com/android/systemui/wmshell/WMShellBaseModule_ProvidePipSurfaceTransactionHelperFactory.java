package com.android.systemui.wmshell;

import com.android.p011wm.shell.pip.PipSurfaceTransactionHelper;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class WMShellBaseModule_ProvidePipSurfaceTransactionHelperFactory implements Factory<PipSurfaceTransactionHelper> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final WMShellBaseModule_ProvidePipSurfaceTransactionHelperFactory INSTANCE = new WMShellBaseModule_ProvidePipSurfaceTransactionHelperFactory();
    }

    public PipSurfaceTransactionHelper get() {
        return providePipSurfaceTransactionHelper();
    }

    public static WMShellBaseModule_ProvidePipSurfaceTransactionHelperFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static PipSurfaceTransactionHelper providePipSurfaceTransactionHelper() {
        return (PipSurfaceTransactionHelper) Preconditions.checkNotNullFromProvides(WMShellBaseModule.providePipSurfaceTransactionHelper());
    }
}
