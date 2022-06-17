package com.android.systemui.p006qs.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.logging.QSLogger$logPanelExpanded$2 */
/* compiled from: QSLogger.kt */
final class QSLogger$logPanelExpanded$2 extends Lambda implements Function1<LogMessage, String> {
    public static final QSLogger$logPanelExpanded$2 INSTANCE = new QSLogger$logPanelExpanded$2();

    QSLogger$logPanelExpanded$2() {
        super(1);
    }

    @NotNull
    public final String invoke(@NotNull LogMessage logMessage) {
        Intrinsics.checkNotNullParameter(logMessage, "$this$log");
        return logMessage.getStr1() + " expanded=" + logMessage.getBool1();
    }
}
