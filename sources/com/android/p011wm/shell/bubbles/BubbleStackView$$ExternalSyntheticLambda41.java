package com.android.p011wm.shell.bubbles;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleStackView$$ExternalSyntheticLambda41 */
public final /* synthetic */ class BubbleStackView$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ BubbleStackView$$ExternalSyntheticLambda41(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void run() {
        this.f$0.accept(Boolean.TRUE);
    }
}
