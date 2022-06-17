package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logFullScreenIntentNotImportantEnough$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1953x141720c8 extends Lambda implements Function1<LogMessage, String> {
    public static final C1953x141720c8 INSTANCE = new C1953x141720c8();

    C1953x141720c8() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("No Fullscreen intent: not important enough: ", logMessage.getStr1());
    }
}
