package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationSectionsLogger.kt */
final class NotificationSectionsLogger$logPosition$4 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationSectionsLogger$logPosition$4 INSTANCE = new NotificationSectionsLogger$logPosition$4();

    NotificationSectionsLogger$logPosition$4() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return logMessage.getInt1() + ": " + logMessage.getStr1();
    }
}
