package com.android.systemui.wmshell;

import com.android.p011wm.shell.onehanded.OneHanded;
import com.android.p011wm.shell.onehanded.OneHandedController;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideOneHandedFactory implements Factory<Optional<OneHanded>> {
    private final Provider<Optional<OneHandedController>> oneHandedControllerProvider;

    public WMShellBaseModule_ProvideOneHandedFactory(Provider<Optional<OneHandedController>> provider) {
        this.oneHandedControllerProvider = provider;
    }

    public Optional<OneHanded> get() {
        return provideOneHanded(this.oneHandedControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideOneHandedFactory create(Provider<Optional<OneHandedController>> provider) {
        return new WMShellBaseModule_ProvideOneHandedFactory(provider);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.util.Optional, java.util.Optional<com.android.wm.shell.onehanded.OneHandedController>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Optional<com.android.p011wm.shell.onehanded.OneHanded> provideOneHanded(java.util.Optional<com.android.p011wm.shell.onehanded.OneHandedController> r0) {
        /*
            java.util.Optional r0 = com.android.systemui.wmshell.WMShellBaseModule.provideOneHanded(r0)
            java.lang.Object r0 = dagger.internal.Preconditions.checkNotNullFromProvides(r0)
            java.util.Optional r0 = (java.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.wmshell.WMShellBaseModule_ProvideOneHandedFactory.provideOneHanded(java.util.Optional):java.util.Optional");
    }
}
