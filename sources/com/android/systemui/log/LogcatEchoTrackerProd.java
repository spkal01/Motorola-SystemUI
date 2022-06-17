package com.android.systemui.log;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LogcatEchoTrackerProd.kt */
public final class LogcatEchoTrackerProd implements LogcatEchoTracker {
    public boolean isBufferLoggable(@NotNull String str, @NotNull LogLevel logLevel) {
        Intrinsics.checkNotNullParameter(str, "bufferName");
        Intrinsics.checkNotNullParameter(logLevel, "level");
        return logLevel.compareTo(LogLevel.WARNING) >= 0;
    }

    public boolean isTagLoggable(@NotNull String str, @NotNull LogLevel logLevel) {
        Intrinsics.checkNotNullParameter(str, "tagName");
        Intrinsics.checkNotNullParameter(logLevel, "level");
        return logLevel.compareTo(LogLevel.WARNING) >= 0;
    }
}
