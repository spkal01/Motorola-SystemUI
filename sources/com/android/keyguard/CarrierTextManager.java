package com.android.keyguard;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.R$string;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.telephony.TelephonyListenerManager;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class CarrierTextManager {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private final Executor mBgExecutor;
    protected final KeyguardUpdateMonitorCallback mCallback;
    /* access modifiers changed from: private */
    public CarrierTextCallback mCarrierTextCallback;
    private final Context mContext;
    private final boolean mIsEmergencyCallCapable;
    protected KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final Executor mMainExecutor;
    /* access modifiers changed from: private */
    public final AtomicBoolean mNetworkSupported;
    private final TelephonyCallback.ActiveDataSubscriptionIdListener mPhoneStateListener;
    private final CharSequence mSeparator;
    private final boolean mShowAirplaneMode;
    private boolean mShowAirplaneModeForWFC;
    private final boolean mShowMissingSim;
    /* access modifiers changed from: private */
    public final boolean[] mSimErrorState;
    /* access modifiers changed from: private */
    public final int mSimSlotsNumber;
    /* access modifiers changed from: private */
    public boolean mTelephonyCapable;
    private final TelephonyListenerManager mTelephonyListenerManager;
    private final TelephonyManager mTelephonyManager;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    private final WakefulnessLifecycle.Observer mWakefulnessObserver;
    private final WifiManager mWifiManager;

    public interface CarrierTextCallback {
        void finishedWakingUp() {
        }

        void startedGoingToSleep() {
        }

        void updateCarrierInfo(CarrierTextCallbackInfo carrierTextCallbackInfo) {
        }
    }

    private enum StatusMode {
        Normal,
        NetworkLocked,
        SimMissing,
        SimMissingLocked,
        SimPukLocked,
        SimLocked,
        SimPermDisabled,
        SimNotReady,
        SimIoError,
        SimUnknown
    }

    private CarrierTextManager(Context context, CharSequence charSequence, boolean z, boolean z2, WifiManager wifiManager, TelephonyManager telephonyManager, TelephonyListenerManager telephonyListenerManager, WakefulnessLifecycle wakefulnessLifecycle, Executor executor, Executor executor2, KeyguardUpdateMonitor keyguardUpdateMonitor) {
        this.mNetworkSupported = new AtomicBoolean();
        this.mWakefulnessObserver = new WakefulnessLifecycle.Observer() {
            public void onFinishedWakingUp() {
                CarrierTextCallback access$000 = CarrierTextManager.this.mCarrierTextCallback;
                if (access$000 != null) {
                    access$000.finishedWakingUp();
                }
            }

            public void onStartedGoingToSleep() {
                CarrierTextCallback access$000 = CarrierTextManager.this.mCarrierTextCallback;
                if (access$000 != null) {
                    access$000.startedGoingToSleep();
                }
            }
        };
        this.mCallback = new KeyguardUpdateMonitorCallback() {
            public void onRefreshCarrierInfo() {
                if (CarrierTextManager.DEBUG) {
                    Log.d("CarrierTextController", "onRefreshCarrierInfo(), mTelephonyCapable: " + Boolean.toString(CarrierTextManager.this.mTelephonyCapable));
                }
                CarrierTextManager.this.updateCarrierText();
            }

            public void onTelephonyCapable(boolean z) {
                if (CarrierTextManager.DEBUG) {
                    Log.d("CarrierTextController", "onTelephonyCapable() mTelephonyCapable: " + Boolean.toString(z));
                }
                boolean unused = CarrierTextManager.this.mTelephonyCapable = z;
                CarrierTextManager.this.updateCarrierText();
            }

            public void onSimStateChanged(int i, int i2, int i3) {
                if (i2 < 0 || i2 >= CarrierTextManager.this.mSimSlotsNumber) {
                    Log.d("CarrierTextController", "onSimStateChanged() - slotId invalid: " + i2 + " mTelephonyCapable: " + Boolean.toString(CarrierTextManager.this.mTelephonyCapable));
                    return;
                }
                if (CarrierTextManager.DEBUG) {
                    Log.d("CarrierTextController", "onSimStateChanged: " + CarrierTextManager.this.getStatusForIccState(i3));
                }
                if (CarrierTextManager.this.getStatusForIccState(i3) == StatusMode.SimIoError) {
                    CarrierTextManager.this.mSimErrorState[i2] = true;
                    CarrierTextManager.this.updateCarrierText();
                } else if (CarrierTextManager.this.mSimErrorState[i2]) {
                    CarrierTextManager.this.mSimErrorState[i2] = false;
                    CarrierTextManager.this.updateCarrierText();
                }
            }
        };
        this.mPhoneStateListener = new TelephonyCallback.ActiveDataSubscriptionIdListener() {
            public void onActiveDataSubscriptionIdChanged(int i) {
                if (CarrierTextManager.this.mNetworkSupported.get() && CarrierTextManager.this.mCarrierTextCallback != null) {
                    CarrierTextManager.this.updateCarrierText();
                }
            }
        };
        this.mContext = context;
        this.mIsEmergencyCallCapable = telephonyManager.isVoiceCapable();
        this.mShowAirplaneMode = z;
        this.mShowMissingSim = z2;
        this.mWifiManager = wifiManager;
        this.mTelephonyManager = telephonyManager;
        this.mSeparator = charSequence;
        this.mTelephonyListenerManager = telephonyListenerManager;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        int supportedModemCount = getTelephonyManager().getSupportedModemCount();
        this.mSimSlotsNumber = supportedModemCount;
        this.mSimErrorState = new boolean[supportedModemCount];
        this.mMainExecutor = executor;
        this.mBgExecutor = executor2;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        executor2.execute(new CarrierTextManager$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        boolean hasSystemFeature = this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
        if (hasSystemFeature && this.mNetworkSupported.compareAndSet(false, hasSystemFeature)) {
            lambda$setListening$4(this.mCarrierTextCallback);
        }
    }

    private TelephonyManager getTelephonyManager() {
        return this.mTelephonyManager;
    }

    private CharSequence updateCarrierTextWithSimIoError(CharSequence charSequence, CharSequence[] charSequenceArr, int[] iArr, boolean z) {
        CharSequence carrierTextForSimState = getCarrierTextForSimState(8, "");
        for (int i = 0; i < getTelephonyManager().getActiveModemCount(); i++) {
            if (this.mSimErrorState[i]) {
                if (z) {
                    return concatenate(carrierTextForSimState, getContext().getText(17040169), this.mSeparator);
                }
                if (iArr[i] != -1) {
                    int i2 = iArr[i];
                    charSequenceArr[i2] = concatenate(carrierTextForSimState, charSequenceArr[i2], this.mSeparator);
                } else {
                    charSequence = concatenate(charSequence, carrierTextForSimState, this.mSeparator);
                }
            }
        }
        return charSequence;
    }

    /* access modifiers changed from: private */
    /* renamed from: handleSetListening */
    public void lambda$setListening$4(CarrierTextCallback carrierTextCallback) {
        if (carrierTextCallback != null) {
            this.mCarrierTextCallback = carrierTextCallback;
            if (this.mNetworkSupported.get()) {
                this.mMainExecutor.execute(new CarrierTextManager$$ExternalSyntheticLambda4(this));
                this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
                this.mTelephonyListenerManager.addActiveDataSubscriptionIdListener(this.mPhoneStateListener);
                return;
            }
            this.mMainExecutor.execute(new CarrierTextManager$$ExternalSyntheticLambda0(carrierTextCallback));
            return;
        }
        this.mCarrierTextCallback = null;
        this.mMainExecutor.execute(new CarrierTextManager$$ExternalSyntheticLambda3(this));
        this.mWakefulnessLifecycle.removeObserver(this.mWakefulnessObserver);
        this.mTelephonyListenerManager.removeActiveDataSubscriptionIdListener(this.mPhoneStateListener);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleSetListening$1() {
        this.mKeyguardUpdateMonitor.registerCallback(this.mCallback);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleSetListening$3() {
        this.mKeyguardUpdateMonitor.removeCallback(this.mCallback);
    }

    public void setListening(CarrierTextCallback carrierTextCallback) {
        this.mBgExecutor.execute(new CarrierTextManager$$ExternalSyntheticLambda5(this, carrierTextCallback));
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getSubscriptionInfo() {
        return this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x050c  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01d8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateCarrierText() {
        /*
            r26 = this;
            r0 = r26
            java.util.List r1 = r26.getSubscriptionInfo()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            int r3 = r1.size()
            int[] r8 = new int[r3]
            int r4 = r0.mSimSlotsNumber
            int[] r4 = new int[r4]
            r6 = 0
        L_0x0016:
            int r7 = r0.mSimSlotsNumber
            if (r6 >= r7) goto L_0x0020
            r7 = -1
            r4[r6] = r7
            int r6 = r6 + 1
            goto L_0x0016
        L_0x0020:
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r3]
            boolean r7 = DEBUG
            java.lang.String r9 = "CarrierTextController"
            if (r7 == 0) goto L_0x003d
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r10 = "updateCarrierText(): "
            r7.append(r10)
            r7.append(r3)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r9, r7)
        L_0x003d:
            r10 = 0
            r11 = 0
            r12 = 1
            r13 = 0
            r14 = 0
            r15 = 0
        L_0x0043:
            java.lang.String r16 = ""
            if (r10 >= r3) goto L_0x01e8
            java.lang.Object r17 = r1.get(r10)
            android.telephony.SubscriptionInfo r17 = (android.telephony.SubscriptionInfo) r17
            int r7 = r17.getSubscriptionId()
            java.lang.Object r17 = r1.get(r10)
            android.telephony.SubscriptionInfo r17 = (android.telephony.SubscriptionInfo) r17
            int r5 = r17.getSimSlotIndex()
            r6[r10] = r16
            r8[r10] = r7
            java.lang.Object r17 = r1.get(r10)
            android.telephony.SubscriptionInfo r17 = (android.telephony.SubscriptionInfo) r17
            int r17 = r17.getSimSlotIndex()
            r4[r17] = r10
            r17 = r12
            com.android.keyguard.KeyguardUpdateMonitor r12 = r0.mKeyguardUpdateMonitor
            int r12 = r12.getSimState(r7)
            java.lang.Object r20 = r1.get(r10)
            android.telephony.SubscriptionInfo r20 = (android.telephony.SubscriptionInfo) r20
            java.lang.CharSequence r20 = r20.getCarrierName()
            r21 = r8
            com.android.keyguard.KeyguardUpdateMonitor r8 = r0.mKeyguardUpdateMonitor
            java.lang.String r8 = r8.getBroadcastSPNForSubId(r7)
            r22 = r15
            com.android.keyguard.KeyguardUpdateMonitor r15 = r0.mKeyguardUpdateMonitor
            java.lang.String r15 = r15.getBroadcastPLMNForSubId(r7)
            if (r8 == 0) goto L_0x00ce
            int r23 = r8.length()
            if (r23 <= 0) goto L_0x00ce
            if (r15 == 0) goto L_0x00ce
            int r23 = r15.length()
            if (r23 <= 0) goto L_0x00ce
            boolean r20 = r8.equals(r15)
            if (r20 == 0) goto L_0x00ac
            r23 = r4
            r20 = r8
        L_0x00a7:
            r25 = r13
            r24 = r14
            goto L_0x00d1
        L_0x00ac:
            android.content.Context r20 = r26.getContext()
            r23 = r4
            android.content.res.Resources r4 = r20.getResources()
            r24 = r14
            int r14 = com.android.systemui.R$string.carrier_name_with_plmn
            r25 = r13
            r13 = 2
            java.lang.Object[] r13 = new java.lang.Object[r13]
            r19 = 0
            r13[r19] = r15
            r18 = 1
            r13[r18] = r8
            java.lang.String r4 = r4.getString(r14, r13)
            r20 = r4
            goto L_0x00d1
        L_0x00ce:
            r23 = r4
            goto L_0x00a7
        L_0x00d1:
            if (r15 == 0) goto L_0x00de
            java.lang.String r4 = "rejectCode"
            boolean r4 = r15.equals(r4)
            if (r4 == 0) goto L_0x00de
            r4 = r16
            goto L_0x00e0
        L_0x00de:
            r4 = r20
        L_0x00e0:
            java.lang.CharSequence r8 = r0.getCarrierTextForSimState(r12, r4)
            boolean r13 = DEBUG
            if (r13 == 0) goto L_0x010c
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "Handling (subId="
            r14.append(r15)
            r14.append(r7)
            java.lang.String r15 = "): "
            r14.append(r15)
            r14.append(r12)
            java.lang.String r15 = " "
            r14.append(r15)
            r14.append(r4)
            java.lang.String r4 = r14.toString()
            android.util.Log.d(r9, r4)
        L_0x010c:
            r4 = 6
            if (r12 != r4) goto L_0x0119
            com.android.keyguard.KeyguardUpdateMonitor r4 = r0.mKeyguardUpdateMonitor
            boolean r4 = r4.isEsim(r5)
            if (r4 == 0) goto L_0x0119
            r4 = 1
            goto L_0x011a
        L_0x0119:
            r4 = 0
        L_0x011a:
            if (r8 == 0) goto L_0x013e
            if (r4 != 0) goto L_0x013e
            r6[r10] = r8
            com.android.keyguard.CarrierTextManager$CarrierTextForSim r4 = new com.android.keyguard.CarrierTextManager$CarrierTextForSim
            java.lang.Object r5 = r1.get(r10)
            android.telephony.SubscriptionInfo r5 = (android.telephony.SubscriptionInfo) r5
            int r5 = r5.getSubscriptionId()
            java.lang.Object r14 = r1.get(r10)
            android.telephony.SubscriptionInfo r14 = (android.telephony.SubscriptionInfo) r14
            int r14 = r14.getSimSlotIndex()
            r4.<init>(r5, r14, r8)
            r2.add(r4)
            r17 = 0
        L_0x013e:
            r4 = 5
            if (r12 != r4) goto L_0x01d8
            com.android.keyguard.KeyguardUpdateMonitor r4 = r0.mKeyguardUpdateMonitor
            java.util.HashMap<java.lang.Integer, android.telephony.ServiceState> r4 = r4.mServiceStates
            java.lang.Integer r5 = java.lang.Integer.valueOf(r7)
            java.lang.Object r4 = r4.get(r5)
            android.telephony.ServiceState r4 = (android.telephony.ServiceState) r4
            if (r4 == 0) goto L_0x019d
            int r5 = r4.getDataRegistrationState()
            if (r5 != 0) goto L_0x019d
            int r5 = r4.getRilDataRadioTechnology()
            r8 = 18
            if (r5 != r8) goto L_0x017d
            android.net.wifi.WifiManager r5 = r0.mWifiManager
            if (r5 == 0) goto L_0x019d
            boolean r5 = r5.isWifiEnabled()
            if (r5 == 0) goto L_0x019d
            android.net.wifi.WifiManager r5 = r0.mWifiManager
            android.net.wifi.WifiInfo r5 = r5.getConnectionInfo()
            if (r5 == 0) goto L_0x019d
            android.net.wifi.WifiManager r5 = r0.mWifiManager
            android.net.wifi.WifiInfo r5 = r5.getConnectionInfo()
            java.lang.String r5 = r5.getBSSID()
            if (r5 == 0) goto L_0x019d
        L_0x017d:
            if (r13 == 0) goto L_0x019b
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r8 = "SIM ready and in service: subId="
            r5.append(r8)
            r5.append(r7)
            java.lang.String r8 = ", ss="
            r5.append(r8)
            r5.append(r4)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r9, r5)
        L_0x019b:
            r13 = 1
            goto L_0x019f
        L_0x019d:
            r13 = r25
        L_0x019f:
            if (r4 == 0) goto L_0x01b2
            r5 = 1
            if (r3 != r5) goto L_0x01b2
            int r8 = r4.getDataRegState()
            if (r8 != r5) goto L_0x01b2
            boolean r4 = r4.isEmergencyOnly()
            if (r4 != 0) goto L_0x01b2
            r14 = 1
            goto L_0x01b4
        L_0x01b2:
            r14 = r24
        L_0x01b4:
            android.content.Context r4 = r0.mContext
            android.content.res.Resources r4 = android.telephony.SubscriptionManager.getResourcesForSubId(r4, r7)
            int r5 = com.android.systemui.R$bool.config_show_airplane_when_wfc_unavailable
            boolean r4 = r4.getBoolean(r5)
            if (r4 == 0) goto L_0x01c3
            r11 = 1
        L_0x01c3:
            android.content.Context r4 = r0.mContext
            android.telephony.TelephonyManager r4 = android.telephony.TelephonyManager.from(r4)
            android.telephony.TelephonyManager r4 = r4.createForSubscriptionId(r7)
            boolean r4 = r4.isWifiCallingAvailable()
            if (r4 == 0) goto L_0x01d5
            r15 = 1
            goto L_0x01de
        L_0x01d5:
            r15 = r22
            goto L_0x01de
        L_0x01d8:
            r15 = r22
            r14 = r24
            r13 = r25
        L_0x01de:
            int r10 = r10 + 1
            r12 = r17
            r8 = r21
            r4 = r23
            goto L_0x0043
        L_0x01e8:
            r23 = r4
            r21 = r8
            r17 = r12
            r25 = r13
            r24 = r14
            r22 = r15
            r0.mShowAirplaneModeForWFC = r11
            com.android.keyguard.KeyguardUpdateMonitor r4 = r0.mKeyguardUpdateMonitor
            java.util.HashMap r4 = r4.getInvalidCards()
            boolean r5 = DEBUG
            if (r5 == 0) goto L_0x0214
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Currently invalidCards: "
            r5.append(r7)
            r5.append(r4)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r9, r5)
        L_0x0214:
            boolean r5 = r4.isEmpty()
            r7 = 17040169(0x1040329, float:2.4246838E-38)
            if (r5 != 0) goto L_0x026b
            if (r3 != 0) goto L_0x0228
            android.content.Context r5 = r26.getContext()
            java.lang.CharSequence r5 = r5.getText(r7)
            goto L_0x022a
        L_0x0228:
            r5 = r16
        L_0x022a:
            java.util.Set r4 = r4.entrySet()
            java.util.Iterator r4 = r4.iterator()
        L_0x0232:
            boolean r8 = r4.hasNext()
            if (r8 == 0) goto L_0x0266
            java.lang.Object r8 = r4.next()
            java.util.Map$Entry r8 = (java.util.Map.Entry) r8
            java.lang.Object r10 = r8.getKey()
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            java.lang.Object r8 = r8.getValue()
            com.android.keyguard.KeyguardUpdateMonitor$InvalidCardData r8 = (com.android.keyguard.KeyguardUpdateMonitor.InvalidCardData) r8
            java.lang.String r11 = r8.plmn
            if (r11 == 0) goto L_0x0253
            r5 = r11
        L_0x0253:
            r11 = 8
            java.lang.CharSequence r5 = r0.getCarrierTextForSimState(r11, r5)
            com.android.keyguard.CarrierTextManager$CarrierTextForSim r11 = new com.android.keyguard.CarrierTextManager$CarrierTextForSim
            int r8 = r8.slotId
            r11.<init>(r10, r8, r5)
            r2.add(r11)
            r5 = r16
            goto L_0x0232
        L_0x0266:
            java.util.Collections.sort(r2)
            r12 = 0
            goto L_0x026d
        L_0x026b:
            r12 = r17
        L_0x026d:
            boolean r4 = DEBUG
            if (r4 == 0) goto L_0x0286
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "slotTextList: "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r9, r4)
        L_0x0286:
            android.content.Context r4 = r0.mContext
            android.content.res.Resources r4 = r4.getResources()
            android.content.res.Configuration r4 = r4.getConfiguration()
            int r4 = r4.getLayoutDirection()
            r5 = 1
            if (r4 == r5) goto L_0x0299
            r4 = 1
            goto L_0x029a
        L_0x0299:
            r4 = 0
        L_0x029a:
            r5 = 0
            if (r4 == 0) goto L_0x02b6
            java.util.Iterator r2 = r2.iterator()
        L_0x02a1:
            boolean r4 = r2.hasNext()
            if (r4 == 0) goto L_0x0329
            java.lang.Object r4 = r2.next()
            com.android.keyguard.CarrierTextManager$CarrierTextForSim r4 = (com.android.keyguard.CarrierTextManager.CarrierTextForSim) r4
            java.lang.CharSequence r4 = r4.carrierTextForSimState
            java.lang.CharSequence r8 = r0.mSeparator
            java.lang.CharSequence r5 = concatenate(r5, r4, r8)
            goto L_0x02a1
        L_0x02b6:
            int r4 = r2.size()
            if (r4 <= 0) goto L_0x0311
            r8 = 1
            int r4 = r4 - r8
        L_0x02be:
            if (r4 < 0) goto L_0x02f3
            java.lang.Object r8 = r2.get(r4)
            com.android.keyguard.CarrierTextManager$CarrierTextForSim r8 = (com.android.keyguard.CarrierTextManager.CarrierTextForSim) r8
            if (r8 == 0) goto L_0x02f0
            java.lang.CharSequence r10 = r8.carrierTextForSimState
            boolean r10 = android.text.TextUtils.isEmpty(r10)
            if (r10 != 0) goto L_0x02f0
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "⁨"
            r10.append(r11)
            java.lang.CharSequence r8 = r8.carrierTextForSimState
            r10.append(r8)
            java.lang.String r8 = "⁩"
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            java.lang.CharSequence r10 = r0.mSeparator
            java.lang.CharSequence r5 = concatenate(r5, r8, r10)
        L_0x02f0:
            int r4 = r4 + -1
            goto L_0x02be
        L_0x02f3:
            boolean r2 = android.text.TextUtils.isEmpty(r5)
            if (r2 != 0) goto L_0x0311
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "‪"
            r2.append(r4)
            r2.append(r5)
            java.lang.String r4 = "‬"
            r2.append(r4)
            java.lang.String r5 = r2.toString()
        L_0x0311:
            boolean r2 = DEBUG
            if (r2 == 0) goto L_0x0329
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "displayText = "
            r2.append(r4)
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r9, r2)
        L_0x0329:
            if (r12 == 0) goto L_0x0335
            com.android.keyguard.KeyguardUpdateMonitor r2 = r0.mKeyguardUpdateMonitor
            boolean r2 = r2.hasSIM()
            if (r2 != 0) goto L_0x0335
            r2 = 1
            goto L_0x0336
        L_0x0335:
            r2 = 0
        L_0x0336:
            boolean r4 = DEBUG
            if (r4 == 0) goto L_0x0356
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "allSimsMissing = "
            r8.append(r10)
            r8.append(r2)
            java.lang.String r10 = " displayText = "
            r8.append(r10)
            r8.append(r5)
            java.lang.String r8 = r8.toString()
            android.util.Log.d(r9, r8)
        L_0x0356:
            java.lang.String r8 = "Emergency only for slot: "
            java.lang.String r10 = " ,PLMN: "
            java.lang.String r11 = "Slot: "
            java.lang.String r12 = " slots"
            if (r2 == 0) goto L_0x0422
            if (r25 != 0) goto L_0x0422
            if (r3 == 0) goto L_0x0379
            java.lang.String r3 = r26.getMissingSimMessage()
            r13 = 0
            java.lang.Object r1 = r1.get(r13)
            android.telephony.SubscriptionInfo r1 = (android.telephony.SubscriptionInfo) r1
            java.lang.CharSequence r1 = r1.getCarrierName()
            java.lang.CharSequence r5 = r0.makeCarrierStringOnEmergencyCapable(r3, r1)
            goto L_0x0423
        L_0x0379:
            r13 = 0
            android.telephony.TelephonyManager r1 = android.telephony.TelephonyManager.getDefault()
            int r1 = r1.getPhoneCount()
            if (r4 == 0) goto L_0x039b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "No SIMs, no subscriptions, "
            r3.append(r4)
            r3.append(r1)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r9, r3)
        L_0x039b:
            android.content.res.Resources r3 = android.content.res.Resources.getSystem()
            java.lang.CharSequence r3 = r3.getText(r7)
            java.lang.String r3 = r3.toString()
            r4 = r13
        L_0x03a8:
            if (r4 >= r1) goto L_0x03ee
            com.android.keyguard.KeyguardUpdateMonitor r5 = r0.mKeyguardUpdateMonitor
            java.lang.String r5 = r5.getBroadcastPLMNForSlot(r4)
            boolean r14 = DEBUG
            if (r14 == 0) goto L_0x03cc
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r11)
            r15.append(r4)
            r15.append(r10)
            r15.append(r5)
            java.lang.String r15 = r15.toString()
            android.util.Log.d(r9, r15)
        L_0x03cc:
            if (r5 == 0) goto L_0x03eb
            boolean r5 = r5.equals(r3)
            if (r5 == 0) goto L_0x03eb
            if (r14 == 0) goto L_0x03e8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r9, r1)
        L_0x03e8:
            r19 = 1
            goto L_0x03f0
        L_0x03eb:
            int r4 = r4 + 1
            goto L_0x03a8
        L_0x03ee:
            r19 = r13
        L_0x03f0:
            if (r19 != 0) goto L_0x0401
            android.content.res.Resources r1 = android.content.res.Resources.getSystem()
            r3 = 17040554(0x10404aa, float:2.4247917E-38)
            java.lang.CharSequence r1 = r1.getText(r3)
            java.lang.String r3 = r1.toString()
        L_0x0401:
            java.lang.String r1 = r26.getMissingSimMessage()
            java.lang.CharSequence r5 = r0.makeCarrierStringOnEmergencyCapable(r1, r3)
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0423
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "makeCarrierStringOnEmergencyCapable - displayText: "
            r1.append(r3)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r9, r1)
            goto L_0x0423
        L_0x0422:
            r13 = 0
        L_0x0423:
            if (r24 == 0) goto L_0x04b9
            android.telephony.TelephonyManager r1 = android.telephony.TelephonyManager.getDefault()
            int r1 = r1.getPhoneCount()
            boolean r3 = DEBUG
            if (r3 == 0) goto L_0x0448
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "isSpecialForInvalidSim = true, "
            r3.append(r4)
            r3.append(r1)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r9, r3)
        L_0x0448:
            android.content.res.Resources r3 = android.content.res.Resources.getSystem()
            java.lang.CharSequence r3 = r3.getText(r7)
            java.lang.String r3 = r3.toString()
            r4 = r13
        L_0x0455:
            if (r4 >= r1) goto L_0x049b
            com.android.keyguard.KeyguardUpdateMonitor r7 = r0.mKeyguardUpdateMonitor
            java.lang.String r7 = r7.getBroadcastPLMNForSlot(r4)
            boolean r12 = DEBUG
            if (r12 == 0) goto L_0x0479
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r11)
            r14.append(r4)
            r14.append(r10)
            r14.append(r7)
            java.lang.String r14 = r14.toString()
            android.util.Log.d(r9, r14)
        L_0x0479:
            if (r7 == 0) goto L_0x0498
            boolean r7 = r7.equals(r3)
            if (r7 == 0) goto L_0x0498
            if (r12 == 0) goto L_0x0495
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r9, r1)
        L_0x0495:
            r19 = 1
            goto L_0x049d
        L_0x0498:
            int r4 = r4 + 1
            goto L_0x0455
        L_0x049b:
            r19 = r13
        L_0x049d:
            if (r19 == 0) goto L_0x04a0
            r5 = r3
        L_0x04a0:
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x04b9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "special displayText = "
            r1.append(r3)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r9, r1)
        L_0x04b9:
            boolean r1 = android.text.TextUtils.isEmpty(r5)
            if (r1 == 0) goto L_0x04dd
            java.lang.CharSequence r1 = r0.mSeparator
            java.lang.CharSequence r5 = joinNotEmpty(r1, r6)
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x04dd
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "joinNotEmpty - displayText: "
            r1.append(r3)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r9, r1)
        L_0x04dd:
            r1 = r23
            java.lang.CharSequence r1 = r0.updateCarrierTextWithSimIoError(r5, r6, r1, r2)
            if (r25 != 0) goto L_0x04f4
            android.content.Context r3 = r0.mContext
            boolean r3 = com.android.settingslib.WirelessUtils.isAirplaneModeOn(r3)
            if (r3 == 0) goto L_0x04f4
            java.lang.String r1 = r26.getAirplaneModeMessage()
        L_0x04f1:
            r5 = r1
            r13 = 1
            goto L_0x0508
        L_0x04f4:
            boolean r3 = r0.mShowAirplaneModeForWFC
            if (r3 == 0) goto L_0x0507
            if (r22 != 0) goto L_0x0507
            android.content.Context r3 = r0.mContext
            boolean r3 = com.android.settingslib.WirelessUtils.isAirplaneModeOn(r3)
            if (r3 == 0) goto L_0x0507
            java.lang.String r1 = r26.getAirplaneModeMessage()
            goto L_0x04f1
        L_0x0507:
            r5 = r1
        L_0x0508:
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0520
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Final - displayText: "
            r1.append(r3)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r9, r1)
        L_0x0520:
            com.android.keyguard.CarrierTextManager$CarrierTextCallbackInfo r1 = new com.android.keyguard.CarrierTextManager$CarrierTextCallbackInfo
            r3 = 1
            r7 = r2 ^ 1
            r4 = r1
            r8 = r21
            r9 = r13
            r4.<init>(r5, r6, r7, r8, r9)
            r0.postToCallback(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.CarrierTextManager.updateCarrierText():void");
    }

    /* access modifiers changed from: protected */
    public void postToCallback(CarrierTextCallbackInfo carrierTextCallbackInfo) {
        CarrierTextCallback carrierTextCallback = this.mCarrierTextCallback;
        if (carrierTextCallback != null) {
            this.mMainExecutor.execute(new CarrierTextManager$$ExternalSyntheticLambda1(carrierTextCallback, carrierTextCallbackInfo));
        } else {
            Log.e("CarrierTextController", "postToCallback - CarrierTextCallback is null.");
        }
    }

    private Context getContext() {
        return this.mContext;
    }

    private String getMissingSimMessage() {
        return (!this.mShowMissingSim || !this.mTelephonyCapable) ? "" : getContext().getString(R$string.keyguard_missing_sim_message_short);
    }

    private String getAirplaneModeMessage() {
        if (this.mShowAirplaneMode || this.mShowAirplaneModeForWFC) {
            return getContext().getString(R$string.airplane_mode);
        }
        return "";
    }

    /* renamed from: com.android.keyguard.CarrierTextManager$4 */
    static /* synthetic */ class C05684 {
        static final /* synthetic */ int[] $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode;

        /* JADX WARNING: Can't wrap try/catch for region: R(20:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|(3:19|20|22)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(22:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|22) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.keyguard.CarrierTextManager$StatusMode[] r0 = com.android.keyguard.CarrierTextManager.StatusMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode = r0
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.Normal     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimNotReady     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.NetworkLocked     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimMissing     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimPermDisabled     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimMissingLocked     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimLocked     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimPukLocked     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimIoError     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                int[] r0 = $SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.android.keyguard.CarrierTextManager$StatusMode r1 = com.android.keyguard.CarrierTextManager.StatusMode.SimUnknown     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r2 = 10
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.CarrierTextManager.C05684.<clinit>():void");
        }
    }

    private CharSequence getCarrierTextForSimState(int i, CharSequence charSequence) {
        switch (C05684.$SwitchMap$com$android$keyguard$CarrierTextManager$StatusMode[getStatusForIccState(i).ordinal()]) {
            case 1:
                return charSequence;
            case 2:
                return "";
            case 3:
                return makeCarrierStringOnEmergencyCapable(this.mContext.getText(R$string.keyguard_network_locked_message), charSequence);
            case 5:
                return makeCarrierStringOnEmergencyCapable(getContext().getText(R$string.keyguard_permanent_disabled_sim_message_short), charSequence);
            case 7:
                return makeCarrierStringOnLocked(getContext().getText(R$string.keyguard_sim_locked_message), charSequence);
            case 8:
                return makeCarrierStringOnLocked(getContext().getText(R$string.keyguard_sim_puk_locked_message), charSequence);
            case 9:
                return makeCarrierStringOnEmergencyCapable(getContext().getText(R$string.keyguard_sim_error_message_short), charSequence);
            default:
                return null;
        }
    }

    private CharSequence makeCarrierStringOnEmergencyCapable(CharSequence charSequence, CharSequence charSequence2) {
        return this.mIsEmergencyCallCapable ? concatenate(charSequence, charSequence2, this.mSeparator) : charSequence;
    }

    private CharSequence makeCarrierStringOnLocked(CharSequence charSequence, CharSequence charSequence2) {
        boolean z = !TextUtils.isEmpty(charSequence);
        boolean z2 = !TextUtils.isEmpty(charSequence2);
        if (z && z2) {
            return this.mContext.getString(R$string.keyguard_carrier_name_with_sim_locked_template, new Object[]{charSequence2, charSequence});
        } else if (z) {
            return charSequence;
        } else {
            return z2 ? charSequence2 : "";
        }
    }

    /* access modifiers changed from: private */
    public StatusMode getStatusForIccState(int i) {
        boolean z = true;
        if (this.mKeyguardUpdateMonitor.isDeviceProvisioned() || !(i == 1 || i == 7)) {
            z = false;
        }
        if (z) {
            return StatusMode.SimMissingLocked;
        }
        switch (i) {
            case 0:
                return StatusMode.SimUnknown;
            case 1:
                return StatusMode.SimMissing;
            case 2:
                return StatusMode.SimLocked;
            case 3:
                return StatusMode.SimPukLocked;
            case 4:
                return StatusMode.NetworkLocked;
            case 5:
                return StatusMode.Normal;
            case 6:
                return StatusMode.SimNotReady;
            case 7:
                return StatusMode.SimPermDisabled;
            case 8:
                return StatusMode.SimIoError;
            default:
                return StatusMode.SimUnknown;
        }
    }

    private static CharSequence concatenate(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
        boolean z = !TextUtils.isEmpty(charSequence);
        boolean z2 = !TextUtils.isEmpty(charSequence2);
        if (z && z2) {
            StringBuilder sb = new StringBuilder();
            sb.append(charSequence);
            sb.append(charSequence3);
            sb.append(charSequence2);
            return sb.toString();
        } else if (z) {
            return charSequence;
        } else {
            return z2 ? charSequence2 : "";
        }
    }

    private static CharSequence joinNotEmpty(CharSequence charSequence, CharSequence[] charSequenceArr) {
        int length = charSequenceArr.length;
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (!TextUtils.isEmpty(charSequenceArr[i])) {
                if (!TextUtils.isEmpty(sb)) {
                    sb.append(charSequence);
                }
                sb.append(charSequenceArr[i]);
            }
        }
        return sb.toString();
    }

    public static class Builder {
        private final Executor mBgExecutor;
        private final Context mContext;
        private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
        private final Executor mMainExecutor;
        private final String mSeparator;
        private boolean mShowAirplaneMode;
        private boolean mShowMissingSim;
        private final TelephonyListenerManager mTelephonyListenerManager;
        private final TelephonyManager mTelephonyManager;
        private final WakefulnessLifecycle mWakefulnessLifecycle;
        private final WifiManager mWifiManager;

        public Builder(Context context, Resources resources, WifiManager wifiManager, TelephonyManager telephonyManager, TelephonyListenerManager telephonyListenerManager, WakefulnessLifecycle wakefulnessLifecycle, Executor executor, Executor executor2, KeyguardUpdateMonitor keyguardUpdateMonitor) {
            this.mContext = context;
            this.mSeparator = resources.getString(17040521);
            this.mWifiManager = wifiManager;
            this.mTelephonyManager = telephonyManager;
            this.mTelephonyListenerManager = telephonyListenerManager;
            this.mWakefulnessLifecycle = wakefulnessLifecycle;
            this.mMainExecutor = executor;
            this.mBgExecutor = executor2;
            this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        }

        public Builder setShowAirplaneMode(boolean z) {
            this.mShowAirplaneMode = z;
            return this;
        }

        public Builder setShowMissingSim(boolean z) {
            this.mShowMissingSim = z;
            return this;
        }

        public CarrierTextManager build() {
            return new CarrierTextManager(this.mContext, this.mSeparator, this.mShowAirplaneMode, this.mShowMissingSim, this.mWifiManager, this.mTelephonyManager, this.mTelephonyListenerManager, this.mWakefulnessLifecycle, this.mMainExecutor, this.mBgExecutor, this.mKeyguardUpdateMonitor);
        }
    }

    public static final class CarrierTextCallbackInfo {
        public boolean airplaneMode;
        public final boolean anySimReady;
        public final CharSequence carrierText;
        public final CharSequence[] listOfCarriers;
        public final int[] subscriptionIds;

        public CarrierTextCallbackInfo(CharSequence charSequence, CharSequence[] charSequenceArr, boolean z, int[] iArr) {
            this(charSequence, charSequenceArr, z, iArr, false);
        }

        public CarrierTextCallbackInfo(CharSequence charSequence, CharSequence[] charSequenceArr, boolean z, int[] iArr, boolean z2) {
            this.carrierText = charSequence;
            this.listOfCarriers = charSequenceArr;
            this.anySimReady = z;
            this.subscriptionIds = iArr;
            this.airplaneMode = z2;
        }
    }

    class CarrierTextForSim implements Comparable<CarrierTextForSim> {
        CharSequence carrierTextForSimState;
        int slotId;
        int subId;

        CarrierTextForSim(int i, int i2, CharSequence charSequence) {
            this.subId = i;
            this.slotId = i2;
            this.carrierTextForSimState = charSequence;
        }

        public int compareTo(CarrierTextForSim carrierTextForSim) {
            int i = this.slotId;
            int i2 = carrierTextForSim.slotId;
            return i == i2 ? this.subId - carrierTextForSim.subId : i - i2;
        }
    }
}
