package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logStartNotificationIntent$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1960x1850e613 extends Lambda implements Function1<LogMessage, String> {
    public static final C1960x1850e613 INSTANCE = new C1960x1850e613();

    C1960x1850e613() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "(4/4) Starting " + logMessage.getStr2() + " for notification " + logMessage.getStr1();
    }
}
