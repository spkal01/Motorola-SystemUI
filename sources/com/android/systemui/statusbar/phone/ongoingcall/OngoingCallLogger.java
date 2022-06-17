package com.android.systemui.statusbar.phone.ongoingcall;

import com.android.internal.logging.UiEventLogger;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: OngoingCallLogger.kt */
public final class OngoingCallLogger {
    private boolean chipIsVisible;
    @NotNull
    private final UiEventLogger logger;

    public OngoingCallLogger(@NotNull UiEventLogger uiEventLogger) {
        Intrinsics.checkNotNullParameter(uiEventLogger, "logger");
        this.logger = uiEventLogger;
    }

    public final void logChipClicked() {
        this.logger.log(OngoingCallEvents.ONGOING_CALL_CLICKED);
    }

    public final void logChipVisibilityChanged(boolean z) {
        if (z && z != this.chipIsVisible) {
            this.logger.log(OngoingCallEvents.ONGOING_CALL_VISIBLE);
        }
        this.chipIsVisible = z;
    }

    /* compiled from: OngoingCallLogger.kt */
    public enum OngoingCallEvents implements UiEventLogger.UiEventEnum {
        ONGOING_CALL_VISIBLE(813),
        ONGOING_CALL_CLICKED(814);
        
        private final int metricId;

        private OngoingCallEvents(int i) {
            this.metricId = i;
        }

        public int getId() {
            return this.metricId;
        }
    }
}
