package com.motorola.systemui.screenshot;

import android.graphics.Bitmap;

public class BitmapOffsetCalculate {
    public native int calcY(Bitmap bitmap, Bitmap bitmap2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int[] iArr);

    static {
        System.loadLibrary("BitmapOffsetCalculate_jni");
    }
}
