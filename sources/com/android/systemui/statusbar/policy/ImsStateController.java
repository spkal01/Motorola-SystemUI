package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.RegistrationManager;
import android.telephony.ims.feature.MmTelFeature;
import android.util.Log;
import com.android.systemui.R$bool;
import com.android.systemui.R$integer;
import com.android.systemui.statusbar.policy.ImsIconController;
import java.util.Objects;

public class ImsStateController {
    public static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public MmTelFeature.MmTelCapabilities mCapabilities = null;
    private final ImsMmTelManager.CapabilityCallback mCapabilityCallback = new ImsMmTelManager.CapabilityCallback() {
        public void onCapabilitiesStatusChanged(MmTelFeature.MmTelCapabilities mmTelCapabilities) {
            if (ImsStateController.DEBUG) {
                String str = ImsStateController.this.mTag;
                Log.i(str, "onCapabilitiesStatusChanged:" + mmTelCapabilities);
            }
            MmTelFeature.MmTelCapabilities unused = ImsStateController.this.mCapabilities = mmTelCapabilities;
            ImsStateController.this.updateImsState();
        }
    };
    public Context mContext;
    public Handler mHandler;
    public ImsIconController mImsIconController;
    private ImsMmTelManager mImsMmTelManager;
    /* access modifiers changed from: private */
    public int mImsRadioTech = -1;
    private ImsState mImsState;
    BroadcastReceiver mImsStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ImsStateController.this.handleBroadcast(intent);
        }
    };
    private ImsState mLastImsState;
    private final RegistrationManager.RegistrationCallback mRegistrationCallback = new RegistrationManager.RegistrationCallback() {
        public void onRegistered(int i) {
            if (ImsStateController.DEBUG) {
                String str = ImsStateController.this.mTag;
                Log.i(str, "onRegistered: " + i);
            }
            int unused = ImsStateController.this.mImsRadioTech = i;
            ImsStateController.this.updateImsState();
        }

        public void onRegistering(int i) {
            if (ImsStateController.DEBUG) {
                String str = ImsStateController.this.mTag;
                Log.i(str, "onRegistering: " + i);
            }
        }

        public void onUnregistered(ImsReasonInfo imsReasonInfo) {
            if (ImsStateController.DEBUG) {
                String str = ImsStateController.this.mTag;
                Log.i(str, "onUnregistered: " + imsReasonInfo);
            }
            int unused = ImsStateController.this.mImsRadioTech = -1;
            ImsStateController.this.updateImsState();
        }

        public void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) {
            if (ImsStateController.DEBUG) {
                String str = ImsStateController.this.mTag;
                Log.i(str, "onTechnologyChangeFailed imsRadioTech = " + i + " info: " + imsReasonInfo);
            }
        }
    };
    public int mSlotId;
    public int mSubId;
    public SubscriptionInfo mSubscriptionInfo;
    public String mTag;
    private ContentObserver mWfcSettingsObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            ImsStateController imsStateController = ImsStateController.this;
            boolean unused = imsStateController.mWifiCallingEnabled = ImsMmTelManager.createForSubscriptionId(imsStateController.mSubId).isVoWiFiSettingEnabled();
            if (ImsStateController.DEBUG) {
                String str = ImsStateController.this.mTag;
                Log.i(str, "mWifiCallingEnabled: " + ImsStateController.this.mWifiCallingEnabled);
            }
            ImsStateController.this.updateImsState();
        }
    };
    /* access modifiers changed from: private */
    public boolean mWifiCallingEnabled;
    private boolean mWifiConnected;
    private boolean mWifiEnabled;

    public ImsStateController(Context context, ImsIconController imsIconController, SubscriptionInfo subscriptionInfo) {
        this.mContext = context;
        this.mImsIconController = imsIconController;
        updateSubInfo(subscriptionInfo);
        if (DEBUG) {
            Log.i(this.mTag, "new ImsStateController");
        }
        this.mImsState = new ImsState();
        this.mLastImsState = new ImsState();
        updateImsConfig();
        registerListener();
    }

    public void cleanupImsState() {
        ImsState imsState = this.mImsState;
        imsState.mVoLteRegistered = false;
        imsState.mVoWifiRegistered = false;
        imsState.mImsType = ImsIconController.ImsType.INVALID_STATE;
        this.mLastImsState.copyFrom(imsState);
        ImsIconController imsIconController = this.mImsIconController;
        if (imsIconController != null) {
            imsIconController.updateImsIcon();
        }
    }

    public void resetSubInfo(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return;
        }
        if (subscriptionInfo.getSubscriptionId() != this.mSubscriptionInfo.getSubscriptionId() || subscriptionInfo.getMcc() != this.mSubscriptionInfo.getMcc() || subscriptionInfo.getMnc() != this.mSubscriptionInfo.getMnc()) {
            updateSubInfo(subscriptionInfo);
            if (DEBUG) {
                String str = this.mTag;
                Log.i(str, "resetSubInfo info: " + subscriptionInfo);
            }
            updateImsConfig();
            unRegisterListener();
            registerListener();
        }
    }

    public void updateWifiState(boolean z, boolean z2) {
        this.mWifiEnabled = z;
        this.mWifiConnected = z2;
        updateImsState();
    }

    private void updateSubInfo(SubscriptionInfo subscriptionInfo) {
        this.mSubscriptionInfo = subscriptionInfo;
        this.mSlotId = subscriptionInfo.getSimSlotIndex();
        this.mSubId = this.mSubscriptionInfo.getSubscriptionId();
        this.mTag = "ImsIconController(slot: " + this.mSlotId + " subId: " + this.mSubId + ")";
    }

    public void updateMobileState(boolean z) {
        ImsState imsState = this.mImsState;
        if (imsState.mIs4GState != z) {
            imsState.mIs4GState = z;
            updateImsType();
            updateImsState();
        }
    }

    public void registerListener() {
        registerBroadcastListener();
        registerImsCallback();
        registerSettingsContentObserver();
    }

    public void unRegisterListener() {
        unRegisterBroadcastListener();
        unRegisterImsCallback();
        unregisterSettingsContentObserver();
    }

    public void registerBroadcastListener() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mContext.registerReceiver(this.mImsStateReceiver, intentFilter);
    }

    public void unRegisterBroadcastListener() {
        this.mContext.unregisterReceiver(this.mImsStateReceiver);
    }

    private void registerSettingsContentObserver() {
        this.mContext.getContentResolver().registerContentObserver(Uri.withAppendedPath(SubscriptionManager.CONTENT_URI, "wfc_ims_enabled"), true, this.mWfcSettingsObserver);
    }

    private void unregisterSettingsContentObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mWfcSettingsObserver);
    }

    public void registerImsCallback() {
        try {
            this.mImsMmTelManager = ImsMmTelManager.createForSubscriptionId(this.mSubId);
            if (DEBUG) {
                String str = this.mTag;
                Log.i(str, "registerImsCallback SlotId: " + this.mSlotId + " mSubId: " + this.mSubId);
            }
            this.mImsMmTelManager.registerImsRegistrationCallback(this.mContext.getMainExecutor(), this.mRegistrationCallback);
            this.mImsMmTelManager.registerMmTelCapabilityCallback(this.mContext.getMainExecutor(), this.mCapabilityCallback);
        } catch (ImsException | IllegalArgumentException e) {
            Log.w("ImsIconController", "illegal subscription ID: " + e);
        }
    }

    public void unRegisterImsCallback() {
        if (this.mImsMmTelManager != null) {
            if (DEBUG) {
                String str = this.mTag;
                Log.i(str, "unRegisterImsCallback SubId: " + this.mSubId + " SlotId: " + this.mSlotId);
            }
            this.mImsMmTelManager.unregisterImsRegistrationCallback(this.mRegistrationCallback);
            this.mImsMmTelManager.unregisterMmTelCapabilityCallback(this.mCapabilityCallback);
            this.mImsMmTelManager = null;
        }
    }

    /* access modifiers changed from: private */
    public void updateImsState() {
        this.mImsState.mVoLteRegistered = capabilitiesOfLte();
        this.mImsState.mVoWifiRegistered = capabilitiesOfWifi();
        this.mImsState.mImsType = updateImsType();
        if (!this.mImsState.equals(this.mLastImsState)) {
            if (DEBUG) {
                String str = this.mTag;
                Log.i(str, "old ImsState:" + this.mLastImsState + "\nnew ImsState:" + this.mImsState);
            }
            this.mLastImsState.copyFrom(this.mImsState);
            ImsIconController imsIconController = this.mImsIconController;
            if (imsIconController != null) {
                imsIconController.updateImsIcon();
            }
        }
    }

    private ImsIconController.ImsType updateImsType() {
        ImsState imsState = this.mImsState;
        if (imsState.mVoLteRegistered && imsState.mConfigShowImsIcon && !imsState.mHideVoLteIcon && imsState.mIs4GState) {
            return ImsIconController.ImsType.VOLTE_STATE;
        }
        boolean z = imsState.mVoWifiRegistered;
        if (z && imsState.mConfigShowImsIcon && !imsState.mHideVoWifiIcon) {
            return ImsIconController.ImsType.VOWIFI_STATE;
        }
        if (!imsState.mConfigShowImsIcon || imsState.mHideVoWifiIcon || !imsState.mShowNotReadyIcon || z || !this.mWifiEnabled || !this.mWifiConnected || !this.mWifiCallingEnabled) {
            return ImsIconController.ImsType.INVALID_STATE;
        }
        return ImsIconController.ImsType.VOWIFI_NO_READY_STATE;
    }

    private boolean capabilitiesOfLte() {
        MmTelFeature.MmTelCapabilities mmTelCapabilities;
        if (this.mImsRadioTech != 1 || (mmTelCapabilities = this.mCapabilities) == null) {
            return false;
        }
        if (mmTelCapabilities.isCapable(1) || this.mCapabilities.isCapable(2)) {
            return true;
        }
        return false;
    }

    private boolean capabilitiesOfWifi() {
        MmTelFeature.MmTelCapabilities mmTelCapabilities;
        if (this.mImsRadioTech != 2 || (mmTelCapabilities = this.mCapabilities) == null) {
            return false;
        }
        if (mmTelCapabilities.isCapable(1) || this.mCapabilities.isCapable(2)) {
            return true;
        }
        return false;
    }

    public void handleBroadcast(Intent intent) {
        if (intent.getAction().equals("android.telephony.action.CARRIER_CONFIG_CHANGED") && this.mSlotId == intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1)) {
            if (DEBUG) {
                Log.i(this.mTag, "ACTION_CARRIER_CONFIG_CHANGED");
            }
            updateImsConfig();
            updateImsState();
        }
    }

    private void updateImsConfig() {
        boolean z = false;
        boolean z2 = SystemProperties.getInt("persist.dbg.show_ims_icon", 0) == 1;
        Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
        this.mImsState.mConfigShowImsIcon = resourcesForSubId.getBoolean(R$bool.config_show_ims_registration_icon) || z2;
        this.mImsState.mShowNotReadyIcon = resourcesForSubId.getBoolean(R$bool.config_show_vowifi_not_ready_icon);
        ImsState imsState = this.mImsState;
        if (imsState.mConfigShowImsIcon) {
            imsState.mIconMode = resourcesForSubId.getInteger(R$integer.config_ims_icon_mode);
            ImsState imsState2 = this.mImsState;
            int i = imsState2.mIconMode;
            imsState2.mHideVoLteIcon = (i & 1) != 0;
            imsState2.mHideVoWifiIcon = (i & 2) != 0;
            if ((i & 4) != 0) {
                z = true;
            }
            imsState2.mShowVoLteBadge = z;
        }
        this.mImsState.mShowSTCVoWifi = resourcesForSubId.getBoolean(R$bool.config_show_stc_vowifi_icon);
        PersistableBundle configForSubId = ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        if (configForSubId != null) {
            this.mImsState.mShow4GForLTE = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        }
    }

    public ImsState getImsState() {
        return this.mImsState;
    }

    public static class ImsState {
        boolean mConfigShowImsIcon = false;
        boolean mHideVoLteIcon = false;
        boolean mHideVoWifiIcon = false;
        int mIconMode;
        ImsIconController.ImsType mImsType = ImsIconController.ImsType.INVALID_STATE;
        boolean mIs4GState = false;
        boolean mShow4GForLTE = false;
        boolean mShowNotReadyIcon = false;
        boolean mShowSTCVoWifi = false;
        boolean mShowVoLteBadge = false;
        boolean mVoLteRegistered = false;
        boolean mVoWifiRegistered = false;

        public String toString() {
            return " mImsType: " + this.mImsType + ", mVoLteRegistered: " + this.mVoLteRegistered + ", mVoWifiRegistered: " + this.mVoWifiRegistered + ", mConfigShowImsIcon: " + this.mConfigShowImsIcon + ", mShowNotReadyIcon: " + this.mShowNotReadyIcon + ", mHideVoLteIcon: " + this.mHideVoLteIcon + ", mHideVoWifiIcon: " + this.mHideVoWifiIcon + ", mShowVoLteBadge: " + this.mShowVoLteBadge + ", mShow4GForLTE: " + this.mShow4GForLTE + ", mShowSTCVoWifi: " + this.mShowSTCVoWifi + ", mIs4GState: " + this.mIs4GState + ", mIconMode: " + this.mIconMode;
        }

        /* access modifiers changed from: private */
        public void copyFrom(ImsState imsState) {
            this.mVoLteRegistered = imsState.mVoLteRegistered;
            this.mVoWifiRegistered = imsState.mVoWifiRegistered;
            this.mConfigShowImsIcon = imsState.mConfigShowImsIcon;
            this.mShowNotReadyIcon = imsState.mShowNotReadyIcon;
            this.mHideVoLteIcon = imsState.mHideVoLteIcon;
            this.mHideVoWifiIcon = imsState.mHideVoWifiIcon;
            this.mShowVoLteBadge = imsState.mShowVoLteBadge;
            this.mShow4GForLTE = imsState.mShow4GForLTE;
            this.mShowSTCVoWifi = imsState.mShowSTCVoWifi;
            this.mImsType = imsState.mImsType;
            this.mIs4GState = imsState.mIs4GState;
            this.mIconMode = imsState.mIconMode;
        }

        public boolean equals(Object obj) {
            if (obj == null || ImsState.class != obj.getClass()) {
                return false;
            }
            ImsState imsState = (ImsState) obj;
            if (this.mVoLteRegistered == imsState.mVoLteRegistered && this.mVoWifiRegistered == imsState.mVoWifiRegistered && this.mConfigShowImsIcon == imsState.mConfigShowImsIcon && this.mShowNotReadyIcon == imsState.mShowNotReadyIcon && this.mHideVoLteIcon == imsState.mHideVoLteIcon && this.mHideVoWifiIcon == imsState.mHideVoWifiIcon && this.mShowVoLteBadge == imsState.mShowVoLteBadge && this.mShow4GForLTE == imsState.mShow4GForLTE && this.mShowSTCVoWifi == imsState.mShowSTCVoWifi && this.mImsType == imsState.mImsType && this.mIs4GState == imsState.mIs4GState && this.mIconMode == imsState.mIconMode) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.mImsType, Boolean.valueOf(this.mVoLteRegistered), Boolean.valueOf(this.mVoWifiRegistered), Boolean.valueOf(this.mConfigShowImsIcon), Boolean.valueOf(this.mShowNotReadyIcon), Boolean.valueOf(this.mHideVoLteIcon), Integer.valueOf(this.mIconMode), Boolean.valueOf(this.mHideVoWifiIcon), Boolean.valueOf(this.mShowVoLteBadge), Boolean.valueOf(this.mShow4GForLTE), Boolean.valueOf(this.mShowSTCVoWifi), Boolean.valueOf(this.mIs4GState)});
        }
    }

    public String toString() {
        return "ImsStateController (slot: " + this.mSlotId + " subId: " + this.mSubId + ")." + "\n  mWifiEnabled: " + this.mWifiEnabled + "\n  mWifiConnected: " + this.mWifiConnected + "\n  mWifiCallingEnabled: " + this.mWifiCallingEnabled + "\n  mImsState: " + this.mImsState.toString() + "\n  mLastImsState: " + this.mLastImsState.toString();
    }
}
