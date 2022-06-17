package com.android.systemui.privacy.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyLogger.kt */
final class PrivacyLogger$logUpdatedItemFromAppOps$2 extends Lambda implements Function1<LogMessage, String> {
    public static final PrivacyLogger$logUpdatedItemFromAppOps$2 INSTANCE = new PrivacyLogger$logUpdatedItemFromAppOps$2();

    PrivacyLogger$logUpdatedItemFromAppOps$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "App Op: " + logMessage.getInt1() + " for " + logMessage.getStr1() + '(' + logMessage.getInt2() + "), active=" + logMessage.getBool1();
    }
}
