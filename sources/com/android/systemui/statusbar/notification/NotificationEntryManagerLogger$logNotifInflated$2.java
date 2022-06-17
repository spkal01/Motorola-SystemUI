package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationEntryManagerLogger.kt */
final class NotificationEntryManagerLogger$logNotifInflated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationEntryManagerLogger$logNotifInflated$2 INSTANCE = new NotificationEntryManagerLogger$logNotifInflated$2();

    NotificationEntryManagerLogger$logNotifInflated$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "NOTIF INFLATED " + logMessage.getStr1() + " isNew=" + logMessage.getBool1() + '}';
    }
}
