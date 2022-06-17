package com.android.systemui.wmshell;

import com.android.p011wm.shell.pip.PipSnapAlgorithm;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class WMShellModule_ProvidePipSnapAlgorithmFactory implements Factory<PipSnapAlgorithm> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final WMShellModule_ProvidePipSnapAlgorithmFactory INSTANCE = new WMShellModule_ProvidePipSnapAlgorithmFactory();
    }

    public PipSnapAlgorithm get() {
        return providePipSnapAlgorithm();
    }

    public static WMShellModule_ProvidePipSnapAlgorithmFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static PipSnapAlgorithm providePipSnapAlgorithm() {
        return (PipSnapAlgorithm) Preconditions.checkNotNullFromProvides(WMShellModule.providePipSnapAlgorithm());
    }
}
