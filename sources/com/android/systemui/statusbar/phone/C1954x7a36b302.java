package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logFullScreenIntentSuppressedByDnD$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1954x7a36b302 extends Lambda implements Function1<LogMessage, String> {
    public static final C1954x7a36b302 INSTANCE = new C1954x7a36b302();

    C1954x7a36b302() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("No Fullscreen intent: suppressed by DND: ", logMessage.getStr1());
    }
}
