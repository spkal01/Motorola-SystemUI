package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$2$$ExternalSyntheticLambda0 */
public final /* synthetic */ class BubbleController$2$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BubbleController.C22352 f$0;
    public final /* synthetic */ Boolean f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ BubbleController$2$$ExternalSyntheticLambda0(BubbleController.C22352 r1, Boolean bool, int i) {
        this.f$0 = r1;
        this.f$1 = bool;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$onTaskMovedToFront$0(this.f$1, this.f$2);
    }
}
