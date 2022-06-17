package com.motorola.systemui.cli.navgesture.util;

import com.motorola.systemui.cli.navgesture.util.TimeoutRecorder;

public final /* synthetic */ class MotionPauseDetector$$ExternalSyntheticLambda0 implements TimeoutRecorder.TimeoutListener {
    public final /* synthetic */ MotionPauseDetector f$0;

    public /* synthetic */ MotionPauseDetector$$ExternalSyntheticLambda0(MotionPauseDetector motionPauseDetector) {
        this.f$0 = motionPauseDetector;
    }

    public final void onTimeout(TimeoutRecorder timeoutRecorder) {
        this.f$0.lambda$new$0(timeoutRecorder);
    }
}
