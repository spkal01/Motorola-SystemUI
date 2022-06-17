package com.android.systemui.wmshell;

import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class BubblesManager$$ExternalSyntheticLambda0 implements NotificationRemoveInterceptor {
    public final /* synthetic */ BubblesManager f$0;

    public /* synthetic */ BubblesManager$$ExternalSyntheticLambda0(BubblesManager bubblesManager) {
        this.f$0 = bubblesManager;
    }

    public final boolean onNotificationRemoveRequested(String str, NotificationEntry notificationEntry, int i) {
        return this.f$0.lambda$setupNEM$1(str, notificationEntry, i);
    }
}
