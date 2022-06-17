package com.android.systemui.screenrecord;

import android.view.MotionEvent;
import android.view.View;

public final /* synthetic */ class ScreenRecordPanel$$ExternalSyntheticLambda1 implements View.OnTouchListener {
    public final /* synthetic */ ScreenRecordPanel f$0;

    public /* synthetic */ ScreenRecordPanel$$ExternalSyntheticLambda1(ScreenRecordPanel screenRecordPanel) {
        this.f$0 = screenRecordPanel;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$createRecordingBarWindow$0(view, motionEvent);
    }
}
