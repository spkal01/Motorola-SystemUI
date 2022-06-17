package com.android.p011wm.shell.bubbles;

/* renamed from: com.android.wm.shell.bubbles.BubbleStackView$$ExternalSyntheticLambda36 */
public final /* synthetic */ class BubbleStackView$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ BubbleStackView f$0;
    public final /* synthetic */ BubbleViewProvider f$1;
    public final /* synthetic */ BubbleViewProvider f$2;

    public /* synthetic */ BubbleStackView$$ExternalSyntheticLambda36(BubbleStackView bubbleStackView, BubbleViewProvider bubbleViewProvider, BubbleViewProvider bubbleViewProvider2) {
        this.f$0 = bubbleStackView;
        this.f$1 = bubbleViewProvider;
        this.f$2 = bubbleViewProvider2;
    }

    public final void run() {
        this.f$0.lambda$showNewlySelectedBubble$19(this.f$1, this.f$2);
    }
}
