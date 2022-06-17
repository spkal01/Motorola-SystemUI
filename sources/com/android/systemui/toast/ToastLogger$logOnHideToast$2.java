package com.android.systemui.toast;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: ToastLogger.kt */
final class ToastLogger$logOnHideToast$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ToastLogger$logOnHideToast$2 INSTANCE = new ToastLogger$logOnHideToast$2();

    ToastLogger$logOnHideToast$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return '[' + logMessage.getStr2() + "] Hide toast for [" + logMessage.getStr1() + ']';
    }
}
