package com.android.systemui.p006qs.tiles;

import android.os.Handler;
import android.os.Looper;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.DejankUtils;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.statusbar.policy.KeyguardStateController;

/* renamed from: com.android.systemui.qs.tiles.MicrophoneToggleTile */
public class MicrophoneToggleTile extends SensorPrivacyToggleTile {
    public int getIconRes(boolean z) {
        return z ? 17303642 : 17303641;
    }

    public String getRestriction() {
        return "disallow_microphone_toggle";
    }

    public int getSensorId() {
        return 1;
    }

    protected MicrophoneToggleTile(QSHost qSHost, Looper looper, Handler handler, MetricsLogger metricsLogger, FalsingManager falsingManager, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, IndividualSensorPrivacyController individualSensorPrivacyController, KeyguardStateController keyguardStateController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger, individualSensorPrivacyController, keyguardStateController);
    }

    public boolean isAvailable() {
        if (!this.mSensorPrivacyController.supportsSensorToggle(1) || !((Boolean) DejankUtils.whitelistIpcs(MicrophoneToggleTile$$ExternalSyntheticLambda0.INSTANCE)).booleanValue()) {
            return false;
        }
        return true;
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_mic_label);
    }
}
