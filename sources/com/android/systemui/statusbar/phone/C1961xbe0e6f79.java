package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logStartingActivityFromClick$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1961xbe0e6f79 extends Lambda implements Function1<LogMessage, String> {
    public static final C1961xbe0e6f79 INSTANCE = new C1961xbe0e6f79();

    C1961xbe0e6f79() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("(1/4) onNotificationClicked: ", logMessage.getStr1());
    }
}
