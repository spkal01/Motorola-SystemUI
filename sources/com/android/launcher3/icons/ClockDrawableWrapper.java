package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.icons.BitmapInfo;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

@TargetApi(26)
public class ClockDrawableWrapper extends AdaptiveIconDrawable implements BitmapInfo.Extender {
    public static final long TICK_MS = TimeUnit.MINUTES.toMillis(1);
    private final AnimationInfo mAnimationInfo = new AnimationInfo();
    private int mTargetSdkVersion;
    protected ThemedIconDrawable$ThemeData mThemeData;

    public ClockDrawableWrapper(AdaptiveIconDrawable adaptiveIconDrawable) {
        super(adaptiveIconDrawable.getBackground(), adaptiveIconDrawable.getForeground());
    }

    public static ClockDrawableWrapper forPackage(Context context, String str, int i, Drawable drawable) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 8320);
            return forExtras(applicationInfo, applicationInfo.metaData, new ClockDrawableWrapper$$ExternalSyntheticLambda0(packageManager.getResourcesForApplication(applicationInfo), i), drawable);
        } catch (Exception e) {
            Log.d("ClockDrawableWrapper", "Unable to load clock drawable info", e);
            return null;
        }
    }

    private static ClockDrawableWrapper forExtras(ApplicationInfo applicationInfo, Bundle bundle, IntFunction<Drawable> intFunction, Drawable drawable) {
        int i;
        if (drawable == null || !(drawable instanceof AdaptiveIconDrawable)) {
            if (bundle == null || (i = bundle.getInt("com.android.launcher3.LEVEL_PER_TICK_ICON_ROUND", 0)) == 0) {
                return null;
            }
            drawable = intFunction.apply(i).mutate();
        }
        if (!(drawable instanceof AdaptiveIconDrawable)) {
            return null;
        }
        ClockDrawableWrapper clockDrawableWrapper = new ClockDrawableWrapper((AdaptiveIconDrawable) drawable);
        clockDrawableWrapper.mTargetSdkVersion = applicationInfo.targetSdkVersion;
        AnimationInfo animationInfo = clockDrawableWrapper.mAnimationInfo;
        animationInfo.baseDrawableState = drawable.getConstantState();
        animationInfo.hourLayerIndex = bundle.getInt("com.android.launcher3.HOUR_LAYER_INDEX", -1);
        animationInfo.minuteLayerIndex = bundle.getInt("com.android.launcher3.MINUTE_LAYER_INDEX", -1);
        animationInfo.secondLayerIndex = bundle.getInt("com.android.launcher3.SECOND_LAYER_INDEX", -1);
        animationInfo.defaultHour = bundle.getInt("com.android.launcher3.DEFAULT_HOUR", 0);
        animationInfo.defaultMinute = bundle.getInt("com.android.launcher3.DEFAULT_MINUTE", 0);
        animationInfo.defaultSecond = bundle.getInt("com.android.launcher3.DEFAULT_SECOND", 0);
        LayerDrawable layerDrawable = (LayerDrawable) clockDrawableWrapper.getForeground();
        int numberOfLayers = layerDrawable.getNumberOfLayers();
        int i2 = animationInfo.hourLayerIndex;
        if (i2 < 0 || i2 >= numberOfLayers) {
            animationInfo.hourLayerIndex = -1;
        }
        int i3 = animationInfo.minuteLayerIndex;
        if (i3 < 0 || i3 >= numberOfLayers) {
            animationInfo.minuteLayerIndex = -1;
        }
        int i4 = animationInfo.secondLayerIndex;
        if (i4 < 0 || i4 >= numberOfLayers) {
            animationInfo.secondLayerIndex = -1;
        } else {
            layerDrawable.setDrawable(i4, (Drawable) null);
            animationInfo.secondLayerIndex = -1;
        }
        animationInfo.applyTime(Calendar.getInstance(), layerDrawable);
        return clockDrawableWrapper;
    }

    public ClockBitmapInfo getExtendedInfo(Bitmap bitmap, int i, BaseIconFactory baseIconFactory, float f, UserHandle userHandle) {
        baseIconFactory.disableColorExtraction();
        AdaptiveIconDrawable adaptiveIconDrawable = new AdaptiveIconDrawable(getBackground().getConstantState().newDrawable(), (Drawable) null);
        if (userHandle == null) {
            userHandle = Process.myUserHandle();
        }
        return new ClockBitmapInfo(bitmap, i, f, this.mAnimationInfo, baseIconFactory.createBadgedIconBitmap(adaptiveIconDrawable, userHandle, this.mTargetSdkVersion, false).icon, this.mThemeData);
    }

    public void drawForPersistence(Canvas canvas) {
        LayerDrawable layerDrawable = (LayerDrawable) getForeground();
        resetLevel(layerDrawable, this.mAnimationInfo.hourLayerIndex);
        resetLevel(layerDrawable, this.mAnimationInfo.minuteLayerIndex);
        resetLevel(layerDrawable, this.mAnimationInfo.secondLayerIndex);
        draw(canvas);
        this.mAnimationInfo.applyTime(Calendar.getInstance(), (LayerDrawable) getForeground());
    }

    private void resetLevel(LayerDrawable layerDrawable, int i) {
        if (i != -1) {
            layerDrawable.getDrawable(i).setLevel(0);
        }
    }

    private static class AnimationInfo {
        public Drawable.ConstantState baseDrawableState;
        public int defaultHour;
        public int defaultMinute;
        public int defaultSecond;
        public int hourLayerIndex;
        public int minuteLayerIndex;
        public int secondLayerIndex;

        private AnimationInfo() {
        }

        /* access modifiers changed from: package-private */
        public boolean applyTime(Calendar calendar, LayerDrawable layerDrawable) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            int i = (calendar.get(10) + (12 - this.defaultHour)) % 12;
            int i2 = (calendar.get(12) + (60 - this.defaultMinute)) % 60;
            int i3 = (calendar.get(13) + (60 - this.defaultSecond)) % 60;
            int i4 = this.hourLayerIndex;
            boolean z = i4 != -1 && layerDrawable.getDrawable(i4).setLevel((i * 60) + calendar.get(12));
            int i5 = this.minuteLayerIndex;
            if (i5 != -1 && layerDrawable.getDrawable(i5).setLevel((calendar.get(10) * 60) + i2)) {
                z = true;
            }
            int i6 = this.secondLayerIndex;
            if (i6 == -1 || !layerDrawable.getDrawable(i6).setLevel(i3 * 10)) {
                return z;
            }
            return true;
        }
    }

    static class ClockBitmapInfo extends BitmapInfo {
        public final AnimationInfo animInfo;
        public final ColorFilter bgFilter;
        public final Bitmap mFlattenedBackground;
        public final int offset;
        public final float scale;
        public final ThemedIconDrawable$ThemeData themeData;

        ClockBitmapInfo(Bitmap bitmap, int i, float f, AnimationInfo animationInfo, Bitmap bitmap2, ThemedIconDrawable$ThemeData themedIconDrawable$ThemeData) {
            this(bitmap, i, f, animationInfo, bitmap2, themedIconDrawable$ThemeData, (ColorFilter) null);
        }

        ClockBitmapInfo(Bitmap bitmap, int i, float f, AnimationInfo animationInfo, Bitmap bitmap2, ThemedIconDrawable$ThemeData themedIconDrawable$ThemeData, ColorFilter colorFilter) {
            super(bitmap, i);
            this.scale = f;
            this.animInfo = animationInfo;
            this.offset = (int) Math.ceil((double) (((float) bitmap.getWidth()) * 0.03125f));
            this.mFlattenedBackground = bitmap2;
            this.themeData = themedIconDrawable$ThemeData;
            this.bgFilter = colorFilter;
        }
    }
}
