package com.android.keyguard;

import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManagerGlobal;
import java.text.DecimalFormat;

public class PtDisplayFontUtils {
    private static final boolean DEBUG = KeyguardConstants.DEBUG;

    public static int getScreenHeight(Display display) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth(Display display) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static float caculateMultiple(float f, Display display) {
        int i;
        try {
            i = WindowManagerGlobal.getWindowManagerService().getInitialDisplayDensity(display.getDisplayId());
            if (DEBUG) {
                Log.d("PtDisplayFontUtils", "defaultDpi = " + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            i = 160;
            if (DEBUG) {
                Log.d("PtDisplayFontUtils", "Get default dpi error, set to default: 160");
            }
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        float f2 = (displayMetrics.density * 160.0f) / ((float) i);
        float f3 = 1.0f;
        float f4 = f2 > 1.0f ? f2 * 1.0f * 0.9f : f2 * 1.0f;
        String str = SystemProperties.get("persist.desktop.font_size_scale", "1.0");
        if (!TextUtils.isEmpty(str)) {
            try {
                f = Float.parseFloat(str);
            } catch (Exception e2) {
                e2.printStackTrace();
                f = 1.0f;
            }
        }
        if (f > 1.0f) {
            f4 = f4 * f * 0.9f;
        }
        try {
            f3 = Float.parseFloat(new DecimalFormat("#.0").format((double) f2));
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        if ((f2 >= 1.5f || f3 >= 1.5f) && f >= 1.3f) {
            if (DEBUG) {
                Log.d("PtDisplayFontUtils", "Display and font size are largest.");
            }
            f4 *= 0.9f;
        }
        if (DEBUG) {
            Log.d("PtDisplayFontUtils", "approxDensity=" + f3 + "  realDensity=" + f2 + "  fontScale=" + f + "  multiple=" + f4);
        }
        return f4;
    }
}
