package com.android.systemui.statusbar.notification.collection.render;

import android.content.Context;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeViewManager.kt */
public final class ShadeViewManagerFactory {
    @NotNull
    private final Context context;
    @NotNull
    private final ShadeViewDifferLogger logger;
    @NotNull
    private final NotificationIconAreaController notificationIconAreaController;
    @NotNull
    private final NotifViewBarn viewBarn;

    public ShadeViewManagerFactory(@NotNull Context context2, @NotNull ShadeViewDifferLogger shadeViewDifferLogger, @NotNull NotifViewBarn notifViewBarn, @NotNull NotificationIconAreaController notificationIconAreaController2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(shadeViewDifferLogger, "logger");
        Intrinsics.checkNotNullParameter(notifViewBarn, "viewBarn");
        Intrinsics.checkNotNullParameter(notificationIconAreaController2, "notificationIconAreaController");
        this.context = context2;
        this.logger = shadeViewDifferLogger;
        this.viewBarn = notifViewBarn;
        this.notificationIconAreaController = notificationIconAreaController2;
    }

    @NotNull
    public final ShadeViewManager create(@NotNull NotificationListContainer notificationListContainer) {
        Intrinsics.checkNotNullParameter(notificationListContainer, "listContainer");
        return new ShadeViewManager(this.context, notificationListContainer, this.logger, this.viewBarn, this.notificationIconAreaController);
    }
}
