package com.android.keyguard;

import com.android.keyguard.AnimatableClockView;

public final /* synthetic */ class AnimatableClockView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ AnimatableClockView f$0;
    public final /* synthetic */ AnimatableClockView.DozeStateGetter f$1;

    public /* synthetic */ AnimatableClockView$$ExternalSyntheticLambda1(AnimatableClockView animatableClockView, AnimatableClockView.DozeStateGetter dozeStateGetter) {
        this.f$0 = animatableClockView;
        this.f$1 = dozeStateGetter;
    }

    public final void run() {
        this.f$0.lambda$animateCharge$1(this.f$1);
    }
}
