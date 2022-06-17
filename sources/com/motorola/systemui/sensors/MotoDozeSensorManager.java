package com.motorola.systemui.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.R$bool;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.wakelock.WakeLock;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.systemui.sensors.IMotoDozeSensor;
import com.motorola.systemui.sensors.MotoDozeSensorTypeBuilder;
import java.util.ArrayList;
import java.util.List;

public class MotoDozeSensorManager {
    public static final boolean DEBUG = (!Build.IS_USER);
    private boolean mApproachEnabled;
    /* access modifiers changed from: private */
    public final Callback mCallback;
    private DozeHost mDozeHost;
    /* access modifiers changed from: private */
    public DozeParameters mDozeParameters;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private List<IMotoDozeSensor> mMotoDozeSensors;
    /* access modifiers changed from: private */
    public boolean mMultipleSensorEnabled;
    /* access modifiers changed from: private */
    public boolean mOnceSensorEnabled;
    private boolean mPickupInsteadofLTV;
    private SensorEventListener mSensorEventListener;
    /* access modifiers changed from: private */
    public SensorManager mSensorManager;
    private List<MotoDozeSensorTypeBuilder.MotoDozeSensorType> mSensorTypes;
    private TriggerEventListener mTriggerEventListener;
    /* access modifiers changed from: private */
    public final WakeLock mWakeLock;

    public interface Callback {
        void onSensorPulse(int i, float f, float f2, float[] fArr);
    }

    public MotoDozeSensorManager(Context context, DozeHost dozeHost, DozeParameters dozeParameters, WakeLock wakeLock, Callback callback) {
        if (DEBUG) {
            Log.d("MotoDozeSensor", "MotoDozeSensorManager init.");
        }
        this.mPickupInsteadofLTV = context.getResources().getBoolean(R$bool.zz_moto_pickup_insteadof_LTV);
        this.mApproachEnabled = MotorolaSettings.Secure.getIntForUser(context.getContentResolver(), "is_approach_to_wake_enable", context.getResources().getBoolean(17891377) ? 1 : 0, KeyguardUpdateMonitor.getCurrentUser()) != 1 ? false : true;
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        this.mSensorEventListener = new MotoDozeSensorEventListener();
        this.mTriggerEventListener = new MotoTriggerEventListener();
        this.mSensorTypes = MotoDozeSensorTypeBuilder.getInstance().build(MotoFeature.getInstance(context).isSupportFod(), this.mSensorManager, this.mPickupInsteadofLTV, MotoFeature.getInstance(context).isSupportCli(), this.mApproachEnabled);
        this.mMotoDozeSensors = new ArrayList();
        for (MotoDozeSensorTypeBuilder.MotoDozeSensorType motoDozeSensorImpl : this.mSensorTypes) {
            this.mMotoDozeSensors.add(new MotoDozeSensorImpl(this.mSensorManager, motoDozeSensorImpl));
        }
        this.mDozeParameters = dozeParameters;
        this.mDozeHost = dozeHost;
        this.mCallback = callback;
        this.mWakeLock = wakeLock;
    }

    public void startListening() {
        if (!this.mDozeHost.isProvisioned()) {
            Log.d("MotoDozeSensor", "The provision value is " + this.mDozeHost.isProvisioned());
            return;
        }
        List<IMotoDozeSensor> list = this.mMotoDozeSensors;
        if (list != null && list.size() != 0) {
            startOnceSensorListening();
            startMultipleSensorListening();
        } else if (DEBUG) {
            Log.d("MotoDozeSensor", "No sensor is initialized.");
        }
    }

    private void startOnceSensorListening() {
        for (IMotoDozeSensor next : this.mMotoDozeSensors) {
            if (next.getTriggerMode() == IMotoDozeSensor.TriggerMode.ONCE) {
                if (DEBUG) {
                    Log.d("MotoDozeSensor", "startListening once sensor: type = " + next.getType());
                }
                next.setListener(this.mTriggerEventListener);
                next.activate();
                this.mOnceSensorEnabled = true;
            }
        }
    }

    private void startMultipleSensorListening() {
        if (!this.mMultipleSensorEnabled) {
            for (IMotoDozeSensor next : this.mMotoDozeSensors) {
                if (next.getTriggerMode() == IMotoDozeSensor.TriggerMode.MULTIPLE) {
                    if (DEBUG) {
                        Log.d("MotoDozeSensor", "startListening multiple sensor: type = " + next.getType());
                    }
                    next.setListener(this.mSensorEventListener);
                    next.activate();
                    this.mMultipleSensorEnabled = true;
                }
            }
        }
    }

    public void stopListening() {
        if (!this.mDozeHost.isProvisioned()) {
            Log.d("MotoDozeSensor", "The provision value is " + this.mDozeHost.isProvisioned());
            return;
        }
        if (DEBUG) {
            Log.d("MotoDozeSensor", "stopListening all sensors.");
        }
        stopSensorsWithOnce();
        stopSensorsWithMutiple();
    }

    public void stopSensorsWithOnce() {
        if (DEBUG) {
            Log.d("MotoDozeSensor", "Stop singleShot sensors");
        }
        this.mOnceSensorEnabled = false;
        for (MotoDozeSensorTypeBuilder.MotoDozeSensorType next : this.mSensorTypes) {
            if (next.getTriggerMode() == IMotoDozeSensor.TriggerMode.ONCE) {
                SensorManager sensorManager = this.mSensorManager;
                sensorManager.cancelTriggerSensor(this.mTriggerEventListener, sensorManager.getDefaultSensor(next.getType(), true));
            }
        }
    }

    private void stopSensorsWithMutiple() {
        if (DEBUG) {
            Log.d("MotoDozeSensor", "Stop not-singleShot sensors");
        }
        this.mMultipleSensorEnabled = false;
        this.mSensorManager.unregisterListener(this.mSensorEventListener);
    }

    class MotoDozeSensorEventListener implements SensorEventListener {
        long mLastNear;

        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public MotoDozeSensorEventListener() {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            if (MotoDozeSensorManager.DEBUG) {
                Log.d("MotoDozeSensor", "onSensorChanged: " + MotoDozeSensorManager.this.eventToString(sensorEvent));
            }
            if (sensorEvent != null) {
                if (!MotoDozeSensorManager.this.mMultipleSensorEnabled) {
                    MotoDozeSensorManager.this.mSensorManager.unregisterListener(this);
                }
                MotoDozeSensorManager.this.mHandler.post(MotoDozeSensorManager.this.mWakeLock.wrap(new C2784xec54a56c(this, sensorEvent)));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSensorChanged$0(SensorEvent sensorEvent) {
            if (sensorEvent != null) {
                int type = sensorEvent.sensor.getType();
                float[] fArr = sensorEvent.values;
                boolean z = false;
                int i = (int) fArr[0];
                if (type == 65555 || type == 65565) {
                    if (i == 1 || i == 2 || i == 4 || i == 16 || i == 32) {
                        MotoDozeSensorManager.this.mCallback.onSensorPulse(12, -1.0f, -1.0f, sensorEvent.values);
                        MotoDozeSensorManager.this.stopSensorsWithOnce();
                    }
                } else if (type == 65548) {
                    if (i == 1 || i == 2 || i == 4 || i == 16) {
                        MotoDozeSensorManager.this.mCallback.onSensorPulse(12, -1.0f, -1.0f, sensorEvent.values);
                        MotoDozeSensorManager.this.stopSensorsWithOnce();
                    }
                } else if (type == 65556) {
                    MotoDozeSensorManager.this.mCallback.onSensorPulse(12, -1.0f, -1.0f, sensorEvent.values);
                    MotoDozeSensorManager.this.stopSensorsWithOnce();
                } else if (type == 8) {
                    if (fArr[0] >= sensorEvent.sensor.getMaximumRange()) {
                        z = true;
                    }
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    if (!z) {
                        this.mLastNear = elapsedRealtime;
                    } else if (z && elapsedRealtime - this.mLastNear < MotoDozeSensorManager.this.mDozeParameters.getProxCooldownTriggerMs()) {
                        MotoDozeSensorManager.this.mCallback.onSensorPulse(12, -1.0f, -1.0f, sensorEvent.values);
                        MotoDozeSensorManager.this.stopSensorsWithOnce();
                    }
                }
            }
        }
    }

    class MotoTriggerEventListener extends TriggerEventListener {
        MotoTriggerEventListener() {
        }

        public void onTrigger(TriggerEvent triggerEvent) {
            if (MotoDozeSensorManager.DEBUG) {
                Log.d("MotoDozeSensor", "onTrigger: " + MotoDozeSensorManager.this.eventToString(triggerEvent));
            }
            if (triggerEvent != null) {
                if (!MotoDozeSensorManager.this.mOnceSensorEnabled) {
                    MotoDozeSensorManager.this.stopSensorsWithOnce();
                }
                MotoDozeSensorManager.this.mHandler.post(MotoDozeSensorManager.this.mWakeLock.wrap(new C2785xf79e0d26(this, triggerEvent)));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTrigger$0(TriggerEvent triggerEvent) {
            if (triggerEvent != null) {
                int type = triggerEvent.sensor.getType();
                float f = triggerEvent.values[0];
                if (type == 25) {
                    MotoDozeSensorManager.this.mCallback.onSensorPulse(12, -1.0f, -1.0f, triggerEvent.values);
                    MotoDozeSensorManager.this.stopSensorsWithOnce();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public String eventToString(Object obj) {
        String str;
        float[] fArr;
        Sensor sensor;
        if (obj == null) {
            return null;
        }
        if (obj instanceof TriggerEvent) {
            TriggerEvent triggerEvent = (TriggerEvent) obj;
            sensor = triggerEvent.sensor;
            fArr = triggerEvent.values;
            str = triggerEvent.getClass().getName();
        } else if (obj instanceof SensorEvent) {
            SensorEvent sensorEvent = (SensorEvent) obj;
            sensor = sensorEvent.sensor;
            fArr = sensorEvent.values;
            str = sensorEvent.getClass().getName();
        } else {
            str = null;
            sensor = null;
            fArr = null;
        }
        if (sensor == null || fArr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str);
        sb.append("[name:" + sensor.getName());
        sb.append(",");
        sb.append("type:" + sensor.getType());
        for (float append : fArr) {
            sb.append(',');
            sb.append(append);
        }
        sb.append("]");
        return sb.toString();
    }
}
