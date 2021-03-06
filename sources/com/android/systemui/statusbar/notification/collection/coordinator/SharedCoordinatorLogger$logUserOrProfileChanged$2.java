package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: SharedCoordinatorLogger.kt */
final class SharedCoordinatorLogger$logUserOrProfileChanged$2 extends Lambda implements Function1<LogMessage, String> {
    public static final SharedCoordinatorLogger$logUserOrProfileChanged$2 INSTANCE = new SharedCoordinatorLogger$logUserOrProfileChanged$2();

    SharedCoordinatorLogger$logUserOrProfileChanged$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Current user or profiles changed. Current user is " + logMessage.getInt1() + "; profiles are " + logMessage.getStr1();
    }
}
