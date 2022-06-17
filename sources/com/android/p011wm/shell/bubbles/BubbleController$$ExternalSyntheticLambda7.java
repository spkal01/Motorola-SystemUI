package com.android.p011wm.shell.bubbles;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$$ExternalSyntheticLambda7 */
public final /* synthetic */ class BubbleController$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ BubbleController f$0;
    public final /* synthetic */ BubbleEntry f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Bubble f$3;

    public /* synthetic */ BubbleController$$ExternalSyntheticLambda7(BubbleController bubbleController, BubbleEntry bubbleEntry, boolean z, Bubble bubble) {
        this.f$0 = bubbleController;
        this.f$1 = bubbleEntry;
        this.f$2 = z;
        this.f$3 = bubble;
    }

    public final void run() {
        this.f$0.lambda$setIsBubble$11(this.f$1, this.f$2, this.f$3);
    }
}
