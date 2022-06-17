package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: StatusBarNotificationActivityStarterLogger.kt */
public final class StatusBarNotificationActivityStarterLogger {
    @NotNull
    private final LogBuffer buffer;

    public StatusBarNotificationActivityStarterLogger(@NotNull LogBuffer logBuffer) {
        Intrinsics.checkNotNullParameter(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logStartingActivityFromClick(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        C1961xbe0e6f79 statusBarNotificationActivityStarterLogger$logStartingActivityFromClick$2 = C1961xbe0e6f79.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logStartingActivityFromClick$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logHandleClickAfterKeyguardDismissed(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        C1955x5700bdb1 statusBarNotificationActivityStarterLogger$logHandleClickAfterKeyguardDismissed$2 = C1955x5700bdb1.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logHandleClickAfterKeyguardDismissed$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logHandleClickAfterPanelCollapsed(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        C1956xe49d9e41 statusBarNotificationActivityStarterLogger$logHandleClickAfterPanelCollapsed$2 = C1956xe49d9e41.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logHandleClickAfterPanelCollapsed$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logStartNotificationIntent(@NotNull String str, @NotNull PendingIntent pendingIntent) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(pendingIntent, "pendingIntent");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.INFO;
        C1960x1850e613 statusBarNotificationActivityStarterLogger$logStartNotificationIntent$2 = C1960x1850e613.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logStartNotificationIntent$2);
            obtain.setStr1(str);
            obtain.setStr2(pendingIntent.getIntent().toString());
            logBuffer.push(obtain);
        }
    }

    public final void logExpandingBubble(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        StatusBarNotificationActivityStarterLogger$logExpandingBubble$2 statusBarNotificationActivityStarterLogger$logExpandingBubble$2 = StatusBarNotificationActivityStarterLogger$logExpandingBubble$2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logExpandingBubble$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logSendingIntentFailed(@NotNull Exception exc) {
        Intrinsics.checkNotNullParameter(exc, "e");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.WARNING;
        C1959x40a889d statusBarNotificationActivityStarterLogger$logSendingIntentFailed$2 = C1959x40a889d.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logSendingIntentFailed$2);
            obtain.setStr1(exc.toString());
            logBuffer.push(obtain);
        }
    }

    public final void logNonClickableNotification(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.ERROR;
        C1957x823c33d2 statusBarNotificationActivityStarterLogger$logNonClickableNotification$2 = C1957x823c33d2.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logNonClickableNotification$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logFullScreenIntentSuppressedByDnD(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        C1954x7a36b302 statusBarNotificationActivityStarterLogger$logFullScreenIntentSuppressedByDnD$2 = C1954x7a36b302.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logFullScreenIntentSuppressedByDnD$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logFullScreenIntentNotImportantEnough(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.DEBUG;
        C1953x141720c8 statusBarNotificationActivityStarterLogger$logFullScreenIntentNotImportantEnough$2 = C1953x141720c8.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logFullScreenIntentNotImportantEnough$2);
            obtain.setStr1(str);
            logBuffer.push(obtain);
        }
    }

    public final void logSendingFullScreenIntent(@NotNull String str, @NotNull PendingIntent pendingIntent) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(pendingIntent, "pendingIntent");
        LogBuffer logBuffer = this.buffer;
        LogLevel logLevel = LogLevel.INFO;
        C1958x57d5767b statusBarNotificationActivityStarterLogger$logSendingFullScreenIntent$2 = C1958x57d5767b.INSTANCE;
        if (!logBuffer.getFrozen()) {
            LogMessageImpl obtain = logBuffer.obtain("NotifActivityStarter", logLevel, statusBarNotificationActivityStarterLogger$logSendingFullScreenIntent$2);
            obtain.setStr1(str);
            obtain.setStr2(pendingIntent.getIntent().toString());
            logBuffer.push(obtain);
        }
    }
}
