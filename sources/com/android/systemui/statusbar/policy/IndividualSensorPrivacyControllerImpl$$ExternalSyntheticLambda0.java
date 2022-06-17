package com.android.systemui.statusbar.policy;

import android.hardware.SensorPrivacyManager;

public final /* synthetic */ class IndividualSensorPrivacyControllerImpl$$ExternalSyntheticLambda0 implements SensorPrivacyManager.OnSensorPrivacyChangedListener {
    public final /* synthetic */ IndividualSensorPrivacyControllerImpl f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ IndividualSensorPrivacyControllerImpl$$ExternalSyntheticLambda0(IndividualSensorPrivacyControllerImpl individualSensorPrivacyControllerImpl, int i) {
        this.f$0 = individualSensorPrivacyControllerImpl;
        this.f$1 = i;
    }

    public final void onSensorPrivacyChanged(int i, boolean z) {
        this.f$0.lambda$init$0(this.f$1, i, z);
    }
}
