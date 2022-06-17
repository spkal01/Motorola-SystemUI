package com.android.systemui.wmshell;

import com.android.p011wm.shell.splitscreen.SplitScreen;
import com.android.p011wm.shell.splitscreen.SplitScreenController;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideSplitScreenFactory implements Factory<Optional<SplitScreen>> {
    private final Provider<Optional<SplitScreenController>> splitScreenControllerProvider;

    public WMShellBaseModule_ProvideSplitScreenFactory(Provider<Optional<SplitScreenController>> provider) {
        this.splitScreenControllerProvider = provider;
    }

    public Optional<SplitScreen> get() {
        return provideSplitScreen(this.splitScreenControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideSplitScreenFactory create(Provider<Optional<SplitScreenController>> provider) {
        return new WMShellBaseModule_ProvideSplitScreenFactory(provider);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.util.Optional<com.android.wm.shell.splitscreen.SplitScreenController>, java.util.Optional] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Optional<com.android.p011wm.shell.splitscreen.SplitScreen> provideSplitScreen(java.util.Optional<com.android.p011wm.shell.splitscreen.SplitScreenController> r0) {
        /*
            java.util.Optional r0 = com.android.systemui.wmshell.WMShellBaseModule.provideSplitScreen(r0)
            java.lang.Object r0 = dagger.internal.Preconditions.checkNotNullFromProvides(r0)
            java.util.Optional r0 = (java.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.wmshell.WMShellBaseModule_ProvideSplitScreenFactory.provideSplitScreen(java.util.Optional):java.util.Optional");
    }
}
