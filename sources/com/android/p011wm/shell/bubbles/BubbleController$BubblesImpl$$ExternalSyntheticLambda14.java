package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;
import com.android.p011wm.shell.bubbles.Bubbles;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda14 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ Bubbles.BubbleExpandListener f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda14(BubbleController.BubblesImpl bubblesImpl, Bubbles.BubbleExpandListener bubbleExpandListener) {
        this.f$0 = bubblesImpl;
        this.f$1 = bubbleExpandListener;
    }

    public final void run() {
        this.f$0.lambda$setExpandListener$14(this.f$1);
    }
}
