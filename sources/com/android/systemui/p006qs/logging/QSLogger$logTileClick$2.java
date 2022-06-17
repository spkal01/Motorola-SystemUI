package com.android.systemui.p006qs.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.logging.QSLogger$logTileClick$2 */
/* compiled from: QSLogger.kt */
final class QSLogger$logTileClick$2 extends Lambda implements Function1<LogMessage, String> {
    public static final QSLogger$logTileClick$2 INSTANCE = new QSLogger$logTileClick$2();

    QSLogger$logTileClick$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return '[' + logMessage.getStr1() + "] Tile clicked. StatusBarState=" + logMessage.getStr2() + ". TileState=" + logMessage.getStr3();
    }
}
