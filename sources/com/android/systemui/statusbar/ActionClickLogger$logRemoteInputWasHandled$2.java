package com.android.systemui.statusbar;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ActionClickLogger.kt */
final class ActionClickLogger$logRemoteInputWasHandled$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ActionClickLogger$logRemoteInputWasHandled$2 INSTANCE = new ActionClickLogger$logRemoteInputWasHandled$2();

    ActionClickLogger$logRemoteInputWasHandled$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "  [Action click] Triggered remote input (for " + logMessage.getStr1() + "))";
    }
}
