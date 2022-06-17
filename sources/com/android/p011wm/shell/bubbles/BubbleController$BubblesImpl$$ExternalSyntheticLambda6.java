package com.android.p011wm.shell.bubbles;

import android.service.notification.NotificationListenerService;
import com.android.p011wm.shell.bubbles.BubbleController;
import java.util.HashMap;

/* renamed from: com.android.wm.shell.bubbles.BubbleController$BubblesImpl$$ExternalSyntheticLambda6 */
public final /* synthetic */ class BubbleController$BubblesImpl$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ BubbleController.BubblesImpl f$0;
    public final /* synthetic */ NotificationListenerService.RankingMap f$1;
    public final /* synthetic */ HashMap f$2;

    public /* synthetic */ BubbleController$BubblesImpl$$ExternalSyntheticLambda6(BubbleController.BubblesImpl bubblesImpl, NotificationListenerService.RankingMap rankingMap, HashMap hashMap) {
        this.f$0 = bubblesImpl;
        this.f$1 = rankingMap;
        this.f$2 = hashMap;
    }

    public final void run() {
        this.f$0.lambda$onRankingUpdated$18(this.f$1, this.f$2);
    }
}
