package com.android.p011wm.shell.bubbles;

import android.os.Bundle;
import com.android.p011wm.shell.bubbles.BubbleController;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda5 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ Bundle f$1;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda5(BubbleController.BubblesImpl bubblesImpl, Bundle bundle) {
        this.f$0 = bubblesImpl;
        this.f$1 = bundle;
    }

    public final void run() {
        this.f$0.lambda$onTaskbarChanged$7(this.f$1);
    }
}
