package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import android.text.Html;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.AccessibilityContentDescriptions;
import com.android.settingslib.SignalIcon$IconGroup;
import com.android.settingslib.SignalIcon$MobileIconGroup;
import com.android.settingslib.SignalIcon$State;
import com.android.settingslib.graph.SignalDrawable;
import com.android.settingslib.mobile.TelephonyIcons;
import com.android.settingslib.wifi.WifiStatusTracker;
import com.android.systemui.R$bool;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.moto.ExtendedMobileDataInfo;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.policy.NetworkController;
import java.io.PrintWriter;
import java.util.Objects;

public class WifiSignalController extends SignalController<WifiState, SignalIcon$IconGroup> {
    private final String PASS_POINT_EXTRA = "PASSPOINT_EXTRA";
    private final String PASS_POINT_HOME = "HOME";
    private final String PASS_POINT_ROAM = "ROAM";
    private final SignalIcon$MobileIconGroup mCarrierMergedWifiIconGroup = TelephonyIcons.CARRIER_MERGED_WIFI;
    private final String mDefault = "DEFAULT";
    private final boolean mHasMobileDataFeature;
    private final boolean mProviderModelSetting;
    private final SignalIcon$IconGroup mUnmergedWifi6IconGroup = WifiIcons.UNMERGED_WIFI_6;
    private final SignalIcon$IconGroup mUnmergedWifiIconGroup;
    private final WifiManager mWifiManager;
    private WIFI_STATE_PASSPOINT mWifiState = WIFI_STATE_PASSPOINT.WIFI_DEFAULT;
    private final WifiStatusTracker mWifiTracker;

    private enum WIFI_STATE_PASSPOINT {
        WIFI_DEFAULT,
        WIFI_R,
        WIFI_H
    }

    public WifiSignalController(Context context, boolean z, CallbackHandler callbackHandler, NetworkControllerImpl networkControllerImpl, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, FeatureFlags featureFlags) {
        super("WifiSignalController", context, 1, callbackHandler, networkControllerImpl);
        SignalIcon$IconGroup signalIcon$IconGroup = WifiIcons.UNMERGED_WIFI;
        this.mUnmergedWifiIconGroup = signalIcon$IconGroup;
        this.mWifiManager = wifiManager;
        WifiStatusTracker wifiStatusTracker = new WifiStatusTracker(this.mContext, wifiManager, networkScoreManager, connectivityManager, new WifiSignalController$$ExternalSyntheticLambda0(this));
        this.mWifiTracker = wifiStatusTracker;
        wifiStatusTracker.setListening(true);
        this.mHasMobileDataFeature = z;
        if (wifiManager != null) {
            wifiManager.registerTrafficStateCallback(context.getMainExecutor(), new WifiTrafficStateCallback());
        }
        ((WifiState) this.mLastState).iconGroup = signalIcon$IconGroup;
        ((WifiState) this.mCurrentState).iconGroup = signalIcon$IconGroup;
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            this.mProviderModelSetting = false;
        } else {
            this.mProviderModelSetting = featureFlags.isProviderModelSettingEnabled();
        }
    }

    /* access modifiers changed from: protected */
    public WifiState cleanState() {
        return new WifiState();
    }

    /* access modifiers changed from: package-private */
    public void refreshLocale() {
        this.mWifiTracker.refreshLocale();
    }

    public void notifyListeners(NetworkController.SignalCallback signalCallback) {
        T t = this.mCurrentState;
        if (!((WifiState) t).isCarrierMerged) {
            notifyListenersForNonCarrierWifi(signalCallback);
        } else if (((WifiState) t).isDefault) {
            notifyListenersForCarrierWifi(signalCallback);
        }
    }

    private void notifyListenersForNonCarrierWifi(NetworkController.SignalCallback signalCallback) {
        int i;
        int i2;
        NetworkController.SignalCallback signalCallback2 = signalCallback;
        boolean z = this.mContext.getResources().getBoolean(R$bool.config_showWifiIndicatorWhenEnabled);
        T t = this.mCurrentState;
        boolean z2 = ((WifiState) t).enabled && ((((WifiState) t).connected && ((WifiState) t).inetCondition == 1) || !this.mHasMobileDataFeature || ((WifiState) t).isDefault || z);
        NetworkController.IconState iconState = null;
        String str = ((WifiState) t).connected ? ((WifiState) t).ssid : null;
        boolean z3 = z2 && ((WifiState) t).ssid != null;
        String charSequence = getTextIfExists(getContentDescription()).toString();
        if (((WifiState) this.mCurrentState).inetCondition == 0) {
            charSequence = charSequence + "," + this.mContext.getString(R$string.data_connection_no_internet);
        }
        if (this.mProviderModelSetting) {
            NetworkController.IconState iconState2 = new NetworkController.IconState(z2, getCurrentIconId(), charSequence);
            if (((WifiState) this.mCurrentState).isDefault || (!this.mNetworkController.isRadioOn() && !this.mNetworkController.isEthernetDefault())) {
                boolean z4 = ((WifiState) this.mCurrentState).connected;
                if (this.mWifiTracker.isCaptivePortal) {
                    i2 = R$drawable.ic_qs_wifi_disconnected;
                } else {
                    i2 = getQsCurrentIconId();
                }
                iconState = new NetworkController.IconState(z4, i2, charSequence);
            }
            NetworkController.IconState iconState3 = iconState;
            T t2 = this.mCurrentState;
            signalCallback2.setWifiIndicators(new NetworkController.WifiIndicators(((WifiState) t2).enabled, iconState2, iconState3, z3 && ((WifiState) t2).activityIn, z3 && ((WifiState) t2).activityOut, str, ((WifiState) t2).isTransient, ((WifiState) t2).statusLabel, this.mNetworkController.isShowActivityIconOnSB(), this.mNetworkController.isShowActivityIconOnQS(), ((WifiState) this.mCurrentState).epdgState));
            return;
        }
        NetworkController.IconState iconState4 = new NetworkController.IconState(z2, getCurrentIconId(), charSequence);
        boolean z5 = ((WifiState) this.mCurrentState).connected;
        if (this.mWifiTracker.isCaptivePortal) {
            i = R$drawable.ic_qs_wifi_disconnected;
        } else {
            i = getQsCurrentIconId();
        }
        NetworkController.IconState iconState5 = new NetworkController.IconState(z5, i, charSequence);
        T t3 = this.mCurrentState;
        signalCallback2.setWifiIndicators(new NetworkController.WifiIndicators(((WifiState) t3).enabled, iconState4, iconState5, z3 && ((WifiState) t3).activityIn, z3 && ((WifiState) t3).activityOut, str, ((WifiState) t3).isTransient, ((WifiState) t3).statusLabel, this.mNetworkController.isShowActivityIconOnSB(), this.mNetworkController.isShowActivityIconOnQS(), ((WifiState) this.mCurrentState).epdgState));
    }

    private void notifyListenersForCarrierWifi(NetworkController.SignalCallback signalCallback) {
        int i;
        SignalIcon$MobileIconGroup signalIcon$MobileIconGroup = this.mCarrierMergedWifiIconGroup;
        String charSequence = getTextIfExists(getContentDescription()).toString();
        CharSequence textIfExists = getTextIfExists(signalIcon$MobileIconGroup.dataContentDescription);
        String obj = Html.fromHtml(textIfExists.toString(), 0).toString();
        if (((WifiState) this.mCurrentState).inetCondition == 0) {
            obj = this.mContext.getString(R$string.data_connection_no_internet);
        }
        String str = obj;
        T t = this.mCurrentState;
        boolean z = ((WifiState) t).enabled && ((WifiState) t).connected && ((WifiState) t).isDefault;
        NetworkController.IconState iconState = new NetworkController.IconState(z, getCurrentIconIdForCarrierWifi(), charSequence);
        int i2 = z ? signalIcon$MobileIconGroup.dataType : 0;
        NetworkController.IconState iconState2 = null;
        if (z) {
            i = signalIcon$MobileIconGroup.qsDataType;
            iconState2 = new NetworkController.IconState(((WifiState) this.mCurrentState).connected, getQsCurrentIconIdForCarrierWifi(), charSequence);
        } else {
            i = 0;
        }
        String networkNameForCarrierWiFi = this.mNetworkController.getNetworkNameForCarrierWiFi(((WifiState) this.mCurrentState).subId);
        T t2 = this.mCurrentState;
        signalCallback.setMobileDataIndicators(new NetworkController.MobileDataIndicators(iconState, iconState2, i2, i, ((WifiState) t2).activityIn, ((WifiState) t2).activityOut, str, textIfExists, networkNameForCarrierWiFi, signalIcon$MobileIconGroup.isWide, ((WifiState) t2).subId, false, true, (ExtendedMobileDataInfo) null));
    }

    private int getCurrentIconIdForCarrierWifi() {
        int i = ((WifiState) this.mCurrentState).level;
        boolean z = true;
        int maxSignalLevel = this.mWifiManager.getMaxSignalLevel() + 1;
        T t = this.mCurrentState;
        if (((WifiState) t).inetCondition != 0) {
            z = false;
        }
        if (((WifiState) t).connected) {
            return SignalDrawable.getState(i, maxSignalLevel, z);
        }
        if (((WifiState) t).enabled) {
            return SignalDrawable.getEmptyState(maxSignalLevel);
        }
        return 0;
    }

    private int getQsCurrentIconIdForCarrierWifi() {
        return getCurrentIconIdForCarrierWifi();
    }

    public void fetchInitialState() {
        this.mWifiTracker.fetchInitialState();
        copyWifiStates();
        updateWifiConectionState();
        notifyListenersIfNecessary();
    }

    public void handleBroadcast(Intent intent) {
        this.mWifiTracker.handleBroadcast(intent);
        copyWifiStates();
        updateWifiConectionState();
        notifyListenersIfNecessary();
    }

    /* access modifiers changed from: private */
    public void handleStatusUpdated() {
        copyWifiStates();
        updateWifiConectionState();
        notifyListenersIfNecessary();
    }

    private void updateWifiConectionState() {
        ImsIconController instance = ImsIconController.getInstance(this.mContext);
        T t = this.mCurrentState;
        instance.onWifiConnectionStateChanged(((WifiState) t).enabled, ((WifiState) t).connected);
        NetworkControllerImpl networkControllerImpl = this.mNetworkController;
        T t2 = this.mCurrentState;
        networkControllerImpl.onWifiConnectionStateChanged(((WifiState) t2).enabled, ((WifiState) t2).connected);
    }

    private void copyWifiStates() {
        SignalIcon$IconGroup signalIcon$IconGroup;
        T t = this.mCurrentState;
        WifiStatusTracker wifiStatusTracker = this.mWifiTracker;
        ((WifiState) t).enabled = wifiStatusTracker.enabled;
        ((WifiState) t).isDefault = wifiStatusTracker.isDefaultNetwork;
        ((WifiState) t).connected = wifiStatusTracker.connected;
        ((WifiState) t).ssid = wifiStatusTracker.ssid;
        ((WifiState) t).rssi = wifiStatusTracker.rssi;
        notifyWifiLevelChangeIfNecessary(wifiStatusTracker.level);
        T t2 = this.mCurrentState;
        WifiStatusTracker wifiStatusTracker2 = this.mWifiTracker;
        ((WifiState) t2).level = wifiStatusTracker2.level;
        ((WifiState) t2).statusLabel = wifiStatusTracker2.statusLabel;
        ((WifiState) t2).isCarrierMerged = wifiStatusTracker2.isCarrierMerged;
        ((WifiState) t2).subId = wifiStatusTracker2.subId;
        ((WifiState) t2).isStandard11ax = wifiStatusTracker2.isStandard11ax;
        WifiState wifiState = (WifiState) t2;
        if (((WifiState) t2).isCarrierMerged) {
            signalIcon$IconGroup = this.mCarrierMergedWifiIconGroup;
        } else if (((WifiState) t2).isStandard11ax) {
            signalIcon$IconGroup = this.mUnmergedWifi6IconGroup;
        } else {
            signalIcon$IconGroup = this.mUnmergedWifiIconGroup;
        }
        wifiState.iconGroup = signalIcon$IconGroup;
    }

    /* access modifiers changed from: package-private */
    public void notifyWifiLevelChangeIfNecessary(int i) {
        if (i != ((WifiState) this.mCurrentState).level) {
            this.mNetworkController.notifyWifiLevelChange(i);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isCarrierMergedWifi(int i) {
        T t = this.mCurrentState;
        return ((WifiState) t).isDefault && ((WifiState) t).isCarrierMerged && ((WifiState) t).subId == i;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setActivity(int i) {
        T t = this.mCurrentState;
        boolean z = false;
        ((WifiState) t).activityIn = i == 3 || i == 1;
        WifiState wifiState = (WifiState) t;
        if (i == 3 || i == 2) {
            z = true;
        }
        wifiState.activityOut = z;
        notifyListenersIfNecessary();
    }

    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
        this.mWifiTracker.dump(printWriter);
    }

    public void handlePasspointBroadcast(Intent intent) {
        WIFI_STATE_PASSPOINT wifi_state_passpoint;
        SignalIcon$IconGroup signalIcon$IconGroup;
        String stringExtra = intent.getStringExtra("PASSPOINT_EXTRA");
        if (stringExtra.equals("HOME")) {
            wifi_state_passpoint = WIFI_STATE_PASSPOINT.WIFI_H;
            signalIcon$IconGroup = getIconGroup(WifiIcons.WIFI_SIGNAL_STRENGTH_H, WifiIcons.QS_WIFI_SIGNAL_STRENGTH_H);
        } else if (stringExtra.equals("ROAM")) {
            wifi_state_passpoint = WIFI_STATE_PASSPOINT.WIFI_R;
            signalIcon$IconGroup = getIconGroup(WifiIcons.WIFI_SIGNAL_STRENGTH_R, WifiIcons.QS_WIFI_SIGNAL_STRENGTH_R);
        } else {
            wifi_state_passpoint = WIFI_STATE_PASSPOINT.WIFI_DEFAULT;
            signalIcon$IconGroup = getIconGroup(WifiIcons.WIFI_SIGNAL_STRENGTH, WifiIcons.QS_WIFI_SIGNAL_STRENGTH);
        }
        if (wifi_state_passpoint != this.mWifiState) {
            this.mWifiState = wifi_state_passpoint;
            ((WifiState) this.mCurrentState).iconGroup = signalIcon$IconGroup;
            notifyListenersIfNecessary();
        }
    }

    private SignalIcon$IconGroup getIconGroup(int[][] iArr, int[][] iArr2) {
        return new SignalIcon$IconGroup("Wi-Fi Icons", iArr, iArr2, AccessibilityContentDescriptions.WIFI_CONNECTION_STRENGTH, 17303763, 17303763, 17303763, 17303763, AccessibilityContentDescriptions.WIFI_NO_CONNECTION);
    }

    private class WifiTrafficStateCallback implements WifiManager.TrafficStateCallback {
        private WifiTrafficStateCallback() {
        }

        public void onStateChanged(int i) {
            WifiSignalController.this.setActivity(i);
        }
    }

    static class WifiState extends SignalIcon$State {
        boolean epdgState;
        public boolean isCarrierMerged;
        public boolean isDefault;
        boolean isStandard11ax;
        public boolean isTransient;
        public String ssid;
        public String statusLabel;
        public int subId;

        WifiState() {
        }

        public void copyFrom(SignalIcon$State signalIcon$State) {
            super.copyFrom(signalIcon$State);
            WifiState wifiState = (WifiState) signalIcon$State;
            this.ssid = wifiState.ssid;
            this.isTransient = wifiState.isTransient;
            this.isDefault = wifiState.isDefault;
            this.statusLabel = wifiState.statusLabel;
            this.isCarrierMerged = wifiState.isCarrierMerged;
            this.subId = wifiState.subId;
            this.epdgState = wifiState.epdgState;
            this.isStandard11ax = wifiState.isStandard11ax;
        }

        /* access modifiers changed from: protected */
        public void toString(StringBuilder sb) {
            super.toString(sb);
            sb.append(",ssid=");
            sb.append(this.ssid);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(",isDefault=");
            sb.append(this.isDefault);
            sb.append(",statusLabel=");
            sb.append(this.statusLabel);
            sb.append(",isCarrierMerged=");
            sb.append(this.isCarrierMerged);
            sb.append(",subId=");
            sb.append(this.subId);
            sb.append(",isStandard11AX=");
            sb.append(this.isStandard11ax);
            sb.append(",epdgState=");
            sb.append(this.epdgState);
        }

        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            WifiState wifiState = (WifiState) obj;
            if (Objects.equals(wifiState.ssid, this.ssid) && wifiState.isTransient == this.isTransient && wifiState.isDefault == this.isDefault && TextUtils.equals(wifiState.statusLabel, this.statusLabel) && wifiState.isCarrierMerged == this.isCarrierMerged && wifiState.subId == this.subId && wifiState.epdgState == this.epdgState && wifiState.isStandard11ax == this.isStandard11ax) {
                return true;
            }
            return false;
        }
    }

    public void handleEPDGBroadcast(Intent intent) {
        ((WifiState) this.mCurrentState).epdgState = intent.getBooleanExtra("state", false);
        notifyListenersIfNecessary();
    }

    public void handleEPDGBroadcast(boolean z) {
        ((WifiState) this.mCurrentState).epdgState = z;
        notifyListenersIfNecessary();
    }
}
