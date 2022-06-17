package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ImageUtils {
    private static final Paint PAINT = new Paint(7);

    public enum ScalingLogic {
        CROP,
        FIT
    }

    public static Bitmap createScaledBitmapByWidth(Bitmap bitmap, int i, ScalingLogic scalingLogic) {
        return createScaledBitmap(bitmap, i, (int) ((((float) i) / ((float) bitmap.getWidth())) * ((float) bitmap.getHeight())), scalingLogic);
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int i, int i2, ScalingLogic scalingLogic) {
        Rect calculateSrcRect = calculateSrcRect(bitmap.getWidth(), bitmap.getHeight(), i, i2, scalingLogic);
        Rect calculateDstRect = calculateDstRect(bitmap.getWidth(), bitmap.getHeight(), i, i2, scalingLogic);
        Bitmap createBitmap = Bitmap.createBitmap(calculateDstRect.width(), calculateDstRect.height(), bitmap.getConfig());
        Canvas canvas = new Canvas();
        canvas.setBitmap(createBitmap);
        canvas.drawBitmap(bitmap, calculateSrcRect, calculateDstRect, PAINT);
        canvas.setBitmap((Bitmap) null);
        return createBitmap;
    }

    public static Rect calculateSrcRect(int i, int i2, int i3, int i4, ScalingLogic scalingLogic) {
        if (scalingLogic != ScalingLogic.CROP) {
            return new Rect(0, 0, i, i2);
        }
        float f = (float) i;
        float f2 = (float) i2;
        float f3 = ((float) i3) / ((float) i4);
        if (f / f2 > f3) {
            int i5 = (int) (f2 * f3);
            int i6 = (i - i5) / 2;
            return new Rect(i6, 0, i5 + i6, i2);
        }
        int i7 = (int) (f / f3);
        int i8 = (i2 - i7) / 2;
        return new Rect(0, i8, i, i7 + i8);
    }

    public static Rect calculateDstRect(int i, int i2, int i3, int i4, ScalingLogic scalingLogic) {
        if (scalingLogic != ScalingLogic.FIT) {
            return new Rect(0, 0, i3, i4);
        }
        float f = ((float) i) / ((float) i2);
        float f2 = (float) i3;
        float f3 = (float) i4;
        if (f > f2 / f3) {
            return new Rect(0, 0, i3, (int) (f2 / f));
        }
        return new Rect(0, 0, (int) (f3 * f), i4);
    }
}
