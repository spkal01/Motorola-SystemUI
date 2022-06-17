package com.android.systemui.statusbar.policy;

import com.android.systemui.Dumpable;
import com.android.systemui.demomode.DemoMode;

public interface BatteryController extends DemoMode, Dumpable, CallbackController<BatteryStateChangeCallback> {

    public interface BatteryStateChangeCallback {
        void onAdaptiveChargingChanged(boolean z) {
        }

        void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        }

        void onBatteryUnknownStateChanged(boolean z) {
        }

        void onPowerSaveChanged(boolean z) {
        }

        void onWirelessChargingChanged(boolean z) {
        }
    }

    public interface EstimateFetchCompletion {
        void onBatteryRemainingEstimateRetrieved(String str);
    }

    void getEstimatedTimeRemainingString(EstimateFetchCompletion estimateFetchCompletion) {
    }

    void init() {
    }

    boolean isAodPowerSave();

    boolean isPluggedIn();

    boolean isPluggedInWireless() {
        return false;
    }

    boolean isPowerSave();

    boolean isWirelessCharging() {
        return false;
    }

    void setPowerSaveMode(boolean z);
}
