package com.motorola.systemui.areacode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R$array;
import com.motorola.systemui.statusbar.policy.CellLocationController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CBUtil {
    private static Boolean sIsMultiSimEnabled;

    public static void updateAreaInfo(Context context, int i, String str) {
        if (str == null) {
            str = "";
        }
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "CB message " + str);
        }
        saveLatestAreaInfo(context, str, i);
        broadcastAreaInfo(context, str, i);
    }

    public static void saveLatestAreaInfo(Context context, String str, int i) {
        ((CellLocationController) Dependency.get(CellLocationController.class)).setLatestBrazilAreaInfo(getSlotIndexBySubId(i), str);
    }

    public static void broadcastAreaInfo(Context context, String str, int i) {
        if (isAreaUpdateBroadcastEnabled(context)) {
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CBUtil", "Broadcast CB message " + str);
            }
            Intent intent = new Intent("com.android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED");
            intent.putExtra("message", str);
            intent.putExtra("subId", i);
            context.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.RECEIVE_EMERGENCY_BROADCAST");
        }
    }

    public static void clearAreaInfoOnSystemUI(Context context) {
        for (Integer intValue : getValidSubIds(context)) {
            clearAreaInfoOnSystemUI(context, intValue.intValue());
        }
    }

    public static void clearAreaInfoOnSystemUI(Context context, int i) {
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "clearAreaInfoOnSystemUI for subId: " + i);
        }
        updateAreaInfo(context, i, (String) null);
    }

    public static void broadcastEnableIntent(Context context, boolean z) {
        for (Integer intValue : getValidSubIds(context)) {
            broadcastEnableIntent(context, z, intValue.intValue());
        }
    }

    public static void broadcastEnableIntent(Context context, boolean z, int i) {
        Intent intent = new Intent("com.motorola.cellbroadcastreceiver.CB_INFO_ON_SYSTEMUI");
        intent.putExtra("enable", z);
        intent.putExtra("subscription", i);
        context.sendBroadcastAsUser(intent, UserHandle.ALL, "com.motorola.permission.CB_ENABLE");
    }

    public static int getSlotIndexBySubId(int i) {
        int slotIndex = SubscriptionManager.getSlotIndex(i);
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "slot index: " + slotIndex + " obtainded for subId: " + i);
        }
        return slotIndex;
    }

    public static int getSubIdBySlotIndex(int i) {
        int[] subId = SubscriptionManager.getSubId(i);
        if (subId == null || subId.length <= 0 || !SubscriptionManager.isValidSubscriptionId(subId[0])) {
            return -1;
        }
        int i2 = subId[0];
        if (!LogUtil.isLoggableD()) {
            return i2;
        }
        LogUtil.m95d("CBUtil", "subId obtained: " + i2);
        return i2;
    }

    public static boolean isBrazil(Context context) {
        for (Integer intValue : getValidSubIds(context)) {
            if (isBrazil(context, intValue.intValue())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBrazil(Context context, int i) {
        boolean z;
        if (isMultiSimEnabled(context)) {
            z = SubscriptionManager.isValidSubscriptionId(i) ? "br".equals(TelephonyManager.getTelephonyProperty(SubscriptionManager.getPhoneId(i), "gsm.sim.operator.iso-country", "")) : false;
        } else {
            z = "br".equals(TelephonyManager.from(context).getSimCountryIso());
        }
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "isBrazil (" + i + "): " + z);
        }
        return z;
    }

    public static boolean isBrazilSettingsEnabled(Context context) {
        for (Integer next : getValidSubIds(context)) {
            if (isBrazilSettingsEnabled(context, next.intValue())) {
                if (LogUtil.isLoggableD()) {
                    LogUtil.m95d("CBUtil", "isBrazilSettingsEnabled (" + next + "): " + true);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isBrazilSettingsEnabled(Context context, int i) {
        boolean z = Resources.getSystem().getBoolean(17891711);
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "isBrazilSettingsEnabled " + z + " subId " + i);
        }
        return z;
    }

    public static boolean isMultiSimEnabled(Context context) {
        if (sIsMultiSimEnabled == null) {
            TelephonyManager from = TelephonyManager.from(context);
            sIsMultiSimEnabled = Boolean.valueOf(from != null && from.isMultiSimEnabled());
        }
        return sIsMultiSimEnabled.booleanValue();
    }

    public static String getSimPlmnId(Context context, int i) {
        if (isMultiSimEnabled(context)) {
            return TelephonyManager.getTelephonyProperty(SubscriptionManager.getPhoneId(i), "gsm.sim.operator.numeric", (String) null);
        }
        return TelephonyManager.from(context).getSimOperator();
    }

    public static String getNwPlmnId(Context context, int i) {
        if (!isMultiSimEnabled(context)) {
            return TelephonyManager.from(context).getNetworkOperator();
        }
        TelephonyManager from = TelephonyManager.from(context);
        if (from != null) {
            return from.getNetworkOperator(i);
        }
        return null;
    }

    public static List<Integer> getValidSubIds(Context context) {
        ArrayList arrayList = new ArrayList();
        List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionId : activeSubscriptionInfoList) {
                arrayList.add(Integer.valueOf(subscriptionId.getSubscriptionId()));
            }
        }
        return arrayList;
    }

    public static boolean isVivoSimCard(Context context, int i) {
        int indexOf = Arrays.asList(context.getResources().getStringArray(R$array.plmn_ids)).indexOf(getSimPlmnId(context, i));
        boolean equals = "VIVO".equals(indexOf > -1 ? context.getResources().getStringArray(R$array.carrier_name)[indexOf] : "");
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "Is VIVO SIM Card? " + equals);
        }
        return equals;
    }

    public static void updateCellLocation(Context context, int i, int i2) {
        String simPlmnId = getSimPlmnId(context, i2);
        String nwPlmnId = getNwPlmnId(context, i2);
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "plmnId " + simPlmnId + " nwPlmnId " + nwPlmnId + " for subscription" + i2);
        }
        if (simPlmnId != null && i != -1 && isLocalNetwork(nwPlmnId)) {
            String cBMsg = getCBMsg(context, simPlmnId, Integer.toString(i));
            if (!TextUtils.isEmpty(cBMsg)) {
                updateAreaInfo(context, i2, cBMsg);
            }
        }
    }

    private static boolean isLocalNetwork(String str) {
        return str != null && str.startsWith("724");
    }

    private static String getCBMsg(Context context, String str, String str2) {
        String substring;
        int indexOf;
        int indexOf2 = Arrays.asList(context.getResources().getStringArray(R$array.plmn_ids)).indexOf(str);
        if (indexOf2 > -1) {
            str = context.getResources().getStringArray(R$array.carrier_name)[indexOf2];
        }
        int length = str2.length() - 2;
        if (length <= 0 || (indexOf = Arrays.asList(context.getResources().getStringArray(R$array.lac_array)).indexOf((substring = str2.substring(length)))) <= -1) {
            return "";
        }
        return str + " " + context.getResources().getStringArray(R$array.state_array)[indexOf] + " " + substring;
    }

    public static void updateListenerState(boolean z, Context context, int i) {
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "Sub " + i + " registered " + z);
        }
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("subscription" + i, z).commit();
    }

    public static void restoreListenerState(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (Integer next : getValidSubIds(context)) {
            String str = "subscription" + next.toString();
            if (LogUtil.isLoggableD()) {
                LogUtil.m95d("CBUtil", "Register listener after restart for the sub " + str);
            }
            if (defaultSharedPreferences.getBoolean(str, false)) {
                CBPhoneStateListener.registerStateListener(context, next.intValue());
            }
        }
    }

    public static void saveAreaUpdateBroadcastState(boolean z, Context context) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("is_area_update_broadcast_enabled", z);
        edit.apply();
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "CBR_ENABLE_KEY state is: " + z);
        }
    }

    public static boolean isAreaUpdateBroadcastEnabled(Context context) {
        boolean z = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_area_update_broadcast_enabled", true);
        if (LogUtil.isLoggableD()) {
            LogUtil.m95d("CBUtil", "Area update broadcast state: " + z);
        }
        return z;
    }
}
