package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$2$$ExternalSyntheticLambda1 */
public final /* synthetic */ class BubbleController$2$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ BubbleController.C22352 f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ BubbleController$2$$ExternalSyntheticLambda1(BubbleController.C22352 r1, int i) {
        this.f$0 = r1;
        this.f$1 = i;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onTaskMovedToFront$1(this.f$1, (Boolean) obj);
    }
}
