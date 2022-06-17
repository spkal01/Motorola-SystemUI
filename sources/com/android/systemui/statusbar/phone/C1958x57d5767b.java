package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logSendingFullScreenIntent$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1958x57d5767b extends Lambda implements Function1<LogMessage, String> {
    public static final C1958x57d5767b INSTANCE = new C1958x57d5767b();

    C1958x57d5767b() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Notification " + logMessage.getStr1() + " has fullScreenIntent; sending fullScreenIntent " + logMessage.getStr2();
    }
}
