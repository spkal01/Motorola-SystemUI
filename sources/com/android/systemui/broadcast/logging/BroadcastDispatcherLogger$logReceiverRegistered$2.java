package com.android.systemui.broadcast.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: BroadcastDispatcherLogger.kt */
final class BroadcastDispatcherLogger$logReceiverRegistered$2 extends Lambda implements Function1<LogMessage, String> {
    public static final BroadcastDispatcherLogger$logReceiverRegistered$2 INSTANCE = new BroadcastDispatcherLogger$logReceiverRegistered$2();

    BroadcastDispatcherLogger$logReceiverRegistered$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Receiver " + logMessage.getStr1() + " registered for user " + logMessage.getInt1();
    }
}
