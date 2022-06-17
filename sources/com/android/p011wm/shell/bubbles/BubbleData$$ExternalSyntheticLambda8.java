package com.android.p011wm.shell.bubbles;

import java.util.function.Predicate;

/* renamed from: com.android.wm.shell.bubbles.BubbleData$$ExternalSyntheticLambda8 */
public final /* synthetic */ class BubbleData$$ExternalSyntheticLambda8 implements Predicate {
    public final /* synthetic */ String f$0;

    public /* synthetic */ BubbleData$$ExternalSyntheticLambda8(String str) {
        this.f$0 = str;
    }

    public final boolean test(Object obj) {
        return ((Bubble) obj).getPackageName().equals(this.f$0);
    }
}
