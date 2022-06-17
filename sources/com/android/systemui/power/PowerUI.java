package com.android.systemui.power;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IThermalEventListener;
import android.os.IThermalService;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Temperature;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import android.widget.Toast;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$integer;
import com.android.systemui.R$string;
import com.android.systemui.SystemUI;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Future;
import motorola.core_services.power.MotoPowerManager;

public class PowerUI extends SystemUI implements CommandQueue.Callbacks {
    static final boolean DEBUG = Log.isLoggable("PowerUI", 3);
    private static final long SIX_HOURS_MILLIS = Duration.ofHours(6).toMillis();
    /* access modifiers changed from: private */
    public static boolean mShowMotoUsbHighTemperatureAlarm = false;
    /* access modifiers changed from: private */
    public ActivityManagerWrapper mActivityManagerWrapper;
    @VisibleForTesting
    int mBatteryLevel = 100;
    /* access modifiers changed from: private */
    public int mBatteryLevelWhenPlugged;
    @VisibleForTesting
    int mBatteryStatus = 1;
    /* access modifiers changed from: private */
    public final BroadcastDispatcher mBroadcastDispatcher;
    /* access modifiers changed from: private */
    public int mChargeRate = 0;
    private final CommandQueue mCommandQueue;
    @VisibleForTesting
    BatteryStateSnapshot mCurrentBatteryStateSnapshot;
    private boolean mEnableSkinTemperatureWarning;
    private boolean mEnableUsbTemperatureAlarm;
    private EnhancedEstimates mEnhancedEstimates;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private boolean mHasPowerSharingFeature;
    /* access modifiers changed from: private */
    public int mInvalidCharger = 0;
    @VisibleForTesting
    BatteryStateSnapshot mLastBatteryStateSnapshot;
    private final Configuration mLastConfiguration = new Configuration();
    private boolean mLastLpdState;
    /* access modifiers changed from: private */
    public Future mLastShowWarningTask;
    private boolean mLastVbusState;
    /* access modifiers changed from: private */
    public int mLowBatteryAlertCloseLevel;
    /* access modifiers changed from: private */
    public final int[] mLowBatteryReminderLevels = new int[2];
    @VisibleForTesting
    boolean mLowWarningShownThisChargeCycle;
    /* access modifiers changed from: private */
    public boolean mLpdState;
    /* access modifiers changed from: private */
    public MotoPowerManager mMotoPowerManager;
    private InattentiveSleepWarningView mOverlayView;
    /* access modifiers changed from: private */
    public int mPlugType = 0;
    /* access modifiers changed from: private */
    public PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public PowerSharingNotification mPowerSharingNotification;
    private Toast mPowerSharingToast;
    @VisibleForTesting
    final Receiver mReceiver = new Receiver();
    /* access modifiers changed from: private */
    public long mScreenOffTime = -1;
    @VisibleForTesting
    boolean mSevereWarningShownThisChargeCycle;
    private IThermalEventListener mSkinThermalEventListener;
    /* access modifiers changed from: private */
    public final Lazy<StatusBar> mStatusBarLazy;
    @VisibleForTesting
    IThermalService mThermalService;
    /* access modifiers changed from: private */
    public boolean mToastControl;
    private IThermalEventListener mUsbThermalEventListener;
    /* access modifiers changed from: private */
    public boolean mVbusState;
    /* access modifiers changed from: private */
    public WarningsUI mWarnings;
    private MotoPowerManager.WirelessPowerShareCallback mWirelessPowerShareCallback;
    /* access modifiers changed from: private */
    public boolean mWirelessPowerShareCondition;
    /* access modifiers changed from: private */
    public int mWirelessPowerShareState;

    public interface WarningsUI {
        void dismissFullyChargedPluggedWarning();

        void dismissHighTemperatureWarning();

        void dismissInvalidChargerWarning();

        void dismissLowBatteryWarning();

        void dump(PrintWriter printWriter);

        boolean isInvalidChargerWarningShowing();

        void localeChanged();

        void showChargerThermalWarning(boolean z);

        void showFullyChargedPluggedWarning();

        void showHighTemperatureWarning();

        void showInvalidChargerWarning();

        void showLowBatteryWarning(boolean z);

        void showMotoUsbHighTemperatureAlarm(float f);

        boolean showMotoUsbLpdAlarm(boolean z, boolean z2);

        void showThermalShutdownWarning();

        void showTurboPowerToast(boolean z);

        void showUsbHighTemperatureAlarm();

        void showWirelessChargerToast();

        void update(int i, int i2, long j);

        void updateLowBatteryWarning();

        void updateSnapshot(BatteryStateSnapshot batteryStateSnapshot);

        void userSwitched();
    }

    public PowerUI(Context context, BroadcastDispatcher broadcastDispatcher, CommandQueue commandQueue, Lazy<StatusBar> lazy) {
        super(context);
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mCommandQueue = commandQueue;
        this.mStatusBarLazy = lazy;
    }

    public void start() {
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mMotoPowerManager = MotoPowerManager.getInstance(this.mContext);
        this.mScreenOffTime = this.mPowerManager.isScreenOn() ? -1 : SystemClock.elapsedRealtime();
        this.mWarnings = (WarningsUI) Dependency.get(WarningsUI.class);
        this.mEnhancedEstimates = (EnhancedEstimates) Dependency.get(EnhancedEstimates.class);
        this.mLastConfiguration.setTo(this.mContext.getResources().getConfiguration());
        this.mActivityManagerWrapper = ActivityManagerWrapper.getInstance();
        registerPowerSharingControl();
        mShowMotoUsbHighTemperatureAlarm = this.mContext.getResources().getBoolean(R$bool.zz_moto_config_show_high_temperature_alarm);
        C11371 r0 = new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                PowerUI.this.updateBatteryWarningLevels();
            }
        };
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), false, r0, -1);
        updateBatteryWarningLevels();
        this.mReceiver.init();
        showWarnOnThermalShutdown();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("show_temperature_warning"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                PowerUI.this.doSkinThermalEventListenerRegistration();
            }
        });
        contentResolver.registerContentObserver(Settings.Global.getUriFor("show_usb_temperature_alarm"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                PowerUI.this.doUsbThermalEventListenerRegistration();
            }
        });
        initThermalEventListeners();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
    }

    private void registerPowerSharingControl() {
        boolean hasSystemFeature = this.mContext.getPackageManager().hasSystemFeature("com.motorola.hardware.wireless_power_share");
        this.mHasPowerSharingFeature = hasSystemFeature;
        if (hasSystemFeature) {
            this.mPowerSharingNotification = new PowerSharingNotification(this.mContext);
            C11404 r0 = new MotoPowerManager.WirelessPowerShareCallback() {
                public void onStateChanged(int i) {
                    if (PowerUI.DEBUG) {
                        Slog.d("PowerUI", "WirelessPowerShareEdgeLights: onStateChanged: state=" + i);
                    }
                    int unused = PowerUI.this.mWirelessPowerShareState = i;
                    boolean unused2 = PowerUI.this.mWirelessPowerShareCondition = i == 1;
                    Resources resources = PowerUI.this.mContext.getResources();
                    if (i == 0 && PowerUI.this.mToastControl) {
                        PowerUI.this.showPowerSharingToast(resources.getString(R$string.wireless_power_sharing_off));
                        boolean unused3 = PowerUI.this.mToastControl = false;
                    }
                    if (PowerUI.this.mWirelessPowerShareCondition && !PowerUI.this.mToastControl) {
                        PowerUI.this.showPowerSharingToast(resources.getString(R$string.wireless_power_sharing_on));
                        boolean unused4 = PowerUI.this.mToastControl = true;
                    }
                }

                public void onAvailable() {
                    if (PowerUI.DEBUG) {
                        Slog.d("PowerUI", "WirelessPowerShareEdgeLights: onAvailable called, removing Notification");
                    }
                    PowerUI.this.mPowerSharingNotification.cancel();
                }

                public void onUnavailable() {
                    if (PowerUI.this.mWirelessPowerShareState != 0) {
                        int lastWirelessPowerShareUnavailReason = PowerUI.this.mMotoPowerManager.getLastWirelessPowerShareUnavailReason();
                        if (PowerUI.DEBUG) {
                            Slog.d("PowerUI", "WirelessPowerShare: onUnavailable : " + lastWirelessPowerShareUnavailReason);
                        }
                        PowerUI.this.mPowerSharingNotification.notify(lastWirelessPowerShareUnavailReason);
                    }
                }

                public void onError(int i) {
                    if (PowerUI.DEBUG) {
                        Slog.d("PowerUI", "onError errorCode=" + i);
                    }
                }
            };
            this.mWirelessPowerShareCallback = r0;
            this.mMotoPowerManager.registerWirelessPowerShareCallback(r0, new Handler(Looper.getMainLooper()));
        }
    }

    /* access modifiers changed from: private */
    public void showPowerSharingToast(String str) {
        Toast toast = this.mPowerSharingToast;
        if (toast != null) {
            toast.cancel();
        }
        Toast makeText = Toast.makeText(this.mContext, str, 1);
        this.mPowerSharingToast = makeText;
        makeText.show();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        if ((this.mLastConfiguration.updateFrom(configuration) & 3) != 0) {
            this.mHandler.post(new PowerUI$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: package-private */
    public void updateBatteryWarningLevels() {
        int integer = this.mContext.getResources().getInteger(17694766);
        int integer2 = this.mContext.getResources().getInteger(17694862);
        if (integer2 < integer) {
            integer2 = integer;
        }
        int[] iArr = this.mLowBatteryReminderLevels;
        iArr[0] = integer2;
        iArr[1] = integer;
        this.mLowBatteryAlertCloseLevel = iArr[0] + this.mContext.getResources().getInteger(17694861);
    }

    /* access modifiers changed from: private */
    public int findBatteryLevelBucket(int i) {
        if (i >= this.mLowBatteryAlertCloseLevel) {
            return 1;
        }
        int[] iArr = this.mLowBatteryReminderLevels;
        if (i > iArr[0]) {
            return 0;
        }
        for (int length = iArr.length - 1; length >= 0; length--) {
            if (i <= this.mLowBatteryReminderLevels[length]) {
                return -1 - length;
            }
        }
        throw new RuntimeException("not possible!");
    }

    @VisibleForTesting
    final class Receiver extends BroadcastReceiver {
        private boolean mHasReceivedBattery = false;

        Receiver() {
        }

        public void init() {
            Intent registerReceiver;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
            PowerUI.this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, PowerUI.this.mHandler);
            if (!this.mHasReceivedBattery && (registerReceiver = PowerUI.this.mContext.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"))) != null) {
                onReceive(PowerUI.this.mContext, registerReceiver);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:56:0x028c  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x02b3  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r19, android.content.Intent r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r20
                java.lang.String r2 = r20.getAction()
                java.lang.String r3 = "android.os.action.POWER_SAVE_MODE_CHANGED"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x001a
                com.android.systemui.power.PowerUI$Receiver$$ExternalSyntheticLambda0 r1 = new com.android.systemui.power.PowerUI$Receiver$$ExternalSyntheticLambda0
                r1.<init>(r0)
                com.android.settingslib.utils.ThreadUtils.postOnBackgroundThread(r1)
                goto L_0x03b5
            L_0x001a:
                java.lang.String r3 = "android.intent.action.BATTERY_CHANGED"
                boolean r3 = r3.equals(r2)
                java.lang.String r4 = "PowerUI"
                if (r3 == 0) goto L_0x035a
                r2 = 1
                r0.mHasReceivedBattery = r2
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                int r5 = r3.mBatteryLevel
                java.lang.String r6 = "level"
                r7 = 100
                int r6 = r1.getIntExtra(r6, r7)
                r3.mBatteryLevel = r6
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                int r6 = r3.mBatteryStatus
                java.lang.String r8 = "status"
                int r8 = r1.getIntExtra(r8, r2)
                r3.mBatteryStatus = r8
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                int r3 = r3.mPlugType
                com.android.systemui.power.PowerUI r8 = com.android.systemui.power.PowerUI.this
                com.android.systemui.shared.system.ActivityManagerWrapper r8 = r8.mActivityManagerWrapper
                int r8 = r8.getCurrentUserId()
                java.lang.String r8 = java.lang.String.valueOf(r8)
                com.android.systemui.power.PowerUI r9 = com.android.systemui.power.PowerUI.this
                java.lang.String r10 = "plugged"
                int r10 = r1.getIntExtra(r10, r2)
                int unused = r9.mPlugType = r10
                com.android.systemui.power.PowerUI r9 = com.android.systemui.power.PowerUI.this
                int r9 = r9.mInvalidCharger
                com.android.systemui.power.PowerUI r10 = com.android.systemui.power.PowerUI.this
                java.lang.String r11 = "invalid_charger"
                r12 = 0
                int r11 = r1.getIntExtra(r11, r12)
                int unused = r10.mInvalidCharger = r11
                com.android.systemui.power.PowerUI r10 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.BatteryStateSnapshot r11 = r10.mCurrentBatteryStateSnapshot
                r10.mLastBatteryStateSnapshot = r11
                int r10 = r10.mPlugType
                if (r10 == 0) goto L_0x0081
                r10 = r2
                goto L_0x0082
            L_0x0081:
                r10 = r12
            L_0x0082:
                if (r3 == 0) goto L_0x0086
                r11 = r2
                goto L_0x0087
            L_0x0086:
                r11 = r12
            L_0x0087:
                com.android.systemui.power.PowerUI r13 = com.android.systemui.power.PowerUI.this
                int r13 = r13.findBatteryLevelBucket(r5)
                com.android.systemui.power.PowerUI r14 = com.android.systemui.power.PowerUI.this
                int r15 = r14.mBatteryLevel
                int r14 = r14.findBatteryLevelBucket(r15)
                if (r10 == 0) goto L_0x00a0
                if (r11 != 0) goto L_0x00a0
                com.android.systemui.power.PowerUI r15 = com.android.systemui.power.PowerUI.this
                int r7 = r15.mBatteryLevel
                int unused = r15.mBatteryLevelWhenPlugged = r7
            L_0x00a0:
                com.android.systemui.power.PowerUI r7 = com.android.systemui.power.PowerUI.this
                int r7 = r7.mChargeRate
                com.android.systemui.power.PowerUI r15 = com.android.systemui.power.PowerUI.this
                java.lang.String r2 = "charge_rate"
                int r2 = r1.getIntExtra(r2, r12)
                int unused = r15.mChargeRate = r2
                boolean r2 = com.android.systemui.power.PowerUI.DEBUG
                if (r2 == 0) goto L_0x01c6
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                java.lang.String r12 = "buckets   ....."
                r15.append(r12)
                com.android.systemui.power.PowerUI r12 = com.android.systemui.power.PowerUI.this
                int r12 = r12.mLowBatteryAlertCloseLevel
                r15.append(r12)
                java.lang.String r12 = " .. "
                r15.append(r12)
                r17 = r8
                com.android.systemui.power.PowerUI r8 = com.android.systemui.power.PowerUI.this
                int[] r8 = r8.mLowBatteryReminderLevels
                r16 = 0
                r8 = r8[r16]
                r15.append(r8)
                r15.append(r12)
                com.android.systemui.power.PowerUI r8 = com.android.systemui.power.PowerUI.this
                int[] r8 = r8.mLowBatteryReminderLevels
                r12 = 1
                r8 = r8[r12]
                r15.append(r8)
                java.lang.String r8 = r15.toString()
                android.util.Slog.d(r4, r8)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r12 = "level          "
                r8.append(r12)
                r8.append(r5)
                java.lang.String r12 = " --> "
                r8.append(r12)
                com.android.systemui.power.PowerUI r15 = com.android.systemui.power.PowerUI.this
                int r15 = r15.mBatteryLevel
                r8.append(r15)
                java.lang.String r8 = r8.toString()
                android.util.Slog.d(r4, r8)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r15 = "status         "
                r8.append(r15)
                r8.append(r6)
                r8.append(r12)
                com.android.systemui.power.PowerUI r6 = com.android.systemui.power.PowerUI.this
                int r6 = r6.mBatteryStatus
                r8.append(r6)
                java.lang.String r6 = r8.toString()
                android.util.Slog.d(r4, r6)
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r8 = "plugType       "
                r6.append(r8)
                r6.append(r3)
                r6.append(r12)
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                int r3 = r3.mPlugType
                r6.append(r3)
                java.lang.String r3 = r6.toString()
                android.util.Slog.d(r4, r3)
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r6 = "invalidCharger "
                r3.append(r6)
                r3.append(r9)
                r3.append(r12)
                com.android.systemui.power.PowerUI r6 = com.android.systemui.power.PowerUI.this
                int r6 = r6.mInvalidCharger
                r3.append(r6)
                java.lang.String r3 = r3.toString()
                android.util.Slog.d(r4, r3)
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r6 = "bucket         "
                r3.append(r6)
                r3.append(r13)
                r3.append(r12)
                r3.append(r14)
                java.lang.String r3 = r3.toString()
                android.util.Slog.d(r4, r3)
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r6 = "plugged        "
                r3.append(r6)
                r3.append(r11)
                r3.append(r12)
                r3.append(r10)
                java.lang.String r3 = r3.toString()
                android.util.Slog.d(r4, r3)
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r6 = "chargeRate     "
                r3.append(r6)
                r3.append(r7)
                r3.append(r12)
                com.android.systemui.power.PowerUI r6 = com.android.systemui.power.PowerUI.this
                int r6 = r6.mChargeRate
                r3.append(r6)
                java.lang.String r3 = r3.toString()
                android.util.Slog.d(r4, r3)
                goto L_0x01c8
            L_0x01c6:
                r17 = r8
            L_0x01c8:
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r3 = r3.mWarnings
                com.android.systemui.power.PowerUI r6 = com.android.systemui.power.PowerUI.this
                int r8 = r6.mBatteryLevel
                long r12 = r6.mScreenOffTime
                r3.update(r8, r14, r12)
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                java.lang.String r6 = "lpd_present_state"
                r8 = 0
                boolean r6 = r1.getBooleanExtra(r6, r8)
                boolean unused = r3.mLpdState = r6
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                java.lang.String r6 = "vbus_present_state"
                boolean r1 = r1.getBooleanExtra(r6, r8)
                boolean unused = r3.mVbusState = r1
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                boolean r1 = r1.isLpdOrVbusStateChanged()
                if (r1 == 0) goto L_0x0212
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                com.android.systemui.power.PowerUI r3 = com.android.systemui.power.PowerUI.this
                boolean r3 = r3.mLpdState
                com.android.systemui.power.PowerUI r6 = com.android.systemui.power.PowerUI.this
                boolean r6 = r6.mVbusState
                boolean r1 = r1.showMotoUsbLpdAlarm(r3, r6)
                if (r1 == 0) goto L_0x0212
                return
            L_0x0212:
                if (r9 != 0) goto L_0x022b
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                int r1 = r1.mInvalidCharger
                if (r1 == 0) goto L_0x022b
                java.lang.String r1 = "showing invalid charger warning"
                android.util.Slog.d(r4, r1)
                com.android.systemui.power.PowerUI r0 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r0 = r0.mWarnings
                r0.showInvalidChargerWarning()
                return
            L_0x022b:
                if (r9 == 0) goto L_0x023f
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                int r1 = r1.mInvalidCharger
                if (r1 != 0) goto L_0x023f
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                r1.dismissInvalidChargerWarning()
                goto L_0x0253
            L_0x023f:
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                boolean r1 = r1.isInvalidChargerWarningShowing()
                if (r1 == 0) goto L_0x0253
                if (r2 == 0) goto L_0x0252
                java.lang.String r0 = "Bad Charger"
                android.util.Slog.d(r4, r0)
            L_0x0252:
                return
            L_0x0253:
                if (r10 == 0) goto L_0x025f
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                int r1 = r1.mBatteryLevel
                r3 = 100
                if (r1 != r3) goto L_0x0261
                r1 = 1
                goto L_0x0262
            L_0x025f:
                r3 = 100
            L_0x0261:
                r1 = 0
            L_0x0262:
                if (r11 == 0) goto L_0x0268
                if (r5 != r3) goto L_0x0268
                r3 = 1
                goto L_0x0269
            L_0x0268:
                r3 = 0
            L_0x0269:
                if (r1 != 0) goto L_0x0277
                if (r3 == 0) goto L_0x0277
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                r1.dismissFullyChargedPluggedWarning()
                goto L_0x0284
            L_0x0277:
                if (r1 == 0) goto L_0x0284
                if (r3 != 0) goto L_0x0284
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                r1.showFullyChargedPluggedWarning()
            L_0x0284:
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                java.util.concurrent.Future r1 = r1.mLastShowWarningTask
                if (r1 == 0) goto L_0x029d
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                java.util.concurrent.Future r1 = r1.mLastShowWarningTask
                r3 = 1
                r1.cancel(r3)
                if (r2 == 0) goto L_0x029d
                java.lang.String r1 = "cancelled task"
                android.util.Slog.d(r4, r1)
            L_0x029d:
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$Receiver$$ExternalSyntheticLambda1 r2 = new com.android.systemui.power.PowerUI$Receiver$$ExternalSyntheticLambda1
                r2.<init>(r0, r10, r14, r5)
                java.util.concurrent.Future r2 = com.android.settingslib.utils.ThreadUtils.postOnBackgroundThread(r2)
                java.util.concurrent.Future unused = r1.mLastShowWarningTask = r2
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                int r1 = r1.mChargeRate
                if (r1 == r7) goto L_0x0339
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                int r1 = r1.mChargeRate
                boolean r1 = com.android.systemui.power.MotoPowerUtil.isTurboPowerCharge(r1)
                java.lang.String r2 = "TurboChargerAlertShowed"
                if (r1 == 0) goto L_0x02fb
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                android.content.Context r1 = r1.mContext
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r2)
                r5 = r17
                r3.append(r5)
                java.lang.String r2 = r3.toString()
                r3 = 0
                boolean r1 = com.android.systemui.Prefs.getBoolean(r1, r2, r3)
                if (r1 != 0) goto L_0x02f0
                java.lang.String r1 = "ro.vendor.build.motfactory"
                int r1 = android.os.SystemProperties.getInt(r1, r3)
                r2 = 1
                if (r1 == r2) goto L_0x02f1
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                r1.showChargerThermalWarning(r2)
                goto L_0x0339
            L_0x02f0:
                r2 = 1
            L_0x02f1:
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                r1.showTurboPowerToast(r2)
                goto L_0x0339
            L_0x02fb:
                r5 = r17
                boolean r1 = com.android.systemui.power.MotoPowerUtil.isTurboPowerCharge(r7)
                if (r1 == 0) goto L_0x0339
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                android.content.Context r1 = r1.mContext
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r2)
                r3.append(r5)
                java.lang.String r2 = r3.toString()
                r3 = 0
                boolean r1 = com.android.systemui.Prefs.getBoolean(r1, r2, r3)
                if (r1 != 0) goto L_0x0326
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                r1.showChargerThermalWarning(r3)
            L_0x0326:
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                int r2 = r1.mBatteryLevel
                int r1 = r1.mBatteryLevelWhenPlugged
                if (r2 <= r1) goto L_0x0339
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r1 = r1.mWarnings
                r1.showTurboPowerToast(r3)
            L_0x0339:
                if (r10 == 0) goto L_0x03b5
                if (r11 != 0) goto L_0x03b5
                com.android.systemui.power.PowerUI r1 = com.android.systemui.power.PowerUI.this
                int r2 = r1.mBatteryStatus
                r3 = 2
                if (r2 != r3) goto L_0x03b5
                int r1 = r1.mPlugType
                r2 = 4
                if (r1 != r2) goto L_0x03b5
                java.lang.String r1 = "showing wireless charger toast"
                android.util.Slog.d(r4, r1)
                com.android.systemui.power.PowerUI r0 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r0 = r0.mWarnings
                r0.showWirelessChargerToast()
                goto L_0x03b5
            L_0x035a:
                java.lang.String r3 = "android.intent.action.SCREEN_OFF"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x036c
                com.android.systemui.power.PowerUI r0 = com.android.systemui.power.PowerUI.this
                long r1 = android.os.SystemClock.elapsedRealtime()
                long unused = r0.mScreenOffTime = r1
                goto L_0x03b5
            L_0x036c:
                java.lang.String r3 = "android.intent.action.SCREEN_ON"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x037c
                com.android.systemui.power.PowerUI r0 = com.android.systemui.power.PowerUI.this
                r1 = -1
                long unused = r0.mScreenOffTime = r1
                goto L_0x03b5
            L_0x037c:
                java.lang.String r3 = "android.intent.action.USER_SWITCHED"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x038e
                com.android.systemui.power.PowerUI r0 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r0 = r0.mWarnings
                r0.userSwitched()
                goto L_0x03b5
            L_0x038e:
                java.lang.String r3 = "android.intent.action.LOCALE_CHANGED"
                boolean r2 = r3.equals(r2)
                if (r2 == 0) goto L_0x03a0
                com.android.systemui.power.PowerUI r0 = com.android.systemui.power.PowerUI.this
                com.android.systemui.power.PowerUI$WarningsUI r0 = r0.mWarnings
                r0.localeChanged()
                goto L_0x03b5
            L_0x03a0:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r2 = "unknown intent: "
                r0.append(r2)
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                android.util.Slog.w(r4, r0)
            L_0x03b5:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.power.PowerUI.Receiver.onReceive(android.content.Context, android.content.Intent):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0() {
            if (PowerUI.this.mPowerManager.isPowerSaveMode()) {
                PowerUI.this.mWarnings.dismissLowBatteryWarning();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$1(boolean z, int i, int i2) {
            PowerUI powerUI = PowerUI.this;
            powerUI.maybeShowBatteryWarningV2(z, i, i2 != powerUI.mBatteryLevel);
        }
    }

    /* access modifiers changed from: protected */
    public void maybeShowBatteryWarningV2(boolean z, int i, boolean z2) {
        boolean isHybridNotificationEnabled = this.mEnhancedEstimates.isHybridNotificationEnabled();
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        boolean z3 = DEBUG;
        if (z3) {
            Slog.d("PowerUI", "evaluating which notification to show");
        }
        if (isHybridNotificationEnabled) {
            if (z3) {
                Slog.d("PowerUI", "using hybrid");
            }
            Estimate refreshEstimateIfNeeded = refreshEstimateIfNeeded();
            int i2 = this.mBatteryLevel;
            int i3 = this.mBatteryStatus;
            int[] iArr = this.mLowBatteryReminderLevels;
            this.mCurrentBatteryStateSnapshot = new BatteryStateSnapshot(i2, isPowerSaveMode, z, i, i3, iArr[1], iArr[0], refreshEstimateIfNeeded.getEstimateMillis(), refreshEstimateIfNeeded.getAverageDischargeTime(), this.mEnhancedEstimates.getSevereWarningThreshold(), this.mEnhancedEstimates.getLowWarningThreshold(), refreshEstimateIfNeeded.isBasedOnUsage(), this.mEnhancedEstimates.getLowWarningEnabled());
        } else {
            if (z3) {
                Slog.d("PowerUI", "using standard");
            }
            int i4 = this.mBatteryLevel;
            int i5 = this.mBatteryStatus;
            int[] iArr2 = this.mLowBatteryReminderLevels;
            this.mCurrentBatteryStateSnapshot = new BatteryStateSnapshot(i4, isPowerSaveMode, z, i, i5, iArr2[1], iArr2[0]);
        }
        this.mWarnings.updateSnapshot(this.mCurrentBatteryStateSnapshot);
        if (this.mCurrentBatteryStateSnapshot.isHybrid()) {
            maybeShowHybridWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot);
        } else {
            maybeShowBatteryWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot, z2);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Estimate refreshEstimateIfNeeded() {
        BatteryStateSnapshot batteryStateSnapshot = this.mLastBatteryStateSnapshot;
        if (batteryStateSnapshot != null && batteryStateSnapshot.getTimeRemainingMillis() != -1 && this.mBatteryLevel == this.mLastBatteryStateSnapshot.getBatteryLevel()) {
            return new Estimate(this.mLastBatteryStateSnapshot.getTimeRemainingMillis(), this.mLastBatteryStateSnapshot.isBasedOnUsage(), this.mLastBatteryStateSnapshot.getAverageTimeToDischargeMillis());
        }
        Estimate estimate = this.mEnhancedEstimates.getEstimate();
        if (DEBUG) {
            Slog.d("PowerUI", "updated estimate: " + estimate.getEstimateMillis());
        }
        return estimate;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void maybeShowHybridWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2) {
        long timeRemainingMillis = batteryStateSnapshot.getTimeRemainingMillis();
        boolean z = false;
        if (batteryStateSnapshot.getBatteryLevel() >= 45 && (timeRemainingMillis > SIX_HOURS_MILLIS || timeRemainingMillis == -1)) {
            this.mLowWarningShownThisChargeCycle = false;
            this.mSevereWarningShownThisChargeCycle = false;
            if (DEBUG) {
                Slog.d("PowerUI", "Charge cycle reset! Can show warnings again");
            }
        }
        if (batteryStateSnapshot.getBucket() != batteryStateSnapshot2.getBucket() || batteryStateSnapshot2.getPlugged()) {
            z = true;
        }
        if (shouldShowHybridWarning(batteryStateSnapshot)) {
            this.mWarnings.showLowBatteryWarning(z);
            if ((timeRemainingMillis == -1 || timeRemainingMillis > batteryStateSnapshot.getSevereThresholdMillis()) && batteryStateSnapshot.getBatteryLevel() > batteryStateSnapshot.getSevereLevelThreshold()) {
                Slog.d("PowerUI", "Low warning marked as shown this cycle");
                this.mLowWarningShownThisChargeCycle = true;
                return;
            }
            this.mSevereWarningShownThisChargeCycle = true;
            this.mLowWarningShownThisChargeCycle = true;
            if (DEBUG) {
                Slog.d("PowerUI", "Severe warning marked as shown this cycle");
            }
        } else if (shouldDismissHybridWarning(batteryStateSnapshot)) {
            if (DEBUG) {
                Slog.d("PowerUI", "Dismissing warning");
            }
            this.mWarnings.dismissLowBatteryWarning();
        } else {
            if (DEBUG) {
                Slog.d("PowerUI", "Updating warning");
            }
            this.mWarnings.updateLowBatteryWarning();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldShowHybridWarning(BatteryStateSnapshot batteryStateSnapshot) {
        boolean z = false;
        boolean z2 = true;
        if (batteryStateSnapshot.getPlugged() || batteryStateSnapshot.getBatteryStatus() == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't show warning due to - plugged: ");
            sb.append(batteryStateSnapshot.getPlugged());
            sb.append(" status unknown: ");
            if (batteryStateSnapshot.getBatteryStatus() != 1) {
                z2 = false;
            }
            sb.append(z2);
            Slog.d("PowerUI", sb.toString());
            return false;
        }
        long timeRemainingMillis = batteryStateSnapshot.getTimeRemainingMillis();
        boolean z3 = batteryStateSnapshot.isLowWarningEnabled() && !this.mLowWarningShownThisChargeCycle && !batteryStateSnapshot.isPowerSaver() && ((timeRemainingMillis != -1 && timeRemainingMillis < batteryStateSnapshot.getLowThresholdMillis()) || batteryStateSnapshot.getBatteryLevel() <= batteryStateSnapshot.getLowLevelThreshold());
        boolean z4 = !this.mSevereWarningShownThisChargeCycle && ((timeRemainingMillis != -1 && timeRemainingMillis < batteryStateSnapshot.getSevereThresholdMillis()) || batteryStateSnapshot.getBatteryLevel() <= batteryStateSnapshot.getSevereLevelThreshold());
        if (z3 || z4) {
            z = true;
        }
        if (DEBUG) {
            Slog.d("PowerUI", "Enhanced trigger is: " + z + "\nwith battery snapshot: mLowWarningShownThisChargeCycle: " + this.mLowWarningShownThisChargeCycle + " mSevereWarningShownThisChargeCycle: " + this.mSevereWarningShownThisChargeCycle + "\n" + batteryStateSnapshot.toString());
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldDismissHybridWarning(BatteryStateSnapshot batteryStateSnapshot) {
        return batteryStateSnapshot.getPlugged() || batteryStateSnapshot.getTimeRemainingMillis() > batteryStateSnapshot.getLowThresholdMillis();
    }

    /* access modifiers changed from: protected */
    public void maybeShowBatteryWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2, boolean z) {
        boolean z2 = batteryStateSnapshot.getBucket() != batteryStateSnapshot2.getBucket() || batteryStateSnapshot2.getPlugged();
        if (shouldShowLowBatteryWarning(batteryStateSnapshot, batteryStateSnapshot2)) {
            this.mWarnings.showLowBatteryWarning(z2);
        } else if (shouldDismissLowBatteryWarning(batteryStateSnapshot, batteryStateSnapshot2)) {
            this.mWarnings.dismissLowBatteryWarning();
        } else if (z) {
            this.mWarnings.updateLowBatteryWarning();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldShowLowBatteryWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2) {
        if (batteryStateSnapshot.getPlugged() || batteryStateSnapshot.isPowerSaver() || ((batteryStateSnapshot.getBucket() >= batteryStateSnapshot2.getBucket() && !batteryStateSnapshot2.getPlugged()) || batteryStateSnapshot.getBucket() >= 0 || batteryStateSnapshot.getBatteryStatus() == 1)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean shouldDismissLowBatteryWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2) {
        return batteryStateSnapshot.isPowerSaver() || batteryStateSnapshot.getPlugged() || (batteryStateSnapshot.getBucket() > batteryStateSnapshot2.getBucket() && batteryStateSnapshot.getBucket() > 0);
    }

    /* access modifiers changed from: private */
    public void initThermalEventListeners() {
        doSkinThermalEventListenerRegistration();
        doUsbThermalEventListenerRegistration();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public synchronized void doSkinThermalEventListenerRegistration() {
        boolean z;
        boolean z2 = this.mEnableSkinTemperatureWarning;
        boolean z3 = true;
        boolean z4 = Settings.Global.getInt(this.mContext.getContentResolver(), "show_temperature_warning", this.mContext.getResources().getInteger(R$integer.config_showTemperatureWarning)) != 0;
        this.mEnableSkinTemperatureWarning = z4;
        if (z4 != z2) {
            try {
                if (this.mSkinThermalEventListener == null) {
                    this.mSkinThermalEventListener = new SkinThermalEventListener();
                }
                if (this.mThermalService == null) {
                    this.mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
                }
                if (this.mEnableSkinTemperatureWarning) {
                    z = this.mThermalService.registerThermalEventListenerWithType(this.mSkinThermalEventListener, 3);
                } else {
                    z = this.mThermalService.unregisterThermalEventListener(this.mSkinThermalEventListener);
                }
            } catch (RemoteException e) {
                Slog.e("PowerUI", "Exception while (un)registering skin thermal event listener.", e);
                z = false;
            }
            if (!z) {
                if (this.mEnableSkinTemperatureWarning) {
                    z3 = false;
                }
                this.mEnableSkinTemperatureWarning = z3;
                Slog.e("PowerUI", "Failed to register or unregister skin thermal event listener.");
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public synchronized void doUsbThermalEventListenerRegistration() {
        boolean z;
        boolean z2 = this.mEnableUsbTemperatureAlarm;
        boolean z3 = true;
        boolean z4 = false;
        boolean z5 = Settings.Global.getInt(this.mContext.getContentResolver(), "show_usb_temperature_alarm", this.mContext.getResources().getInteger(R$integer.config_showUsbPortAlarm)) != 0;
        this.mEnableUsbTemperatureAlarm = z5;
        if (z5 != z2) {
            try {
                if (this.mUsbThermalEventListener == null) {
                    this.mUsbThermalEventListener = new UsbThermalEventListener();
                }
                if (this.mThermalService == null) {
                    this.mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
                }
                if (this.mEnableUsbTemperatureAlarm) {
                    z = this.mThermalService.registerThermalEventListenerWithType(this.mUsbThermalEventListener, 4);
                } else {
                    z = this.mThermalService.unregisterThermalEventListener(this.mUsbThermalEventListener);
                }
            } catch (RemoteException e) {
                Slog.e("PowerUI", "Exception while (un)registering usb thermal event listener.", e);
                z = false;
            }
            if (!z) {
                if (this.mEnableUsbTemperatureAlarm) {
                    z3 = false;
                }
                this.mEnableUsbTemperatureAlarm = z3;
                Slog.e("PowerUI", "Failed to register or unregister usb thermal event listener.");
            }
            z4 = z;
        }
        if (DEBUG) {
            Slog.d("PowerUI", "UsbThermalEventListener register state: mEnableUsbTemperatureAlarm " + this.mEnableUsbTemperatureAlarm + " ret: " + z4);
        }
    }

    private void showWarnOnThermalShutdown() {
        int i = -1;
        int i2 = this.mContext.getSharedPreferences("powerui_prefs", 0).getInt("boot_count", -1);
        try {
            i = Settings.Global.getInt(this.mContext.getContentResolver(), "boot_count");
        } catch (Settings.SettingNotFoundException unused) {
            Slog.e("PowerUI", "Failed to read system boot count from Settings.Global.BOOT_COUNT");
        }
        if (i > i2) {
            this.mContext.getSharedPreferences("powerui_prefs", 0).edit().putInt("boot_count", i).apply();
            if (this.mPowerManager.getLastShutdownReason() == 4) {
                this.mWarnings.showThermalShutdownWarning();
            }
        }
    }

    public void showInattentiveSleepWarning() {
        if (this.mOverlayView == null) {
            this.mOverlayView = new InattentiveSleepWarningView(this.mContext);
        }
        this.mOverlayView.show();
    }

    public void dismissInattentiveSleepWarning(boolean z) {
        InattentiveSleepWarningView inattentiveSleepWarningView = this.mOverlayView;
        if (inattentiveSleepWarningView != null) {
            inattentiveSleepWarningView.dismiss(z);
        }
    }

    /* access modifiers changed from: private */
    public boolean isLpdOrVbusStateChanged() {
        boolean z;
        boolean z2 = this.mLastLpdState;
        boolean z3 = this.mLpdState;
        if (z2 != z3) {
            this.mLastLpdState = z3;
            z = true;
        } else {
            z = false;
        }
        boolean z4 = this.mLastVbusState;
        boolean z5 = this.mVbusState;
        if (z4 == z5) {
            return z;
        }
        this.mLastVbusState = z5;
        return true;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("mLowBatteryAlertCloseLevel=");
        printWriter.println(this.mLowBatteryAlertCloseLevel);
        printWriter.print("mLowBatteryReminderLevels=");
        printWriter.println(Arrays.toString(this.mLowBatteryReminderLevels));
        printWriter.print("mBatteryLevel=");
        printWriter.println(Integer.toString(this.mBatteryLevel));
        printWriter.print("mBatteryStatus=");
        printWriter.println(Integer.toString(this.mBatteryStatus));
        printWriter.print("mPlugType=");
        printWriter.println(Integer.toString(this.mPlugType));
        printWriter.print("mInvalidCharger=");
        printWriter.println(Integer.toString(this.mInvalidCharger));
        printWriter.print("mScreenOffTime=");
        printWriter.print(this.mScreenOffTime);
        if (this.mScreenOffTime >= 0) {
            printWriter.print(" (");
            printWriter.print(SystemClock.elapsedRealtime() - this.mScreenOffTime);
            printWriter.print(" ago)");
        }
        printWriter.println();
        printWriter.print("soundTimeout=");
        printWriter.println(Settings.Global.getInt(this.mContext.getContentResolver(), "low_battery_sound_timeout", 0));
        printWriter.print("bucket: ");
        printWriter.println(Integer.toString(findBatteryLevelBucket(this.mBatteryLevel)));
        printWriter.print("mEnableSkinTemperatureWarning=");
        printWriter.println(this.mEnableSkinTemperatureWarning);
        printWriter.print("mEnableUsbTemperatureAlarm=");
        printWriter.println(this.mEnableUsbTemperatureAlarm);
        this.mWarnings.dump(printWriter);
    }

    @VisibleForTesting
    final class SkinThermalEventListener extends IThermalEventListener.Stub {
        SkinThermalEventListener() {
        }

        public void notifyThrottling(Temperature temperature) {
            int status = temperature.getStatus();
            if (status < 5) {
                PowerUI.this.mWarnings.dismissHighTemperatureWarning();
            } else if (!((StatusBar) PowerUI.this.mStatusBarLazy.get()).isDeviceInVrMode()) {
                PowerUI.this.mWarnings.showHighTemperatureWarning();
                Slog.d("PowerUI", "SkinThermalEventListener: notifyThrottling was called , current skin status = " + status + ", temperature = " + temperature.getValue());
            }
        }
    }

    @VisibleForTesting
    final class UsbThermalEventListener extends IThermalEventListener.Stub {
        UsbThermalEventListener() {
        }

        public void notifyThrottling(Temperature temperature) {
            int status = temperature.getStatus();
            if (status >= 5) {
                PowerUI.this.mWarnings.showUsbHighTemperatureAlarm();
            } else if (status == 2 && PowerUI.mShowMotoUsbHighTemperatureAlarm) {
                PowerUI.this.mWarnings.showMotoUsbHighTemperatureAlarm(temperature.getValue());
            }
            Slog.d("PowerUI", "UsbThermalEventListener: notifyThrottling was called , current usb port status = " + status + ", temperature = " + temperature.getValue());
        }
    }
}
