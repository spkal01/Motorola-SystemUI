package com.android.systemui.statusbar.phone;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Consumer;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda43 implements Consumer {
    public static final /* synthetic */ StatusBar$$ExternalSyntheticLambda43 INSTANCE = new StatusBar$$ExternalSyntheticLambda43();

    private /* synthetic */ StatusBar$$ExternalSyntheticLambda43() {
    }

    public final void accept(Object obj) {
        ((LegacySplitScreen) obj).onAppTransitionFinished();
    }
}
