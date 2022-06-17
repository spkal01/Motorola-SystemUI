package com.android.systemui.scrim;

public final /* synthetic */ class ScrimView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ScrimView f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ ScrimView$$ExternalSyntheticLambda1(ScrimView scrimView, float f) {
        this.f$0 = scrimView;
        this.f$1 = f;
    }

    public final void run() {
        this.f$0.lambda$setViewAlpha$5(this.f$1);
    }
}
