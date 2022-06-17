package com.android.systemui.privacy.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyLogger.kt */
final class PrivacyLogger$logCurrentProfilesChanged$2 extends Lambda implements Function1<LogMessage, String> {
    public static final PrivacyLogger$logCurrentProfilesChanged$2 INSTANCE = new PrivacyLogger$logCurrentProfilesChanged$2();

    PrivacyLogger$logCurrentProfilesChanged$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("Profiles changed: ", logMessage.getStr1());
    }
}
