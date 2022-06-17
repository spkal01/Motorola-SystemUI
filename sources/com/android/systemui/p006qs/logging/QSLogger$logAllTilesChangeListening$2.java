package com.android.systemui.p006qs.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.logging.QSLogger$logAllTilesChangeListening$2 */
/* compiled from: QSLogger.kt */
final class QSLogger$logAllTilesChangeListening$2 extends Lambda implements Function1<LogMessage, String> {
    public static final QSLogger$logAllTilesChangeListening$2 INSTANCE = new QSLogger$logAllTilesChangeListening$2();

    QSLogger$logAllTilesChangeListening$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return "Tiles listening=" + logMessage.getBool1() + " in " + logMessage.getStr1() + ". " + logMessage.getStr2();
    }
}
