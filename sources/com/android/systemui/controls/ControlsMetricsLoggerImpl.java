package com.android.systemui.controls;

import com.android.internal.logging.InstanceIdSequence;
import com.android.systemui.controls.ControlsMetricsLogger;
import com.android.systemui.controls.p004ui.ControlViewHolder;
import com.android.systemui.shared.system.SysUiStatsLog;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsMetricsLoggerImpl.kt */
public final class ControlsMetricsLoggerImpl implements ControlsMetricsLogger {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private int instanceId;
    @NotNull
    private final InstanceIdSequence instanceIdSequence = new InstanceIdSequence(8192);

    public void drag(@NotNull ControlViewHolder controlViewHolder, boolean z) {
        ControlsMetricsLogger.DefaultImpls.drag(this, controlViewHolder, z);
    }

    public void longPress(@NotNull ControlViewHolder controlViewHolder, boolean z) {
        ControlsMetricsLogger.DefaultImpls.longPress(this, controlViewHolder, z);
    }

    public void refreshBegin(int i, boolean z) {
        ControlsMetricsLogger.DefaultImpls.refreshBegin(this, i, z);
    }

    public void refreshEnd(@NotNull ControlViewHolder controlViewHolder, boolean z) {
        ControlsMetricsLogger.DefaultImpls.refreshEnd(this, controlViewHolder, z);
    }

    public void touch(@NotNull ControlViewHolder controlViewHolder, boolean z) {
        ControlsMetricsLogger.DefaultImpls.touch(this, controlViewHolder, z);
    }

    /* compiled from: ControlsMetricsLoggerImpl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public void assignInstanceId() {
        this.instanceId = this.instanceIdSequence.newInstanceId().getId();
    }

    public void log(int i, int i2, int i3, boolean z) {
        SysUiStatsLog.write(349, i, this.instanceId, i2, i3, z);
    }
}
