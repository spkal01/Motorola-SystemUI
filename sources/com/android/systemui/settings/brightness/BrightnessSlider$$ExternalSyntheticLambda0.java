package com.android.systemui.settings.brightness;

import android.view.MotionEvent;
import com.android.systemui.settings.brightness.BrightnessSliderView;

public final /* synthetic */ class BrightnessSlider$$ExternalSyntheticLambda0 implements BrightnessSliderView.DispatchTouchEventListener {
    public final /* synthetic */ BrightnessSlider f$0;

    public /* synthetic */ BrightnessSlider$$ExternalSyntheticLambda0(BrightnessSlider brightnessSlider) {
        this.f$0 = brightnessSlider;
    }

    public final boolean onDispatchTouchEvent(MotionEvent motionEvent) {
        return this.f$0.mirrorTouchEvent(motionEvent);
    }
}
