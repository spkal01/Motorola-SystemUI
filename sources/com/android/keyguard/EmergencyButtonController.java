package com.android.keyguard;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.ViewController;
import java.util.List;

public class EmergencyButtonController extends ViewController<EmergencyButton> {
    private final ActivityTaskManager mActivityTaskManager;
    private final ConfigurationController mConfigurationController;
    private final ConfigurationController.ConfigurationListener mConfigurationListener;
    private EmergencyButtonCallback mEmergencyButtonCallback;
    private final KeyguardUpdateMonitorCallback mInfoCallback;
    /* access modifiers changed from: private */
    public boolean mIsCellAvailable;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final MetricsLogger mMetricsLogger;
    private final PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public ServiceState mServiceState;
    private ShadeController mShadeController;
    private final TelecomManager mTelecomManager;
    private final TelephonyManager mTelephonyManager;

    public interface EmergencyButtonCallback {
        void onEmergencyButtonClickedWhenInCall();
    }

    private EmergencyButtonController(EmergencyButton emergencyButton, ConfigurationController configurationController, KeyguardUpdateMonitor keyguardUpdateMonitor, TelephonyManager telephonyManager, PowerManager powerManager, ActivityTaskManager activityTaskManager, ShadeController shadeController, TelecomManager telecomManager, MetricsLogger metricsLogger) {
        super(emergencyButton);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() {
            public void onSimStateChanged(int i, int i2, int i3) {
                EmergencyButtonController.this.requestCellInfoUpdate();
                EmergencyButtonController.this.updateEmergencyCallButton();
            }

            public void onPhoneStateChanged(int i) {
                EmergencyButtonController.this.requestCellInfoUpdate();
                EmergencyButtonController.this.updateEmergencyCallButton();
            }

            public void onServiceStateChanged(int i, ServiceState serviceState) {
                ServiceState unused = EmergencyButtonController.this.mServiceState = serviceState;
                EmergencyButtonController.this.requestCellInfoUpdate();
                EmergencyButtonController.this.updateEmergencyCallButton();
            }
        };
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            public void onConfigChanged(Configuration configuration) {
                EmergencyButtonController.this.updateEmergencyCallButton();
            }
        };
        this.mConfigurationController = configurationController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mTelephonyManager = telephonyManager;
        this.mPowerManager = powerManager;
        this.mActivityTaskManager = activityTaskManager;
        this.mShadeController = shadeController;
        this.mTelecomManager = telecomManager;
        this.mMetricsLogger = metricsLogger;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        DejankUtils.whitelistIpcs((Runnable) new EmergencyButtonController$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        this.mKeyguardUpdateMonitor.registerCallback(this.mInfoCallback);
        this.mConfigurationController.addCallback(this.mConfigurationListener);
        ((EmergencyButton) this.mView).setOnClickListener(new EmergencyButtonController$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$0(View view) {
        takeEmergencyCallAction();
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mKeyguardUpdateMonitor.removeCallback(this.mInfoCallback);
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
    }

    /* access modifiers changed from: private */
    public void updateEmergencyCallButton() {
        T t = this.mView;
        if (t != null) {
            EmergencyButton emergencyButton = (EmergencyButton) t;
            TelecomManager telecomManager = this.mTelecomManager;
            boolean z = true;
            boolean z2 = telecomManager != null && telecomManager.isInCall();
            if (!this.mTelephonyManager.isVoiceCapable() || !isEmergencyCapable()) {
                z = false;
            }
            emergencyButton.updateEmergencyCallButton(z2, z, this.mKeyguardUpdateMonitor.isSimPinVoiceSecure());
        }
    }

    /* access modifiers changed from: private */
    public void requestCellInfoUpdate() {
        Context context = getContext();
        if (MotoFeature.getInstance(context).getRoCarrier().equals("cmcc")) {
            try {
                this.mTelephonyManager.createForSubscriptionId(-1).requestCellInfoUpdate(context.getMainExecutor(), new TelephonyManager.CellInfoCallback() {
                    public void onCellInfo(List<CellInfo> list) {
                        if (list == null || list.isEmpty()) {
                            Log.d("EmergencyButton", "requestCellInfoUpdate.onCellInfo is null or empty");
                            boolean unused = EmergencyButtonController.this.mIsCellAvailable = false;
                        } else {
                            boolean unused2 = EmergencyButtonController.this.mIsCellAvailable = true;
                        }
                        EmergencyButtonController.this.updateEmergencyCallButton();
                    }
                });
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmergencyCapable() {
        boolean z = true;
        if (!MotoFeature.getInstance(getContext()).getRoCarrier().equals("cmcc")) {
            return true;
        }
        boolean isOOS = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isOOS();
        ServiceState serviceState = this.mServiceState;
        boolean z2 = serviceState != null && serviceState.isEmergencyOnly();
        if (isOOS && !this.mIsCellAvailable && !z2) {
            z = false;
        }
        Log.d("EmergencyButton", "isEmergencyCapable = " + z + " isOOS = " + isOOS + " isEmergencyOnly = " + z2 + " mIsCellAvailable = " + this.mIsCellAvailable);
        return z;
    }

    public void setEmergencyButtonCallback(EmergencyButtonCallback emergencyButtonCallback) {
        this.mEmergencyButtonCallback = emergencyButtonCallback;
    }

    public void takeEmergencyCallAction() {
        this.mMetricsLogger.action(200);
        PowerManager powerManager = this.mPowerManager;
        if (powerManager != null) {
            powerManager.userActivity(SystemClock.uptimeMillis(), true);
        }
        this.mActivityTaskManager.stopSystemLockTaskMode();
        this.mShadeController.collapsePanel(false);
        TelecomManager telecomManager = this.mTelecomManager;
        if (telecomManager != null && telecomManager.isInCall()) {
            this.mTelecomManager.showInCallScreen(false);
            EmergencyButtonCallback emergencyButtonCallback = this.mEmergencyButtonCallback;
            if (emergencyButtonCallback != null) {
                emergencyButtonCallback.onEmergencyButtonClickedWhenInCall();
            }
        } else if (!MotoFeature.getInstance(getContext()).isSupportCli() || !MotoFeature.isCliContext(getContext())) {
            this.mKeyguardUpdateMonitor.reportEmergencyCallAction(true);
            TelecomManager telecomManager2 = this.mTelecomManager;
            if (telecomManager2 == null) {
                Log.wtf("EmergencyButton", "TelecomManager was null, cannot launch emergency dialer");
                return;
            }
            getContext().startActivityAsUser(telecomManager2.createLaunchEmergencyDialerIntent((String) null).setFlags(343932928).putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 1), ActivityOptions.makeCustomAnimation(getContext(), 0, 0).toBundle(), new UserHandle(KeyguardUpdateMonitor.getCurrentUser()));
        } else {
            TelecomManager telecomManager3 = this.mTelecomManager;
            if (telecomManager3 == null) {
                Log.wtf("EmergencyButton", "TelecomManager was null, cannot launch emergency dialer");
                return;
            }
            Intent putExtra = telecomManager3.createLaunchEmergencyDialerIntent((String) null).setFlags(343932928).putExtra("com.android.phone.EmergencyDialer.extra.ENTRY_TYPE", 1);
            ActivityOptions makeCustomAnimation = ActivityOptions.makeCustomAnimation(getContext(), 0, 0);
            makeCustomAnimation.setLaunchDisplayId(1);
            getContext().startActivityAsUser(putExtra, makeCustomAnimation.toBundle(), new UserHandle(KeyguardUpdateMonitor.getCurrentUser()));
            getContext().sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            this.mKeyguardUpdateMonitor.reportEmergencyCallAction(true);
        }
    }

    public static class Factory {
        private final ActivityTaskManager mActivityTaskManager;
        private final ConfigurationController mConfigurationController;
        private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
        private final MetricsLogger mMetricsLogger;
        private final PowerManager mPowerManager;
        private ShadeController mShadeController;
        private final TelecomManager mTelecomManager;
        private final TelephonyManager mTelephonyManager;

        public Factory(ConfigurationController configurationController, KeyguardUpdateMonitor keyguardUpdateMonitor, TelephonyManager telephonyManager, PowerManager powerManager, ActivityTaskManager activityTaskManager, ShadeController shadeController, TelecomManager telecomManager, MetricsLogger metricsLogger) {
            this.mConfigurationController = configurationController;
            this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
            this.mTelephonyManager = telephonyManager;
            this.mPowerManager = powerManager;
            this.mActivityTaskManager = activityTaskManager;
            this.mShadeController = shadeController;
            this.mTelecomManager = telecomManager;
            this.mMetricsLogger = metricsLogger;
        }

        public EmergencyButtonController create(EmergencyButton emergencyButton) {
            return new EmergencyButtonController(emergencyButton, this.mConfigurationController, this.mKeyguardUpdateMonitor, this.mTelephonyManager, this.mPowerManager, this.mActivityTaskManager, this.mShadeController, this.mTelecomManager, this.mMetricsLogger);
        }
    }
}
