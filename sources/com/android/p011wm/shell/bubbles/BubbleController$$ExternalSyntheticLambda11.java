package com.android.p011wm.shell.bubbles;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$$ExternalSyntheticLambda11 */
public final /* synthetic */ class BubbleController$$ExternalSyntheticLambda11 implements Consumer {
    public final /* synthetic */ BubbleController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Bubble f$2;

    public /* synthetic */ BubbleController$$ExternalSyntheticLambda11(BubbleController bubbleController, boolean z, Bubble bubble) {
        this.f$0 = bubbleController;
        this.f$1 = z;
        this.f$2 = bubble;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$setIsBubble$12(this.f$1, this.f$2, (BubbleEntry) obj);
    }
}
