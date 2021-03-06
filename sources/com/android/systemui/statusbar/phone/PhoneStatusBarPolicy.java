package com.android.systemui.statusbar.phone;

import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserManager;
import android.service.notification.ZenModeConfig;
import android.telecom.TelecomManager;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.systemui.R$bool;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p006qs.tiles.RotationLockTile;
import com.android.systemui.privacy.PrivacyItem;
import com.android.systemui.privacy.PrivacyItemController;
import com.android.systemui.privacy.PrivacyType;
import com.android.systemui.privacy.logging.PrivacyLogger;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.HotspotIcons;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.RingerModeTracker;
import com.android.systemui.util.time.DateFormatUtil;
import com.motorola.systemui.statusbar.policy.NfcController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class PhoneStatusBarPolicy implements BluetoothController.Callback, CommandQueue.Callbacks, RotationLockController.RotationLockControllerCallback, DataSaverController.Listener, ZenModeController.Callback, DeviceProvisionedController.DeviceProvisionedListener, KeyguardStateController.Callback, PrivacyItemController.Callback, LocationController.LocationChangeCallback, RecordingController.RecordingStateChangeCallback, NfcController.NfcChangeCallback {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("PhoneStatusBarPolicy", 3));
    static final int LOCATION_STATUS_ICON_ID = PrivacyType.TYPE_LOCATION.getIconId();
    private final AlarmManager mAlarmManager;
    private BluetoothController mBluetooth;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final CastController mCast;
    private final CastController.Callback mCastCallback = new CastController.Callback() {
        public void onCastDevicesChanged() {
            PhoneStatusBarPolicy.this.updateCast();
        }
    };
    private final CommandQueue mCommandQueue;
    private boolean mCurrentUserSetup;
    private final DataSaverController mDataSaver;
    private final DateFormatUtil mDateFormatUtil;
    private final int mDisplayId;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private final HotspotController mHotspot;
    private final HotspotController.Callback mHotspotCallback = new HotspotController.Callback() {
        public void onHotspotChanged(boolean z, int i) {
            if (PhoneStatusBarPolicy.this.mResources.getBoolean(R$bool.config_show_hotspot_connected_devices)) {
                Log.d("PhoneStatusBarPolicy", "onHotspotChanged, enabled: " + z + " numDevices: " + i);
                int[] iArr = HotspotIcons.HOTSPOT_ICONS;
                if (i > iArr.length - 1) {
                    i = iArr.length - 1;
                }
                PhoneStatusBarPolicy.this.mIconController.setIcon(PhoneStatusBarPolicy.this.mSlotHotspot, iArr[i], PhoneStatusBarPolicy.this.mResources.getString(R$string.accessibility_status_bar_hotspot));
            }
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotHotspot, z);
        }
    };
    private final IActivityManager mIActivityManager;
    /* access modifiers changed from: private */
    public final StatusBarIconController mIconController;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action.hashCode();
            char c = 65535;
            switch (action.hashCode()) {
                case -1676458352:
                    if (action.equals("android.intent.action.HEADSET_PLUG")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1238404651:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_UNAVAILABLE")) {
                        c = 1;
                        break;
                    }
                    break;
                case -864107122:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_AVAILABLE")) {
                        c = 2;
                        break;
                    }
                    break;
                case -229777127:
                    if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1051344550:
                    if (action.equals("android.telecom.action.CURRENT_TTY_MODE_CHANGED")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1051477093:
                    if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) {
                        c = 5;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    PhoneStatusBarPolicy.this.updateHeadsetPlug(intent);
                    return;
                case 1:
                case 2:
                case 5:
                    PhoneStatusBarPolicy.this.updateManagedProfile();
                    return;
                case 3:
                    intent.getBooleanExtra("rebroadcastOnUnlock", false);
                    return;
                case 4:
                    PhoneStatusBarPolicy.this.updateTTY(intent.getIntExtra("android.telecom.extra.CURRENT_TTY_MODE", 0));
                    return;
                default:
                    return;
            }
        }
    };
    private final KeyguardStateController mKeyguardStateController;
    private final LocationController mLocationController;
    private boolean mManagedProfileIconVisible = false;
    private boolean mMuteVisible;
    /* access modifiers changed from: private */
    public AlarmManager.AlarmClockInfo mNextAlarm;
    private final NextAlarmController.NextAlarmChangeCallback mNextAlarmCallback = new NextAlarmController.NextAlarmChangeCallback() {
        public void onNextAlarmChanged(AlarmManager.AlarmClockInfo alarmClockInfo) {
            AlarmManager.AlarmClockInfo unused = PhoneStatusBarPolicy.this.mNextAlarm = alarmClockInfo;
            PhoneStatusBarPolicy.this.updateAlarm();
        }
    };
    private final NextAlarmController mNextAlarmController;
    /* access modifiers changed from: private */
    public final NfcController mNfcController;
    private final PrivacyItemController mPrivacyItemController;
    private final PrivacyLogger mPrivacyLogger;
    private final DeviceProvisionedController mProvisionedController;
    private final RecordingController mRecordingController;
    private Runnable mRemoveCastIconRunnable = new Runnable() {
        public void run() {
            if (PhoneStatusBarPolicy.DEBUG) {
                Log.v("PhoneStatusBarPolicy", "updateCast: hiding icon NOW");
            }
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotCast, false);
        }
    };
    /* access modifiers changed from: private */
    public final Resources mResources;
    private final RingerModeTracker mRingerModeTracker;
    private final RotationLockController mRotationLockController;
    private final SensorPrivacyController mSensorPrivacyController;
    private final SensorPrivacyController.OnSensorPrivacyChangedListener mSensorPrivacyListener = new SensorPrivacyController.OnSensorPrivacyChangedListener() {
        public void onSensorPrivacyChanged(boolean z) {
            PhoneStatusBarPolicy.this.mHandler.post(new PhoneStatusBarPolicy$5$$ExternalSyntheticLambda0(this, z));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSensorPrivacyChanged$0(boolean z) {
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotSensorsOff, z);
        }
    };
    private final SharedPreferences mSharedPreferences;
    private final String mSlotAlarmClock;
    private final String mSlotBluetooth;
    private final String mSlotCamera;
    /* access modifiers changed from: private */
    public final String mSlotCast;
    private final String mSlotDataSaver;
    private final String mSlotHeadset;
    /* access modifiers changed from: private */
    public final String mSlotHotspot;
    private final String mSlotLocation;
    private final String mSlotManagedProfile;
    private final String mSlotMicrophone;
    private final String mSlotMute;
    private final String mSlotNfc;
    private final String mSlotRotate;
    private final String mSlotScreenRecord;
    /* access modifiers changed from: private */
    public final String mSlotSensorsOff;
    private final String mSlotTty;
    private final String mSlotVibrate;
    private final String mSlotZen;
    private final TelecomManager mTelecomManager;
    private final Executor mUiBgExecutor;
    /* access modifiers changed from: private */
    public final UserInfoController mUserInfoController;
    private final UserManager mUserManager;
    private final SynchronousUserSwitchObserver mUserSwitchListener = new SynchronousUserSwitchObserver() {
        public void onUserSwitching(int i) throws RemoteException {
            PhoneStatusBarPolicy.this.mHandler.post(new PhoneStatusBarPolicy$1$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onUserSwitching$0() {
            PhoneStatusBarPolicy.this.mUserInfoController.reloadUserInfo();
            PhoneStatusBarPolicy.this.mNfcController.unregisterNfcIconObserver(-2);
        }

        public void onUserSwitchComplete(int i) throws RemoteException {
            PhoneStatusBarPolicy.this.mHandler.post(new PhoneStatusBarPolicy$1$$ExternalSyntheticLambda1(this, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onUserSwitchComplete$1(int i) {
            PhoneStatusBarPolicy.this.updateAlarm();
            PhoneStatusBarPolicy.this.updateManagedProfile();
            PhoneStatusBarPolicy.this.mNfcController.registerNfcIconObserver(i);
        }
    };
    private boolean mVibrateVisible;
    private final ZenModeController mZenController;
    private boolean mZenVisible;

    public PhoneStatusBarPolicy(StatusBarIconController statusBarIconController, CommandQueue commandQueue, BroadcastDispatcher broadcastDispatcher, Executor executor, Resources resources, CastController castController, HotspotController hotspotController, BluetoothController bluetoothController, NextAlarmController nextAlarmController, UserInfoController userInfoController, RotationLockController rotationLockController, DataSaverController dataSaverController, ZenModeController zenModeController, DeviceProvisionedController deviceProvisionedController, KeyguardStateController keyguardStateController, LocationController locationController, SensorPrivacyController sensorPrivacyController, IActivityManager iActivityManager, AlarmManager alarmManager, UserManager userManager, RecordingController recordingController, NfcController nfcController, TelecomManager telecomManager, int i, SharedPreferences sharedPreferences, DateFormatUtil dateFormatUtil, RingerModeTracker ringerModeTracker, PrivacyItemController privacyItemController, PrivacyLogger privacyLogger) {
        this.mIconController = statusBarIconController;
        this.mCommandQueue = commandQueue;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mResources = resources;
        this.mCast = castController;
        this.mHotspot = hotspotController;
        this.mBluetooth = bluetoothController;
        this.mNextAlarmController = nextAlarmController;
        this.mAlarmManager = alarmManager;
        this.mUserInfoController = userInfoController;
        this.mIActivityManager = iActivityManager;
        this.mUserManager = userManager;
        this.mRotationLockController = rotationLockController;
        this.mDataSaver = dataSaverController;
        this.mZenController = zenModeController;
        this.mProvisionedController = deviceProvisionedController;
        this.mKeyguardStateController = keyguardStateController;
        this.mLocationController = locationController;
        this.mPrivacyItemController = privacyItemController;
        this.mSensorPrivacyController = sensorPrivacyController;
        this.mRecordingController = recordingController;
        this.mUiBgExecutor = executor;
        this.mTelecomManager = telecomManager;
        this.mRingerModeTracker = ringerModeTracker;
        this.mPrivacyLogger = privacyLogger;
        this.mSlotCast = resources.getString(17041496);
        this.mSlotHotspot = resources.getString(17041504);
        this.mSlotBluetooth = resources.getString(17041493);
        this.mSlotTty = resources.getString(17041524);
        this.mSlotZen = resources.getString(17041528);
        this.mSlotMute = resources.getString(17041511);
        this.mSlotVibrate = resources.getString(17041525);
        this.mSlotAlarmClock = resources.getString(17041491);
        this.mSlotManagedProfile = resources.getString(17041508);
        this.mSlotRotate = resources.getString(17041517);
        this.mSlotHeadset = resources.getString(17041503);
        this.mSlotDataSaver = resources.getString(17041501);
        this.mSlotLocation = resources.getString(17041507);
        this.mSlotMicrophone = resources.getString(17041509);
        this.mSlotCamera = resources.getString(17041495);
        this.mSlotSensorsOff = resources.getString(17041520);
        this.mSlotScreenRecord = resources.getString(17041518);
        this.mNfcController = nfcController;
        this.mSlotNfc = resources.getString(17041513);
        this.mDisplayId = i;
        this.mSharedPreferences = sharedPreferences;
        this.mDateFormatUtil = dateFormatUtil;
    }

    public void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.telecom.action.CURRENT_TTY_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mIntentReceiver, intentFilter, this.mHandler);
        PhoneStatusBarPolicy$$ExternalSyntheticLambda0 phoneStatusBarPolicy$$ExternalSyntheticLambda0 = new PhoneStatusBarPolicy$$ExternalSyntheticLambda0(this);
        this.mRingerModeTracker.getRingerMode().observeForever(phoneStatusBarPolicy$$ExternalSyntheticLambda0);
        this.mRingerModeTracker.getRingerModeInternal().observeForever(phoneStatusBarPolicy$$ExternalSyntheticLambda0);
        try {
            this.mIActivityManager.registerUserSwitchObserver(this.mUserSwitchListener, "PhoneStatusBarPolicy");
        } catch (RemoteException unused) {
        }
        updateTTY();
        updateBluetooth();
        this.mIconController.setIcon(this.mSlotAlarmClock, R$drawable.stat_sys_alarm, (CharSequence) null);
        this.mIconController.setIconVisibility(this.mSlotAlarmClock, false);
        this.mIconController.setIcon(this.mSlotZen, R$drawable.stat_sys_dnd, (CharSequence) null);
        this.mIconController.setIconVisibility(this.mSlotZen, false);
        this.mIconController.setIcon(this.mSlotVibrate, R$drawable.stat_sys_ringer_vibrate, this.mResources.getString(R$string.accessibility_ringer_vibrate));
        this.mIconController.setIconVisibility(this.mSlotVibrate, false);
        this.mIconController.setIcon(this.mSlotMute, R$drawable.stat_sys_ringer_silent, this.mResources.getString(R$string.accessibility_ringer_silent));
        this.mIconController.setIconVisibility(this.mSlotMute, false);
        updateVolumeZen();
        this.mIconController.setIcon(this.mSlotCast, R$drawable.stat_sys_cast, (CharSequence) null);
        this.mIconController.setIconVisibility(this.mSlotCast, false);
        this.mIconController.setIcon(this.mSlotHotspot, R$drawable.stat_sys_hotspot, this.mResources.getString(R$string.accessibility_status_bar_hotspot));
        this.mIconController.setIconVisibility(this.mSlotHotspot, this.mHotspot.isHotspotEnabled());
        this.mIconController.setIcon(this.mSlotManagedProfile, R$drawable.stat_sys_managed_profile_status, this.mResources.getString(R$string.accessibility_managed_profile));
        this.mIconController.setIconVisibility(this.mSlotManagedProfile, this.mManagedProfileIconVisible);
        this.mIconController.setIcon(this.mSlotDataSaver, R$drawable.stat_sys_data_saver, this.mResources.getString(R$string.accessibility_data_saver_on));
        this.mIconController.setIconVisibility(this.mSlotDataSaver, false);
        this.mIconController.setIcon(this.mSlotRotate, R$drawable.stat_sys_auto_rotate_portrait, this.mResources.getString(R$string.accessibility_rotation_lock_off));
        this.mIconController.setIconVisibility(this.mSlotRotate, false);
        Resources resources = this.mResources;
        PrivacyType privacyType = PrivacyType.TYPE_MICROPHONE;
        String string = resources.getString(privacyType.getNameId());
        Resources resources2 = this.mResources;
        int i = R$string.ongoing_privacy_chip_content_multiple_apps;
        this.mIconController.setIcon(this.mSlotMicrophone, privacyType.getIconId(), resources2.getString(i, new Object[]{string}));
        this.mIconController.setIconVisibility(this.mSlotMicrophone, false);
        Resources resources3 = this.mResources;
        PrivacyType privacyType2 = PrivacyType.TYPE_CAMERA;
        String string2 = resources3.getString(privacyType2.getNameId());
        this.mIconController.setIcon(this.mSlotCamera, privacyType2.getIconId(), this.mResources.getString(i, new Object[]{string2}));
        this.mIconController.setIconVisibility(this.mSlotCamera, false);
        this.mIconController.setIcon(this.mSlotLocation, LOCATION_STATUS_ICON_ID, this.mResources.getString(R$string.accessibility_location_active));
        this.mIconController.setIconVisibility(this.mSlotLocation, false);
        this.mIconController.setIcon(this.mSlotSensorsOff, R$drawable.stat_sys_sensors_off, this.mResources.getString(R$string.accessibility_sensors_off_active));
        this.mIconController.setIconVisibility(this.mSlotSensorsOff, this.mSensorPrivacyController.isSensorPrivacyEnabled());
        this.mIconController.setIcon(this.mSlotScreenRecord, R$drawable.stat_sys_screen_record, (CharSequence) null);
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, false);
        this.mIconController.setIcon(this.mSlotNfc, R$drawable.zz_moto_stat_sys_nfc, this.mResources.getString(R$string.quick_settings_nfc_on));
        this.mIconController.setIconVisibility(this.mSlotNfc, false);
        this.mRotationLockController.addCallback(this);
        this.mBluetooth.addCallback(this);
        this.mProvisionedController.addCallback(this);
        this.mZenController.addCallback(this);
        this.mCast.addCallback(this.mCastCallback);
        this.mHotspot.addCallback(this.mHotspotCallback);
        this.mNextAlarmController.addCallback(this.mNextAlarmCallback);
        this.mDataSaver.addCallback(this);
        this.mKeyguardStateController.addCallback(this);
        this.mPrivacyItemController.addCallback((PrivacyItemController.Callback) this);
        this.mSensorPrivacyController.addCallback(this.mSensorPrivacyListener);
        this.mLocationController.addCallback(this);
        this.mRecordingController.addCallback((RecordingController.RecordingStateChangeCallback) this);
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mNfcController.addCallback(this);
        this.mNfcController.registerNfcIconObserver(-2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0(Integer num) {
        this.mHandler.post(new PhoneStatusBarPolicy$$ExternalSyntheticLambda1(this));
    }

    public void onZenChanged(int i) {
        updateVolumeZen();
    }

    public void onConfigChanged(ZenModeConfig zenModeConfig) {
        updateVolumeZen();
    }

    /* access modifiers changed from: private */
    public void updateAlarm() {
        int i;
        AlarmManager.AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(-2);
        boolean z = true;
        boolean z2 = nextAlarmClock != null && nextAlarmClock.getTriggerTime() > 0;
        boolean z3 = this.mZenController.getZen() == 2;
        StatusBarIconController statusBarIconController = this.mIconController;
        String str = this.mSlotAlarmClock;
        if (z3) {
            i = R$drawable.stat_sys_alarm_dim;
        } else {
            i = R$drawable.stat_sys_alarm;
        }
        statusBarIconController.setIcon(str, i, buildAlarmContentDescription());
        StatusBarIconController statusBarIconController2 = this.mIconController;
        String str2 = this.mSlotAlarmClock;
        if (!this.mCurrentUserSetup || !z2) {
            z = false;
        }
        statusBarIconController2.setIconVisibility(str2, z);
    }

    private String buildAlarmContentDescription() {
        if (this.mNextAlarm == null) {
            return this.mResources.getString(R$string.status_bar_alarm);
        }
        return this.mResources.getString(R$string.accessibility_quick_settings_alarm, new Object[]{DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), this.mDateFormatUtil.is24HourFormat() ? "EHm" : "Ehma"), this.mNextAlarm.getTriggerTime()).toString()});
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0073, code lost:
        if (r0.intValue() == 0) goto L_0x0077;
     */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x009e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void updateVolumeZen() {
        /*
            r8 = this;
            com.android.systemui.statusbar.policy.ZenModeController r0 = r8.mZenController
            int r0 = r0.getZen()
            android.content.SharedPreferences r1 = r8.mSharedPreferences
            boolean r1 = com.android.systemui.p006qs.tiles.DndTile.isVisible(r1)
            r2 = 0
            r3 = 1
            if (r1 != 0) goto L_0x003c
            android.content.SharedPreferences r1 = r8.mSharedPreferences
            boolean r1 = com.android.systemui.p006qs.tiles.DndTile.isCombinedIcon(r1)
            if (r1 == 0) goto L_0x0019
            goto L_0x003c
        L_0x0019:
            r1 = 2
            if (r0 != r1) goto L_0x002a
            int r1 = com.android.systemui.R$drawable.stat_sys_dnd
            android.content.res.Resources r4 = r8.mResources
            int r5 = com.android.systemui.R$string.interruption_level_none
            java.lang.String r4 = r4.getString(r5)
        L_0x0026:
            r5 = r4
            r4 = r1
            r1 = r3
            goto L_0x004b
        L_0x002a:
            if (r0 != r3) goto L_0x0037
            int r1 = com.android.systemui.R$drawable.stat_sys_dnd
            android.content.res.Resources r4 = r8.mResources
            int r5 = com.android.systemui.R$string.interruption_level_priority
            java.lang.String r4 = r4.getString(r5)
            goto L_0x0026
        L_0x0037:
            r4 = 0
            r1 = r2
            r5 = r4
            r4 = r1
            goto L_0x004b
        L_0x003c:
            if (r0 == 0) goto L_0x0040
            r1 = r3
            goto L_0x0041
        L_0x0040:
            r1 = r2
        L_0x0041:
            int r4 = com.android.systemui.R$drawable.stat_sys_dnd
            android.content.res.Resources r5 = r8.mResources
            int r6 = com.android.systemui.R$string.quick_settings_dnd_label
            java.lang.String r5 = r5.getString(r6)
        L_0x004b:
            com.android.systemui.statusbar.policy.ZenModeController r6 = r8.mZenController
            android.app.NotificationManager$Policy r6 = r6.getConsolidatedPolicy()
            boolean r0 = android.service.notification.ZenModeConfig.isZenOverridingRinger(r0, r6)
            if (r0 != 0) goto L_0x0076
            com.android.systemui.util.RingerModeTracker r0 = r8.mRingerModeTracker
            androidx.lifecycle.LiveData r0 = r0.getRingerModeInternal()
            java.lang.Object r0 = r0.getValue()
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 == 0) goto L_0x0076
            int r6 = r0.intValue()
            if (r6 != r3) goto L_0x006f
            r7 = r3
            r3 = r2
            r2 = r7
            goto L_0x0077
        L_0x006f:
            int r0 = r0.intValue()
            if (r0 != 0) goto L_0x0076
            goto L_0x0077
        L_0x0076:
            r3 = r2
        L_0x0077:
            if (r1 == 0) goto L_0x0080
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r8.mIconController
            java.lang.String r6 = r8.mSlotZen
            r0.setIcon(r6, r4, r5)
        L_0x0080:
            boolean r0 = r8.mZenVisible
            if (r1 == r0) goto L_0x008d
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r8.mIconController
            java.lang.String r4 = r8.mSlotZen
            r0.setIconVisibility(r4, r1)
            r8.mZenVisible = r1
        L_0x008d:
            boolean r0 = r8.mVibrateVisible
            if (r2 == r0) goto L_0x009a
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r8.mIconController
            java.lang.String r1 = r8.mSlotVibrate
            r0.setIconVisibility(r1, r2)
            r8.mVibrateVisible = r2
        L_0x009a:
            boolean r0 = r8.mMuteVisible
            if (r3 == r0) goto L_0x00a7
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r8.mIconController
            java.lang.String r1 = r8.mSlotMute
            r0.setIconVisibility(r1, r3)
            r8.mMuteVisible = r3
        L_0x00a7:
            r8.updateAlarm()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.updateVolumeZen():void");
    }

    public void onBluetoothDevicesChanged() {
        updateBluetooth();
    }

    public void onBluetoothStateChange(boolean z) {
        updateBluetooth();
    }

    private final void updateBluetooth() {
        boolean z;
        int i = R$drawable.stat_sys_data_bluetooth_connected;
        String string = this.mResources.getString(R$string.accessibility_quick_settings_bluetooth_on);
        BluetoothController bluetoothController = this.mBluetooth;
        if (bluetoothController == null || !bluetoothController.isBluetoothConnected() || (!this.mBluetooth.isBluetoothAudioActive() && this.mBluetooth.isBluetoothAudioProfileOnly())) {
            z = false;
        } else {
            string = this.mResources.getString(R$string.accessibility_bluetooth_connected);
            z = this.mBluetooth.isBluetoothEnabled();
        }
        this.mIconController.setIcon(this.mSlotBluetooth, i, string);
        this.mIconController.setIconVisibility(this.mSlotBluetooth, z);
    }

    private final void updateTTY() {
        TelecomManager telecomManager = this.mTelecomManager;
        if (telecomManager == null) {
            updateTTY(0);
        } else {
            updateTTY(telecomManager.getCurrentTtyMode());
        }
    }

    /* access modifiers changed from: private */
    public final void updateTTY(int i) {
        boolean z = i != 0;
        boolean z2 = DEBUG;
        if (z2) {
            Log.v("PhoneStatusBarPolicy", "updateTTY: enabled: " + z);
        }
        if (z) {
            if (z2) {
                Log.v("PhoneStatusBarPolicy", "updateTTY: set TTY on");
            }
            this.mIconController.setIcon(this.mSlotTty, R$drawable.stat_sys_tty_mode, this.mResources.getString(R$string.accessibility_tty_enabled));
            this.mIconController.setIconVisibility(this.mSlotTty, true);
            return;
        }
        if (z2) {
            Log.v("PhoneStatusBarPolicy", "updateTTY: set TTY off");
        }
        this.mIconController.setIconVisibility(this.mSlotTty, false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0020 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:3:0x0011  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateCast() {
        /*
            r6 = this;
            com.android.systemui.statusbar.policy.CastController r0 = r6.mCast
            java.util.List r0 = r0.getCastDevices()
            java.util.Iterator r0 = r0.iterator()
        L_0x000a:
            boolean r1 = r0.hasNext()
            r2 = 1
            if (r1 == 0) goto L_0x0020
            java.lang.Object r1 = r0.next()
            com.android.systemui.statusbar.policy.CastController$CastDevice r1 = (com.android.systemui.statusbar.policy.CastController.CastDevice) r1
            int r1 = r1.state
            if (r1 == r2) goto L_0x001e
            r3 = 2
            if (r1 != r3) goto L_0x000a
        L_0x001e:
            r0 = r2
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            boolean r1 = DEBUG
            java.lang.String r3 = "PhoneStatusBarPolicy"
            if (r1 == 0) goto L_0x003b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "updateCast: isCasting: "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            android.util.Log.v(r3, r4)
        L_0x003b:
            android.os.Handler r4 = r6.mHandler
            java.lang.Runnable r5 = r6.mRemoveCastIconRunnable
            r4.removeCallbacks(r5)
            if (r0 == 0) goto L_0x0065
            com.android.systemui.screenrecord.RecordingController r0 = r6.mRecordingController
            boolean r0 = r0.isRecording()
            if (r0 != 0) goto L_0x0065
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r6.mIconController
            java.lang.String r1 = r6.mSlotCast
            int r3 = com.android.systemui.R$drawable.stat_sys_cast
            android.content.res.Resources r4 = r6.mResources
            int r5 = com.android.systemui.R$string.accessibility_casting
            java.lang.String r4 = r4.getString(r5)
            r0.setIcon(r1, r3, r4)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r6.mIconController
            java.lang.String r6 = r6.mSlotCast
            r0.setIconVisibility(r6, r2)
            goto L_0x0075
        L_0x0065:
            if (r1 == 0) goto L_0x006c
            java.lang.String r0 = "updateCast: hiding icon in 3 sec..."
            android.util.Log.v(r3, r0)
        L_0x006c:
            android.os.Handler r0 = r6.mHandler
            java.lang.Runnable r6 = r6.mRemoveCastIconRunnable
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.postDelayed(r6, r1)
        L_0x0075:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.updateCast():void");
    }

    /* access modifiers changed from: private */
    public void updateManagedProfile() {
        this.mUiBgExecutor.execute(new PhoneStatusBarPolicy$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateManagedProfile$2() {
        try {
            this.mHandler.post(new PhoneStatusBarPolicy$$ExternalSyntheticLambda7(this, this.mUserManager.isManagedProfile(ActivityTaskManager.getService().getLastResumedActivityUserId())));
        } catch (RemoteException e) {
            Log.w("PhoneStatusBarPolicy", "updateManagedProfile: ", e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateManagedProfile$1(boolean z) {
        boolean z2;
        if (!z || (this.mKeyguardStateController.isShowing() && !this.mKeyguardStateController.isOccluded())) {
            z2 = false;
        } else {
            z2 = true;
            this.mIconController.setIcon(this.mSlotManagedProfile, R$drawable.stat_sys_managed_profile_status, this.mResources.getString(R$string.accessibility_managed_profile));
        }
        if (this.mManagedProfileIconVisible != z2) {
            this.mIconController.setIconVisibility(this.mSlotManagedProfile, z2);
            this.mManagedProfileIconVisible = z2;
        }
    }

    public void appTransitionStarting(int i, long j, long j2, boolean z) {
        if (this.mDisplayId == i) {
            updateManagedProfile();
        }
    }

    public void onKeyguardShowingChanged() {
        updateManagedProfile();
    }

    public void onUserSetupChanged() {
        DeviceProvisionedController deviceProvisionedController = this.mProvisionedController;
        boolean isUserSetup = deviceProvisionedController.isUserSetup(deviceProvisionedController.getCurrentUser());
        if (this.mCurrentUserSetup != isUserSetup) {
            this.mCurrentUserSetup = isUserSetup;
            updateAlarm();
        }
    }

    public void onRotationLockStateChanged(boolean z, boolean z2) {
        boolean isCurrentOrientationLockPortrait = RotationLockTile.isCurrentOrientationLockPortrait(this.mRotationLockController, this.mResources);
        if (z) {
            if (isCurrentOrientationLockPortrait) {
                this.mIconController.setIcon(this.mSlotRotate, R$drawable.stat_sys_rotate_portrait, this.mResources.getString(R$string.accessibility_rotation_lock_on_portrait));
            } else {
                this.mIconController.setIcon(this.mSlotRotate, R$drawable.stat_sys_rotate_landscape, this.mResources.getString(R$string.accessibility_rotation_lock_on_landscape));
            }
            this.mIconController.setIconVisibility(this.mSlotRotate, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotRotate, false);
    }

    /* access modifiers changed from: private */
    public void updateHeadsetPlug(Intent intent) {
        int i;
        int i2;
        boolean z = intent.getIntExtra("state", 0) != 0;
        boolean z2 = intent.getIntExtra("microphone", 0) != 0;
        if (z) {
            Resources resources = this.mResources;
            if (z2) {
                i = R$string.accessibility_status_bar_headset;
            } else {
                i = R$string.accessibility_status_bar_headphones;
            }
            String string = resources.getString(i);
            StatusBarIconController statusBarIconController = this.mIconController;
            String str = this.mSlotHeadset;
            if (z2) {
                i2 = R$drawable.stat_sys_headset_mic;
            } else {
                i2 = R$drawable.stat_sys_headset;
            }
            statusBarIconController.setIcon(str, i2, string);
            this.mIconController.setIconVisibility(this.mSlotHeadset, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotHeadset, false);
    }

    public void onDataSaverChanged(boolean z) {
        this.mIconController.setIconVisibility(this.mSlotDataSaver, z);
    }

    public void onPrivacyItemsChanged(List<PrivacyItem> list) {
        updatePrivacyItems(list);
    }

    private void updatePrivacyItems(List<PrivacyItem> list) {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (PrivacyItem next : list) {
            if (next != null) {
                int i = C18908.$SwitchMap$com$android$systemui$privacy$PrivacyType[next.getPrivacyType().ordinal()];
                if (i == 1) {
                    z = true;
                } else if (i == 2) {
                    z3 = true;
                } else if (i == 3) {
                    z2 = true;
                }
            } else {
                Log.e("PhoneStatusBarPolicy", "updatePrivacyItems - null item found");
                StringWriter stringWriter = new StringWriter();
                this.mPrivacyItemController.dump((FileDescriptor) null, new PrintWriter(stringWriter), (String[]) null);
                throw new NullPointerException(stringWriter.toString());
            }
        }
        this.mPrivacyLogger.logStatusBarIconsVisible(z, z2, z3);
    }

    /* renamed from: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy$8 */
    static /* synthetic */ class C18908 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$privacy$PrivacyType;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.android.systemui.privacy.PrivacyType[] r0 = com.android.systemui.privacy.PrivacyType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$systemui$privacy$PrivacyType = r0
                com.android.systemui.privacy.PrivacyType r1 = com.android.systemui.privacy.PrivacyType.TYPE_CAMERA     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$systemui$privacy$PrivacyType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.privacy.PrivacyType r1 = com.android.systemui.privacy.PrivacyType.TYPE_LOCATION     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$systemui$privacy$PrivacyType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.privacy.PrivacyType r1 = com.android.systemui.privacy.PrivacyType.TYPE_MICROPHONE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.C18908.<clinit>():void");
        }
    }

    public void onLocationActiveChanged(boolean z) {
        if (!this.mPrivacyItemController.getLocationAvailable()) {
            updateLocationFromController();
        }
    }

    private void updateLocationFromController() {
        if (this.mLocationController.isLocationActive()) {
            this.mIconController.setIconVisibility(this.mSlotLocation, true);
        } else {
            this.mIconController.setIconVisibility(this.mSlotLocation, false);
        }
    }

    public void onCountdown(long j) {
        if (DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: countdown " + j);
        }
        int floorDiv = (int) Math.floorDiv(j + 500, 1000);
        int i = R$drawable.stat_sys_screen_record;
        String num = Integer.toString(floorDiv);
        if (floorDiv == 1) {
            i = R$drawable.stat_sys_screen_record_1;
        } else if (floorDiv == 2) {
            i = R$drawable.stat_sys_screen_record_2;
        } else if (floorDiv == 3) {
            i = R$drawable.stat_sys_screen_record_3;
        }
        this.mIconController.setIcon(this.mSlotScreenRecord, i, num);
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, true);
        this.mIconController.setIconAccessibilityLiveRegion(this.mSlotScreenRecord, 2);
    }

    public void onCountdownEnd() {
        if (DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: hiding icon during countdown");
        }
        this.mHandler.post(new PhoneStatusBarPolicy$$ExternalSyntheticLambda2(this));
        this.mHandler.post(new PhoneStatusBarPolicy$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCountdownEnd$3() {
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCountdownEnd$4() {
        this.mIconController.setIconAccessibilityLiveRegion(this.mSlotScreenRecord, 0);
    }

    public void onRecordingStart() {
        if (DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: showing icon");
        }
        this.mIconController.setIcon(this.mSlotScreenRecord, R$drawable.stat_sys_screen_record, this.mResources.getString(R$string.screenrecord_ongoing_screen_only));
        this.mHandler.post(new PhoneStatusBarPolicy$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRecordingStart$5() {
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, true);
    }

    public void onRecordingEnd() {
        if (DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: hiding icon");
        }
        this.mHandler.post(new PhoneStatusBarPolicy$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRecordingEnd$6() {
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, false);
    }

    public void onNfcIconVisibleChanged(boolean z) {
        updateNfc(z);
    }

    private final void updateNfc(boolean z) {
        this.mIconController.setIconVisibility(this.mSlotNfc, z);
    }
}
