package com.android.systemui.log;

import android.util.Log;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LogBuffer.kt */
public final class LogBuffer {
    @NotNull
    private final ArrayDeque<LogMessageImpl> buffer = new ArrayDeque<>();
    private boolean frozen;
    @NotNull
    private final LogcatEchoTracker logcatEchoTracker;
    private final int maxLogs;
    @NotNull
    private final String name;
    private final int poolSize;

    /* compiled from: LogBuffer.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[LogLevel.values().length];
            iArr[LogLevel.VERBOSE.ordinal()] = 1;
            iArr[LogLevel.DEBUG.ordinal()] = 2;
            iArr[LogLevel.INFO.ordinal()] = 3;
            iArr[LogLevel.WARNING.ordinal()] = 4;
            iArr[LogLevel.ERROR.ordinal()] = 5;
            iArr[LogLevel.WTF.ordinal()] = 6;
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public LogBuffer(@NotNull String str, int i, int i2, @NotNull LogcatEchoTracker logcatEchoTracker2) {
        Intrinsics.checkNotNullParameter(str, "name");
        Intrinsics.checkNotNullParameter(logcatEchoTracker2, "logcatEchoTracker");
        this.name = str;
        this.maxLogs = i;
        this.poolSize = i2;
        this.logcatEchoTracker = logcatEchoTracker2;
    }

    public final boolean getFrozen() {
        return this.frozen;
    }

    @NotNull
    public final synchronized LogMessageImpl obtain(@NotNull String str, @NotNull LogLevel logLevel, @NotNull Function1<? super LogMessage, String> function1) {
        LogMessageImpl logMessageImpl;
        Intrinsics.checkNotNullParameter(str, "tag");
        Intrinsics.checkNotNullParameter(logLevel, "level");
        Intrinsics.checkNotNullParameter(function1, "printer");
        if (this.frozen) {
            logMessageImpl = LogMessageImpl.Factory.create();
        } else if (this.buffer.size() > this.maxLogs - this.poolSize) {
            logMessageImpl = this.buffer.removeFirst();
        } else {
            logMessageImpl = LogMessageImpl.Factory.create();
        }
        logMessageImpl.reset(str, logLevel, System.currentTimeMillis(), function1);
        Intrinsics.checkNotNullExpressionValue(logMessageImpl, "message");
        return logMessageImpl;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void push(@org.jetbrains.annotations.NotNull com.android.systemui.log.LogMessage r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r0 = "message"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r4, r0)     // Catch:{ all -> 0x006c }
            boolean r0 = r3.frozen     // Catch:{ all -> 0x006c }
            if (r0 == 0) goto L_0x000c
            monitor-exit(r3)
            return
        L_0x000c:
            java.util.ArrayDeque<com.android.systemui.log.LogMessageImpl> r0 = r3.buffer     // Catch:{ all -> 0x006c }
            int r0 = r0.size()     // Catch:{ all -> 0x006c }
            int r1 = r3.maxLogs     // Catch:{ all -> 0x006c }
            if (r0 != r1) goto L_0x0038
            java.lang.String r0 = "LogBuffer"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x006c }
            r1.<init>()     // Catch:{ all -> 0x006c }
            java.lang.String r2 = "LogBuffer "
            r1.append(r2)     // Catch:{ all -> 0x006c }
            java.lang.String r2 = r3.name     // Catch:{ all -> 0x006c }
            r1.append(r2)     // Catch:{ all -> 0x006c }
            java.lang.String r2 = " has exceeded its pool size"
            r1.append(r2)     // Catch:{ all -> 0x006c }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x006c }
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x006c }
            java.util.ArrayDeque<com.android.systemui.log.LogMessageImpl> r0 = r3.buffer     // Catch:{ all -> 0x006c }
            r0.removeFirst()     // Catch:{ all -> 0x006c }
        L_0x0038:
            java.util.ArrayDeque<com.android.systemui.log.LogMessageImpl> r0 = r3.buffer     // Catch:{ all -> 0x006c }
            r1 = r4
            com.android.systemui.log.LogMessageImpl r1 = (com.android.systemui.log.LogMessageImpl) r1     // Catch:{ all -> 0x006c }
            r0.add(r1)     // Catch:{ all -> 0x006c }
            com.android.systemui.log.LogcatEchoTracker r0 = r3.logcatEchoTracker     // Catch:{ all -> 0x006c }
            java.lang.String r1 = r3.name     // Catch:{ all -> 0x006c }
            r2 = r4
            com.android.systemui.log.LogMessageImpl r2 = (com.android.systemui.log.LogMessageImpl) r2     // Catch:{ all -> 0x006c }
            com.android.systemui.log.LogLevel r2 = r2.getLevel()     // Catch:{ all -> 0x006c }
            boolean r0 = r0.isBufferLoggable(r1, r2)     // Catch:{ all -> 0x006c }
            if (r0 != 0) goto L_0x0067
            com.android.systemui.log.LogcatEchoTracker r0 = r3.logcatEchoTracker     // Catch:{ all -> 0x006c }
            r1 = r4
            com.android.systemui.log.LogMessageImpl r1 = (com.android.systemui.log.LogMessageImpl) r1     // Catch:{ all -> 0x006c }
            java.lang.String r1 = r1.getTag()     // Catch:{ all -> 0x006c }
            r2 = r4
            com.android.systemui.log.LogMessageImpl r2 = (com.android.systemui.log.LogMessageImpl) r2     // Catch:{ all -> 0x006c }
            com.android.systemui.log.LogLevel r2 = r2.getLevel()     // Catch:{ all -> 0x006c }
            boolean r0 = r0.isTagLoggable(r1, r2)     // Catch:{ all -> 0x006c }
            if (r0 == 0) goto L_0x006a
        L_0x0067:
            r3.echoToLogcat(r4)     // Catch:{ all -> 0x006c }
        L_0x006a:
            monitor-exit(r3)
            return
        L_0x006c:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.log.LogBuffer.push(com.android.systemui.log.LogMessage):void");
    }

    public final synchronized void dump(@NotNull PrintWriter printWriter, int i) {
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        int i2 = 0;
        int size = i <= 0 ? 0 : this.buffer.size() - i;
        Iterator<LogMessageImpl> it = this.buffer.iterator();
        while (it.hasNext()) {
            int i3 = i2 + 1;
            LogMessageImpl next = it.next();
            if (i2 >= size) {
                Intrinsics.checkNotNullExpressionValue(next, "message");
                dumpMessage(next, printWriter);
            }
            i2 = i3;
        }
    }

    public final synchronized void freeze() {
        if (!this.frozen) {
            LogLevel logLevel = LogLevel.DEBUG;
            LogBuffer$freeze$2 logBuffer$freeze$2 = LogBuffer$freeze$2.INSTANCE;
            if (!getFrozen()) {
                LogMessageImpl obtain = obtain("LogBuffer", logLevel, logBuffer$freeze$2);
                obtain.setStr1(this.name);
                push(obtain);
            }
            this.frozen = true;
        }
    }

    public final synchronized void unfreeze() {
        if (this.frozen) {
            LogLevel logLevel = LogLevel.DEBUG;
            LogBuffer$unfreeze$2 logBuffer$unfreeze$2 = LogBuffer$unfreeze$2.INSTANCE;
            if (!getFrozen()) {
                LogMessageImpl obtain = obtain("LogBuffer", logLevel, logBuffer$unfreeze$2);
                obtain.setStr1(this.name);
                push(obtain);
            }
            this.frozen = false;
        }
    }

    private final void dumpMessage(LogMessage logMessage, PrintWriter printWriter) {
        printWriter.print(LogBufferKt.DATE_FORMAT.format(Long.valueOf(logMessage.getTimestamp())));
        printWriter.print(" ");
        printWriter.print(logMessage.getLevel().getShortString());
        printWriter.print(" ");
        printWriter.print(logMessage.getTag());
        printWriter.print(": ");
        printWriter.println(logMessage.getPrinter().invoke(logMessage));
    }

    private final void echoToLogcat(LogMessage logMessage) {
        String invoke = logMessage.getPrinter().invoke(logMessage);
        switch (WhenMappings.$EnumSwitchMapping$0[logMessage.getLevel().ordinal()]) {
            case 1:
                Log.v(logMessage.getTag(), invoke);
                return;
            case 2:
                Log.d(logMessage.getTag(), invoke);
                return;
            case 3:
                Log.i(logMessage.getTag(), invoke);
                return;
            case 4:
                Log.w(logMessage.getTag(), invoke);
                return;
            case 5:
                Log.e(logMessage.getTag(), invoke);
                return;
            case 6:
                Log.wtf(logMessage.getTag(), invoke);
                return;
            default:
                return;
        }
    }
}
