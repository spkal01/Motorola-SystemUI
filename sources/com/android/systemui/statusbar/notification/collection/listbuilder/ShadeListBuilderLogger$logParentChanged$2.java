package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logParentChanged$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logParentChanged$2 INSTANCE = new ShadeListBuilderLogger$logParentChanged$2();

    ShadeListBuilderLogger$logParentChanged$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        if (logMessage.getStr1() == null && logMessage.getStr2() != null) {
            return "(Build " + logMessage.getInt1() + ")     Parent is {" + logMessage.getStr2() + '}';
        } else if (logMessage.getStr1() == null || logMessage.getStr2() != null) {
            return "(Build " + logMessage.getInt1() + ")     Reparent: {" + logMessage.getStr1() + "} -> {" + logMessage.getStr2() + '}';
        } else {
            return "(Build " + logMessage.getInt1() + ")     Parent was {" + logMessage.getStr1() + '}';
        }
    }
}
