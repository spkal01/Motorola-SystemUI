package com.motorola.settingslib.hotspothelper;

import android.content.Context;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import java.util.Arrays;
import java.util.List;

public class HotspotHelper {
    private static int getDoNotShowWarning(Context context, String str) {
        return Settings.Global.getInt(context.getContentResolver(), str, 0);
    }

    private static void setDoNotShowWarning(Context context, String str) {
        Settings.Global.putInt(context.getContentResolver(), str, 1);
    }

    private static int getCarrierId(Context context) {
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).getSimCarrierId();
    }

    public static boolean shouldWarnWhenEnablingTethering(Context context) {
        return getCarrierIdsFromResourceArray(R$array.carrier_id_warn_enable_tethering, context).contains(String.valueOf(getCarrierId(context)));
    }

    public static boolean shouldWarnWhenEnablingWifi(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R$array.carrier_id_warn_enable_wifi)).contains(String.valueOf(getCarrierId(context)));
    }

    public static boolean isWifiHotspotActive(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).isWifiApEnabled();
    }

    public static void disableWifiHotspot(Context context) {
        TetheringManager tetheringManager = (TetheringManager) context.getSystemService("tethering");
        if (tetheringManager != null) {
            tetheringManager.stopTethering(0);
        }
    }

    public static boolean getDoNotShowHotspotWarning(Context context) {
        return getDoNotShowWarning(context, "mot_do_not_show_hs_warning") == 1;
    }

    public static void setDoNotShowHotspotWarning(Context context) {
        setDoNotShowWarning(context, "mot_do_not_show_hs_warning");
    }

    public static boolean getDoNotShowWifiWarning(Context context) {
        return getDoNotShowWarning(context, "mot_do_not_show_wifi_warning") == 1;
    }

    public static void setDoNotShowWifiWarning(Context context) {
        setDoNotShowWarning(context, "mot_do_not_show_wifi_warning");
    }

    public static String getWarningDialogTitle(Context context) {
        if (isAttCid(context)) {
            return context.getResources().getString(R$string.att_title_warning);
        }
        return context.getResources().getString(R$string.title_warning);
    }

    public static String getTetheringWarningDialogMessage(Context context) {
        if (isAttCid(context)) {
            return context.getResources().getString(R$string.att_tether_enable_warning_message);
        }
        return context.getResources().getString(R$string.tether_enable_warning_message);
    }

    public static String getWifiWarningDialogMessage(Context context) {
        return context.getResources().getString(R$string.wifi_enable_warning_message);
    }

    private static boolean isAttCid(Context context) {
        return getCarrierIdsFromResourceArray(R$array.att_tethering_warn_carrier_ids, context).contains(String.valueOf(getCarrierId(context)));
    }

    private static List getCarrierIdsFromResourceArray(int i, Context context) {
        return Arrays.asList(context.getResources().getStringArray(i));
    }
}
