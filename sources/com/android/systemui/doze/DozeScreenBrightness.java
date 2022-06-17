package com.android.systemui.doze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.Trace;
import android.provider.Settings;
import com.android.systemui.Dependency;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.sensors.AsyncSensorManager;
import java.io.PrintWriter;
import java.util.Optional;

public class DozeScreenBrightness extends BroadcastReceiver implements DozeMachine.Part, SensorEventListener {
    private static final boolean DEBUG_AOD_BRIGHTNESS = SystemProperties.getBoolean("debug.aod_brightness", false);
    private final Context mContext;
    private int mDebugBrightnessBucket = -1;
    private int mDefaultDozeBrightness;
    private final DozeHost mDozeHost;
    private final DozeParameters mDozeParameters;
    private final DozeMachine.Service mDozeService;
    private final Handler mHandler;
    private int mLastSensorValue = -1;
    private final Optional<Sensor> mLightSensorOptional;
    private MotoDisplayManager mMotoDisplayManager;
    private boolean mPaused = false;
    private boolean mRegistered;
    private final int mScreenBrightnessDim;
    private boolean mScreenOff = false;
    private final SensorManager mSensorManager;
    private final int[] mSensorToBrightness;
    private final int[] mSensorToScrimOpacity;
    private final WakefulnessLifecycle mWakefulnessLifecycle;

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public DozeScreenBrightness(Context context, DozeMachine.Service service, AsyncSensorManager asyncSensorManager, Optional<Sensor> optional, DozeHost dozeHost, Handler handler, AlwaysOnDisplayPolicy alwaysOnDisplayPolicy, WakefulnessLifecycle wakefulnessLifecycle, DozeParameters dozeParameters) {
        this.mContext = context;
        this.mDozeService = service;
        this.mSensorManager = asyncSensorManager;
        this.mLightSensorOptional = optional;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mDozeParameters = dozeParameters;
        this.mDozeHost = dozeHost;
        this.mHandler = handler;
        this.mDefaultDozeBrightness = alwaysOnDisplayPolicy.defaultDozeBrightness;
        this.mScreenBrightnessDim = alwaysOnDisplayPolicy.dimBrightness;
        this.mSensorToBrightness = alwaysOnDisplayPolicy.screenBrightnessArray;
        this.mSensorToScrimOpacity = alwaysOnDisplayPolicy.dimmingScrimArray;
        initMotoDisplayManagerIfNeed();
    }

    /* renamed from: com.android.systemui.doze.DozeScreenBrightness$1 */
    static /* synthetic */ class C09201 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.android.systemui.doze.DozeMachine$State[] r0 = com.android.systemui.doze.DozeMachine.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$systemui$doze$DozeMachine$State = r0
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.INITIALIZED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.DOZE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$systemui$doze$DozeMachine$State     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.doze.DozeMachine$State r1 = com.android.systemui.doze.DozeMachine.State.FINISH     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeScreenBrightness.C09201.<clinit>():void");
        }
    }

    public void transitionTo(DozeMachine.State state, DozeMachine.State state2) {
        int i = C09201.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state2.ordinal()];
        boolean z = true;
        if (i == 1 || i == 2) {
            resetBrightnessToDefault();
        } else if (i == 3) {
            onDestroy();
        }
        if (state2 != DozeMachine.State.FINISH) {
            setScreenOff(state2 == DozeMachine.State.DOZE);
            if (state2 != DozeMachine.State.DOZE_AOD_PAUSED) {
                z = false;
            }
            setPaused(z);
        }
    }

    public void onScreenState(int i) {
        if (i == 3 || i == 4) {
            setLightSensorEnabled(true);
        } else {
            setLightSensorEnabled(false);
        }
    }

    private void onDestroy() {
        setLightSensorEnabled(false);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        Trace.beginSection("DozeScreenBrightness.onSensorChanged" + sensorEvent.values[0]);
        try {
            if (this.mRegistered) {
                this.mLastSensorValue = (int) sensorEvent.values[0];
                updateBrightnessAndReady(false);
            }
        } finally {
            Trace.endSection();
        }
    }

    public void updateBrightnessAndReady(boolean z) {
        int i = -1;
        if (z || this.mRegistered || this.mDebugBrightnessBucket != -1 || !MotoDisplayManager.isAospAD()) {
            int i2 = this.mDebugBrightnessBucket;
            if (i2 == -1) {
                i2 = this.mLastSensorValue;
            }
            initMotoDisplayManagerIfNeed();
            int computeBrightness = (MotoDisplayManager.isAospAD() || this.mMotoDisplayManager == null || MotoFeature.isLidClosed(this.mContext)) ? computeBrightness(i2) : this.mMotoDisplayManager.getLastScreenBrightness();
            if (MotoFeature.isLidClosed(this.mContext) && computeBrightness < 0) {
                computeBrightness = this.mContext.getResources().getInteger(17694934);
            }
            boolean z2 = computeBrightness > 0;
            if (z2) {
                if (MotoDisplayManager.isAospAD() || MotoFeature.isLidClosed(this.mContext)) {
                    this.mDozeService.setDozeScreenBrightness(clampToUserSetting(computeBrightness));
                } else {
                    this.mDozeService.setDozeScreenBrightness(computeBrightness);
                }
            }
            if (!this.mLightSensorOptional.isPresent()) {
                i = 0;
            } else if (z2) {
                i = computeScrimOpacity(i2);
            }
            if (i >= 0) {
                this.mDozeHost.setAodDimmingScrim(((float) i) / 255.0f);
            }
        }
    }

    private int computeScrimOpacity(int i) {
        if (i < 0) {
            return -1;
        }
        int[] iArr = this.mSensorToScrimOpacity;
        if (i >= iArr.length) {
            return -1;
        }
        return iArr[i];
    }

    private int computeBrightness(int i) {
        if (i < 0) {
            return -1;
        }
        int[] iArr = this.mSensorToBrightness;
        if (i >= iArr.length) {
            return -1;
        }
        return iArr[i];
    }

    private void resetBrightnessToDefault() {
        this.mDozeService.setDozeScreenBrightness(clampToDimBrightnessForScreenOff(clampToUserSetting(this.mDefaultDozeBrightness)));
        this.mDozeHost.setAodDimmingScrim(0.0f);
    }

    private int clampToUserSetting(int i) {
        return Math.min(i, Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", Integer.MAX_VALUE, -2));
    }

    private int clampToDimBrightnessForScreenOff(int i) {
        return (!this.mDozeParameters.shouldControlUnlockedScreenOff() || this.mWakefulnessLifecycle.getLastSleepReason() != 2) ? i : Math.min(this.mScreenBrightnessDim, i);
    }

    private void setLightSensorEnabled(boolean z) {
        if (!MotoDisplayManager.isAospAD() && !MotoFeature.isLidClosed(this.mContext)) {
            return;
        }
        if (z && !this.mRegistered && this.mLightSensorOptional.isPresent()) {
            this.mRegistered = this.mSensorManager.registerListener(this, this.mLightSensorOptional.get(), 3, this.mHandler);
            this.mLastSensorValue = -1;
        } else if (!z && this.mRegistered) {
            this.mSensorManager.unregisterListener(this);
            this.mRegistered = false;
            this.mLastSensorValue = -1;
        }
    }

    private void setPaused(boolean z) {
        if (this.mPaused != z) {
            this.mPaused = z;
            updateBrightnessAndReady(false);
        }
    }

    private void setScreenOff(boolean z) {
        if (this.mScreenOff != z) {
            this.mScreenOff = z;
            updateBrightnessAndReady(true);
        }
    }

    public void onReceive(Context context, Intent intent) {
        this.mDebugBrightnessBucket = intent.getIntExtra("brightness_bucket", -1);
        updateBrightnessAndReady(false);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("DozeScreenBrightnessSensorRegistered=" + this.mRegistered);
    }

    private void initMotoDisplayManagerIfNeed() {
        if (!MotoDisplayManager.isAospAD() && this.mMotoDisplayManager == null) {
            MotoDisplayManager motoDisplayManager = (MotoDisplayManager) Dependency.get(MotoDisplayManager.class);
            this.mMotoDisplayManager = motoDisplayManager;
            motoDisplayManager.setDozeScreenBrightness(this);
            if (!MotoFeature.isLidClosed(this.mContext)) {
                this.mDefaultDozeBrightness = this.mMotoDisplayManager.getLastScreenBrightness();
            }
        }
    }

    public void setDarkenBrightnessForCli() {
        this.mDozeService.setDozeScreenBrightness(3);
    }
}
