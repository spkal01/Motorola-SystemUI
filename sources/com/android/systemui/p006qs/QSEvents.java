package com.android.systemui.p006qs;

import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.QSEvents */
/* compiled from: QSEvents.kt */
public final class QSEvents {
    @NotNull
    public static final QSEvents INSTANCE = new QSEvents();
    @NotNull
    private static UiEventLogger qsUiEventsLogger = new UiEventLoggerImpl();

    private QSEvents() {
    }

    @NotNull
    public final UiEventLogger getQsUiEventsLogger() {
        return qsUiEventsLogger;
    }
}
