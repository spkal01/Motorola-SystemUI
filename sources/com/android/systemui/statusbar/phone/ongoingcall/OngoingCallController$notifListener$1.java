package com.android.systemui.statusbar.phone.ongoingcall;

import android.app.PendingIntent;
import android.content.Intent;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallController;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: OngoingCallController.kt */
public final class OngoingCallController$notifListener$1 implements NotifCollectionListener {
    final /* synthetic */ OngoingCallController this$0;

    OngoingCallController$notifListener$1(OngoingCallController ongoingCallController) {
        this.this$0 = ongoingCallController;
    }

    public void onEntryAdded(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        onEntryUpdated(notificationEntry);
    }

    public void onEntryUpdated(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Intent intent = null;
        if (this.this$0.callNotificationInfo != null || !OngoingCallControllerKt.isCallNotification(notificationEntry)) {
            String key = notificationEntry.getSbn().getKey();
            OngoingCallController.CallNotificationInfo access$getCallNotificationInfo$p = this.this$0.callNotificationInfo;
            if (!Intrinsics.areEqual((Object) key, (Object) access$getCallNotificationInfo$p == null ? null : access$getCallNotificationInfo$p.getKey())) {
                return;
            }
        }
        String key2 = notificationEntry.getSbn().getKey();
        Intrinsics.checkNotNullExpressionValue(key2, "entry.sbn.key");
        long j = notificationEntry.getSbn().getNotification().when;
        PendingIntent pendingIntent = notificationEntry.getSbn().getNotification().contentIntent;
        if (pendingIntent != null) {
            intent = pendingIntent.getIntent();
        }
        OngoingCallController.CallNotificationInfo callNotificationInfo = new OngoingCallController.CallNotificationInfo(key2, j, intent, notificationEntry.getSbn().getUid(), notificationEntry.getSbn().getNotification().extras.getInt("android.callType", -1) == 2);
        if (!Intrinsics.areEqual((Object) callNotificationInfo, (Object) this.this$0.callNotificationInfo)) {
            this.this$0.callNotificationInfo = callNotificationInfo;
            if (callNotificationInfo.isOngoing()) {
                this.this$0.updateChip();
            } else {
                this.this$0.removeChip();
            }
        }
    }

    public void onEntryRemoved(@NotNull NotificationEntry notificationEntry, int i) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        String key = notificationEntry.getSbn().getKey();
        OngoingCallController.CallNotificationInfo access$getCallNotificationInfo$p = this.this$0.callNotificationInfo;
        if (Intrinsics.areEqual((Object) key, (Object) access$getCallNotificationInfo$p == null ? null : access$getCallNotificationInfo$p.getKey())) {
            this.this$0.removeChip();
        }
    }
}
