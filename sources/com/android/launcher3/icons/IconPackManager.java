package com.android.launcher3.icons;

import android.content.Context;
import android.provider.Settings;

public class IconPackManager {
    public static String getAppliedIconPack(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "pref_icon_pack");
        return string == null ? "system" : string;
    }
}
