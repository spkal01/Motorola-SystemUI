package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logHandleClickAfterKeyguardDismissed$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1955x5700bdb1 extends Lambda implements Function1<LogMessage, String> {
    public static final C1955x5700bdb1 INSTANCE = new C1955x5700bdb1();

    C1955x5700bdb1() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("(2/4) handleNotificationClickAfterKeyguardDismissed: ", logMessage.getStr1());
    }
}
