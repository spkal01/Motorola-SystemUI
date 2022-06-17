package com.android.systemui.classifier;

import android.view.MotionEvent;
import com.android.systemui.classifier.FalsingDataProvider;

public final /* synthetic */ class FalsingClassifier$$ExternalSyntheticLambda0 implements FalsingDataProvider.MotionEventListener {
    public final /* synthetic */ FalsingClassifier f$0;

    public /* synthetic */ FalsingClassifier$$ExternalSyntheticLambda0(FalsingClassifier falsingClassifier) {
        this.f$0 = falsingClassifier;
    }

    public final void onMotionEvent(MotionEvent motionEvent) {
        this.f$0.onTouchEvent(motionEvent);
    }
}
