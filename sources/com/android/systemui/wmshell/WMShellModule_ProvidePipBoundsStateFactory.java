package com.android.systemui.wmshell;

import android.content.Context;
import com.android.p011wm.shell.pip.PipBoundsState;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class WMShellModule_ProvidePipBoundsStateFactory implements Factory<PipBoundsState> {
    private final Provider<Context> contextProvider;

    public WMShellModule_ProvidePipBoundsStateFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public PipBoundsState get() {
        return providePipBoundsState(this.contextProvider.get());
    }

    public static WMShellModule_ProvidePipBoundsStateFactory create(Provider<Context> provider) {
        return new WMShellModule_ProvidePipBoundsStateFactory(provider);
    }

    public static PipBoundsState providePipBoundsState(Context context) {
        return (PipBoundsState) Preconditions.checkNotNullFromProvides(WMShellModule.providePipBoundsState(context));
    }
}
