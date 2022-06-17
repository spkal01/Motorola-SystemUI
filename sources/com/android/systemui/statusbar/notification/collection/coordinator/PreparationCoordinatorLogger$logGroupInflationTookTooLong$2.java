package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: PreparationCoordinatorLogger.kt */
final class PreparationCoordinatorLogger$logGroupInflationTookTooLong$2 extends Lambda implements Function1<LogMessage, String> {
    public static final PreparationCoordinatorLogger$logGroupInflationTookTooLong$2 INSTANCE = new PreparationCoordinatorLogger$logGroupInflationTookTooLong$2();

    PreparationCoordinatorLogger$logGroupInflationTookTooLong$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Group inflation took too long far " + logMessage.getStr1() + ", releasing children early";
    }
}
