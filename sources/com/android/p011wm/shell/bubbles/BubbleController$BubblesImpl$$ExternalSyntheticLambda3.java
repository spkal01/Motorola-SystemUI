package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda3 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda3(BubbleController.BubblesImpl bubblesImpl, int i) {
        this.f$0 = bubblesImpl;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$onUserChanged$22(this.f$1);
    }
}
