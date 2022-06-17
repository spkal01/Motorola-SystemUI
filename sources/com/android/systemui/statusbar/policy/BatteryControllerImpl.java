package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.demomode.DemoMode;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.statusbar.policy.BatteryController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatteryControllerImpl extends BroadcastReceiver implements BatteryController {
    private static final boolean DEBUG = Log.isLoggable("BatteryController", 3);
    private boolean mAodPowerSave;
    private final Handler mBgHandler;
    protected final BroadcastDispatcher mBroadcastDispatcher;
    protected final ArrayList<BatteryController.BatteryStateChangeCallback> mChangeCallbacks = new ArrayList<>();
    private boolean mCharged;
    protected boolean mCharging;
    protected boolean mChargingDisabledByAdaptive;
    protected final Context mContext;
    private final DemoModeController mDemoModeController;
    private Estimate mEstimate;
    private final EnhancedEstimates mEstimates;
    private final ArrayList<BatteryController.EstimateFetchCompletion> mFetchCallbacks = new ArrayList<>();
    private boolean mFetchingEstimate = false;
    @VisibleForTesting
    boolean mHasReceivedBattery = false;
    protected int mLevel;
    /* access modifiers changed from: private */
    public final Handler mMainHandler;
    protected boolean mPluggedIn;
    private boolean mPluggedInWireless;
    private final PowerManager mPowerManager;
    protected boolean mPowerSave;
    private boolean mStateUnknown = false;
    /* access modifiers changed from: private */
    public boolean mTestMode = false;
    private boolean mWirelessCharging;

    @VisibleForTesting
    public BatteryControllerImpl(Context context, EnhancedEstimates enhancedEstimates, PowerManager powerManager, BroadcastDispatcher broadcastDispatcher, DemoModeController demoModeController, Handler handler, Handler handler2) {
        this.mContext = context;
        this.mMainHandler = handler;
        this.mBgHandler = handler2;
        this.mPowerManager = powerManager;
        this.mEstimates = enhancedEstimates;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mDemoModeController = demoModeController;
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        Intent registerReceiver = this.mContext.registerReceiver(this, intentFilter);
        if (registerReceiver != null) {
            onReceive(this.mContext, registerReceiver);
        } else {
            Log.w("BatteryController", "Battery service not ready");
        }
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter2.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter2.addAction("com.android.systemui.BATTERY_LEVEL_TEST");
        this.mBroadcastDispatcher.registerReceiver(this, intentFilter2);
    }

    public void init() {
        Intent registerReceiver;
        registerReceiver();
        if (!this.mHasReceivedBattery && (registerReceiver = this.mContext.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"))) != null && !this.mHasReceivedBattery) {
            onReceive(this.mContext, registerReceiver);
        }
        this.mDemoModeController.addCallback((DemoMode) this);
        updatePowerSave();
        updateEstimate();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("BatteryController state:");
        printWriter.print("  mLevel=");
        printWriter.println(this.mLevel);
        printWriter.print("  mPluggedIn=");
        printWriter.println(this.mPluggedIn);
        printWriter.print("  mCharging=");
        printWriter.println(this.mCharging);
        printWriter.print("  mCharged=");
        printWriter.println(this.mCharged);
        printWriter.print("  mPowerSave=");
        printWriter.println(this.mPowerSave);
        printWriter.print("  mStateUnknown=");
        printWriter.println(this.mStateUnknown);
    }

    public void setPowerSaveMode(boolean z) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, z, true);
    }

    public void addCallback(BatteryController.BatteryStateChangeCallback batteryStateChangeCallback) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.add(batteryStateChangeCallback);
        }
        if (this.mHasReceivedBattery) {
            batteryStateChangeCallback.onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            batteryStateChangeCallback.onPowerSaveChanged(this.mPowerSave);
            batteryStateChangeCallback.onBatteryUnknownStateChanged(this.mStateUnknown);
            batteryStateChangeCallback.onWirelessChargingChanged(this.mWirelessCharging);
            batteryStateChangeCallback.onAdaptiveChargingChanged(this.mChargingDisabledByAdaptive);
        }
    }

    public void removeCallback(BatteryController.BatteryStateChangeCallback batteryStateChangeCallback) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.remove(batteryStateChangeCallback);
        }
    }

    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.BATTERY_CHANGED")) {
            if (!this.mTestMode || intent.getBooleanExtra("testmode", false)) {
                this.mHasReceivedBattery = true;
                this.mLevel = (int) ((((float) intent.getIntExtra("level", 0)) * 100.0f) / ((float) intent.getIntExtra("scale", 100)));
                this.mPluggedIn = intent.getIntExtra("plugged", 0) != 0;
                this.mPluggedInWireless = intent.getIntExtra("plugged", 0) == 4;
                int intExtra = intent.getIntExtra("status", 1);
                boolean z = intExtra == 5;
                this.mCharged = z;
                boolean z2 = z || intExtra == 2;
                this.mCharging = z2;
                if (this.mWirelessCharging != (z2 && intent.getIntExtra("plugged", 0) == 4)) {
                    this.mWirelessCharging = !this.mWirelessCharging;
                    fireWirelessChargingChanged();
                }
                boolean z3 = !intent.getBooleanExtra("present", true);
                if (z3 != this.mStateUnknown) {
                    this.mStateUnknown = z3;
                    fireBatteryUnknownStateChanged();
                }
                this.mChargingDisabledByAdaptive = intent.getBooleanExtra("disabled_by_adaptive_charging", false);
                fireBatteryLevelChanged();
                fireAdaptiveChargingChanged();
                if (DEBUG) {
                    Log.d("BatteryController", "Receiced Broadcast ACTION_BATTERY_CHANGED: mLevel: " + this.mLevel + " mPluggedIn: " + this.mPluggedIn + " status: " + intExtra + " mChargingDisabledByAdaptive: " + this.mChargingDisabledByAdaptive);
                }
            }
        } else if (action.equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
            this.mChargingDisabledByAdaptive = intent.getBooleanExtra("disabled_by_adaptive_charging", false);
            fireAdaptiveChargingChanged();
        } else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
            updatePowerSave();
        } else if (action.equals("com.android.systemui.BATTERY_LEVEL_TEST")) {
            this.mTestMode = true;
            this.mMainHandler.post(new Runnable() {
                int mCurrentLevel = 0;
                int mIncrement = 1;
                int mSavedLevel;
                boolean mSavedPluggedIn;
                Intent mTestIntent;

                {
                    this.mSavedLevel = BatteryControllerImpl.this.mLevel;
                    this.mSavedPluggedIn = BatteryControllerImpl.this.mPluggedIn;
                    this.mTestIntent = new Intent("android.intent.action.BATTERY_CHANGED");
                }

                public void run() {
                    int i = this.mCurrentLevel;
                    int i2 = 0;
                    if (i < 0) {
                        boolean unused = BatteryControllerImpl.this.mTestMode = false;
                        this.mTestIntent.putExtra("level", this.mSavedLevel);
                        this.mTestIntent.putExtra("plugged", this.mSavedPluggedIn);
                        this.mTestIntent.putExtra("testmode", false);
                    } else {
                        this.mTestIntent.putExtra("level", i);
                        Intent intent = this.mTestIntent;
                        if (this.mIncrement > 0) {
                            i2 = 1;
                        }
                        intent.putExtra("plugged", i2);
                        this.mTestIntent.putExtra("testmode", true);
                    }
                    context.sendBroadcast(this.mTestIntent);
                    if (BatteryControllerImpl.this.mTestMode) {
                        int i3 = this.mCurrentLevel;
                        int i4 = this.mIncrement;
                        int i5 = i3 + i4;
                        this.mCurrentLevel = i5;
                        if (i5 == 100) {
                            this.mIncrement = i4 * -1;
                        }
                        BatteryControllerImpl.this.mMainHandler.postDelayed(this, 200);
                    }
                }
            });
        }
    }

    private void fireWirelessChargingChanged() {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.forEach(new BatteryControllerImpl$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fireWirelessChargingChanged$0(BatteryController.BatteryStateChangeCallback batteryStateChangeCallback) {
        batteryStateChangeCallback.onWirelessChargingChanged(this.mWirelessCharging);
    }

    public boolean isPluggedIn() {
        return this.mPluggedIn;
    }

    public boolean isPowerSave() {
        return this.mPowerSave;
    }

    public boolean isAodPowerSave() {
        return this.mAodPowerSave;
    }

    public boolean isWirelessCharging() {
        return this.mWirelessCharging;
    }

    public boolean isPluggedInWireless() {
        return this.mPluggedInWireless;
    }

    public void getEstimatedTimeRemainingString(BatteryController.EstimateFetchCompletion estimateFetchCompletion) {
        synchronized (this.mFetchCallbacks) {
            this.mFetchCallbacks.add(estimateFetchCompletion);
        }
        updateEstimateInBackground();
    }

    private String generateTimeRemainingString() {
        synchronized (this.mFetchCallbacks) {
            Estimate estimate = this.mEstimate;
            if (estimate == null) {
                return null;
            }
            String batteryRemainingShortStringFormatted = PowerUtil.getBatteryRemainingShortStringFormatted(this.mContext, estimate.getEstimateMillis());
            return batteryRemainingShortStringFormatted;
        }
    }

    private void updateEstimateInBackground() {
        if (!this.mFetchingEstimate) {
            this.mFetchingEstimate = true;
            this.mBgHandler.post(new BatteryControllerImpl$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEstimateInBackground$1() {
        synchronized (this.mFetchCallbacks) {
            this.mEstimate = null;
            if (this.mEstimates.isHybridNotificationEnabled()) {
                updateEstimate();
            }
        }
        this.mFetchingEstimate = false;
        this.mMainHandler.post(new BatteryControllerImpl$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public void notifyEstimateFetchCallbacks() {
        synchronized (this.mFetchCallbacks) {
            String generateTimeRemainingString = generateTimeRemainingString();
            Iterator<BatteryController.EstimateFetchCompletion> it = this.mFetchCallbacks.iterator();
            while (it.hasNext()) {
                it.next().onBatteryRemainingEstimateRetrieved(generateTimeRemainingString);
            }
            this.mFetchCallbacks.clear();
        }
    }

    private void updateEstimate() {
        Estimate cachedEstimateIfAvailable = Estimate.getCachedEstimateIfAvailable(this.mContext);
        this.mEstimate = cachedEstimateIfAvailable;
        if (cachedEstimateIfAvailable == null) {
            Estimate estimate = this.mEstimates.getEstimate();
            this.mEstimate = estimate;
            if (estimate != null) {
                Estimate.storeCachedEstimate(this.mContext, estimate);
            }
        }
    }

    private void updatePowerSave() {
        setPowerSave(this.mPowerManager.isPowerSaveMode());
    }

    private void setPowerSave(boolean z) {
        if (z != this.mPowerSave) {
            this.mPowerSave = z;
            this.mAodPowerSave = this.mPowerManager.getPowerSaveState(14).batterySaverEnabled;
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Power save is ");
                sb.append(this.mPowerSave ? "on" : "off");
                Log.d("BatteryController", sb.toString());
            }
            firePowerSaveChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void fireBatteryLevelChanged() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                this.mChangeCallbacks.get(i).onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            }
        }
    }

    private void fireBatteryUnknownStateChanged() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                this.mChangeCallbacks.get(i).onBatteryUnknownStateChanged(this.mStateUnknown);
            }
        }
    }

    private void firePowerSaveChanged() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                this.mChangeCallbacks.get(i).onPowerSaveChanged(this.mPowerSave);
            }
        }
    }

    private void fireAdaptiveChargingChanged() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                this.mChangeCallbacks.get(i).onAdaptiveChargingChanged(this.mChargingDisabledByAdaptive);
            }
        }
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        if (this.mDemoModeController.isInDemoMode()) {
            String string = bundle.getString("level");
            String string2 = bundle.getString("plugged");
            String string3 = bundle.getString("powersave");
            String string4 = bundle.getString("present");
            if (string != null) {
                this.mLevel = Math.min(Math.max(Integer.parseInt(string), 0), 100);
            }
            if (string2 != null) {
                this.mPluggedIn = Boolean.parseBoolean(string2);
            }
            if (string3 != null) {
                this.mPowerSave = string3.equals("true");
                firePowerSaveChanged();
            }
            if (string4 != null) {
                this.mStateUnknown = !string4.equals("true");
                fireBatteryUnknownStateChanged();
            }
            fireBatteryLevelChanged();
        }
    }

    public List<String> demoCommands() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("battery");
        return arrayList;
    }

    public void onDemoModeStarted() {
        this.mBroadcastDispatcher.unregisterReceiver(this);
    }

    public void onDemoModeFinished() {
        registerReceiver();
        updatePowerSave();
    }
}
