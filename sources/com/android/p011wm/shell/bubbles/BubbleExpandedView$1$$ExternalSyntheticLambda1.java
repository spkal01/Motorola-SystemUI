package com.android.p011wm.shell.bubbles;

import android.app.ActivityOptions;
import android.graphics.Rect;
import com.android.p011wm.shell.bubbles.BubbleExpandedView;

/* renamed from: com.android.wm.shell.bubbles.BubbleExpandedView$1$$ExternalSyntheticLambda1 */
public final /* synthetic */ class BubbleExpandedView$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ BubbleExpandedView.C22401 f$0;
    public final /* synthetic */ ActivityOptions f$1;
    public final /* synthetic */ Rect f$2;

    public /* synthetic */ BubbleExpandedView$1$$ExternalSyntheticLambda1(BubbleExpandedView.C22401 r1, ActivityOptions activityOptions, Rect rect) {
        this.f$0 = r1;
        this.f$1 = activityOptions;
        this.f$2 = rect;
    }

    public final void run() {
        this.f$0.lambda$onInitialized$0(this.f$1, this.f$2);
    }
}
