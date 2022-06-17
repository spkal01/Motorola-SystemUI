package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logSectionChangeSuppressed$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logSectionChangeSuppressed$2 INSTANCE = new ShadeListBuilderLogger$logSectionChangeSuppressed$2();

    ShadeListBuilderLogger$logSectionChangeSuppressed$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "(Build " + logMessage.getLong1() + ")     Suppressing section change to " + logMessage.getStr1() + " (staying at " + logMessage.getStr2() + ')';
    }
}
