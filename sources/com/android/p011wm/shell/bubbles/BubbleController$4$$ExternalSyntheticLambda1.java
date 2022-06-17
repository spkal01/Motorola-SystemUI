package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$4$$ExternalSyntheticLambda1 */
public final /* synthetic */ class BubbleController$4$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ BubbleController.C22374 f$0;
    public final /* synthetic */ Bubble f$1;

    public /* synthetic */ BubbleController$4$$ExternalSyntheticLambda1(BubbleController.C22374 r1, Bubble bubble) {
        this.f$0 = r1;
        this.f$1 = bubble;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$applyUpdate$1(this.f$1, (BubbleEntry) obj);
    }
}
