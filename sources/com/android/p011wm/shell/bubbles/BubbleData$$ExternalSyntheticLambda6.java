package com.android.p011wm.shell.bubbles;

import java.util.function.Function;

/* renamed from: com.android.wm.shell.bubbles.BubbleData$$ExternalSyntheticLambda6 */
public final /* synthetic */ class BubbleData$$ExternalSyntheticLambda6 implements Function {
    public static final /* synthetic */ BubbleData$$ExternalSyntheticLambda6 INSTANCE = new BubbleData$$ExternalSyntheticLambda6();

    private /* synthetic */ BubbleData$$ExternalSyntheticLambda6() {
    }

    public final Object apply(Object obj) {
        return Long.valueOf(BubbleData.sortKey((Bubble) obj));
    }
}
