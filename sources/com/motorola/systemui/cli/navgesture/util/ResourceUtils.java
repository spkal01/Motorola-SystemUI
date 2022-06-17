package com.motorola.systemui.cli.navgesture.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ResourceUtils {
    public static int getNavbarSize(String str, Resources resources) {
        return getDimenByName(str, resources, 48);
    }

    public static int getDimenByName(String str, Resources resources, int i) {
        int identifier = resources.getIdentifier(str, "dimen", "android");
        if (identifier != 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return pxFromDp((float) i, resources.getDisplayMetrics());
    }

    public static int pxFromDp(float f, DisplayMetrics displayMetrics) {
        return Math.round(TypedValue.applyDimension(1, f, displayMetrics));
    }
}
