package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotifCollectionLogger.kt */
final class NotifCollectionLogger$logLifetimeExtensionEnded$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifCollectionLogger$logLifetimeExtensionEnded$2 INSTANCE = new NotifCollectionLogger$logLifetimeExtensionEnded$2();

    NotifCollectionLogger$logLifetimeExtensionEnded$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "LIFETIME EXTENSION ENDED for " + logMessage.getStr1() + " by '" + logMessage.getStr2() + "'; " + logMessage.getInt1() + " remaining extensions";
    }
}
