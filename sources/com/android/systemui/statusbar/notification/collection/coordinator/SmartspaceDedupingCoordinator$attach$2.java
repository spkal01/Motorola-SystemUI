package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SmartspaceDedupingCoordinator.kt */
final class SmartspaceDedupingCoordinator$attach$2 implements NotificationLockscreenUserManager.KeyguardNotificationSuppressor {
    final /* synthetic */ SmartspaceDedupingCoordinator this$0;

    SmartspaceDedupingCoordinator$attach$2(SmartspaceDedupingCoordinator smartspaceDedupingCoordinator) {
        this.this$0 = smartspaceDedupingCoordinator;
    }

    public final boolean shouldSuppressOnKeyguard(NotificationEntry notificationEntry) {
        SmartspaceDedupingCoordinator smartspaceDedupingCoordinator = this.this$0;
        Intrinsics.checkNotNullExpressionValue(notificationEntry, "entry");
        return smartspaceDedupingCoordinator.isDupedWithSmartspaceContent(notificationEntry);
    }
}
