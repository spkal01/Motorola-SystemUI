package com.android.systemui.navigationbar.buttons;

import android.view.View;
import java.util.function.Predicate;

public final /* synthetic */ class NearestTouchFrame$$ExternalSyntheticLambda3 implements Predicate {
    public static final /* synthetic */ NearestTouchFrame$$ExternalSyntheticLambda3 INSTANCE = new NearestTouchFrame$$ExternalSyntheticLambda3();

    private /* synthetic */ NearestTouchFrame$$ExternalSyntheticLambda3() {
    }

    public final boolean test(Object obj) {
        return ((View) obj).isAttachedToWindow();
    }
}
