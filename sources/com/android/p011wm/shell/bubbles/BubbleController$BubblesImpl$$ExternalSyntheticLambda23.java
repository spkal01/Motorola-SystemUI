package com.android.p011wm.shell.bubbles;

import java.util.concurrent.Executor;
import java.util.function.IntConsumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda23 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda23 implements IntConsumer {
    public final /* synthetic */ Executor f$0;
    public final /* synthetic */ IntConsumer f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda23(Executor executor, IntConsumer intConsumer) {
        this.f$0 = executor;
        this.f$1 = intConsumer;
    }

    public final void accept(int i) {
        this.f$0.execute(new BubbleController$BubblesImpl$$ExternalSyntheticLambda21(this.f$1, i));
    }
}
