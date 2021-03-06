package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logSectionChanged$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logSectionChanged$2 INSTANCE = new ShadeListBuilderLogger$logSectionChanged$2();

    ShadeListBuilderLogger$logSectionChanged$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        if (logMessage.getStr1() == null) {
            return "(Build " + logMessage.getLong1() + ")     Section assigned: " + logMessage.getStr2();
        }
        return "(Build " + logMessage.getLong1() + ")     Section changed: " + logMessage.getStr1() + " -> " + logMessage.getStr2();
    }
}
