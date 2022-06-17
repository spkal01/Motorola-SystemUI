package com.android.systemui.navigationbar;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Function;

public final /* synthetic */ class NavigationBar$$ExternalSyntheticLambda22 implements Function {
    public static final /* synthetic */ NavigationBar$$ExternalSyntheticLambda22 INSTANCE = new NavigationBar$$ExternalSyntheticLambda22();

    private /* synthetic */ NavigationBar$$ExternalSyntheticLambda22() {
    }

    public final Object apply(Object obj) {
        return Boolean.valueOf(((LegacySplitScreen) obj).getDividerView().getSnapAlgorithm().isSplitScreenFeasible());
    }
}
