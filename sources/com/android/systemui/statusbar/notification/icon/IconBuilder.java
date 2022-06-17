package com.android.systemui.statusbar.notification.icon;

import android.app.Notification;
import android.content.Context;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: IconBuilder.kt */
public final class IconBuilder {
    @NotNull
    private final Context context;

    public IconBuilder(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        this.context = context2;
    }

    @NotNull
    public final StatusBarIconView createIconView(@NotNull NotificationEntry notificationEntry) {
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        Context context2 = this.context;
        return new StatusBarIconView(context2, notificationEntry.getSbn().getPackageName() + "/0x" + Integer.toHexString(notificationEntry.getSbn().getId()), notificationEntry.getSbn());
    }

    @NotNull
    public final CharSequence getIconContentDescription(@NotNull Notification notification) {
        Intrinsics.checkNotNullParameter(notification, "n");
        String contentDescForNotification = StatusBarIconView.contentDescForNotification(this.context, notification);
        Intrinsics.checkNotNullExpressionValue(contentDescForNotification, "contentDescForNotification(context, n)");
        return contentDescForNotification;
    }
}
