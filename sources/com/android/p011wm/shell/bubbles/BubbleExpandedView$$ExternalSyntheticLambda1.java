package com.android.p011wm.shell.bubbles;

/* renamed from: com.android.wm.shell.bubbles.BubbleExpandedView$$ExternalSyntheticLambda1 */
public final /* synthetic */ class BubbleExpandedView$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ BubbleExpandedView f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ BubbleExpandedView$$ExternalSyntheticLambda1(BubbleExpandedView bubbleExpandedView, boolean z, float f, boolean z2) {
        this.f$0 = bubbleExpandedView;
        this.f$1 = z;
        this.f$2 = f;
        this.f$3 = z2;
    }

    public final void run() {
        this.f$0.lambda$setPointerPosition$1(this.f$1, this.f$2, this.f$3);
    }
}
