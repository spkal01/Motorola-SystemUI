package com.android.systemui.controls;

import com.android.internal.logging.UiEventLogger;
import com.android.systemui.controls.p004ui.ControlViewHolder;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsMetricsLogger.kt */
public interface ControlsMetricsLogger {
    void assignInstanceId();

    void drag(@NotNull ControlViewHolder controlViewHolder, boolean z);

    void log(int i, int i2, int i3, boolean z);

    void longPress(@NotNull ControlViewHolder controlViewHolder, boolean z);

    void refreshBegin(int i, boolean z);

    void refreshEnd(@NotNull ControlViewHolder controlViewHolder, boolean z);

    void touch(@NotNull ControlViewHolder controlViewHolder, boolean z);

    /* compiled from: ControlsMetricsLogger.kt */
    public static final class DefaultImpls {
        public static void touch(@NotNull ControlsMetricsLogger controlsMetricsLogger, @NotNull ControlViewHolder controlViewHolder, boolean z) {
            Intrinsics.checkNotNullParameter(controlsMetricsLogger, "this");
            Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
            controlsMetricsLogger.log(ControlsEvents.CONTROL_TOUCH.getId(), controlViewHolder.getDeviceType(), controlViewHolder.getUid(), z);
        }

        public static void drag(@NotNull ControlsMetricsLogger controlsMetricsLogger, @NotNull ControlViewHolder controlViewHolder, boolean z) {
            Intrinsics.checkNotNullParameter(controlsMetricsLogger, "this");
            Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
            controlsMetricsLogger.log(ControlsEvents.CONTROL_DRAG.getId(), controlViewHolder.getDeviceType(), controlViewHolder.getUid(), z);
        }

        public static void longPress(@NotNull ControlsMetricsLogger controlsMetricsLogger, @NotNull ControlViewHolder controlViewHolder, boolean z) {
            Intrinsics.checkNotNullParameter(controlsMetricsLogger, "this");
            Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
            controlsMetricsLogger.log(ControlsEvents.CONTROL_LONG_PRESS.getId(), controlViewHolder.getDeviceType(), controlViewHolder.getUid(), z);
        }

        public static void refreshBegin(@NotNull ControlsMetricsLogger controlsMetricsLogger, int i, boolean z) {
            Intrinsics.checkNotNullParameter(controlsMetricsLogger, "this");
            controlsMetricsLogger.assignInstanceId();
            controlsMetricsLogger.log(ControlsEvents.CONTROL_REFRESH_BEGIN.getId(), 0, i, z);
        }

        public static void refreshEnd(@NotNull ControlsMetricsLogger controlsMetricsLogger, @NotNull ControlViewHolder controlViewHolder, boolean z) {
            Intrinsics.checkNotNullParameter(controlsMetricsLogger, "this");
            Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
            controlsMetricsLogger.log(ControlsEvents.CONTROL_REFRESH_END.getId(), controlViewHolder.getDeviceType(), controlViewHolder.getUid(), z);
        }
    }

    /* compiled from: ControlsMetricsLogger.kt */
    private enum ControlsEvents implements UiEventLogger.UiEventEnum {
        CONTROL_TOUCH(714),
        CONTROL_DRAG(713),
        CONTROL_LONG_PRESS(715),
        CONTROL_REFRESH_BEGIN(716),
        CONTROL_REFRESH_END(717);
        
        private final int metricId;

        private ControlsEvents(int i) {
            this.metricId = i;
        }

        public int getId() {
            return this.metricId;
        }
    }
}
