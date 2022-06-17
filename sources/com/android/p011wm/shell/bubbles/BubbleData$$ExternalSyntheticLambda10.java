package com.android.p011wm.shell.bubbles;

import java.util.function.ToLongFunction;

/* renamed from: com.android.wm.shell.bubbles.BubbleData$$ExternalSyntheticLambda10 */
public final /* synthetic */ class BubbleData$$ExternalSyntheticLambda10 implements ToLongFunction {
    public static final /* synthetic */ BubbleData$$ExternalSyntheticLambda10 INSTANCE = new BubbleData$$ExternalSyntheticLambda10();

    private /* synthetic */ BubbleData$$ExternalSyntheticLambda10() {
    }

    public final long applyAsLong(Object obj) {
        return ((Bubble) obj).getLastActivity();
    }
}
