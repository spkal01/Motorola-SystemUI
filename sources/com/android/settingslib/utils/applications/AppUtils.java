package com.android.settingslib.utils.applications;

import android.util.Log;

public class AppUtils {
    public static final boolean IS_PRC_PRODUCT = isPrcProduct();
    private static final String TAG = "AppUtils";

    private static boolean isPrcProduct() {
        try {
            return ((Boolean) Class.forName("android.os.Build").getDeclaredField("IS_PRC_PRODUCT").get((Object) null)).booleanValue();
        } catch (Exception e) {
            String str = TAG;
            Log.e(str, "Unable to check prc product: " + e.getMessage());
            return false;
        }
    }
}
