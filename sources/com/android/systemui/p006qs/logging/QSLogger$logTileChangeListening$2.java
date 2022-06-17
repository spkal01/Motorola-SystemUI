package com.android.systemui.p006qs.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.logging.QSLogger$logTileChangeListening$2 */
/* compiled from: QSLogger.kt */
final class QSLogger$logTileChangeListening$2 extends Lambda implements Function1<LogMessage, String> {
    public static final QSLogger$logTileChangeListening$2 INSTANCE = new QSLogger$logTileChangeListening$2();

    QSLogger$logTileChangeListening$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return '[' + logMessage.getStr1() + "] Tile listening=" + logMessage.getBool1();
    }
}
