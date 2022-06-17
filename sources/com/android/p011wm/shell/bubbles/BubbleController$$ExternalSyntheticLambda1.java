package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleViewInfoTask;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$$ExternalSyntheticLambda1 */
public final /* synthetic */ class BubbleController$$ExternalSyntheticLambda1 implements BubbleViewInfoTask.Callback {
    public final /* synthetic */ BubbleController f$0;
    public final /* synthetic */ Bubble f$1;

    public /* synthetic */ BubbleController$$ExternalSyntheticLambda1(BubbleController bubbleController, Bubble bubble) {
        this.f$0 = bubbleController;
        this.f$1 = bubble;
    }

    public final void onBubbleViewsReady(Bubble bubble) {
        this.f$0.lambda$loadOverflowBubblesFromDisk$7(this.f$1, bubble);
    }
}
