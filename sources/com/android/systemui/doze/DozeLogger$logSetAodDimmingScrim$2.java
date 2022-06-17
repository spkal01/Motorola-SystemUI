package com.android.systemui.doze;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: DozeLogger.kt */
final class DozeLogger$logSetAodDimmingScrim$2 extends Lambda implements Function1<LogMessage, String> {
    public static final DozeLogger$logSetAodDimmingScrim$2 INSTANCE = new DozeLogger$logSetAodDimmingScrim$2();

    DozeLogger$logSetAodDimmingScrim$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("Doze aod dimming scrim opacity set, opacity=", Long.valueOf(logMessage.getLong1()));
    }
}
