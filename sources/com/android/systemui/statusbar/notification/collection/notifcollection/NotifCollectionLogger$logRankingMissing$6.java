package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotifCollectionLogger.kt */
final class NotifCollectionLogger$logRankingMissing$6 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifCollectionLogger$logRankingMissing$6 INSTANCE = new NotifCollectionLogger$logRankingMissing$6();

    NotifCollectionLogger$logRankingMissing$6() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return Intrinsics.stringPlus("  ", logMessage.getStr1());
    }
}
