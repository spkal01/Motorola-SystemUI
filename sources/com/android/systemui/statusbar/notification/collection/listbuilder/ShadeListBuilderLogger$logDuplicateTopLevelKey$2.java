package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logDuplicateTopLevelKey$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logDuplicateTopLevelKey$2 INSTANCE = new ShadeListBuilderLogger$logDuplicateTopLevelKey$2();

    ShadeListBuilderLogger$logDuplicateTopLevelKey$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "(Build " + logMessage.getInt1() + ") Duplicate top-level key: " + logMessage.getStr1();
    }
}
