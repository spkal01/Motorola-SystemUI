package com.android.launcher3.icons;

import android.graphics.Bitmap;

public class ThemedIconDrawable$ThemedBitmapInfo extends BitmapInfo {
    final float mNormalizationScale;
    final ThemedIconDrawable$ThemeData mThemeData;
    final Bitmap mUserBadge;

    public ThemedIconDrawable$ThemedBitmapInfo(Bitmap bitmap, int i, ThemedIconDrawable$ThemeData themedIconDrawable$ThemeData, float f, Bitmap bitmap2) {
        super(bitmap, i);
        this.mThemeData = themedIconDrawable$ThemeData;
        this.mNormalizationScale = f;
        this.mUserBadge = bitmap2;
    }
}
