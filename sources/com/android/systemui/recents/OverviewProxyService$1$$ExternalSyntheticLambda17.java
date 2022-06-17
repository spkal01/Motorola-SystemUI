package com.android.systemui.recents;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Function;

public final /* synthetic */ class OverviewProxyService$1$$ExternalSyntheticLambda17 implements Function {
    public static final /* synthetic */ OverviewProxyService$1$$ExternalSyntheticLambda17 INSTANCE = new OverviewProxyService$1$$ExternalSyntheticLambda17();

    private /* synthetic */ OverviewProxyService$1$$ExternalSyntheticLambda17() {
    }

    public final Object apply(Object obj) {
        return ((LegacySplitScreen) obj).getDividerView().getNonMinimizedSplitScreenSecondaryBounds();
    }
}
