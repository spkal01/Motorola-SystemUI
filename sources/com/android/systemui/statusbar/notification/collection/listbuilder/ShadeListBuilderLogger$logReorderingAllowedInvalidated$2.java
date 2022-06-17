package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logReorderingAllowedInvalidated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logReorderingAllowedInvalidated$2 INSTANCE = new ShadeListBuilderLogger$logReorderingAllowedInvalidated$2();

    ShadeListBuilderLogger$logReorderingAllowedInvalidated$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "ReorderingNowAllowed \"" + logMessage.getStr1() + "\" invalidated; pipeline state is " + logMessage.getInt1();
    }
}
