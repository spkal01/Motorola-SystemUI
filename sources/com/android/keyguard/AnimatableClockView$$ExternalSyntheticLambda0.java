package com.android.keyguard;

public final /* synthetic */ class AnimatableClockView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AnimatableClockView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ Integer f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ long f$5;
    public final /* synthetic */ Runnable f$6;

    public /* synthetic */ AnimatableClockView$$ExternalSyntheticLambda0(AnimatableClockView animatableClockView, int i, float f, Integer num, long j, long j2, Runnable runnable) {
        this.f$0 = animatableClockView;
        this.f$1 = i;
        this.f$2 = f;
        this.f$3 = num;
        this.f$4 = j;
        this.f$5 = j2;
        this.f$6 = runnable;
    }

    public final void run() {
        this.f$0.lambda$setTextStyle$2(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
