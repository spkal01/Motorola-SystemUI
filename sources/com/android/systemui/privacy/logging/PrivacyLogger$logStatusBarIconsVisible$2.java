package com.android.systemui.privacy.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyLogger.kt */
final class PrivacyLogger$logStatusBarIconsVisible$2 extends Lambda implements Function1<LogMessage, String> {
    public static final PrivacyLogger$logStatusBarIconsVisible$2 INSTANCE = new PrivacyLogger$logStatusBarIconsVisible$2();

    PrivacyLogger$logStatusBarIconsVisible$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Status bar icons visible: camera=" + logMessage.getBool1() + ", microphone=" + logMessage.getBool2() + ", location=" + logMessage.getBool3();
    }
}
