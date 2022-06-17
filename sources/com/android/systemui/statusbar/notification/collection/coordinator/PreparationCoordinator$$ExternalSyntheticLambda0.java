package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater;

public final /* synthetic */ class PreparationCoordinator$$ExternalSyntheticLambda0 implements NotifInflater.InflationCallback {
    public final /* synthetic */ PreparationCoordinator f$0;

    public /* synthetic */ PreparationCoordinator$$ExternalSyntheticLambda0(PreparationCoordinator preparationCoordinator) {
        this.f$0 = preparationCoordinator;
    }

    public final void onInflationFinished(NotificationEntry notificationEntry) {
        this.f$0.onInflationFinished(notificationEntry);
    }
}
