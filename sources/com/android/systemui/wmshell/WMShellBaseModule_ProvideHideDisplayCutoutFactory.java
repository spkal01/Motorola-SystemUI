package com.android.systemui.wmshell;

import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutout;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutoutController;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideHideDisplayCutoutFactory implements Factory<Optional<HideDisplayCutout>> {
    private final Provider<Optional<HideDisplayCutoutController>> hideDisplayCutoutControllerProvider;

    public WMShellBaseModule_ProvideHideDisplayCutoutFactory(Provider<Optional<HideDisplayCutoutController>> provider) {
        this.hideDisplayCutoutControllerProvider = provider;
    }

    public Optional<HideDisplayCutout> get() {
        return provideHideDisplayCutout(this.hideDisplayCutoutControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideHideDisplayCutoutFactory create(Provider<Optional<HideDisplayCutoutController>> provider) {
        return new WMShellBaseModule_ProvideHideDisplayCutoutFactory(provider);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.util.Optional, java.util.Optional<com.android.wm.shell.hidedisplaycutout.HideDisplayCutoutController>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Optional<com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutout> provideHideDisplayCutout(java.util.Optional<com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutoutController> r0) {
        /*
            java.util.Optional r0 = com.android.systemui.wmshell.WMShellBaseModule.provideHideDisplayCutout(r0)
            java.lang.Object r0 = dagger.internal.Preconditions.checkNotNullFromProvides(r0)
            java.util.Optional r0 = (java.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.wmshell.WMShellBaseModule_ProvideHideDisplayCutoutFactory.provideHideDisplayCutout(java.util.Optional):java.util.Optional");
    }
}
