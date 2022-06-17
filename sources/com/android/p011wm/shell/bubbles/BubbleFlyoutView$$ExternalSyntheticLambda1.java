package com.android.p011wm.shell.bubbles;

import android.graphics.PointF;

/* renamed from: com.android.wm.shell.bubbles.BubbleFlyoutView$$ExternalSyntheticLambda1 */
public final /* synthetic */ class BubbleFlyoutView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ BubbleFlyoutView f$0;
    public final /* synthetic */ PointF f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ BubbleFlyoutView$$ExternalSyntheticLambda1(BubbleFlyoutView bubbleFlyoutView, PointF pointF, boolean z, Runnable runnable) {
        this.f$0 = bubbleFlyoutView;
        this.f$1 = pointF;
        this.f$2 = z;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$setupFlyoutStartingAsDot$3(this.f$1, this.f$2, this.f$3);
    }
}
