package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import android.content.res.TypedArray;

public class Themes {
    public static String getDefaultBodyFont(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(16974253, new int[]{16843692});
        String string = obtainStyledAttributes.getString(0);
        obtainStyledAttributes.recycle();
        return string;
    }

    public static float getDimension(Context context, int i, float f) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        float dimension = obtainStyledAttributes.getDimension(0, f);
        obtainStyledAttributes.recycle();
        return dimension;
    }

    public static int getAttrColor(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        return color;
    }
}
