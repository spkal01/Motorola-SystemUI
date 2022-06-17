package com.android.systemui.sensorprivacy;

/* compiled from: SensorUseStartedActivity.kt */
final class SensorUseStartedActivity$onStop$1 implements Runnable {
    final /* synthetic */ SensorUseStartedActivity this$0;

    SensorUseStartedActivity$onStop$1(SensorUseStartedActivity sensorUseStartedActivity) {
        this.this$0 = sensorUseStartedActivity;
    }

    public final void run() {
        this.this$0.setSuppressed(false);
    }
}
