package com.android.systemui.statusbar.notification;

import android.view.View;
import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

/* compiled from: ConversationNotifications.kt */
/* synthetic */ class ConversationNotificationManager$1$onNotificationRankingUpdated$1 extends FunctionReferenceImpl implements Function1<NotificationContentView, Sequence<? extends View>> {
    public static final ConversationNotificationManager$1$onNotificationRankingUpdated$1 INSTANCE = new ConversationNotificationManager$1$onNotificationRankingUpdated$1();

    ConversationNotificationManager$1$onNotificationRankingUpdated$1() {
        super(1, Intrinsics.Kotlin.class, "getLayouts", "onNotificationRankingUpdated$getLayouts(Lcom/android/systemui/statusbar/notification/row/NotificationContentView;)Lkotlin/sequences/Sequence;", 0);
    }

    @NotNull
    public final Sequence<View> invoke(@NotNull NotificationContentView notificationContentView) {
        Intrinsics.checkNotNullParameter(notificationContentView, "p0");
        return ConversationNotificationManager.C15121.onNotificationRankingUpdated$getLayouts(notificationContentView);
    }
}
