package com.android.systemui.wmshell;

import com.android.p011wm.shell.common.DisplayLayout;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class WMShellBaseModule_ProvideDisplayLayoutFactory implements Factory<DisplayLayout> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final WMShellBaseModule_ProvideDisplayLayoutFactory INSTANCE = new WMShellBaseModule_ProvideDisplayLayoutFactory();
    }

    public DisplayLayout get() {
        return provideDisplayLayout();
    }

    public static WMShellBaseModule_ProvideDisplayLayoutFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static DisplayLayout provideDisplayLayout() {
        return (DisplayLayout) Preconditions.checkNotNullFromProvides(WMShellBaseModule.provideDisplayLayout());
    }
}
