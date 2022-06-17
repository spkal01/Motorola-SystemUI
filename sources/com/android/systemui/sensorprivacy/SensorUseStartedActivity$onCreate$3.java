package com.android.systemui.sensorprivacy;

import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;

/* compiled from: SensorUseStartedActivity.kt */
final class SensorUseStartedActivity$onCreate$3 implements IndividualSensorPrivacyController.Callback {
    final /* synthetic */ SensorUseStartedActivity this$0;

    SensorUseStartedActivity$onCreate$3(SensorUseStartedActivity sensorUseStartedActivity) {
        this.this$0 = sensorUseStartedActivity;
    }

    public final void onSensorBlockedChanged(int i, boolean z) {
        if (i == this.this$0.sensor && !z) {
            this.this$0.dismiss();
        }
    }
}
