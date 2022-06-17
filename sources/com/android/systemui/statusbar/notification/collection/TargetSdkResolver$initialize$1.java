package com.android.systemui.statusbar.notification.collection;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: TargetSdkResolver.kt */
public final class TargetSdkResolver$initialize$1 implements NotifCollectionListener {
    final /* synthetic */ TargetSdkResolver this$0;

    TargetSdkResolver$initialize$1(TargetSdkResolver targetSdkResolver) {
        this.this$0 = targetSdkResolver;
    }

    public void onEntryBind(@NotNull NotificationEntry notificationEntry, @NotNull StatusBarNotification statusBarNotification) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Intrinsics.checkNotNullParameter(statusBarNotification, "sbn");
        notificationEntry.targetSdk = this.this$0.resolveNotificationSdk(statusBarNotification);
    }
}
