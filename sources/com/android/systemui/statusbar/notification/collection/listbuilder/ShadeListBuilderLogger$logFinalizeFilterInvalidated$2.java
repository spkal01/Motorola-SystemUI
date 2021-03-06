package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logFinalizeFilterInvalidated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logFinalizeFilterInvalidated$2 INSTANCE = new ShadeListBuilderLogger$logFinalizeFilterInvalidated$2();

    ShadeListBuilderLogger$logFinalizeFilterInvalidated$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Finalize NotifFilter \"" + logMessage.getStr1() + "\" invalidated; pipeline state is " + logMessage.getInt1();
    }
}
