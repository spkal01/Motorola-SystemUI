package com.motorola.systemui.areacode;

import android.util.Log;

public class LogUtil {
    public static boolean isLoggableD() {
        return Log.isLoggable("CBR_SystemUI", 3);
    }

    /* renamed from: d */
    public static void m95d(String str, String str2) {
        Log.d("CBR_SystemUI", getLog(str, str2));
    }

    /* renamed from: e */
    public static void m96e(String str, String str2) {
        Log.e("CBR_SystemUI", getLog(str, str2));
    }

    private static String getLog(String str, String str2) {
        return str + " - " + str2;
    }
}
