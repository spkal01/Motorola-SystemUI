package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PreparationCoordinatorLogger.kt */
public final class PreparationCoordinatorLogger {
    @NotNull
    private final LogBuffer buffer;

    public PreparationCoordinatorLogger(@NotNull LogBuffer logBuffer) {
        Intrinsics.checkNotNullParameter(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logNotifInflated(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        PreparationCoordinatorLogger$logNotifInflated$2 preparationCoordinatorLogger$logNotifInflated$2 = PreparationCoordinatorLogger$logNotifInflated$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("PreparationCoordinator", logLevel, preparationCoordinatorLogger$logNotifInflated$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logInflationAborted(@NotNull String str, @NotNull String str2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(str2, "reason");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        PreparationCoordinatorLogger$logInflationAborted$2 preparationCoordinatorLogger$logInflationAborted$2 = PreparationCoordinatorLogger$logInflationAborted$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("PreparationCoordinator", logLevel, preparationCoordinatorLogger$logInflationAborted$2);
            obtain.setStr1(str);
            obtain.setStr2(str2);
            logBuffer.push(obtain);
        }
    }

    public final void logGroupInflationTookTooLong(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "groupKey");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.WARNING;
        PreparationCoordinatorLogger$logGroupInflationTookTooLong$2 preparationCoordinatorLogger$logGroupInflationTookTooLong$2 = PreparationCoordinatorLogger$logGroupInflationTookTooLong$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("PreparationCoordinator", logLevel, preparationCoordinatorLogger$logGroupInflationTookTooLong$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logDelayingGroupRelease(@NotNull String str, @NotNull String str2) {
        Intrinsics.checkNotNullParameter(str, "groupKey");
        Intrinsics.checkNotNullParameter(str2, "childKey");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        PreparationCoordinatorLogger$logDelayingGroupRelease$2 preparationCoordinatorLogger$logDelayingGroupRelease$2 = PreparationCoordinatorLogger$logDelayingGroupRelease$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("PreparationCoordinator", logLevel, preparationCoordinatorLogger$logDelayingGroupRelease$2);
            obtain.setStr1(str);
            obtain.setStr2(str2);
            logBuffer.push(obtain);
        }
    }
}
