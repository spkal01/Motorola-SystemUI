package com.android.systemui.statusbar.policy;

import android.hardware.SensorPrivacyManager;
import android.util.ArraySet;
import android.util.SparseBooleanArray;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import java.util.Set;

public class IndividualSensorPrivacyControllerImpl implements IndividualSensorPrivacyController {
    private static final int[] SENSORS = {2, 1};
    private final Set<IndividualSensorPrivacyController.Callback> mCallbacks = new ArraySet();
    private final SensorPrivacyManager mSensorPrivacyManager;
    private final SparseBooleanArray mState = new SparseBooleanArray();

    public IndividualSensorPrivacyControllerImpl(SensorPrivacyManager sensorPrivacyManager) {
        this.mSensorPrivacyManager = sensorPrivacyManager;
    }

    public void init() {
        for (int i : SENSORS) {
            this.mSensorPrivacyManager.addSensorPrivacyListener(i, new IndividualSensorPrivacyControllerImpl$$ExternalSyntheticLambda0(this, i));
            this.mState.put(i, this.mSensorPrivacyManager.isSensorPrivacyEnabled(i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0(int i, int i2, boolean z) {
        onSensorPrivacyChanged(i, z);
    }

    public boolean supportsSensorToggle(int i) {
        return this.mSensorPrivacyManager.supportsSensorToggle(i);
    }

    public boolean isSensorBlocked(int i) {
        return this.mState.get(i, false);
    }

    public void setSensorBlocked(int i, int i2, boolean z) {
        this.mSensorPrivacyManager.setSensorPrivacyForProfileGroup(i, i2, z);
    }

    public void suppressSensorPrivacyReminders(int i, boolean z) {
        this.mSensorPrivacyManager.suppressSensorPrivacyReminders(i, z);
    }

    public void addCallback(IndividualSensorPrivacyController.Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void removeCallback(IndividualSensorPrivacyController.Callback callback) {
        this.mCallbacks.remove(callback);
    }

    private void onSensorPrivacyChanged(int i, boolean z) {
        this.mState.put(i, z);
        for (IndividualSensorPrivacyController.Callback onSensorBlockedChanged : this.mCallbacks) {
            onSensorBlockedChanged.onSensorBlockedChanged(i, z);
        }
    }
}
