package com.android.systemui.wmshell;

import com.android.p011wm.shell.startingsurface.StartingWindowTypeAlgorithm;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class WMShellModule_ProvideStartingWindowTypeAlgorithmFactory implements Factory<StartingWindowTypeAlgorithm> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final WMShellModule_ProvideStartingWindowTypeAlgorithmFactory INSTANCE = new WMShellModule_ProvideStartingWindowTypeAlgorithmFactory();
    }

    public StartingWindowTypeAlgorithm get() {
        return provideStartingWindowTypeAlgorithm();
    }

    public static WMShellModule_ProvideStartingWindowTypeAlgorithmFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static StartingWindowTypeAlgorithm provideStartingWindowTypeAlgorithm() {
        return (StartingWindowTypeAlgorithm) Preconditions.checkNotNullFromProvides(WMShellModule.provideStartingWindowTypeAlgorithm());
    }
}
