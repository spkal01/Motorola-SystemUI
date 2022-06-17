package com.android.systemui.wmshell;

import com.android.p011wm.shell.apppairs.AppPairs;
import com.android.p011wm.shell.apppairs.AppPairsController;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideAppPairsFactory implements Factory<Optional<AppPairs>> {
    private final Provider<Optional<AppPairsController>> appPairsControllerProvider;

    public WMShellBaseModule_ProvideAppPairsFactory(Provider<Optional<AppPairsController>> provider) {
        this.appPairsControllerProvider = provider;
    }

    public Optional<AppPairs> get() {
        return provideAppPairs(this.appPairsControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideAppPairsFactory create(Provider<Optional<AppPairsController>> provider) {
        return new WMShellBaseModule_ProvideAppPairsFactory(provider);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.util.Optional<com.android.wm.shell.apppairs.AppPairsController>, java.util.Optional] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Optional<com.android.p011wm.shell.apppairs.AppPairs> provideAppPairs(java.util.Optional<com.android.p011wm.shell.apppairs.AppPairsController> r0) {
        /*
            java.util.Optional r0 = com.android.systemui.wmshell.WMShellBaseModule.provideAppPairs(r0)
            java.lang.Object r0 = dagger.internal.Preconditions.checkNotNullFromProvides(r0)
            java.util.Optional r0 = (java.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.wmshell.WMShellBaseModule_ProvideAppPairsFactory.provideAppPairs(java.util.Optional):java.util.Optional");
    }
}
