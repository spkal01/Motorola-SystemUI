package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.IActivityManager;
import android.app.IStopUserCallback;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.ViewClippingUtil;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.Utils;
import com.android.settingslib.fuelgauge.BatteryStatus;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$color;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dock.DockManager;
import com.android.systemui.keyguard.KeyguardIndication;
import com.android.systemui.keyguard.KeyguardIndicationRotateTextViewController;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.MotoPowerUtil;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;

public class KeyguardIndicationController {
    private String mAlignmentIndication;
    /* access modifiers changed from: private */
    public final IBatteryStats mBatteryInfo;
    /* access modifiers changed from: private */
    public int mBatteryLevel;
    /* access modifiers changed from: private */
    public boolean mBatteryOverheated;
    /* access modifiers changed from: private */
    public boolean mBatteryPresent = true;
    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                int unused = KeyguardIndicationController.this.mChargeRate = intent.getIntExtra("charge_rate", 0);
            }
            KeyguardIndicationController.this.mHandler.post(new KeyguardIndicationController$5$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
    };
    protected ColorStateList mBouncerTextColorState;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private BroadcastReceiver mBroadcastReceiver;
    /* access modifiers changed from: private */
    public int mChargeRate = 0;
    /* access modifiers changed from: private */
    public int mChargingSpeed;
    /* access modifiers changed from: private */
    public long mChargingTimeRemaining;
    /* access modifiers changed from: private */
    public boolean mChargingTimeRemainingDisabled;
    /* access modifiers changed from: private */
    public int mChargingWattage;
    /* access modifiers changed from: private */
    public final ViewClippingUtil.ClippingParameters mClippingParams = new ViewClippingUtil.ClippingParameters() {
        public boolean shouldFinish(View view) {
            return view == KeyguardIndicationController.this.mIndicationArea;
        }
    };
    /* access modifiers changed from: private */
    public final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private final DockManager mDockManager;
    /* access modifiers changed from: private */
    public boolean mDozing;
    /* access modifiers changed from: private */
    public boolean mEnableBatteryDefender;
    private final DelayableExecutor mExecutor;
    private final FalsingManager mFalsingManager;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                KeyguardIndicationController.this.hideTransientIndication();
            } else if (i == 2) {
                KeyguardIndicationController.this.showActionToUnlock();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mHideTransientMessageOnScreenOff;
    private final IActivityManager mIActivityManager;
    /* access modifiers changed from: private */
    public ViewGroup mIndicationArea;
    private boolean mInited;
    protected ColorStateList mInitialTextColorState;
    /* access modifiers changed from: private */
    public final KeyguardBypassController mKeyguardBypassController;
    private KeyguardStateController.Callback mKeyguardStateCallback = new KeyguardStateController.Callback() {
        public void onUnlockedChanged() {
            KeyguardIndicationController.this.updateIndication(false);
        }

        public void onKeyguardShowingChanged() {
            if (!KeyguardIndicationController.this.mKeyguardStateController.isShowing()) {
                KeyguardIndicationController.this.mTopIndicationView.clearMessages();
                KeyguardIndicationController.this.mLockScreenIndicationView.clearMessages();
            }
        }
    };
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public KeyguardIndicationTextView mLockScreenIndicationView;
    protected int mLockScreenMode;
    /* access modifiers changed from: private */
    public String mMessageToShowOnScreenOn;
    /* access modifiers changed from: private */
    public boolean mPowerCharged;
    /* access modifiers changed from: private */
    public boolean mPowerPluggedIn;
    /* access modifiers changed from: private */
    public boolean mPowerPluggedInWired;
    private String mRestingIndication;
    protected KeyguardIndicationRotateTextViewController mRotateTextViewController;
    /* access modifiers changed from: private */
    public StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarStateController mStatusBarStateController;
    private StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() {
        public void onStateChanged(int i) {
            KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            keyguardIndicationController.setVisible(z);
        }

        public void onDozingChanged(boolean z) {
            if (KeyguardIndicationController.this.mDozing != z) {
                boolean unused = KeyguardIndicationController.this.mDozing = z;
                if (KeyguardIndicationController.this.mHideTransientMessageOnScreenOff && KeyguardIndicationController.this.mDozing) {
                    KeyguardIndicationController.this.hideTransientIndication();
                }
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
    };
    private final KeyguardUpdateMonitorCallback mTickReceiver = new KeyguardUpdateMonitorCallback() {
        public void onTimeChanged() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }
    };
    /* access modifiers changed from: private */
    public KeyguardIndicationTextView mTopIndicationView;
    private CharSequence mTransientIndication;
    private KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    private final UserManager mUserManager;
    /* access modifiers changed from: private */
    public boolean mVisible;
    private final SettableWakeLock mWakeLock;

    private String getTrustManagedIndication() {
        return null;
    }

    public KeyguardIndicationController(Context context, WakeLock.Builder builder, KeyguardStateController keyguardStateController, StatusBarStateController statusBarStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, DockManager dockManager, BroadcastDispatcher broadcastDispatcher, DevicePolicyManager devicePolicyManager, IBatteryStats iBatteryStats, UserManager userManager, DelayableExecutor delayableExecutor, FalsingManager falsingManager, LockPatternUtils lockPatternUtils, IActivityManager iActivityManager, KeyguardBypassController keyguardBypassController) {
        this.mContext = context;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mDevicePolicyManager = devicePolicyManager;
        this.mKeyguardStateController = keyguardStateController;
        this.mStatusBarStateController = statusBarStateController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mDockManager = dockManager;
        WakeLock.Builder builder2 = builder;
        this.mWakeLock = new SettableWakeLock(builder.setTag("Doze:KeyguardIndication").build(), "KeyguardIndication");
        this.mBatteryInfo = iBatteryStats;
        this.mUserManager = userManager;
        this.mExecutor = delayableExecutor;
        this.mLockPatternUtils = lockPatternUtils;
        this.mIActivityManager = iActivityManager;
        this.mFalsingManager = falsingManager;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mChargingTimeRemainingDisabled = context.getResources().getBoolean(R$bool.zz_moto_config_disable_charge_time_remaining);
    }

    public void init() {
        if (!this.mInited) {
            this.mInited = true;
            this.mDockManager.addAlignmentStateListener(new KeyguardIndicationController$$ExternalSyntheticLambda1(this));
            this.mKeyguardUpdateMonitor.registerCallback(getKeyguardCallback());
            this.mKeyguardUpdateMonitor.registerCallback(this.mTickReceiver);
            this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
            this.mKeyguardStateController.addCallback(this.mKeyguardStateCallback);
            this.mStatusBarStateListener.onDozingChanged(this.mStatusBarStateController.isDozing());
            ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(new ConfigurationController.ConfigurationListener() {
                public void onLocaleListChanged() {
                    KeyguardIndicationController.this.updateDisclosure();
                }
            });
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            this.mContext.registerReceiverAsUser(this.mBatteryReceiver, UserHandle.SYSTEM, intentFilter, (String) null, (Handler) Dependency.get(Dependency.TIME_TICK_HANDLER));
        }
    }

    public void setIndicationArea(ViewGroup viewGroup) {
        this.mIndicationArea = viewGroup;
        this.mTopIndicationView = (KeyguardIndicationTextView) viewGroup.findViewById(R$id.keyguard_indication_text);
        this.mLockScreenIndicationView = (KeyguardIndicationTextView) viewGroup.findViewById(R$id.keyguard_indication_text_bottom);
        KeyguardIndicationTextView keyguardIndicationTextView = this.mTopIndicationView;
        this.mInitialTextColorState = keyguardIndicationTextView != null ? keyguardIndicationTextView.getTextColors() : ColorStateList.valueOf(-1);
        this.mBouncerTextColorState = ColorStateList.valueOf(Utils.getColorAttrDefaultColor(this.mContext, 16842806));
        this.mRotateTextViewController = new KeyguardIndicationRotateTextViewController(this.mLockScreenIndicationView, this.mExecutor, this.mStatusBarStateController);
        updateIndication(false);
        updateDisclosure();
        if (this.mBroadcastReceiver == null) {
            this.mBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    KeyguardIndicationController.this.updateDisclosure();
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
            intentFilter.addAction("android.intent.action.USER_REMOVED");
            this.mBroadcastDispatcher.registerReceiver(this.mBroadcastReceiver, intentFilter);
        }
    }

    /* access modifiers changed from: protected */
    public KeyguardUpdateMonitorCallback getKeyguardCallback() {
        if (this.mUpdateMonitorCallback == null) {
            this.mUpdateMonitorCallback = new BaseKeyguardCallback();
        }
        return this.mUpdateMonitorCallback;
    }

    private void updateIndications(boolean z, int i) {
        updateOwnerInfo();
        updateBattery(z);
        updateUserLocked(i);
        updateTransient();
        updateTrust(i, getTrustGrantedIndication(), getTrustManagedIndication());
        updateAlignment();
        updateLogoutView();
        updateResting();
    }

    /* access modifiers changed from: private */
    public void updateDisclosure() {
        if (((Boolean) DejankUtils.whitelistIpcs(new KeyguardIndicationController$$ExternalSyntheticLambda2(this))).booleanValue()) {
            this.mRotateTextViewController.updateIndication(1, new KeyguardIndication.Builder().setMessage(getDisclosureText(getOrganizationOwnedDeviceOrganizationName())).setTextColor(this.mInitialTextColorState).build(), false);
        } else {
            this.mRotateTextViewController.hideIndication(1);
        }
        updateResting();
    }

    private CharSequence getDisclosureText(CharSequence charSequence) {
        Resources resources = this.mContext.getResources();
        if (charSequence == null) {
            return resources.getText(R$string.do_disclosure_generic);
        }
        if (this.mDevicePolicyManager.isDeviceManaged()) {
            DevicePolicyManager devicePolicyManager = this.mDevicePolicyManager;
            if (devicePolicyManager.getDeviceOwnerType(devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == 1) {
                return resources.getString(R$string.do_financed_disclosure_with_name, new Object[]{charSequence});
            }
        }
        return resources.getString(R$string.do_disclosure_with_name, new Object[]{charSequence});
    }

    private void updateOwnerInfo() {
        String deviceOwnerInfo = this.mLockPatternUtils.getDeviceOwnerInfo();
        if (deviceOwnerInfo == null && this.mLockPatternUtils.isOwnerInfoEnabled(KeyguardUpdateMonitor.getCurrentUser())) {
            deviceOwnerInfo = this.mLockPatternUtils.getOwnerInfo(KeyguardUpdateMonitor.getCurrentUser());
        }
        if (deviceOwnerInfo != null) {
            this.mRotateTextViewController.updateIndication(0, new KeyguardIndication.Builder().setMessage(deviceOwnerInfo).setTextColor(this.mInitialTextColorState).build(), false);
        } else {
            this.mRotateTextViewController.hideIndication(0);
        }
    }

    private void updateBattery(boolean z) {
        if (this.mPowerPluggedIn || this.mEnableBatteryDefender) {
            this.mRotateTextViewController.updateIndication(3, new KeyguardIndication.Builder().setMessage(computePowerIndication()).setTextColor(this.mInitialTextColorState).build(), z);
        } else if (MotoPowerUtil.isTurboPowerCharge(this.mChargeRate)) {
            this.mRotateTextViewController.updateIndication(3, new KeyguardIndication.Builder().setMessage(this.mContext.getResources().getString(R$string.kg_turbo_power)).setTextColor(this.mInitialTextColorState).build(), z);
        } else {
            this.mRotateTextViewController.hideIndication(3);
        }
    }

    private void updateUserLocked(int i) {
        if (!this.mKeyguardUpdateMonitor.isUserUnlocked(i)) {
            this.mRotateTextViewController.updateIndication(8, new KeyguardIndication.Builder().setMessage(this.mContext.getResources().getText(17040590)).setTextColor(this.mInitialTextColorState).build(), false);
        } else {
            this.mRotateTextViewController.hideIndication(8);
        }
    }

    private void updateTransient() {
        if (!TextUtils.isEmpty(this.mTransientIndication)) {
            this.mRotateTextViewController.showTransient(this.mTransientIndication);
        } else {
            this.mRotateTextViewController.hideTransient();
        }
    }

    private void updateTrust(int i, CharSequence charSequence, CharSequence charSequence2) {
        if (!TextUtils.isEmpty(charSequence) && this.mKeyguardUpdateMonitor.getUserHasTrust(i)) {
            this.mRotateTextViewController.updateIndication(6, new KeyguardIndication.Builder().setMessage(charSequence).setTextColor(this.mInitialTextColorState).build(), false);
        } else if (TextUtils.isEmpty(charSequence2) || !this.mKeyguardUpdateMonitor.getUserTrustIsManaged(i) || this.mKeyguardUpdateMonitor.getUserHasTrust(i)) {
            this.mRotateTextViewController.hideIndication(6);
        } else {
            this.mRotateTextViewController.updateIndication(6, new KeyguardIndication.Builder().setMessage(charSequence2).setTextColor(this.mInitialTextColorState).build(), false);
        }
    }

    private void updateAlignment() {
        if (!TextUtils.isEmpty(this.mAlignmentIndication)) {
            this.mRotateTextViewController.updateIndication(4, new KeyguardIndication.Builder().setMessage(this.mAlignmentIndication).setTextColor(ColorStateList.valueOf(this.mContext.getColor(R$color.misalignment_text_color))).build(), true);
        } else {
            this.mRotateTextViewController.hideIndication(4);
        }
    }

    private void updateResting() {
        if (this.mRestingIndication == null || this.mRotateTextViewController.hasIndications()) {
            this.mRotateTextViewController.hideIndication(7);
        } else {
            this.mRotateTextViewController.updateIndication(7, new KeyguardIndication.Builder().setMessage(this.mRestingIndication).setTextColor(this.mInitialTextColorState).build(), false);
        }
    }

    private void updateLogoutView() {
        if (this.mKeyguardUpdateMonitor.isLogoutEnabled() && KeyguardUpdateMonitor.getCurrentUser() != 0) {
            this.mRotateTextViewController.updateIndication(2, new KeyguardIndication.Builder().setMessage(this.mContext.getResources().getString(17040356)).setTextColor(this.mInitialTextColorState).setBackground(this.mContext.getDrawable(R$drawable.logout_button_background)).setClickListener(new KeyguardIndicationController$$ExternalSyntheticLambda0(this)).build(), false);
        } else {
            this.mRotateTextViewController.hideIndication(2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateLogoutView$2(View view) {
        if (!this.mFalsingManager.isFalseTap(1)) {
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            try {
                this.mIActivityManager.switchUser(0);
                this.mIActivityManager.stopUser(currentUser, true, (IStopUserCallback) null);
            } catch (RemoteException e) {
                Log.e("KeyguardIndication", "Failed to logout user", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isOrganizationOwnedDevice() {
        return this.mDevicePolicyManager.isDeviceManaged() || this.mDevicePolicyManager.isOrganizationOwnedDeviceWithManagedProfile();
    }

    private CharSequence getOrganizationOwnedDeviceOrganizationName() {
        if (this.mDevicePolicyManager.isDeviceManaged()) {
            return this.mDevicePolicyManager.getDeviceOwnerOrganizationName();
        }
        if (this.mDevicePolicyManager.isOrganizationOwnedDeviceWithManagedProfile()) {
            return getWorkProfileOrganizationName();
        }
        return null;
    }

    private CharSequence getWorkProfileOrganizationName() {
        int workProfileUserId = getWorkProfileUserId(UserHandle.myUserId());
        if (workProfileUserId == -10000) {
            return null;
        }
        return this.mDevicePolicyManager.getOrganizationNameForUser(workProfileUserId);
    }

    private int getWorkProfileUserId(int i) {
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            if (userInfo.isManagedProfile()) {
                return userInfo.id;
            }
        }
        return -10000;
    }

    public void setVisible(boolean z) {
        this.mVisible = z;
        this.mIndicationArea.setVisibility(z ? 0 : 8);
        if (z) {
            if (!this.mHandler.hasMessages(1)) {
                hideTransientIndication();
            }
            updateIndication(false);
        } else if (!z) {
            hideTransientIndication();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public String getTrustGrantedIndication() {
        return this.mContext.getString(R$string.keyguard_indication_trust_unlocked);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setPowerPluggedIn(boolean z) {
        this.mPowerPluggedIn = z;
    }

    public void hideTransientIndicationDelayed(long j) {
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1), j);
    }

    public void showTransientIndication(int i) {
        showTransientIndication((CharSequence) this.mContext.getResources().getString(i));
    }

    public void showTransientIndication(CharSequence charSequence) {
        showTransientIndication(charSequence, false, false);
    }

    /* access modifiers changed from: private */
    public void showTransientIndication(CharSequence charSequence, boolean z, boolean z2) {
        this.mTransientIndication = charSequence;
        this.mHideTransientMessageOnScreenOff = z2 && charSequence != null;
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        if (this.mDozing && !TextUtils.isEmpty(this.mTransientIndication)) {
            this.mWakeLock.setAcquired(true);
        }
        hideTransientIndicationDelayed(5000);
        updateIndication(false);
    }

    public void hideTransientIndication() {
        if (this.mTransientIndication != null) {
            this.mTransientIndication = null;
            this.mHideTransientMessageOnScreenOff = false;
            this.mHandler.removeMessages(1);
            this.mRotateTextViewController.hideTransient();
            updateIndication(false);
        }
    }

    /* access modifiers changed from: protected */
    public final void updateIndication(boolean z) {
        if (TextUtils.isEmpty(this.mTransientIndication)) {
            this.mWakeLock.setAcquired(false);
        }
        if (this.mVisible) {
            this.mIndicationArea.setVisibility(0);
            this.mIndicationArea.setSelected(true);
            if (this.mDozing) {
                this.mLockScreenIndicationView.setVisibility(8);
                this.mTopIndicationView.setVisibility(0);
                this.mTopIndicationView.setTextColor(-1);
                if (!TextUtils.isEmpty(this.mTransientIndication)) {
                    this.mTopIndicationView.switchIndication(this.mTransientIndication, (KeyguardIndication) null);
                } else if (!this.mBatteryPresent) {
                    this.mIndicationArea.setVisibility(8);
                } else if (!TextUtils.isEmpty(this.mAlignmentIndication)) {
                    this.mTopIndicationView.switchIndication(this.mAlignmentIndication, (KeyguardIndication) null);
                    this.mTopIndicationView.setTextColor(this.mContext.getColor(R$color.misalignment_text_color));
                } else if (this.mPowerPluggedIn || this.mEnableBatteryDefender) {
                    String computePowerIndication = computePowerIndication();
                    if (z) {
                        animateText(this.mTopIndicationView, computePowerIndication);
                    } else {
                        this.mTopIndicationView.switchIndication(computePowerIndication, (KeyguardIndication) null);
                    }
                } else {
                    this.mTopIndicationView.switchIndication(NumberFormat.getPercentInstance().format((double) (((float) this.mBatteryLevel) / 100.0f)), (KeyguardIndication) null);
                }
            } else {
                this.mTopIndicationView.setVisibility(8);
                this.mTopIndicationView.setText((CharSequence) null);
                this.mLockScreenIndicationView.setVisibility(0);
                updateIndications(z, KeyguardUpdateMonitor.getCurrentUser());
            }
        }
    }

    private void animateText(final KeyguardIndicationTextView keyguardIndicationTextView, final String str) {
        int integer = this.mContext.getResources().getInteger(R$integer.wired_charging_keyguard_text_animation_distance);
        int integer2 = this.mContext.getResources().getInteger(R$integer.wired_charging_keyguard_text_animation_duration_up);
        final int integer3 = this.mContext.getResources().getInteger(R$integer.wired_charging_keyguard_text_animation_duration_down);
        keyguardIndicationTextView.animate().cancel();
        ViewClippingUtil.setClippingDeactivated(keyguardIndicationTextView, true, this.mClippingParams);
        keyguardIndicationTextView.animate().translationYBy((float) integer).setInterpolator(Interpolators.LINEAR).setDuration((long) integer2).setListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationStart(Animator animator) {
                keyguardIndicationTextView.switchIndication(str, (KeyguardIndication) null);
            }

            public void onAnimationCancel(Animator animator) {
                keyguardIndicationTextView.setTranslationY(0.0f);
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (this.mCancelled) {
                    ViewClippingUtil.setClippingDeactivated(keyguardIndicationTextView, false, KeyguardIndicationController.this.mClippingParams);
                } else {
                    keyguardIndicationTextView.animate().setDuration((long) integer3).setInterpolator(Interpolators.BOUNCE).translationY(0.0f).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            keyguardIndicationTextView.setTranslationY(0.0f);
                            C14444 r1 = C14444.this;
                            ViewClippingUtil.setClippingDeactivated(keyguardIndicationTextView, false, KeyguardIndicationController.this.mClippingParams);
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public String computePowerIndication() {
        int i;
        if (this.mPowerCharged) {
            return this.mContext.getResources().getString(R$string.keyguard_charged);
        }
        String format = NumberFormat.getPercentInstance().format((double) (((float) this.mBatteryLevel) / 100.0f));
        if (this.mBatteryOverheated) {
            return this.mContext.getResources().getString(R$string.keyguard_plugged_in_charging_limited, new Object[]{format});
        }
        long j = this.mChargingTimeRemaining;
        boolean z = j > 0;
        if (this.mPowerPluggedInWired) {
            int i2 = this.mChargingSpeed;
            if (i2 != 0) {
                if (i2 != 2) {
                    if (z) {
                        i = R$string.keyguard_indication_charging_time;
                    } else {
                        i = R$string.keyguard_plugged_in;
                    }
                } else if (z) {
                    i = R$string.keyguard_indication_charging_time_fast;
                } else {
                    i = R$string.keyguard_plugged_in_charging_fast;
                }
            } else if (z) {
                i = R$string.keyguard_indication_charging_time_slowly;
            } else {
                i = R$string.keyguard_plugged_in_charging_slowly;
            }
        } else if (z) {
            i = R$string.keyguard_indication_charging_time_wireless;
        } else {
            i = R$string.keyguard_plugged_in_wireless;
        }
        if (z) {
            return this.mContext.getResources().getString(i, new Object[]{Formatter.formatShortElapsedTimeRoundingUpToMinutes(this.mContext, j), format});
        } else if (MotoPowerUtil.isTurboPowerCharge(this.mChargeRate)) {
            return this.mContext.getResources().getString(R$string.kg_turbo_power);
        } else {
            return this.mContext.getResources().getString(i, new Object[]{format});
        }
    }

    public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }

    public void showActionToUnlock() {
        if (!this.mDozing) {
            if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                if (!this.mStatusBarKeyguardViewManager.isShowingAlternateAuth() && this.mKeyguardUpdateMonitor.isFaceEnrolled()) {
                    this.mStatusBarKeyguardViewManager.showBouncerMessage(this.mContext.getString(R$string.keyguard_retry), this.mBouncerTextColorState);
                }
            } else if (!this.mKeyguardUpdateMonitor.isScreenOn()) {
            } else {
                if (this.mKeyguardUpdateMonitor.isUdfpsAvailable()) {
                    showTransientIndication(this.mContext.getString(R$string.keyguard_unlock_press), false, true);
                } else {
                    showTransientIndication(this.mContext.getString(R$string.keyguard_unlock), false, true);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void showTryFingerprintMsg() {
        if (!this.mKeyguardUpdateMonitor.isUdfpsAvailable()) {
            showTransientIndication(R$string.keyguard_try_fingerprint);
        } else if (this.mKeyguardBypassController.getUserHasDeviceEntryIntent()) {
            showTransientIndication(R$string.keyguard_unlock_press);
        } else {
            showTransientIndication(R$string.keyguard_face_failed_use_fp);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("KeyguardIndicationController:");
        printWriter.println("  mInitialTextColorState: " + this.mInitialTextColorState);
        printWriter.println("  mBouncerTextColorState: " + this.mBouncerTextColorState);
        printWriter.println("  mPowerPluggedInWired: " + this.mPowerPluggedInWired);
        printWriter.println("  mPowerPluggedIn: " + this.mPowerPluggedIn);
        printWriter.println("  mPowerCharged: " + this.mPowerCharged);
        printWriter.println("  mChargingSpeed: " + this.mChargingSpeed);
        printWriter.println("  mChargingWattage: " + this.mChargingWattage);
        printWriter.println("  mMessageToShowOnScreenOn: " + this.mMessageToShowOnScreenOn);
        printWriter.println("  mDozing: " + this.mDozing);
        printWriter.println("  mBatteryLevel: " + this.mBatteryLevel);
        printWriter.println("  mBatteryPresent: " + this.mBatteryPresent);
        StringBuilder sb = new StringBuilder();
        sb.append("  mTextView.getText(): ");
        KeyguardIndicationTextView keyguardIndicationTextView = this.mTopIndicationView;
        sb.append(keyguardIndicationTextView == null ? null : keyguardIndicationTextView.getText());
        printWriter.println(sb.toString());
        printWriter.println("  computePowerIndication(): " + computePowerIndication());
        this.mRotateTextViewController.dump(fileDescriptor, printWriter, strArr);
    }

    protected class BaseKeyguardCallback extends KeyguardUpdateMonitorCallback {
        protected BaseKeyguardCallback() {
        }

        public void onLockScreenModeChanged(int i) {
            KeyguardIndicationController.this.mLockScreenMode = i;
        }

        public void onRefreshBatteryInfo(BatteryStatus batteryStatus) {
            int i = batteryStatus.status;
            boolean z = false;
            boolean z2 = i == 2 || i == 5;
            boolean access$600 = KeyguardIndicationController.this.mPowerPluggedIn;
            boolean unused = KeyguardIndicationController.this.mPowerPluggedInWired = batteryStatus.isPluggedInWired() && z2;
            boolean unused2 = KeyguardIndicationController.this.mPowerPluggedIn = batteryStatus.isPluggedIn() && z2;
            boolean unused3 = KeyguardIndicationController.this.mPowerCharged = batteryStatus.isCharged();
            int unused4 = KeyguardIndicationController.this.mChargingWattage = batteryStatus.maxChargingWattage;
            KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
            int unused5 = keyguardIndicationController.mChargingSpeed = batteryStatus.getChargingSpeed(keyguardIndicationController.mContext);
            int unused6 = KeyguardIndicationController.this.mBatteryLevel = batteryStatus.level;
            boolean unused7 = KeyguardIndicationController.this.mBatteryPresent = batteryStatus.present;
            boolean unused8 = KeyguardIndicationController.this.mBatteryOverheated = batteryStatus.isOverheated();
            KeyguardIndicationController keyguardIndicationController2 = KeyguardIndicationController.this;
            boolean unused9 = keyguardIndicationController2.mEnableBatteryDefender = keyguardIndicationController2.mBatteryOverheated && batteryStatus.isPluggedIn();
            try {
                if (!KeyguardIndicationController.this.mChargingTimeRemainingDisabled) {
                    KeyguardIndicationController keyguardIndicationController3 = KeyguardIndicationController.this;
                    long unused10 = keyguardIndicationController3.mChargingTimeRemaining = keyguardIndicationController3.mPowerPluggedIn ? KeyguardIndicationController.this.mBatteryInfo.computeChargeTimeRemaining() : -1;
                } else {
                    long unused11 = KeyguardIndicationController.this.mChargingTimeRemaining = -1;
                }
            } catch (RemoteException e) {
                Log.e("KeyguardIndication", "Error calling IBatteryStats: ", e);
                long unused12 = KeyguardIndicationController.this.mChargingTimeRemaining = -1;
            }
            KeyguardIndicationController keyguardIndicationController4 = KeyguardIndicationController.this;
            if (!access$600 && keyguardIndicationController4.mPowerPluggedInWired) {
                z = true;
            }
            keyguardIndicationController4.updateIndication(z);
            if (!KeyguardIndicationController.this.mDozing) {
                return;
            }
            if (!access$600 && KeyguardIndicationController.this.mPowerPluggedIn) {
                KeyguardIndicationController keyguardIndicationController5 = KeyguardIndicationController.this;
                keyguardIndicationController5.showTransientIndication((CharSequence) keyguardIndicationController5.computePowerIndication());
            } else if (access$600 && !KeyguardIndicationController.this.mPowerPluggedIn) {
                KeyguardIndicationController.this.hideTransientIndication();
            }
        }

        public void onBiometricHelp(int i, String str, BiometricSourceType biometricSourceType) {
            boolean z = true;
            if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true)) {
                if (i != -2) {
                    z = false;
                }
                if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                    KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(str, KeyguardIndicationController.this.mBouncerTextColorState);
                } else if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                    if (biometricSourceType != BiometricSourceType.FACE || !shouldSuppressFaceMsgAndShowTryFingerprintMsg()) {
                        KeyguardIndicationController.this.showTransientIndication(str, false, z);
                    } else {
                        KeyguardIndicationController.this.showTryFingerprintMsg();
                    }
                } else if (z) {
                    KeyguardIndicationController.this.mHandler.sendMessageDelayed(KeyguardIndicationController.this.mHandler.obtainMessage(2), 1300);
                }
            }
        }

        public void onBiometricError(int i, String str, BiometricSourceType biometricSourceType) {
            if (!shouldSuppressBiometricError(i, biometricSourceType, KeyguardIndicationController.this.mKeyguardUpdateMonitor)) {
                if (biometricSourceType != BiometricSourceType.FACE || !shouldSuppressFaceMsgAndShowTryFingerprintMsg() || KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing() || !KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                    String str2 = null;
                    if (i == 7) {
                        str2 = str;
                        str = KeyguardIndicationController.this.mContext.getResources().getString(R$string.fingerprint_lockout_hint);
                    }
                    if (i == 3) {
                        if (!KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing() && KeyguardIndicationController.this.mKeyguardUpdateMonitor.isUdfpsEnrolled() && KeyguardIndicationController.this.mKeyguardUpdateMonitor.isFingerprintDetectionRunning()) {
                            KeyguardIndicationController.this.showTryFingerprintMsg();
                        } else if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isShowingAlternateAuth()) {
                            KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(KeyguardIndicationController.this.mContext.getResources().getString(R$string.keyguard_unlock_press), KeyguardIndicationController.this.mInitialTextColorState);
                        } else {
                            KeyguardIndicationController.this.showActionToUnlock();
                        }
                    } else if (KeyguardIndicationController.this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                        if (i == 7) {
                            KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(str2, KeyguardIndicationController.this.mBouncerTextColorState);
                        } else {
                            KeyguardIndicationController.this.mStatusBarKeyguardViewManager.showBouncerMessage(str, KeyguardIndicationController.this.mBouncerTextColorState);
                        }
                    } else if (KeyguardIndicationController.this.mKeyguardUpdateMonitor.isScreenOn()) {
                        KeyguardIndicationController.this.showTransientIndication(str, true, true);
                    } else {
                        String unused = KeyguardIndicationController.this.mMessageToShowOnScreenOn = str;
                    }
                } else {
                    KeyguardIndicationController.this.showTryFingerprintMsg();
                }
            }
        }

        private boolean shouldSuppressBiometricError(int i, BiometricSourceType biometricSourceType, KeyguardUpdateMonitor keyguardUpdateMonitor) {
            if (biometricSourceType == BiometricSourceType.FINGERPRINT) {
                return shouldSuppressFingerprintError(i, keyguardUpdateMonitor);
            }
            if (biometricSourceType == BiometricSourceType.FACE) {
                return shouldSuppressFaceError(i, keyguardUpdateMonitor);
            }
            return false;
        }

        private boolean shouldSuppressFingerprintError(int i, KeyguardUpdateMonitor keyguardUpdateMonitor) {
            return (!keyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true) && i != 9) || i == 5 || i == 10;
        }

        private boolean shouldSuppressFaceMsgAndShowTryFingerprintMsg() {
            if (!KeyguardIndicationController.this.mKeyguardUpdateMonitor.isFingerprintDetectionRunning() || !KeyguardIndicationController.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true)) {
                return false;
            }
            return true;
        }

        private boolean shouldSuppressFaceError(int i, KeyguardUpdateMonitor keyguardUpdateMonitor) {
            return (!keyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true) && i != 9) || i == 5;
        }

        public void onTrustAgentErrorMessage(CharSequence charSequence) {
            KeyguardIndicationController.this.showTransientIndication(charSequence, true, false);
        }

        public void onScreenTurnedOn() {
            if (KeyguardIndicationController.this.mMessageToShowOnScreenOn != null) {
                KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
                keyguardIndicationController.showTransientIndication(keyguardIndicationController.mMessageToShowOnScreenOn, true, false);
                KeyguardIndicationController.this.hideTransientIndicationDelayed(5000);
                String unused = KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }

        public void onBiometricRunningStateChanged(boolean z, BiometricSourceType biometricSourceType) {
            if (z && biometricSourceType == BiometricSourceType.FACE) {
                KeyguardIndicationController.this.hideTransientIndication();
                String unused = KeyguardIndicationController.this.mMessageToShowOnScreenOn = null;
            }
        }

        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            super.onBiometricAuthenticated(i, biometricSourceType, z);
            KeyguardIndicationController.this.mHandler.sendEmptyMessage(1);
            if (biometricSourceType == BiometricSourceType.FACE && !KeyguardIndicationController.this.mKeyguardBypassController.canBypass()) {
                KeyguardIndicationController.this.mHandler.sendEmptyMessage(2);
            }
        }

        public void onUserSwitchComplete(int i) {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }

        public void onUserUnlocked() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }

        public void onLogoutEnabledChanged() {
            if (KeyguardIndicationController.this.mVisible) {
                KeyguardIndicationController.this.updateIndication(false);
            }
        }

        public void onRequireUnlockForNfc() {
            KeyguardIndicationController keyguardIndicationController = KeyguardIndicationController.this;
            keyguardIndicationController.showTransientIndication(keyguardIndicationController.mContext.getString(R$string.require_unlock_for_nfc), false, false);
            KeyguardIndicationController.this.hideTransientIndicationDelayed(5000);
        }
    }
}
