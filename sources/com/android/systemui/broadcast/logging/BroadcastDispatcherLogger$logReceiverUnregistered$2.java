package com.android.systemui.broadcast.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: BroadcastDispatcherLogger.kt */
final class BroadcastDispatcherLogger$logReceiverUnregistered$2 extends Lambda implements Function1<LogMessage, String> {
    public static final BroadcastDispatcherLogger$logReceiverUnregistered$2 INSTANCE = new BroadcastDispatcherLogger$logReceiverUnregistered$2();

    BroadcastDispatcherLogger$logReceiverUnregistered$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Receiver " + logMessage.getStr1() + " unregistered for user " + logMessage.getInt1();
    }
}
