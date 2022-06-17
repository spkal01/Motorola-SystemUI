package com.android.systemui.screenshot;

import android.view.InputEvent;
import com.android.systemui.shared.system.InputChannelCompat;

public final /* synthetic */ class ScreenshotView$$ExternalSyntheticLambda20 implements InputChannelCompat.InputEventListener {
    public final /* synthetic */ ScreenshotView f$0;

    public /* synthetic */ ScreenshotView$$ExternalSyntheticLambda20(ScreenshotView screenshotView) {
        this.f$0 = screenshotView;
    }

    public final void onInputEvent(InputEvent inputEvent) {
        this.f$0.lambda$startInputListening$1(inputEvent);
    }
}
