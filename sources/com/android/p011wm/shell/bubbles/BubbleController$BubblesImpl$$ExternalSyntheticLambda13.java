package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda13 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ BubbleEntry f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda13(BubbleController.BubblesImpl bubblesImpl, BubbleEntry bubbleEntry, boolean z) {
        this.f$0 = bubblesImpl;
        this.f$1 = bubbleEntry;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$onEntryUpdated$16(this.f$1, this.f$2);
    }
}
