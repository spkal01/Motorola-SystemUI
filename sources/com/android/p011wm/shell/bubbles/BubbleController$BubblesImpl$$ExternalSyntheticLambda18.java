package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda18 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda18(BubbleController.BubblesImpl bubblesImpl, boolean z) {
        this.f$0 = bubblesImpl;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.lambda$onStatusBarStateChanged$21(this.f$1);
    }
}
