package com.motorola.settingslib;

import android.content.Context;

public class InstalledAppUtils {
    private static final RestrictedPackagesFilter DISABLE_UI_OF_NOTIF_LISTNER_APP_FILTER = new RestrictedPackagesFilter().addPath("/system/etc/notiflistenerhideordisableui/").addPath("/oem/etc/notiflistenerhideordisableui/").addPath("/product/etc/notiflistenerhideordisableui/").addPath("/system_ext/etc/notiflistenerhideordisableui/");
    private static final RestrictedPackagesFilter HIDE_APP_LIST = new RestrictedPackagesFilter().addPath("/system/etc/hidden/").addPath("/oem/etc/hidden/").addPath("/product/etc/hidden/").addPath("/system_ext/etc/hidden/");
    private static final RestrictedPackagesFilter HIDE_SENSITIVE_CONTENT_APP_FILTER = new RestrictedPackagesFilter().addPath("/system/etc/hidesensitecontentnotif/").addPath("/oem/etc/hidesensitecontentnotif/").addPath("/product/etc/hidesensitecontentnotif/").addPath("/system_ext/etc/hidesensitecontentnotif/");
    private static final RestrictedPackagesFilter NON_DISABLE_APP_FILTER = new RestrictedPackagesFilter().addPath("/system/etc/nondisable/").addPath("/oem/etc/nondisable/").addPath("/product/etc/nondisable/").addPath("/system_ext/etc/nondisable/");
    private static final RestrictedPackagesFilter NON_FORCESTOP_APP_FILTER = new RestrictedPackagesFilter().addPath("/system/etc/nonforcestop/").addPath("/oem/etc/nonforcestop/").addPath("/product/etc/nonforcestop/").addPath("/system_ext/etc/nonforcestop/");
    private static final RestrictedPackagesFilter REMOVE_BLOCK_ALL_APP_FILTER = new RestrictedPackagesFilter().addPath("/system/etc/blocknotifications/").addPath("/oem/etc/blocknotifications/").addPath("/product/etc/blocknotifications/").addPath("/system_ext/etc/blocknotifications/");
    private static InstalledAppUtils sInstance;
    private final Context mContext;

    public static InstalledAppUtils get(Context context) {
        if (sInstance == null) {
            sInstance = new InstalledAppUtils(context);
        }
        return sInstance;
    }

    private InstalledAppUtils(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public boolean isPackageRemoveBlockAll(String str) {
        return REMOVE_BLOCK_ALL_APP_FILTER.contains(this.mContext, str);
    }
}
