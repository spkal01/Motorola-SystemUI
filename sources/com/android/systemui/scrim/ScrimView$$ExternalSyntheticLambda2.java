package com.android.systemui.scrim;

public final /* synthetic */ class ScrimView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ScrimView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ScrimView$$ExternalSyntheticLambda2(ScrimView scrimView, int i, boolean z) {
        this.f$0 = scrimView;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$setTint$4(this.f$1, this.f$2);
    }
}
