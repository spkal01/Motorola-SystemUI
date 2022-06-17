package com.android.systemui.statusbar.notification;

import android.util.FloatProperty;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotificationWakeUpCoordinator.kt */
public final class NotificationWakeUpCoordinator$mNotificationVisibility$1 extends FloatProperty<NotificationWakeUpCoordinator> {
    NotificationWakeUpCoordinator$mNotificationVisibility$1() {
        super("notificationVisibility");
    }

    public void setValue(@NotNull NotificationWakeUpCoordinator notificationWakeUpCoordinator, float f) {
        Intrinsics.checkNotNullParameter(notificationWakeUpCoordinator, "coordinator");
        notificationWakeUpCoordinator.setVisibilityAmount(f);
    }

    @Nullable
    public Float get(@NotNull NotificationWakeUpCoordinator notificationWakeUpCoordinator) {
        Intrinsics.checkNotNullParameter(notificationWakeUpCoordinator, "coordinator");
        return Float.valueOf(notificationWakeUpCoordinator.mLinearVisibilityAmount);
    }
}
