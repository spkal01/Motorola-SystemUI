package com.android.p011wm.shell.bubbles;

import com.android.p011wm.shell.bubbles.BubbleViewInfoTask;

/* renamed from: com.android.wm.shell.bubbles.BubbleViewInfoTask$$ExternalSyntheticLambda0 */
public final /* synthetic */ class BubbleViewInfoTask$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BubbleViewInfoTask f$0;
    public final /* synthetic */ BubbleViewInfoTask.BubbleViewInfo f$1;

    public /* synthetic */ BubbleViewInfoTask$$ExternalSyntheticLambda0(BubbleViewInfoTask bubbleViewInfoTask, BubbleViewInfoTask.BubbleViewInfo bubbleViewInfo) {
        this.f$0 = bubbleViewInfoTask;
        this.f$1 = bubbleViewInfo;
    }

    public final void run() {
        this.f$0.lambda$onPostExecute$0(this.f$1);
    }
}
