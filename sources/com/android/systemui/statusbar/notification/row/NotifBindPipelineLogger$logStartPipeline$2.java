package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotifBindPipelineLogger.kt */
final class NotifBindPipelineLogger$logStartPipeline$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifBindPipelineLogger$logStartPipeline$2 INSTANCE = new NotifBindPipelineLogger$logStartPipeline$2();

    NotifBindPipelineLogger$logStartPipeline$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("Start pipeline for notif: ", logMessage.getStr1());
    }
}
