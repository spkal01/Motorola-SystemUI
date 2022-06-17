package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleViewInfoTask;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$$ExternalSyntheticLambda2 */
public final /* synthetic */ class BubbleController$$ExternalSyntheticLambda2 implements BubbleViewInfoTask.Callback {
    public final /* synthetic */ BubbleController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ BubbleController$$ExternalSyntheticLambda2(BubbleController bubbleController, boolean z, boolean z2) {
        this.f$0 = bubbleController;
        this.f$1 = z;
        this.f$2 = z2;
    }

    public final void onBubbleViewsReady(Bubble bubble) {
        this.f$0.lambda$inflateAndAdd$10(this.f$1, this.f$2, bubble);
    }
}
