package com.android.p011wm.shell.bubbles;

import android.graphics.PointF;
import com.android.p011wm.shell.bubbles.Bubble;

/* renamed from: com.android.wm.shell.bubbles.BubbleFlyoutView$$ExternalSyntheticLambda2 */
public final /* synthetic */ class BubbleFlyoutView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ BubbleFlyoutView f$0;
    public final /* synthetic */ Bubble.FlyoutMessage f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ PointF f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ BubbleFlyoutView$$ExternalSyntheticLambda2(BubbleFlyoutView bubbleFlyoutView, Bubble.FlyoutMessage flyoutMessage, float f, PointF pointF, boolean z) {
        this.f$0 = bubbleFlyoutView;
        this.f$1 = flyoutMessage;
        this.f$2 = f;
        this.f$3 = pointF;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$animateUpdate$2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
