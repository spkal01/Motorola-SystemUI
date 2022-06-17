package com.android.p011wm.shell.bubbles;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda22 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda22 implements Consumer {
    public final /* synthetic */ Executor f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda22(Executor executor, Consumer consumer) {
        this.f$0 = executor;
        this.f$1 = consumer;
    }

    public final void accept(Object obj) {
        this.f$0.execute(new BubbleController$BubblesImpl$$ExternalSyntheticLambda20(this.f$1, (String) obj));
    }
}
