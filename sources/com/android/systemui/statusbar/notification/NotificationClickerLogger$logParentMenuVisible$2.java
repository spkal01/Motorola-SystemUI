package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationClickerLogger.kt */
final class NotificationClickerLogger$logParentMenuVisible$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationClickerLogger$logParentMenuVisible$2 INSTANCE = new NotificationClickerLogger$logParentMenuVisible$2();

    NotificationClickerLogger$logParentMenuVisible$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Ignoring click on " + logMessage.getStr1() + "; parent menu is visible";
    }
}
