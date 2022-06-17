package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.ims.ImsManager;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.ImsStateController;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

public class ImsIconController {
    public static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("ImsIconController", 3));
    private static ImsIconController sInstance;
    private final SparseArray<Boolean> m4GState = new SparseArray<>();
    private Context mContext;
    private HDIconController mHDIconController;
    private Handler mHandler = null;
    private StatusBarIconController mIconController;
    private BroadcastReceiver mImsServiceReceiver = null;
    /* access modifiers changed from: private */
    public final SparseArray<ImsStateController> mImsStateController = new SparseArray<>();
    private String mSlotIMS;
    private boolean mWifiConnected = false;
    private boolean mWifiEnabled = false;

    public enum ImsType {
        INVALID_STATE,
        VOWIFI_NO_READY_STATE,
        VOWIFI_STATE,
        VOLTE_STATE
    }

    private ImsIconController(Context context) {
        this.mContext = context;
    }

    public static ImsIconController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ImsIconController(context);
        }
        return sInstance;
    }

    public void initialize() {
        this.mIconController = (StatusBarIconController) Dependency.get(StatusBarIconController.class);
        String string = this.mContext.getString(17041506);
        this.mSlotIMS = string;
        this.mIconController.setIcon(string, R$drawable.stat_sys_ims, (CharSequence) null);
        this.mIconController.setIconVisibility(this.mSlotIMS, false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        this.mHandler = new Handler();
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ImsIconController.this.updateIMS(intent);
            }
        }, intentFilter);
        this.mImsServiceReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("android:phone_id", -1);
                if (ImsIconController.DEBUG) {
                    Log.v("ImsIconController", "onReceive - " + intent.getAction() + " with phoneId = " + intExtra);
                }
                if (-1 != intExtra) {
                    ImsStateController imsStateController = (ImsStateController) ImsIconController.this.mImsStateController.get(intExtra);
                    if ("com.android.ims.IMS_SERVICE_UP".equalsIgnoreCase(intent.getAction())) {
                        if (imsStateController != null) {
                            imsStateController.registerImsCallback();
                        }
                    } else if ("com.android.ims.IMS_SERVICE_DOWN".equalsIgnoreCase(intent.getAction())) {
                        ImsIconController.this.cleanAndUnregister(imsStateController);
                    }
                }
            }
        };
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.android.ims.IMS_SERVICE_UP");
        intentFilter2.addAction("com.android.ims.IMS_SERVICE_DOWN");
        this.mContext.registerReceiver(this.mImsServiceReceiver, intentFilter2);
        if (MotoFeature.isPrcProduct()) {
            this.mHDIconController = new HDIconController(this.mContext, this.mIconController, this.mSlotIMS);
        }
    }

    public void onWifiConnectionStateChanged(boolean z, boolean z2) {
        if (this.mWifiEnabled != z || this.mWifiConnected != z2) {
            this.mWifiEnabled = z;
            this.mWifiConnected = z2;
            if (DEBUG) {
                Log.v("ImsIconController", "onWifiConnectionStateChanged mWifiEnabled: " + this.mWifiEnabled + " mWifiConnected: " + this.mWifiConnected);
            }
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.post(new Runnable() {
                    public void run() {
                        ImsIconController.this.updateImsWifiState();
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateImsWifiState() {
        for (int i = 0; i < this.mImsStateController.size(); i++) {
            this.mImsStateController.get(this.mImsStateController.keyAt(i)).updateWifiState(this.mWifiEnabled, this.mWifiConnected);
        }
    }

    public void updateVolteIcon(boolean z, int i) {
        if (DEBUG) {
            Log.i("ImsIconController", "UpdateVolteIcon is4GState = " + z + " slotId = " + i);
        }
        ImsStateController imsStateController = this.mImsStateController.get(i);
        this.m4GState.put(i, Boolean.valueOf(z));
        if (imsStateController != null) {
            imsStateController.updateMobileState(z);
        }
    }

    /* access modifiers changed from: private */
    public void updateIMS(Intent intent) {
        String stringExtra = intent.getStringExtra("ss");
        int intExtra = intent.getIntExtra("phone", -1);
        int intExtra2 = intent.getIntExtra("subscription", -1);
        if (DEBUG) {
            Log.v("ImsIconController", "updateIMS: sim state changed: " + stringExtra + " , slotId: " + intExtra + ", subId: " + intExtra2);
        }
        List completeActiveSubscriptionInfoList = SubscriptionManager.from(this.mContext).getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList == null) {
            completeActiveSubscriptionInfoList = Collections.emptyList();
        }
        SparseArray sparseArray = new SparseArray();
        for (int i = 0; i < this.mImsStateController.size(); i++) {
            sparseArray.put(this.mImsStateController.keyAt(i), this.mImsStateController.valueAt(i));
        }
        this.mImsStateController.clear();
        int size = completeActiveSubscriptionInfoList.size();
        for (int i2 = 0; i2 < size; i2++) {
            SubscriptionInfo subscriptionInfo = (SubscriptionInfo) completeActiveSubscriptionInfoList.get(i2);
            int simSlotIndex = subscriptionInfo.getSimSlotIndex();
            ImsStateController imsStateController = (ImsStateController) sparseArray.get(simSlotIndex);
            if (imsStateController == null || hasSubcriptionChanged(imsStateController, subscriptionInfo)) {
                ImsStateController imsStateController2 = new ImsStateController(this.mContext, this, subscriptionInfo);
                imsStateController2.updateMobileState(this.m4GState.indexOfKey(simSlotIndex) >= 0 ? this.m4GState.get(simSlotIndex).booleanValue() : false);
                this.mImsStateController.put(simSlotIndex, imsStateController2);
            } else {
                this.mImsStateController.put(simSlotIndex, imsStateController);
                this.mImsStateController.get(simSlotIndex).resetSubInfo(subscriptionInfo);
                sparseArray.remove(simSlotIndex);
            }
        }
        for (int i3 = 0; i3 < sparseArray.size(); i3++) {
            int keyAt = sparseArray.keyAt(i3);
            ((ImsStateController) sparseArray.get(keyAt)).cleanupImsState();
            ((ImsStateController) sparseArray.get(keyAt)).unRegisterListener();
        }
    }

    private boolean hasSubcriptionChanged(ImsStateController imsStateController, SubscriptionInfo subscriptionInfo) {
        return (imsStateController != null && imsStateController.mSubscriptionInfo.getSimSlotIndex() == subscriptionInfo.getSimSlotIndex() && imsStateController.mSubscriptionInfo.getMcc() == subscriptionInfo.getMcc() && imsStateController.mSubscriptionInfo.getMnc() == subscriptionInfo.getMnc() && imsStateController.mSubscriptionInfo.getSubscriptionId() == subscriptionInfo.getSubscriptionId()) ? false : true;
    }

    public void updateImsIcon() {
        if (MotoFeature.isPrcProduct()) {
            this.mHDIconController.updateHDIcon(this.mImsStateController);
            return;
        }
        ImsType imsType = ImsType.INVALID_STATE;
        int i = -1;
        for (int i2 = 0; i2 < this.mImsStateController.size(); i2++) {
            int keyAt = this.mImsStateController.keyAt(i2);
            ImsStateController.ImsState imsState = this.mImsStateController.get(keyAt).getImsState();
            if (imsType.ordinal() < imsState.mImsType.ordinal()) {
                imsType = imsState.mImsType;
                i = keyAt;
            }
        }
        if (i == -1) {
            this.mIconController.setIconVisibility(this.mSlotIMS, false);
        } else {
            setImsIcon(i, this.mImsStateController.get(i).getImsState());
        }
    }

    private void setImsIcon(int i, ImsStateController.ImsState imsState) {
        int i2;
        if (imsState == null) {
            this.mIconController.setIconVisibility(this.mSlotIMS, false);
            return;
        }
        int i3 = R$drawable.stat_sys_ims;
        String str = null;
        if (DEBUG) {
            Log.v("ImsIconController", "setImsIcon slotId: " + i + " imsType: " + imsState.mImsType);
        }
        int i4 = C20144.f136xae02ae3[imsState.mImsType.ordinal()];
        if (i4 == 1) {
            str = this.mContext.getString(R$string.volte_4g_on);
            if (MotoFeature.isPrcProduct()) {
                i3 = R$drawable.zz_moto_stat_sys_ims_hd;
            } else if (imsState.mShowVoLteBadge) {
                i3 = R$drawable.zz_moto_stat_sys_volte_badge;
                str = this.mContext.getString(R$string.volte_bagde_on);
            } else if (!imsState.mShow4GForLTE) {
                i3 = R$drawable.zz_moto_stat_sys_ims_lte;
                str = this.mContext.getString(R$string.volte_on);
            }
        } else if (i4 == 2) {
            if (imsState.mShowSTCVoWifi) {
                i2 = R$drawable.zz_moto_stat_sys_vowifi_stc;
            } else {
                i2 = R$drawable.stat_sys_vowifi;
            }
            i3 = i2;
            str = this.mContext.getString(R$string.vowifi_on);
        } else if (i4 == 3) {
            i3 = R$drawable.stat_sys_vowifi_not_ready;
            str = this.mContext.getString(R$string.vowifi_not_ready);
        } else if (i4 == 4 && MotoFeature.isPrcProduct() && !imsState.mVoLteRegistered && isPrcCdma(i) && isEnhanced4gLteModeEnabled(i)) {
            i3 = R$drawable.zz_moto_stat_sys_ims_hd_no_voice;
        } else {
            this.mIconController.setIconVisibility(this.mSlotIMS, false);
            return;
        }
        this.mIconController.setIcon(this.mSlotIMS, i3, str);
        this.mIconController.setIconVisibility(this.mSlotIMS, true);
    }

    /* renamed from: com.android.systemui.statusbar.policy.ImsIconController$4 */
    static /* synthetic */ class C20144 {

        /* renamed from: $SwitchMap$com$android$systemui$statusbar$policy$ImsIconController$ImsType */
        static final /* synthetic */ int[] f136xae02ae3;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.android.systemui.statusbar.policy.ImsIconController$ImsType[] r0 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f136xae02ae3 = r0
                com.android.systemui.statusbar.policy.ImsIconController$ImsType r1 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.VOLTE_STATE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f136xae02ae3     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.statusbar.policy.ImsIconController$ImsType r1 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.VOWIFI_STATE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f136xae02ae3     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.statusbar.policy.ImsIconController$ImsType r1 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.VOWIFI_NO_READY_STATE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f136xae02ae3     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.systemui.statusbar.policy.ImsIconController$ImsType r1 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.INVALID_STATE     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.ImsIconController.C20144.<clinit>():void");
        }
    }

    /* access modifiers changed from: private */
    public void cleanAndUnregister(ImsStateController imsStateController) {
        if (imsStateController != null) {
            imsStateController.cleanupImsState();
            imsStateController.unRegisterImsCallback();
        }
    }

    private boolean isEnhanced4gLteModeEnabled(int i) {
        return ImsManager.getInstance(this.mContext, i).isEnhanced4gLteModeSettingEnabledByUser();
    }

    public String getMccMnc(int i) {
        return TelephonyManager.getTelephonyProperty(i, "gsm.sim.operator.numeric", "");
    }

    public boolean isPrcCdma(int i) {
        String mccMnc = getMccMnc(i);
        return mccMnc.startsWith("46003") || mccMnc.startsWith("46011");
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("ImsIconController:");
        printWriter.println("  mWifiEnabled: " + this.mWifiEnabled);
        printWriter.println("  mWifiConnected: " + this.mWifiConnected);
        printWriter.println();
        for (int i = 0; i < this.mImsStateController.size(); i++) {
            printWriter.println(this.mImsStateController.valueAt(i));
        }
        printWriter.println();
    }
}
