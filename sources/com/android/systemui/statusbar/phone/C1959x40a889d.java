package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logSendingIntentFailed$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1959x40a889d extends Lambda implements Function1<LogMessage, String> {
    public static final C1959x40a889d INSTANCE = new C1959x40a889d();

    C1959x40a889d() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("Sending contentIntentFailed: ", logMessage.getStr1());
    }
}
