package com.android.systemui.navigationbar;

import android.view.MotionEvent;
import android.view.View;

public final /* synthetic */ class RotationButtonController$$ExternalSyntheticLambda1 implements View.OnHoverListener {
    public final /* synthetic */ RotationButtonController f$0;

    public /* synthetic */ RotationButtonController$$ExternalSyntheticLambda1(RotationButtonController rotationButtonController) {
        this.f$0 = rotationButtonController;
    }

    public final boolean onHover(View view, MotionEvent motionEvent) {
        return this.f$0.onRotateSuggestionHover(view, motionEvent);
    }
}
