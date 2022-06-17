package com.android.systemui.accessibility;

import android.view.Choreographer;

public final /* synthetic */ class MagnificationModeSwitch$$ExternalSyntheticLambda0 implements Choreographer.FrameCallback {
    public final /* synthetic */ MagnificationModeSwitch f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ MagnificationModeSwitch$$ExternalSyntheticLambda0(MagnificationModeSwitch magnificationModeSwitch, float f, float f2) {
        this.f$0 = magnificationModeSwitch;
        this.f$1 = f;
        this.f$2 = f2;
    }

    public final void doFrame(long j) {
        this.f$0.lambda$moveButton$4(this.f$1, this.f$2, j);
    }
}
