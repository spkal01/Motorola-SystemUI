package com.android.systemui.statusbar.phone;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger$logHandleClickAfterPanelCollapsed$2 */
/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
final class C1956xe49d9e41 extends Lambda implements Function1<LogMessage, String> {
    public static final C1956xe49d9e41 INSTANCE = new C1956xe49d9e41();

    C1956xe49d9e41() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("(3/4) handleNotificationClickAfterPanelCollapsed: ", logMessage.getStr1());
    }
}
