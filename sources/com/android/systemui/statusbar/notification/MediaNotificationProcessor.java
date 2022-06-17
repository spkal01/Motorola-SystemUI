package com.android.systemui.statusbar.notification;

import android.graphics.Bitmap;
import android.util.Log;
import androidx.palette.graphics.Palette;

public class MediaNotificationProcessor {
    public static Palette.Swatch findBackgroundSwatch(Bitmap bitmap) {
        return findBackgroundSwatch(generateArtworkPaletteBuilder(bitmap).generate());
    }

    public static Palette.Swatch findBackgroundSwatch(Palette palette) {
        Palette.Swatch dominantSwatch = palette.getDominantSwatch();
        if (dominantSwatch == null) {
            return new Palette.Swatch(-1, 100);
        }
        if (!isWhiteOrBlack(dominantSwatch.getHsl())) {
            return dominantSwatch;
        }
        float f = -1.0f;
        Palette.Swatch swatch = null;
        for (Palette.Swatch next : palette.getSwatches()) {
            if (next != dominantSwatch && ((float) next.getPopulation()) > f && !isWhiteOrBlack(next.getHsl())) {
                f = (float) next.getPopulation();
                swatch = next;
            }
        }
        return (swatch != null && ((float) dominantSwatch.getPopulation()) / f <= 2.5f) ? swatch : dominantSwatch;
    }

    public static Palette.Builder generateArtworkPaletteBuilder(Bitmap bitmap) {
        try {
            return Palette.from(bitmap).setRegion(0, 0, bitmap.getWidth() / 2, bitmap.getHeight()).clearFilters().resizeBitmapArea(22500);
        } catch (Exception e) {
            Log.e("MediaNotificationProcessor", "Exception message:" + e.getMessage() + "; width = " + bitmap.getWidth() + "; height = " + bitmap.getHeight());
            return null;
        }
    }

    private static boolean isWhiteOrBlack(float[] fArr) {
        return isBlack(fArr) || isWhite(fArr);
    }

    private static boolean isBlack(float[] fArr) {
        return fArr[2] <= 0.08f;
    }

    private static boolean isWhite(float[] fArr) {
        return fArr[2] >= 0.9f;
    }
}
