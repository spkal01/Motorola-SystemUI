package com.android.systemui.navigationbar;

import android.view.MotionEvent;
import android.view.View;

public final /* synthetic */ class NavigationBar$$ExternalSyntheticLambda8 implements View.OnTouchListener {
    public final /* synthetic */ NavigationBar f$0;

    public /* synthetic */ NavigationBar$$ExternalSyntheticLambda8(NavigationBar navigationBar) {
        this.f$0 = navigationBar;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.onHomeTouch(view, motionEvent);
    }
}
