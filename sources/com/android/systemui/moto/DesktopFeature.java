package com.android.systemui.moto;

import android.content.Context;
import android.os.Build;
import android.view.Display;
import com.motorola.internal.app.MotoDesktopManager;

public class DesktopFeature {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static DesktopFeature sInstance;
    private final Context mContext;

    DesktopFeature(Context context) {
        this.mContext = context;
    }

    protected static void initInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DesktopFeature(context.getApplicationContext());
        }
    }

    public static boolean isDesktopSupported() {
        return MotoDesktopManager.isDesktopSupported();
    }

    public static boolean isDesktopConnected(Context context) {
        return MotoDesktopManager.isDesktopConnected(context);
    }

    public static boolean isDesktopMode(Display display) {
        return MotoDesktopManager.isDesktopMode(display) || MotoDesktopManager.isMobileUiMode(display);
    }

    public static boolean isDesktopDisplayContext(Context context) {
        return isDesktopMode(context.getDisplay());
    }

    public static boolean isInMobileUiMode(Context context) {
        return MotoDesktopManager.isMobileUiMode(context.getDisplay());
    }
}
