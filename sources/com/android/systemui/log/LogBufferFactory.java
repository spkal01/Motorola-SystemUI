package com.android.systemui.log;

import com.android.systemui.dump.DumpManager;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LogBufferFactory.kt */
public final class LogBufferFactory {
    @NotNull
    private final DumpManager dumpManager;
    @NotNull
    private final LogcatEchoTracker logcatEchoTracker;

    @NotNull
    public final LogBuffer create(@NotNull String str, int i) {
        Intrinsics.checkNotNullParameter(str, "name");
        return create$default(this, str, i, 0, 4, (Object) null);
    }

    public LogBufferFactory(@NotNull DumpManager dumpManager2, @NotNull LogcatEchoTracker logcatEchoTracker2) {
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        Intrinsics.checkNotNullParameter(logcatEchoTracker2, "logcatEchoTracker");
        this.dumpManager = dumpManager2;
        this.logcatEchoTracker = logcatEchoTracker2;
    }

    public static /* synthetic */ LogBuffer create$default(LogBufferFactory logBufferFactory, String str, int i, int i2, int i3, Object obj) {
        if ((i3 & 4) != 0) {
            i2 = 10;
        }
        return logBufferFactory.create(str, i, i2);
    }

    @NotNull
    public final LogBuffer create(@NotNull String str, int i, int i2) {
        Intrinsics.checkNotNullParameter(str, "name");
        LogBuffer logBuffer = new LogBuffer(str, i, i2, this.logcatEchoTracker);
        this.dumpManager.registerBuffer(str, logBuffer);
        return logBuffer;
    }
}
