package com.android.systemui.navigationbar.buttons;

import android.view.View;
import java.util.function.Predicate;

public final /* synthetic */ class NearestTouchFrame$$ExternalSyntheticLambda2 implements Predicate {
    public final /* synthetic */ NearestTouchFrame f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ NearestTouchFrame$$ExternalSyntheticLambda2(NearestTouchFrame nearestTouchFrame, int i, int i2) {
        this.f$0 = nearestTouchFrame;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$onTouchEvent$1(this.f$1, this.f$2, (View) obj);
    }
}
