package com.android.systemui.moto;

import android.content.Context;
import android.os.Build;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settingslib.SignalIcon$MobileState;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$string;
import com.android.systemui.moto.CarrierIcons;
import com.android.systemui.statusbar.policy.CallbackHandler;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.NetworkController;

public class NetworkStateTracker {
    static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("NetworkStateTracker", 3));
    private static boolean mDifferingIconForSimError = false;
    private static boolean mForceshowActiveRat = false;
    private static boolean mShowDualRat = false;
    public static int sPhoneCount = 1;
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onOverlayChanged() {
            if (NetworkStateTracker.DEBUG) {
                Log.i("NetworkStateTracker", "RROs onOverlayChanged");
            }
            NetworkStateTracker.this.updateConfig();
        }
    };
    private Context mContext;
    /* access modifiers changed from: private */
    public VzwEri mEri = null;
    private EriSoundListener mEriSoundListener = new EriSoundListener() {
        public void playEriSoundAfterBoot() {
            NetworkStateTracker.this.mEri.playEriAlertAfterBoot();
        }

        public void updateEri(ServiceState serviceState, ServiceState serviceState2) {
            NetworkStateTracker.this.mEri.updateEri(serviceState, serviceState2, NetworkStateTracker.this.mTelephonyManager, NetworkStateTracker.this.mHandler);
        }
    };
    /* access modifiers changed from: private */
    public CallbackHandler mHandler;
    private final SubscriptionManager mSubscriptionManager;
    /* access modifiers changed from: private */
    public final TelephonyManager mTelephonyManager;

    public interface DefaultDataSubscriptionListener {
        void updateDefaultDataSubscription();
    }

    public interface EriSoundListener {
        void playEriSoundAfterBoot();

        void updateEri(ServiceState serviceState, ServiceState serviceState2);
    }

    public interface PanelCarrierLabelListener {
        void updateLabel();
    }

    public interface PanelViewExpansionListener {
        void updateExpansion(float f);
    }

    public interface ShortFormLabelListener {
        void updateLabel();
    }

    private static int getActivityState(boolean z, boolean z2, boolean z3, boolean z4) {
        if (z && !z2) {
            return 0;
        }
        if (z3 && z4) {
            return 4;
        }
        if (z3) {
            return 2;
        }
        return z4 ? 3 : 1;
    }

    public NetworkStateTracker(TelephonyManager telephonyManager, SubscriptionManager subscriptionManager, Context context, CallbackHandler callbackHandler, boolean z) {
        this.mTelephonyManager = telephonyManager;
        this.mSubscriptionManager = subscriptionManager;
        sPhoneCount = telephonyManager.getPhoneCount();
        this.mContext = context;
        this.mHandler = callbackHandler;
        if (z) {
            this.mEri = new VzwEri(this.mContext);
            this.mHandler.setEriSoundListening(this.mEriSoundListener, true);
        }
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this.mConfigurationListener);
        mDifferingIconForSimError = context.getResources().getBoolean(R$bool.config_differing_icon_for_sim_error);
        mShowDualRat = context.getResources().getBoolean(R$bool.zz_moto_config_show_dual_rat);
        mForceshowActiveRat = context.getResources().getBoolean(R$bool.zz_moto_config_force_show_active_rat);
    }

    /* access modifiers changed from: private */
    public void updateConfig() {
        mDifferingIconForSimError = this.mContext.getResources().getBoolean(R$bool.config_differing_icon_for_sim_error);
        mShowDualRat = this.mContext.getResources().getBoolean(R$bool.zz_moto_config_show_dual_rat);
        mForceshowActiveRat = this.mContext.getResources().getBoolean(R$bool.zz_moto_config_force_show_active_rat);
        if (DEBUG) {
            Log.i("NetworkStateTracker", "mDifferingIconForSimError = " + mDifferingIconForSimError + " mShowDualRat = " + mShowDualRat + " mForceshowActiveRat = " + mForceshowActiveRat);
        }
    }

    public static boolean shouldDifferSimError() {
        return mDifferingIconForSimError && !isMultipleSimDevice();
    }

    public static boolean isMultipleSimDevice() {
        return sPhoneCount > 1;
    }

    public static int getDataState(ServiceState serviceState, SignalIcon$MobileState signalIcon$MobileState, ExtendedMobileDataInfo extendedMobileDataInfo, NetworkConfig networkConfig, int i, boolean z, boolean z2) {
        SignalIcon$MobileState signalIcon$MobileState2 = signalIcon$MobileState;
        ExtendedMobileDataInfo extendedMobileDataInfo2 = extendedMobileDataInfo;
        NetworkConfig networkConfig2 = networkConfig;
        boolean z3 = z2;
        int i2 = 0;
        boolean z4 = networkConfig2.showDisableRatWhenRoamingDataOff && signalIcon$MobileState2.roaming && !signalIcon$MobileState2.dataRoamingEnabled && (!signalIcon$MobileState2.wifiConnected || extendedMobileDataInfo2.mobileShowMobileWhenWifiActive);
        boolean z5 = !signalIcon$MobileState2.dataSim;
        if (signalIcon$MobileState2.carrierNetworkChangeMode || z3) {
            i2 = -1;
        } else if (!mForceshowActiveRat) {
            if (z5) {
                i2 = getDataStateForSecondarySim();
            } else if (z4) {
                i2 = 2;
            } else {
                i2 = getDataStateForDataSim(serviceState, signalIcon$MobileState2.callState, signalIcon$MobileState2.wifiConnected, signalIcon$MobileState2.dataConnected, i, signalIcon$MobileState2.isDefault, z, signalIcon$MobileState2.userSetup && networkConfig2.showDataDisabledIcon && (!signalIcon$MobileState2.wifiConnected || extendedMobileDataInfo2.mobileShowMobileWhenWifiActive), extendedMobileDataInfo2.mobileShowMobileWhenWifiActive, networkConfig);
            }
        }
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("dataState = ");
            sb.append(i2);
            sb.append(" dataDisabled = ");
            sb.append(z);
            sb.append(" dataConnected = ");
            sb.append(signalIcon$MobileState2.dataConnected);
            sb.append(" dataConnectionState = ");
            sb.append(i);
            sb.append(" callState = ");
            sb.append(signalIcon$MobileState2.callState);
            sb.append(" wifiConnected = ");
            sb.append(signalIcon$MobileState2.wifiConnected);
            sb.append(" isNetworkDefault = ");
            sb.append(signalIcon$MobileState2.isDefault);
            sb.append(" carrierNetworkChangeMode = ");
            sb.append(signalIcon$MobileState2.carrierNetworkChangeMode);
            sb.append(" mForceshowActiveRat = ");
            sb.append(mForceshowActiveRat);
            sb.append(" mShowDualRat = ");
            sb.append(mShowDualRat);
            sb.append(" showDisableRatWhenRoamingDataOff = ");
            sb.append(networkConfig2.showDisableRatWhenRoamingDataOff);
            sb.append(" showDisableRatWhenDataDisconnected = ");
            sb.append(networkConfig2.showDisableRatWhenDataDisconnected);
            sb.append(" isSecondarySim = ");
            sb.append(!signalIcon$MobileState2.dataSim);
            sb.append(" showNoIcon = ");
            sb.append(z3);
            sb.append(" showMobileWhenWifiActive = ");
            sb.append(extendedMobileDataInfo2.mobileShowMobileWhenWifiActive);
            Log.i("NetworkStateTracker", sb.toString());
        }
        return i2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002e, code lost:
        if (r8 != false) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0035, code lost:
        if (r3.getVoiceRegState() == 0) goto L_0x000b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003c, code lost:
        if (r11 != false) goto L_0x000b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getDataStateForDataSim(android.telephony.ServiceState r3, int r4, boolean r5, boolean r6, int r7, boolean r8, boolean r9, boolean r10, boolean r11, com.android.systemui.moto.NetworkConfig r12) {
        /*
            r0 = 2
            r1 = 0
            r2 = 1
            if (r9 == 0) goto L_0x001a
            boolean r3 = com.android.systemui.moto.MotoFeature.isPrcProduct()
            if (r3 == 0) goto L_0x000d
        L_0x000b:
            r0 = r2
            goto L_0x004e
        L_0x000d:
            if (r10 == 0) goto L_0x004d
            if (r11 == 0) goto L_0x004e
            if (r5 == 0) goto L_0x004e
            boolean r3 = isMultipleSimDevice()
            if (r3 == 0) goto L_0x004e
            goto L_0x000b
        L_0x001a:
            if (r3 == 0) goto L_0x0038
            if (r4 == 0) goto L_0x0038
            if (r5 == 0) goto L_0x0024
            if (r11 != 0) goto L_0x0024
            if (r8 == 0) goto L_0x004d
        L_0x0024:
            int r4 = r3.getDataRegState()
            if (r4 != 0) goto L_0x0031
            if (r6 == 0) goto L_0x000b
            if (r5 == 0) goto L_0x0041
            if (r8 == 0) goto L_0x000b
            goto L_0x0041
        L_0x0031:
            int r3 = r3.getVoiceRegState()
            if (r3 != 0) goto L_0x004d
            goto L_0x000b
        L_0x0038:
            if (r5 == 0) goto L_0x003f
            if (r8 != 0) goto L_0x003f
            if (r11 == 0) goto L_0x004d
            goto L_0x000b
        L_0x003f:
            if (r6 == 0) goto L_0x0043
        L_0x0041:
            r0 = r1
            goto L_0x004e
        L_0x0043:
            r3 = 3
            if (r7 != r3) goto L_0x004d
            if (r6 != 0) goto L_0x000b
            boolean r3 = r12.showDisableRatWhenDataDisconnected
            if (r3 == 0) goto L_0x000b
            goto L_0x004e
        L_0x004d:
            r0 = -1
        L_0x004e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.moto.NetworkStateTracker.getDataStateForDataSim(android.telephony.ServiceState, int, boolean, boolean, int, boolean, boolean, boolean, boolean, com.android.systemui.moto.NetworkConfig):int");
    }

    private static int getDataStateForSecondarySim() {
        return mShowDualRat ? 1 : -1;
    }

    public static void updateExtendedInfo(ExtendedMobileDataInfo extendedMobileDataInfo, ServiceState serviceState, boolean z, boolean z2, boolean z3, int i, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, int i2, boolean z11, boolean z12) {
        String str;
        ExtendedMobileDataInfo extendedMobileDataInfo2 = extendedMobileDataInfo;
        boolean z13 = z;
        boolean z14 = z2;
        boolean z15 = z3;
        boolean z16 = z9;
        boolean z17 = z10;
        boolean z18 = z11;
        extendedMobileDataInfo.resetIcons();
        int activityState = getActivityState(z5, z6, z2, z15);
        extendedMobileDataInfo2.isShowVzwRat = z12;
        extendedMobileDataInfo2.isShowAttRat = z18;
        boolean z19 = extendedMobileDataInfo2.enableActivityIconOnSB;
        boolean z20 = false;
        if (z19) {
            extendedMobileDataInfo2.activityIcon = new NetworkController.IconState(z, (!z4 || !z19 || (z18 && z17)) ? 0 : CarrierIcons.ActivityIcon.sbActivity(activityState, z18, z17), "");
            extendedMobileDataInfo2.activityIconForQSCarrier = new NetworkController.IconState(z, z4 ? CarrierIcons.ActivityIcon.sbActivity(activityState, z18, false) : 0, "");
        } else if (MotoFeature.isPrcProduct()) {
            extendedMobileDataInfo2.activityIcon = new NetworkController.IconState(z, z4 ? CarrierIcons.ActivityIcon.sbActivity(activityState, false, z17) : 0, "");
            extendedMobileDataInfo2.activityIconForQSCarrier = new NetworkController.IconState(z, z4 ? CarrierIcons.ActivityIcon.sbActivity(activityState, false, false) : 0, "");
        } else {
            extendedMobileDataInfo2.activityIcon = null;
            extendedMobileDataInfo2.activityIconForQSCarrier = null;
        }
        boolean z21 = extendedMobileDataInfo2.enableCustomActivityIconOnQS;
        extendedMobileDataInfo2.mQsIn = z21 && z14;
        if (z21 && z15) {
            z20 = true;
        }
        extendedMobileDataInfo2.mQsOut = z20;
        if (z8 || z16) {
            ServiceState serviceState2 = serviceState;
            int roamingState = getRoamingState(serviceState, z7, z16);
            if (z8) {
                str = extendedMobileDataInfo2.subContext.getResources().getString(R$string.data_connection_roaming);
            } else {
                str = extendedMobileDataInfo2.subContext.getResources().getString(R$string.zz_moto_data_connection_femtocell);
            }
            extendedMobileDataInfo2.roamIcon = new NetworkController.IconState(true, CarrierIcons.RoamingIcon.sbRoaming(z17, roamingState), str);
        }
        extendedMobileDataInfo2.rejectCode = i2;
    }

    private static int getRoamingState(ServiceState serviceState, boolean z, boolean z2) {
        if (z2) {
            return 4;
        }
        if (z && serviceState != null) {
            serviceState.getCdmaEriIconMode();
        }
        return 0;
    }
}
