package com.android.p011wm.shell.bubbles;

import android.view.ViewTreeObserver;

/* renamed from: com.android.wm.shell.bubbles.BubbleStackView$$ExternalSyntheticLambda10 */
public final /* synthetic */ class BubbleStackView$$ExternalSyntheticLambda10 implements ViewTreeObserver.OnDrawListener {
    public final /* synthetic */ BubbleStackView f$0;

    public /* synthetic */ BubbleStackView$$ExternalSyntheticLambda10(BubbleStackView bubbleStackView) {
        this.f$0 = bubbleStackView;
    }

    public final void onDraw() {
        this.f$0.updateSystemGestureExcludeRects();
    }
}
