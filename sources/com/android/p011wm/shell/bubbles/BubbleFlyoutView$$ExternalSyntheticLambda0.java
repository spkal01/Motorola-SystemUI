package com.android.p011wm.shell.bubbles;

import android.graphics.PointF;

/* renamed from: com.android.wm.shell.bubbles.BubbleFlyoutView$$ExternalSyntheticLambda0 */
public final /* synthetic */ class BubbleFlyoutView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BubbleFlyoutView f$0;
    public final /* synthetic */ PointF f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ BubbleFlyoutView$$ExternalSyntheticLambda0(BubbleFlyoutView bubbleFlyoutView, PointF pointF, boolean z) {
        this.f$0 = bubbleFlyoutView;
        this.f$1 = pointF;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$animateUpdate$1(this.f$1, this.f$2);
    }
}
