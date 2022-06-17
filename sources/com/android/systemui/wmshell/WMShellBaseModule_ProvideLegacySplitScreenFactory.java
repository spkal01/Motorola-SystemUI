package com.android.systemui.wmshell;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideLegacySplitScreenFactory implements Factory<Optional<LegacySplitScreen>> {
    private final Provider<Optional<LegacySplitScreenController>> splitScreenControllerProvider;

    public WMShellBaseModule_ProvideLegacySplitScreenFactory(Provider<Optional<LegacySplitScreenController>> provider) {
        this.splitScreenControllerProvider = provider;
    }

    public Optional<LegacySplitScreen> get() {
        return provideLegacySplitScreen(this.splitScreenControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideLegacySplitScreenFactory create(Provider<Optional<LegacySplitScreenController>> provider) {
        return new WMShellBaseModule_ProvideLegacySplitScreenFactory(provider);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.util.Optional<com.android.wm.shell.legacysplitscreen.LegacySplitScreenController>, java.util.Optional] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Optional<com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen> provideLegacySplitScreen(java.util.Optional<com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController> r0) {
        /*
            java.util.Optional r0 = com.android.systemui.wmshell.WMShellBaseModule.provideLegacySplitScreen(r0)
            java.lang.Object r0 = dagger.internal.Preconditions.checkNotNullFromProvides(r0)
            java.util.Optional r0 = (java.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.wmshell.WMShellBaseModule_ProvideLegacySplitScreenFactory.provideLegacySplitScreen(java.util.Optional):java.util.Optional");
    }
}
