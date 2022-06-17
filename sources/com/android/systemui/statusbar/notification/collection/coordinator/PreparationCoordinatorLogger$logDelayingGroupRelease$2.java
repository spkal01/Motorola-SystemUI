package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: PreparationCoordinatorLogger.kt */
final class PreparationCoordinatorLogger$logDelayingGroupRelease$2 extends Lambda implements Function1<LogMessage, String> {
    public static final PreparationCoordinatorLogger$logDelayingGroupRelease$2 INSTANCE = new PreparationCoordinatorLogger$logDelayingGroupRelease$2();

    PreparationCoordinatorLogger$logDelayingGroupRelease$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Delaying release of group " + logMessage.getStr1() + " because child " + logMessage.getStr2() + " is still inflating";
    }
}
