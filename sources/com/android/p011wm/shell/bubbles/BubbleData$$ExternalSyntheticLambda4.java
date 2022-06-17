package com.android.p011wm.shell.bubbles;

import java.util.ArrayList;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleData$$ExternalSyntheticLambda4 */
public final /* synthetic */ class BubbleData$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ BubbleData$$ExternalSyntheticLambda4(ArrayList arrayList, int i) {
        this.f$0 = arrayList;
        this.f$1 = i;
    }

    public final void accept(Object obj) {
        BubbleData.lambda$trim$5(this.f$0, this.f$1, (Bubble) obj);
    }
}
