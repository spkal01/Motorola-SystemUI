package com.android.systemui.wmshell;

import com.android.p011wm.shell.startingsurface.StartingWindowTypeAlgorithm;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class TvWMShellModule_ProvideStartingWindowTypeAlgorithmFactory implements Factory<StartingWindowTypeAlgorithm> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final TvWMShellModule_ProvideStartingWindowTypeAlgorithmFactory INSTANCE = new TvWMShellModule_ProvideStartingWindowTypeAlgorithmFactory();
    }

    public StartingWindowTypeAlgorithm get() {
        return provideStartingWindowTypeAlgorithm();
    }

    public static TvWMShellModule_ProvideStartingWindowTypeAlgorithmFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static StartingWindowTypeAlgorithm provideStartingWindowTypeAlgorithm() {
        return (StartingWindowTypeAlgorithm) Preconditions.checkNotNullFromProvides(TvWMShellModule.provideStartingWindowTypeAlgorithm());
    }
}
