package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$4$$ExternalSyntheticLambda0 */
public final /* synthetic */ class BubbleController$4$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BubbleController.C22374 f$0;
    public final /* synthetic */ BubbleEntry f$1;
    public final /* synthetic */ Bubble f$2;

    public /* synthetic */ BubbleController$4$$ExternalSyntheticLambda0(BubbleController.C22374 r1, BubbleEntry bubbleEntry, Bubble bubble) {
        this.f$0 = r1;
        this.f$1 = bubbleEntry;
        this.f$2 = bubble;
    }

    public final void run() {
        this.f$0.lambda$applyUpdate$0(this.f$1, this.f$2);
    }
}
