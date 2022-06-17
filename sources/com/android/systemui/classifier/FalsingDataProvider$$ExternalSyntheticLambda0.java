package com.android.systemui.classifier;

import android.view.MotionEvent;
import com.android.systemui.classifier.FalsingDataProvider;
import java.util.function.Consumer;

public final /* synthetic */ class FalsingDataProvider$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ MotionEvent f$0;

    public /* synthetic */ FalsingDataProvider$$ExternalSyntheticLambda0(MotionEvent motionEvent) {
        this.f$0 = motionEvent;
    }

    public final void accept(Object obj) {
        ((FalsingDataProvider.MotionEventListener) obj).onMotionEvent(this.f$0);
    }
}
