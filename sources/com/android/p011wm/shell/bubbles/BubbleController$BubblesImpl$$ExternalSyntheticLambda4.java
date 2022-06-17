package com.android.p011wm.shell.bubbles;

import android.content.res.Configuration;
import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda4 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ Configuration f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda4(BubbleController.BubblesImpl bubblesImpl, Configuration configuration) {
        this.f$0 = bubblesImpl;
        this.f$1 = configuration;
    }

    public final void run() {
        this.f$0.lambda$onConfigChanged$24(this.f$1);
    }
}
