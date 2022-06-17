package com.android.systemui.doze;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.hardware.display.AmbientDisplayConfiguration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.plugins.SensorManagerPlugin;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.sensors.ThresholdSensor;
import com.android.systemui.util.settings.SecureSettings;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.function.Consumer;

public class DozeSensors {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = DozeService.DEBUG;
    /* access modifiers changed from: private */
    public static final UiEventLogger UI_EVENT_LOGGER = new UiEventLoggerImpl();
    private final AODTriggerSensor mAODSensor;
    /* access modifiers changed from: private */
    public final Callback mCallback;
    /* access modifiers changed from: private */
    public final AmbientDisplayConfiguration mConfig;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public long mDebounceFrom;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mListening;
    private boolean mListeningProxSensors;
    private boolean mListeningTouchScreenSensors;
    private final Consumer<Boolean> mProxCallback;
    private final ProximitySensor mProximitySensor;
    private final boolean mScreenOffUdfpsEnabled;
    /* access modifiers changed from: private */
    public final SecureSettings mSecureSettings;
    private boolean mSelectivelyRegisterProxSensors;
    /* access modifiers changed from: private */
    public final AsyncSensorManager mSensorManager;
    protected TriggerSensor[] mSensors;
    private boolean mSettingRegistered;
    /* access modifiers changed from: private */
    public final ContentObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public final WakeLock mWakeLock;

    public interface Callback {
        void onSensorPulse(int i, float f, float f2, float[] fArr);
    }

    public enum DozeSensorsUiEvent implements UiEventLogger.UiEventEnum {
        ACTION_AMBIENT_GESTURE_PICKUP(459);
        
        private final int mId;

        private DozeSensorsUiEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    DozeSensors(Context context, AsyncSensorManager asyncSensorManager, DozeParameters dozeParameters, AmbientDisplayConfiguration ambientDisplayConfiguration, WakeLock wakeLock, Callback callback, Consumer<Boolean> consumer, DozeLog dozeLog, ProximitySensor proximitySensor, SecureSettings secureSettings, AuthController authController) {
        AsyncSensorManager asyncSensorManager2 = asyncSensorManager;
        AmbientDisplayConfiguration ambientDisplayConfiguration2 = ambientDisplayConfiguration;
        ProximitySensor proximitySensor2 = proximitySensor;
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mSettingsObserver = new ContentObserver(handler) {
            public void onChange(boolean z, Collection<Uri> collection, int i, int i2) {
                if (i2 == ActivityManager.getCurrentUser()) {
                    for (TriggerSensor updateListening : DozeSensors.this.mSensors) {
                        updateListening.updateListening();
                    }
                }
            }
        };
        this.mContext = context;
        this.mSensorManager = asyncSensorManager2;
        this.mConfig = ambientDisplayConfiguration2;
        this.mWakeLock = wakeLock;
        this.mProxCallback = consumer;
        this.mSecureSettings = secureSettings;
        this.mCallback = callback;
        this.mProximitySensor = proximitySensor2;
        proximitySensor2.setTag("DozeSensors");
        boolean selectivelyRegisterSensorsUsingProx = dozeParameters.getSelectivelyRegisterSensorsUsingProx();
        this.mSelectivelyRegisterProxSensors = selectivelyRegisterSensorsUsingProx;
        this.mListeningProxSensors = !selectivelyRegisterSensorsUsingProx;
        boolean screenOffUdfpsEnabled = ambientDisplayConfiguration2.screenOffUdfpsEnabled(KeyguardUpdateMonitor.getCurrentUser());
        this.mScreenOffUdfpsEnabled = screenOffUdfpsEnabled;
        boolean isUdfpsEnrolled = authController.isUdfpsEnrolled(KeyguardUpdateMonitor.getCurrentUser());
        boolean alwaysOnEnabled = ambientDisplayConfiguration2.alwaysOnEnabled(-2);
        TriggerSensor[] triggerSensorArr = new TriggerSensor[10];
        triggerSensorArr[0] = new TriggerSensor(this, asyncSensorManager2.getDefaultSensor(17), (String) null, dozeParameters.getPulseOnSigMotion(), 2, false, false, dozeLog);
        boolean z = screenOffUdfpsEnabled;
        TriggerSensor[] triggerSensorArr2 = triggerSensorArr;
        triggerSensorArr2[1] = new TriggerSensor(asyncSensorManager2.getDefaultSensor(25), "doze_pulse_on_pick_up", true, ambientDisplayConfiguration.dozePickupSensorAvailable(), 3, false, false, false, false, dozeLog);
        triggerSensorArr2[2] = new TriggerSensor(this, findSensorWithType(ambientDisplayConfiguration.doubleTapSensorType()), "doze_pulse_on_double_tap", true, 4, dozeParameters.doubleTapReportsTouchCoordinates(), true, dozeLog);
        DozeLog dozeLog2 = dozeLog;
        TriggerSensor[] triggerSensorArr3 = triggerSensorArr2;
        triggerSensorArr3[3] = new TriggerSensor(findSensorWithType(ambientDisplayConfiguration.tapSensorType()), "doze_tap_gesture", true, true, 9, false, true, false, dozeParameters.singleTapUsesProx(), dozeLog2);
        triggerSensorArr3[4] = new TriggerSensor(findSensorWithType(ambientDisplayConfiguration.longPressSensorType()), "doze_pulse_on_long_press", false, true, 5, true, true, false, dozeParameters.longPressUsesProx(), dozeLog2);
        triggerSensorArr3[5] = new TriggerSensor(findSensorWithType(ambientDisplayConfiguration.udfpsLongPressSensorType()), "doze_pulse_on_auth", true, isUdfpsEnrolled && (alwaysOnEnabled || z), 10, true, true, false, dozeParameters.longPressUsesProx(), dozeLog);
        triggerSensorArr3[6] = new PluginSensor(this, new SensorManagerPlugin.Sensor(2), "doze_wake_display_gesture", ambientDisplayConfiguration.wakeScreenGestureAvailable() && alwaysOnEnabled, 7, false, false, dozeLog);
        triggerSensorArr3[7] = new PluginSensor(this, new SensorManagerPlugin.Sensor(1), "doze_wake_screen_gesture", ambientDisplayConfiguration.wakeScreenGestureAvailable(), 8, false, false, ambientDisplayConfiguration.getWakeLockScreenDebounce(), dozeLog);
        triggerSensorArr3[8] = new TriggerSensor(this, findSensorWithType(ambientDisplayConfiguration.quickPickupSensorType()), "doze_quick_pickup_gesture", true, ambientDisplayConfiguration2.quickPickupSensorEnabled(KeyguardUpdateMonitor.getCurrentUser()) && isUdfpsEnrolled, 11, false, false, dozeLog);
        AODTriggerSensor aODTriggerSensor = new AODTriggerSensor(dozeLog);
        this.mAODSensor = aODTriggerSensor;
        triggerSensorArr3[9] = aODTriggerSensor;
        this.mSensors = triggerSensorArr3;
        setProxListening(false);
        proximitySensor.register(new DozeSensors$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ThresholdSensor.ThresholdSensorEvent thresholdSensorEvent) {
        if (thresholdSensorEvent != null) {
            this.mProxCallback.accept(Boolean.valueOf(!thresholdSensorEvent.getBelow()));
        }
    }

    public void destroy() {
        for (TriggerSensor listening : this.mSensors) {
            listening.setListening(false);
        }
        this.mProximitySensor.pause();
    }

    public void requestTemporaryDisable() {
        this.mDebounceFrom = SystemClock.uptimeMillis();
    }

    private Sensor findSensorWithType(String str) {
        return findSensorWithType(this.mSensorManager, str);
    }

    public static Sensor findSensorWithType(SensorManager sensorManager, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        for (Sensor next : sensorManager.getSensorList(-1)) {
            if (str.equals(next.getStringType())) {
                return next;
            }
        }
        return null;
    }

    public void setListening(boolean z, boolean z2) {
        if (this.mListening != z || this.mListeningTouchScreenSensors != z2) {
            this.mListening = z;
            this.mListeningTouchScreenSensors = z2;
            updateListening();
        }
    }

    public void setListening(boolean z, boolean z2, boolean z3) {
        boolean z4 = !this.mSelectivelyRegisterProxSensors || z3;
        if (this.mListening != z || this.mListeningTouchScreenSensors != z2 || this.mListeningProxSensors != z4) {
            this.mListening = z;
            this.mListeningTouchScreenSensors = z2;
            this.mListeningProxSensors = z4;
            updateListening();
        }
    }

    private void updateListening() {
        int i = 0;
        boolean z = false;
        for (TriggerSensor triggerSensor : this.mSensors) {
            boolean z2 = this.mListening && (!triggerSensor.mRequiresTouchscreen || this.mListeningTouchScreenSensors) && (!triggerSensor.mRequiresProx || this.mListeningProxSensors);
            triggerSensor.setListening(z2);
            if (z2) {
                z = true;
            }
        }
        if (!z) {
            this.mSecureSettings.unregisterContentObserver(this.mSettingsObserver);
            TriggerSensor[] triggerSensorArr = this.mSensors;
            int length = triggerSensorArr.length;
            while (i < length) {
                triggerSensorArr[i].onSettingsObserverUnregister();
                i++;
            }
        } else if (!this.mSettingRegistered) {
            TriggerSensor[] triggerSensorArr2 = this.mSensors;
            int length2 = triggerSensorArr2.length;
            while (i < length2) {
                triggerSensorArr2[i].registerSettingsObserver(this.mSettingsObserver);
                i++;
            }
        }
        this.mSettingRegistered = z;
    }

    public void onUserSwitched() {
        for (TriggerSensor updateListening : this.mSensors) {
            updateListening.updateListening();
        }
    }

    /* access modifiers changed from: package-private */
    public void onScreenState(int i) {
        ProximitySensor proximitySensor = this.mProximitySensor;
        boolean z = true;
        if (!(i == 3 || i == 4 || i == 1)) {
            z = false;
        }
        proximitySensor.setSecondarySafe(z);
    }

    public void setProxListening(boolean z) {
        if (this.mProximitySensor.isRegistered() && z) {
            this.mProximitySensor.alertListeners();
        } else if (z) {
            this.mProximitySensor.resume();
        } else {
            this.mProximitySensor.pause();
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("mListening=" + this.mListening);
        printWriter.println("mListeningTouchScreenSensors=" + this.mListeningTouchScreenSensors);
        printWriter.println("mSelectivelyRegisterProxSensors=" + this.mSelectivelyRegisterProxSensors);
        printWriter.println("mListeningProxSensors=" + this.mListeningProxSensors);
        printWriter.println("mScreenOffUdfpsEnabled=" + this.mScreenOffUdfpsEnabled);
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.increaseIndent();
        for (TriggerSensor triggerSensor : this.mSensors) {
            indentingPrintWriter.println("Sensor: " + triggerSensor.toString());
        }
        indentingPrintWriter.println("ProxSensor: " + this.mProximitySensor.toString());
    }

    public Boolean isProximityCurrentlyNear() {
        return this.mProximitySensor.isNear();
    }

    class TriggerSensor extends TriggerEventListener {
        final boolean mConfigured;
        protected boolean mDisabled;
        protected final DozeLog mDozeLog;
        protected boolean mIgnoresSetting;
        private boolean mObserverRegistered;
        final int mPulseReason;
        protected boolean mRegistered;
        private final boolean mReportsTouchCoordinates;
        protected boolean mRequested;
        /* access modifiers changed from: private */
        public final boolean mRequiresProx;
        /* access modifiers changed from: private */
        public final boolean mRequiresTouchscreen;
        final Sensor mSensor;
        private final String mSetting;
        private final boolean mSettingDefault;

        public TriggerSensor(DozeSensors dozeSensors, Sensor sensor, String str, boolean z, int i, boolean z2, boolean z3, DozeLog dozeLog) {
            this(dozeSensors, sensor, str, true, z, i, z2, z3, dozeLog);
        }

        public TriggerSensor(DozeSensors dozeSensors, Sensor sensor, String str, boolean z, boolean z2, int i, boolean z3, boolean z4, DozeLog dozeLog) {
            this(sensor, str, z, z2, i, z3, z4, false, false, dozeLog);
        }

        private TriggerSensor(Sensor sensor, String str, boolean z, boolean z2, int i, boolean z3, boolean z4, boolean z5, boolean z6, DozeLog dozeLog) {
            this.mObserverRegistered = false;
            this.mSensor = sensor;
            this.mSetting = str;
            this.mSettingDefault = z;
            this.mConfigured = z2;
            this.mPulseReason = i;
            this.mReportsTouchCoordinates = z3;
            this.mRequiresTouchscreen = z4;
            this.mIgnoresSetting = z5;
            this.mRequiresProx = z6;
            this.mDozeLog = dozeLog;
        }

        public void setListening(boolean z) {
            if (this.mRequested != z) {
                this.mRequested = z;
                updateListening();
            }
        }

        public boolean getListening() {
            return this.mRequested;
        }

        public boolean getDisabled() {
            return this.mDisabled;
        }

        public void updateListening() {
            if (this.mConfigured && this.mSensor != null) {
                if (this.mRequested && !this.mDisabled && ((enabledBySetting() || this.mIgnoresSetting) && !this.mRegistered)) {
                    this.mRegistered = DozeSensors.this.mSensorManager.requestTriggerSensor(this, this.mSensor);
                    if (DozeSensors.DEBUG) {
                        Log.d("DozeSensors", "requestTriggerSensor " + this.mRegistered);
                    }
                } else if (this.mRegistered) {
                    boolean cancelTriggerSensor = DozeSensors.this.mSensorManager.cancelTriggerSensor(this, this.mSensor);
                    if (DozeSensors.DEBUG) {
                        Log.d("DozeSensors", "cancelTriggerSensor " + cancelTriggerSensor);
                    }
                    this.mRegistered = false;
                }
            }
        }

        /* access modifiers changed from: protected */
        public boolean enabledBySetting() {
            if (!DozeSensors.this.mConfig.enabled(-2)) {
                return false;
            }
            if (TextUtils.isEmpty(this.mSetting)) {
                return true;
            }
            if (DozeSensors.this.mSecureSettings.getIntForUser(this.mSetting, this.mSettingDefault ? 1 : 0, -2) != 0) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "{mRegistered=" + this.mRegistered + ", mRequested=" + this.mRequested + ", mDisabled=" + this.mDisabled + ", mConfigured=" + this.mConfigured + ", mIgnoresSetting=" + this.mIgnoresSetting + ", mSensor=" + this.mSensor + "}";
        }

        public void onTrigger(TriggerEvent triggerEvent) {
            this.mDozeLog.traceSensor(this.mPulseReason);
            DozeSensors.this.mHandler.post(DozeSensors.this.mWakeLock.wrap(new DozeSensors$TriggerSensor$$ExternalSyntheticLambda0(this, triggerEvent)));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x005c  */
        /* JADX WARNING: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onTrigger$0(android.hardware.TriggerEvent r6) {
            /*
                r5 = this;
                boolean r0 = com.android.systemui.doze.DozeSensors.DEBUG
                if (r0 == 0) goto L_0x0020
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "onTrigger: "
                r0.append(r1)
                java.lang.String r1 = r5.triggerEventToString(r6)
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "DozeSensors"
                android.util.Log.d(r1, r0)
            L_0x0020:
                android.hardware.Sensor r0 = r5.mSensor
                if (r0 == 0) goto L_0x0035
                int r0 = r0.getType()
                r1 = 25
                if (r0 != r1) goto L_0x0035
                com.android.internal.logging.UiEventLogger r0 = com.android.systemui.doze.DozeSensors.UI_EVENT_LOGGER
                com.android.systemui.doze.DozeSensors$DozeSensorsUiEvent r1 = com.android.systemui.doze.DozeSensors.DozeSensorsUiEvent.ACTION_AMBIENT_GESTURE_PICKUP
                r0.log(r1)
            L_0x0035:
                r0 = 0
                r5.mRegistered = r0
                boolean r1 = r5.mReportsTouchCoordinates
                r2 = -1082130432(0xffffffffbf800000, float:-1.0)
                if (r1 == 0) goto L_0x004a
                float[] r1 = r6.values
                int r3 = r1.length
                r4 = 2
                if (r3 < r4) goto L_0x004a
                r2 = r1[r0]
                r0 = 1
                r0 = r1[r0]
                goto L_0x004b
            L_0x004a:
                r0 = r2
            L_0x004b:
                com.android.systemui.doze.DozeSensors r1 = com.android.systemui.doze.DozeSensors.this
                com.android.systemui.doze.DozeSensors$Callback r1 = r1.mCallback
                int r3 = r5.mPulseReason
                float[] r6 = r6.values
                r1.onSensorPulse(r3, r2, r0, r6)
                boolean r6 = r5.mRegistered
                if (r6 != 0) goto L_0x005f
                r5.updateListening()
            L_0x005f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeSensors.TriggerSensor.lambda$onTrigger$0(android.hardware.TriggerEvent):void");
        }

        public void registerSettingsObserver(ContentObserver contentObserver) {
            if (this.mConfigured && !TextUtils.isEmpty(this.mSetting) && !this.mObserverRegistered) {
                this.mObserverRegistered = true;
                DozeSensors.this.mSecureSettings.registerContentObserverForUser(this.mSetting, DozeSensors.this.mSettingsObserver, -1);
            }
        }

        public void onSettingsObserverUnregister() {
            this.mObserverRegistered = false;
        }

        /* access modifiers changed from: protected */
        public String triggerEventToString(TriggerEvent triggerEvent) {
            if (triggerEvent == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder("SensorEvent[");
            sb.append(triggerEvent.timestamp);
            sb.append(',');
            sb.append(triggerEvent.sensor.getName());
            if (triggerEvent.values != null) {
                for (float append : triggerEvent.values) {
                    sb.append(',');
                    sb.append(append);
                }
            }
            sb.append(']');
            return sb.toString();
        }
    }

    class PluginSensor extends TriggerSensor implements SensorManagerPlugin.SensorEventListener {
        private long mDebounce;
        final SensorManagerPlugin.Sensor mPluginSensor;
        final /* synthetic */ DozeSensors this$0;

        PluginSensor(DozeSensors dozeSensors, SensorManagerPlugin.Sensor sensor, String str, boolean z, int i, boolean z2, boolean z3, DozeLog dozeLog) {
            this(dozeSensors, sensor, str, z, i, z2, z3, 0, dozeLog);
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        PluginSensor(com.android.systemui.doze.DozeSensors r11, com.android.systemui.plugins.SensorManagerPlugin.Sensor r12, java.lang.String r13, boolean r14, int r15, boolean r16, boolean r17, long r18, com.android.systemui.doze.DozeLog r20) {
            /*
                r10 = this;
                r9 = r10
                r1 = r11
                r9.this$0 = r1
                r2 = 0
                r0 = r10
                r3 = r13
                r4 = r14
                r5 = r15
                r6 = r16
                r7 = r17
                r8 = r20
                r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8)
                r0 = r12
                r9.mPluginSensor = r0
                r0 = r18
                r9.mDebounce = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.doze.DozeSensors.PluginSensor.<init>(com.android.systemui.doze.DozeSensors, com.android.systemui.plugins.SensorManagerPlugin$Sensor, java.lang.String, boolean, int, boolean, boolean, long, com.android.systemui.doze.DozeLog):void");
        }

        public void updateListening() {
            if (this.mConfigured) {
                AsyncSensorManager access$300 = this.this$0.mSensorManager;
                if (this.mRequested && !this.mDisabled && ((enabledBySetting() || this.mIgnoresSetting) && !this.mRegistered)) {
                    access$300.registerPluginListener(this.mPluginSensor, this);
                    this.mRegistered = true;
                    if (DozeSensors.DEBUG) {
                        Log.d("DozeSensors", "registerPluginListener");
                    }
                } else if (this.mRegistered) {
                    access$300.unregisterPluginListener(this.mPluginSensor, this);
                    this.mRegistered = false;
                    if (DozeSensors.DEBUG) {
                        Log.d("DozeSensors", "unregisterPluginListener");
                    }
                }
            }
        }

        public String toString() {
            return "{mRegistered=" + this.mRegistered + ", mRequested=" + this.mRequested + ", mDisabled=" + this.mDisabled + ", mConfigured=" + this.mConfigured + ", mIgnoresSetting=" + this.mIgnoresSetting + ", mSensor=" + this.mPluginSensor + "}";
        }

        private String triggerEventToString(SensorManagerPlugin.SensorEvent sensorEvent) {
            if (sensorEvent == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder("PluginTriggerEvent[");
            sb.append(sensorEvent.getSensor());
            sb.append(',');
            sb.append(sensorEvent.getVendorType());
            if (sensorEvent.getValues() != null) {
                for (float append : sensorEvent.getValues()) {
                    sb.append(',');
                    sb.append(append);
                }
            }
            sb.append(']');
            return sb.toString();
        }

        public void onSensorChanged(SensorManagerPlugin.SensorEvent sensorEvent) {
            this.mDozeLog.traceSensor(this.mPulseReason);
            this.this$0.mHandler.post(this.this$0.mWakeLock.wrap(new DozeSensors$PluginSensor$$ExternalSyntheticLambda0(this, sensorEvent)));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSensorChanged$0(SensorManagerPlugin.SensorEvent sensorEvent) {
            if (SystemClock.uptimeMillis() < this.this$0.mDebounceFrom + this.mDebounce) {
                Log.d("DozeSensors", "onSensorEvent dropped: " + triggerEventToString(sensorEvent));
                return;
            }
            if (DozeSensors.DEBUG) {
                Log.d("DozeSensors", "onSensorEvent: " + triggerEventToString(sensorEvent));
            }
            this.this$0.mCallback.onSensorPulse(this.mPulseReason, -1.0f, -1.0f, sensorEvent.getValues());
        }
    }

    private class AODTriggerSensor extends TriggerSensor {
        private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (DozeSensors.DEBUG) {
                    Log.d("DozeSensors", "AODSensor: " + intent.toUri(0));
                }
                AODTriggerSensor.this.lambda$updateListening$0();
            }
        };
        private boolean mHasRegistered;
        private MotoDisplayManager mMotoDisplayManager = ((MotoDisplayManager) Dependency.get(MotoDisplayManager.class));

        public AODTriggerSensor(DozeLog dozeLog) {
            super(DozeSensors.this, (Sensor) null, (String) null, true, true, 13, false, false, dozeLog);
        }

        public void updateListening() {
            if (!getListening() || getDisabled()) {
                if (this.mHasRegistered) {
                    if (DozeSensors.DEBUG) {
                        Log.d("DozeSensors", "unregisterAODSensor ");
                    }
                    this.mHasRegistered = false;
                    this.mMotoDisplayManager.setAODVirtualSensorListener((Runnable) null);
                    if (Build.IS_DEBUGGABLE) {
                        DozeSensors.this.mContext.unregisterReceiver(this.mBroadcastReceiver);
                    }
                }
            } else if (!this.mHasRegistered) {
                if (DozeSensors.DEBUG) {
                    Log.d("DozeSensors", "registerAODSensor ");
                }
                this.mHasRegistered = true;
                this.mMotoDisplayManager.setAODVirtualSensorListener(new DozeSensors$AODTriggerSensor$$ExternalSyntheticLambda1(this));
                if (Build.IS_DEBUGGABLE) {
                    DozeSensors.this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.motorola.systemui.action.DOZE_TRIGGER"));
                }
            }
        }

        /* renamed from: onTriggerForAOD */
        public void lambda$updateListening$0() {
            DozeSensors.this.mHandler.post(DozeSensors.this.mWakeLock.wrap(new DozeSensors$AODTriggerSensor$$ExternalSyntheticLambda0(this)));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTriggerForAOD$1() {
            DozeSensors.this.mCallback.onSensorPulse(this.mPulseReason, -1.0f, -1.0f, (float[]) null);
        }
    }
}
