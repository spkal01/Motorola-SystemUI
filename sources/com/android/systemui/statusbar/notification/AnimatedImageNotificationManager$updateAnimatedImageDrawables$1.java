package com.android.systemui.statusbar.notification;

import android.view.View;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationNotifications.kt */
final class AnimatedImageNotificationManager$updateAnimatedImageDrawables$1 extends Lambda implements Function1<NotificationContentView, Sequence<? extends View>> {
    public static final AnimatedImageNotificationManager$updateAnimatedImageDrawables$1 INSTANCE = new AnimatedImageNotificationManager$updateAnimatedImageDrawables$1();

    AnimatedImageNotificationManager$updateAnimatedImageDrawables$1() {
        super(1);
    }

    @NotNull
    public final Sequence<View> invoke(NotificationContentView notificationContentView) {
        View[] allViews = notificationContentView.getAllViews();
        Intrinsics.checkNotNullExpressionValue(allViews, "layout.allViews");
        return ArraysKt___ArraysKt.asSequence(allViews);
    }
}
