package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logNonClickableNotification$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1957x823c33d2 extends Lambda implements Function1<LogMessage, String> {
    public static final C1957x823c33d2 INSTANCE = new C1957x823c33d2();

    C1957x823c33d2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("onNotificationClicked called for non-clickable notification! ", logMessage.getStr1());
    }
}
