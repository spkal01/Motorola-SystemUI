package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.NotificationLifetimeExtender;

public final /* synthetic */ class NotificationEntryManager$$ExternalSyntheticLambda0 implements NotificationLifetimeExtender.NotificationSafeToRemoveCallback {
    public final /* synthetic */ NotificationEntryManager f$0;

    public /* synthetic */ NotificationEntryManager$$ExternalSyntheticLambda0(NotificationEntryManager notificationEntryManager) {
        this.f$0 = notificationEntryManager;
    }

    public final void onSafeToRemove(String str) {
        this.f$0.lambda$addNotificationLifetimeExtender$0(str);
    }
}
