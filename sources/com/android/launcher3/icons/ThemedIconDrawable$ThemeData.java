package com.android.launcher3.icons;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;

public class ThemedIconDrawable$ThemeData {
    final int mResID;
    final Resources mResources;

    public ThemedIconDrawable$ThemeData(Resources resources, int i) {
        this.mResources = resources;
        this.mResID = i;
    }

    public Drawable wrapDrawable(Drawable drawable, int i) {
        if (!(drawable instanceof AdaptiveIconDrawable)) {
            return drawable;
        }
        AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) drawable;
        String resourceTypeName = this.mResources.getResourceTypeName(this.mResID);
        if (i == 1 && "array".equals(resourceTypeName)) {
            TypedArray obtainTypedArray = this.mResources.obtainTypedArray(this.mResID);
            int resourceId = obtainTypedArray.getResourceId(IconProvider.getDay(), 0);
            obtainTypedArray.recycle();
            return resourceId == 0 ? drawable : new ThemedIconDrawable$ThemedAdaptiveIcon(adaptiveIconDrawable, new ThemedIconDrawable$ThemeData(this.mResources, resourceId));
        } else if (i != 2 || !"array".equals(resourceTypeName)) {
            return "drawable".equals(resourceTypeName) ? new ThemedIconDrawable$ThemedAdaptiveIcon(adaptiveIconDrawable, this) : drawable;
        } else {
            ((ClockDrawableWrapper) drawable).mThemeData = this;
            return drawable;
        }
    }
}
