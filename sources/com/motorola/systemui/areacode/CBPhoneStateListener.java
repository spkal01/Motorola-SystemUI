package com.motorola.systemui.areacode;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.LongSparseArray;
import com.android.systemui.Dependency;
import com.motorola.systemui.statusbar.policy.CellLocationController;

public class CBPhoneStateListener extends PhoneStateListener {
    private static LongSparseArray<CBPhoneStateListener> sSimStateArray = new LongSparseArray<>();
    private Context mContext;
    private int mNetworkClass = -1;
    private int mRadioTech = -1;
    private int mServiceState = -1;

    private int getNetworkClass(int i) {
        switch (i) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
            case 16:
                return 1;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
            case 17:
                return 2;
            case 13:
            case 18:
            case 19:
                return 3;
            default:
                return 0;
        }
    }

    CBPhoneStateListener(int i, Context context) {
        this.mSubId = Integer.valueOf(i);
        this.mContext = context;
    }

    public void onServiceStateChanged(ServiceState serviceState) {
        int state = serviceState.getState();
        int rilVoiceRadioTechnology = serviceState.getRilVoiceRadioTechnology();
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBPhoneStateListener", "onServiceStateChanged new state:" + state + " new radio tech:" + rilVoiceRadioTechnology);
        }
        if (!(state == this.mServiceState && rilVoiceRadioTechnology == this.mRadioTech)) {
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CBPhoneStateListener", "Service state changed! " + state + "for the subscription " + this.mSubId + " Full: " + serviceState);
            }
            this.mServiceState = state;
            this.mRadioTech = rilVoiceRadioTechnology;
        }
        if (CBUtil.isBrazil(this.mContext, this.mSubId.intValue())) {
            if (shouldClearAreaInfo(this.mContext, serviceState)) {
                CBUtil.clearAreaInfoOnSystemUI(this.mContext, this.mSubId.intValue());
            }
            if (isInService()) {
                this.mNetworkClass = getNetworkClass(ServiceState.rilRadioTechnologyToNetworkType(this.mRadioTech));
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CBPhoneStateListener", "IN_SERVICE with Radio Tech: " + this.mRadioTech + " and NetworkClass: " + this.mNetworkClass);
                }
                if (isRegisteredOn2GNetwork()) {
                    if (LogUtil.isLoggableD()) {
                        LogUtil.m95d("CBPhoneStateListener", "Request area info");
                    }
                    ((CellLocationController) Dependency.get(CellLocationController.class)).requestAreaInfo(this.mSubId.intValue());
                }
            }
        }
    }

    public void onCellLocationChanged(CellLocation cellLocation) {
        int rilRadioTechnologyToNetworkType = ServiceState.rilRadioTechnologyToNetworkType(this.mRadioTech);
        int networkClass = getNetworkClass(rilRadioTechnologyToNetworkType);
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBPhoneStateListener", "Cell Location Changed" + cellLocation + "for subscription " + this.mSubId);
            StringBuilder sb = new StringBuilder();
            sb.append("networkType: ");
            sb.append(rilRadioTechnologyToNetworkType);
            LogUtil.m95d("CBPhoneStateListener", sb.toString());
            LogUtil.m95d("CBPhoneStateListener", "networkClass: " + networkClass);
        }
        if (networkClass == 2 || networkClass == 3) {
            if (cellLocation != null && (cellLocation instanceof GsmCellLocation)) {
                CBUtil.updateCellLocation(this.mContext, ((GsmCellLocation) cellLocation).getLac(), this.mSubId.intValue());
            }
        } else if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBPhoneStateListener", "Ignore cell location changed, network class= " + networkClass);
        }
    }

    private boolean isInService() {
        return this.mServiceState == 0 && this.mRadioTech != 0;
    }

    public boolean isRegisteredOn2GNetwork() {
        return this.mNetworkClass == 1;
    }

    private void listen(Context context, int i, int i2) {
        ((TelephonyManager) context.getSystemService("phone")).createForSubscriptionId(i).listen(this, i2);
    }

    private boolean shouldClearAreaInfo(Context context, ServiceState serviceState) {
        boolean z = true;
        if (serviceState.getState() == 0) {
            int rilVoiceRadioTechnology = serviceState.getRilVoiceRadioTechnology();
            if (CBUtil.isVivoSimCard(context, this.mSubId.intValue()) || rilVoiceRadioTechnology == 1 || rilVoiceRadioTechnology == 2) {
                z = false;
            }
        }
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBPhoneStateListener", "shouldClearAreaInfo for " + this.mSubId + ": " + z);
        }
        return z;
    }

    public static boolean hasService(int i) {
        CBPhoneStateListener cBPhoneStateListener = sSimStateArray.get((long) i);
        return cBPhoneStateListener != null && cBPhoneStateListener.isInService();
    }

    public static boolean isRegisteredOn2GNetwork(int i) {
        CBPhoneStateListener cBPhoneStateListener = sSimStateArray.get((long) i);
        return cBPhoneStateListener != null && cBPhoneStateListener.isRegisteredOn2GNetwork();
    }

    public static void registerStateListener(Context context, int i) {
        long j = (long) i;
        if (sSimStateArray.get(j) == null && SubscriptionManager.isValidSubscriptionId(i) && CBUtil.isBrazil(context, i)) {
            CBPhoneStateListener cBPhoneStateListener = new CBPhoneStateListener(i, context);
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CBPhoneStateListener", "Register PhoneStateListener for subscription:" + i);
            }
            try {
                if (CBUtil.isVivoSimCard(context, i)) {
                    cBPhoneStateListener.listen(context, i, 17);
                } else {
                    cBPhoneStateListener.listen(context, i, 1);
                }
            } catch (Exception unused) {
                LogUtil.m96e("CBPhoneStateListener", "Error trying to register state listener for subscription:" + i);
            }
            sSimStateArray.put(j, cBPhoneStateListener);
            CBUtil.updateListenerState(true, context, i);
        }
    }

    public static void unregisterStateListener(Context context, int i) {
        long j = (long) i;
        CBPhoneStateListener cBPhoneStateListener = sSimStateArray.get(j);
        if (cBPhoneStateListener != null) {
            cBPhoneStateListener.listen(context, i, 0);
            sSimStateArray.remove(j);
            CBUtil.updateListenerState(false, context, i);
        }
    }

    public static void registerAllListeners(Context context) {
        for (Integer intValue : CBUtil.getValidSubIds(context)) {
            registerStateListener(context, intValue.intValue());
        }
    }

    public static void unregisterAllListeners(Context context) {
        for (Integer intValue : CBUtil.getValidSubIds(context)) {
            unregisterStateListener(context, intValue.intValue());
        }
    }

    public static boolean hasRegisteredListeners() {
        return sSimStateArray.size() != 0;
    }
}
