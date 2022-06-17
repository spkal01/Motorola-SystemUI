package com.android.p011wm.shell.bubbles;

import android.view.View;
import com.android.p011wm.shell.bubbles.BubbleController;
import java.util.function.BiConsumer;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda8 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ BiConsumer f$2;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda8(BubbleController.BubblesImpl bubblesImpl, View view, BiConsumer biConsumer) {
        this.f$0 = bubblesImpl;
        this.f$1 = view;
        this.f$2 = biConsumer;
    }

    public final void run() {
        this.f$0.lambda$setBubbleScrim$13(this.f$1, this.f$2);
    }
}
