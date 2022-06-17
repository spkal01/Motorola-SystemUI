package com.android.keyguard;

import android.content.Context;
import android.content.SharedPreferences;

public final class KeyguardPrefs {
    public static boolean getBoolean(Context context, String str, boolean z) {
        return get(context).getBoolean(str, z);
    }

    public static void putBoolean(Context context, String str, boolean z) {
        get(context).edit().putBoolean(str, z).apply();
    }

    private static SharedPreferences get(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0);
    }
}
