package com.android.p011wm.shell.bubbles;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleStackView$$ExternalSyntheticLambda43 */
public final /* synthetic */ class BubbleStackView$$ExternalSyntheticLambda43 implements Consumer {
    public final /* synthetic */ BubbleStackView f$0;
    public final /* synthetic */ BubbleViewProvider f$1;

    public /* synthetic */ BubbleStackView$$ExternalSyntheticLambda43(BubbleStackView bubbleStackView, BubbleViewProvider bubbleViewProvider) {
        this.f$0 = bubbleStackView;
        this.f$1 = bubbleViewProvider;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$setSelectedBubble$18(this.f$1, (Boolean) obj);
    }
}
