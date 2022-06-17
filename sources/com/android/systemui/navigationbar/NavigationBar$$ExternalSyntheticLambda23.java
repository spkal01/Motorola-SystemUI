package com.android.systemui.navigationbar;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Function;

public final /* synthetic */ class NavigationBar$$ExternalSyntheticLambda23 implements Function {
    public static final /* synthetic */ NavigationBar$$ExternalSyntheticLambda23 INSTANCE = new NavigationBar$$ExternalSyntheticLambda23();

    private /* synthetic */ NavigationBar$$ExternalSyntheticLambda23() {
    }

    public final Object apply(Object obj) {
        return Boolean.valueOf(((LegacySplitScreen) obj).isDividerVisible());
    }
}
