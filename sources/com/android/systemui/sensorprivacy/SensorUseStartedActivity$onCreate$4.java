package com.android.systemui.sensorprivacy;

import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;

/* compiled from: SensorUseStartedActivity.kt */
final class SensorUseStartedActivity$onCreate$4 implements IndividualSensorPrivacyController.Callback {
    final /* synthetic */ SensorUseStartedActivity this$0;

    SensorUseStartedActivity$onCreate$4(SensorUseStartedActivity sensorUseStartedActivity) {
        this.this$0 = sensorUseStartedActivity;
    }

    public final void onSensorBlockedChanged(int i, boolean z) {
        if (!z) {
            this.this$0.dismiss();
        }
    }
}
