package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda12 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ BubbleEntry f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda12(BubbleController.BubblesImpl bubblesImpl, BubbleEntry bubbleEntry) {
        this.f$0 = bubblesImpl;
        this.f$1 = bubbleEntry;
    }

    public final void run() {
        this.f$0.lambda$expandStackAndSelectBubble$5(this.f$1);
    }
}
