package com.android.systemui.navigationbar;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Consumer;

public final /* synthetic */ class NavigationBar$$ExternalSyntheticLambda20 implements Consumer {
    public final /* synthetic */ NavigationBarView f$0;

    public /* synthetic */ NavigationBar$$ExternalSyntheticLambda20(NavigationBarView navigationBarView) {
        this.f$0 = navigationBarView;
    }

    public final void accept(Object obj) {
        this.f$0.registerDockedListener((LegacySplitScreen) obj);
    }
}
