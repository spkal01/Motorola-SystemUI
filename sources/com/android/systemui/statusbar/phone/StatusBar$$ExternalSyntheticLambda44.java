package com.android.systemui.statusbar.phone;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda44 implements Consumer {
    public static final /* synthetic */ StatusBar$$ExternalSyntheticLambda44 INSTANCE = new StatusBar$$ExternalSyntheticLambda44();

    private /* synthetic */ StatusBar$$ExternalSyntheticLambda44() {
    }

    public final void accept(Object obj) {
        ((LegacySplitScreen) obj).onAppTransitionFinished();
    }
}
