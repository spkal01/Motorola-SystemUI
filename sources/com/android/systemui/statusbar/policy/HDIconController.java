package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.ims.ImsManager;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.ImsIconController;
import com.android.systemui.statusbar.policy.ImsStateController;

public class HDIconController {
    public static final boolean DEBUG = (!Build.IS_USER);
    private boolean mAirplaneMode = isAirplaneMode();
    private Context mContext;
    private StatusBarIconController mIconController;
    private String mSlotIMS;

    public HDIconController(Context context, StatusBarIconController statusBarIconController, String str) {
        this.mContext = context;
        this.mIconController = statusBarIconController;
        this.mSlotIMS = str;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                    HDIconController.this.updateHDIconForAirplanMode();
                }
            }
        }, intentFilter);
    }

    public void updateHDIcon(SparseArray<ImsStateController> sparseArray) {
        if (this.mAirplaneMode) {
            if (DEBUG) {
                Log.v("HDIconController", "updateHDIcon airplane mode: return");
            }
        } else if (sparseArray.size() == 0) {
            this.mIconController.setIconVisibility(this.mSlotIMS, false);
        } else if (sparseArray.size() == 1) {
            ImsIconController.ImsType imsType = ImsIconController.ImsType.INVALID_STATE;
            int keyAt = sparseArray.keyAt(0);
            ImsStateController.ImsState imsState = sparseArray.get(keyAt).getImsState();
            if (imsType.ordinal() >= imsState.mImsType.ordinal()) {
                keyAt = -1;
            }
            if (DEBUG) {
                Log.v("HDIconController", "updateHDIcon slotId: " + keyAt + " imsType: " + imsState.mImsType);
            }
            if (keyAt == -1) {
                this.mIconController.setIconVisibility(this.mSlotIMS, false);
            } else {
                setSingleHDIcon(keyAt, imsState);
            }
        } else if (sparseArray.size() == 2) {
            setDualHDIcon(0, sparseArray.get(0).getImsState(), 1, sparseArray.get(1).getImsState());
        }
    }

    private void setSingleHDIcon(int i, ImsStateController.ImsState imsState) {
        if (imsState != null) {
            int i2 = R$drawable.zz_moto_stat_sys_ims_hd;
            String str = null;
            int i3 = C20072.f135xae02ae3[imsState.mImsType.ordinal()];
            if (i3 == 1) {
                str = this.mContext.getString(R$string.volte_4g_on);
            } else if (i3 == 2 && !imsState.mVoLteRegistered && isPrcCdma(i) && isEnhanced4gLteModeEnabled(i)) {
                i2 = R$drawable.zz_moto_stat_sys_ims_hd_no_voice;
            } else {
                this.mIconController.setIconVisibility(this.mSlotIMS, false);
                return;
            }
            this.mIconController.setIcon(this.mSlotIMS, i2, str);
            this.mIconController.setIconVisibility(this.mSlotIMS, true);
        }
    }

    /* renamed from: com.android.systemui.statusbar.policy.HDIconController$2 */
    static /* synthetic */ class C20072 {

        /* renamed from: $SwitchMap$com$android$systemui$statusbar$policy$ImsIconController$ImsType */
        static final /* synthetic */ int[] f135xae02ae3;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                com.android.systemui.statusbar.policy.ImsIconController$ImsType[] r0 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f135xae02ae3 = r0
                com.android.systemui.statusbar.policy.ImsIconController$ImsType r1 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.VOLTE_STATE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f135xae02ae3     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.statusbar.policy.ImsIconController$ImsType r1 = com.android.systemui.statusbar.policy.ImsIconController.ImsType.INVALID_STATE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.HDIconController.C20072.<clinit>():void");
        }
    }

    public void setDualHDIcon(int i, ImsStateController.ImsState imsState, int i2, ImsStateController.ImsState imsState2) {
        if (DEBUG) {
            Log.v("HDIconController", "setDualHDIcon slotId0: " + i + " state0.mImsType: " + imsState.mImsType + " slotId1: " + i2 + " state1.mImsType: " + imsState2.mImsType);
        }
        int i3 = R$drawable.stat_sys_ims;
        String string = this.mContext.getString(R$string.volte_4g_on);
        ImsIconController.ImsType imsType = imsState.mImsType;
        ImsIconController.ImsType imsType2 = ImsIconController.ImsType.VOLTE_STATE;
        if (imsType.equals(imsType2) && imsState2.mImsType.equals(imsType2)) {
            i3 = R$drawable.zz_moto_stat_sys_hd_dual;
        } else if (!imsState.mImsType.equals(imsType2) || !imsState2.mImsType.equals(ImsIconController.ImsType.INVALID_STATE)) {
            ImsIconController.ImsType imsType3 = imsState.mImsType;
            ImsIconController.ImsType imsType4 = ImsIconController.ImsType.INVALID_STATE;
            if (imsType3.equals(imsType4) && imsState2.mImsType.equals(imsType2)) {
                i3 = (imsState.mVoLteRegistered || !isPrcCdma(i) || !isEnhanced4gLteModeEnabled(i)) ? R$drawable.zz_moto_stat_sys_hd_slot1 : R$drawable.zz_moto_stat_sys_hd_slot0_no_voice;
            } else if (imsState.mImsType.equals(imsType4) && imsState2.mImsType.equals(imsType4)) {
                string = null;
                if ((imsState.mVoLteRegistered || !isPrcCdma(i) || !isEnhanced4gLteModeEnabled(i)) && (imsState2.mVoLteRegistered || !isPrcCdma(i2) || !isEnhanced4gLteModeEnabled(i2))) {
                    this.mIconController.setIconVisibility(this.mSlotIMS, false);
                    return;
                }
                i3 = R$drawable.zz_moto_stat_sys_hd_dual_no_voice;
            }
        } else {
            i3 = (imsState2.mVoLteRegistered || !isPrcCdma(i2) || !isEnhanced4gLteModeEnabled(i2)) ? R$drawable.zz_moto_stat_sys_hd_slot0 : R$drawable.zz_moto_stat_sys_hd_slot1_no_voice;
        }
        this.mIconController.setIcon(this.mSlotIMS, i3, string);
        this.mIconController.setIconVisibility(this.mSlotIMS, true);
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

    /* access modifiers changed from: private */
    public void updateHDIconForAirplanMode() {
        boolean isAirplaneMode = isAirplaneMode();
        this.mAirplaneMode = isAirplaneMode;
        if (isAirplaneMode) {
            this.mIconController.setIconVisibility(this.mSlotIMS, false);
        }
    }

    private boolean isAirplaneMode() {
        try {
            return Settings.System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0;
        } catch (Exception unused) {
            return false;
        }
    }
}
