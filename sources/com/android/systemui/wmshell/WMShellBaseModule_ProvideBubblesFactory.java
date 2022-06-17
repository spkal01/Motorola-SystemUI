package com.android.systemui.wmshell;

import com.android.p011wm.shell.bubbles.BubbleController;
import com.android.p011wm.shell.bubbles.Bubbles;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class WMShellBaseModule_ProvideBubblesFactory implements Factory<Optional<Bubbles>> {
    private final Provider<Optional<BubbleController>> bubbleControllerProvider;

    public WMShellBaseModule_ProvideBubblesFactory(Provider<Optional<BubbleController>> provider) {
        this.bubbleControllerProvider = provider;
    }

    public Optional<Bubbles> get() {
        return provideBubbles(this.bubbleControllerProvider.get());
    }

    public static WMShellBaseModule_ProvideBubblesFactory create(Provider<Optional<BubbleController>> provider) {
        return new WMShellBaseModule_ProvideBubblesFactory(provider);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.util.Optional, java.util.Optional<com.android.wm.shell.bubbles.BubbleController>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Optional<com.android.p011wm.shell.bubbles.Bubbles> provideBubbles(java.util.Optional<com.android.p011wm.shell.bubbles.BubbleController> r0) {
        /*
            java.util.Optional r0 = com.android.systemui.wmshell.WMShellBaseModule.provideBubbles(r0)
            java.lang.Object r0 = dagger.internal.Preconditions.checkNotNullFromProvides(r0)
            java.util.Optional r0 = (java.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.wmshell.WMShellBaseModule_ProvideBubblesFactory.provideBubbles(java.util.Optional):java.util.Optional");
    }
}
