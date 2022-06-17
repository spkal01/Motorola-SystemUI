package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.NotificationShadeWindowViewController;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationLaunchAnimatorController.kt */
public final class NotificationLaunchAnimatorControllerProvider {
    @NotNull
    private final HeadsUpManagerPhone headsUpManager;
    @NotNull
    private final NotificationListContainer notificationListContainer;
    @NotNull
    private final NotificationShadeWindowViewController notificationShadeWindowViewController;

    public NotificationLaunchAnimatorControllerProvider(@NotNull NotificationShadeWindowViewController notificationShadeWindowViewController2, @NotNull NotificationListContainer notificationListContainer2, @NotNull HeadsUpManagerPhone headsUpManagerPhone) {
        Intrinsics.checkNotNullParameter(notificationShadeWindowViewController2, "notificationShadeWindowViewController");
        Intrinsics.checkNotNullParameter(notificationListContainer2, "notificationListContainer");
        Intrinsics.checkNotNullParameter(headsUpManagerPhone, "headsUpManager");
        this.notificationShadeWindowViewController = notificationShadeWindowViewController2;
        this.notificationListContainer = notificationListContainer2;
        this.headsUpManager = headsUpManagerPhone;
    }

    @NotNull
    public final NotificationLaunchAnimatorController getAnimatorController(@NotNull ExpandableNotificationRow expandableNotificationRow) {
        Intrinsics.checkNotNullParameter(expandableNotificationRow, "notification");
        return new NotificationLaunchAnimatorController(this.notificationShadeWindowViewController, this.notificationListContainer, this.headsUpManager, expandableNotificationRow);
    }
}
