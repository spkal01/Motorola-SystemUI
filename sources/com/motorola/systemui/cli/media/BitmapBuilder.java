package com.motorola.systemui.cli.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

public class BitmapBuilder {
    private static final boolean DEBUG = (!Build.IS_USER);
    private Bitmap mBitmap;

    public BitmapBuilder(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public BitmapBuilder(Drawable drawable) {
        int i;
        Bitmap bitmap = null;
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    bitmap = bitmapDrawable.getBitmap();
                }
            } else {
                int i2 = 1;
                if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                    i = 1;
                } else {
                    i2 = drawable.getIntrinsicWidth();
                    i = drawable.getIntrinsicHeight();
                }
                bitmap = createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                if (bitmap != null) {
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }
            }
        }
        this.mBitmap = bitmap;
    }

    public BitmapBuilder(Parcelable parcelable, Context context) {
        Bitmap bitmap;
        if (parcelable instanceof Bitmap) {
            bitmap = (Bitmap) parcelable;
        } else {
            if (parcelable instanceof Icon) {
                Drawable loadDrawable = ((Icon) parcelable).loadDrawable(context);
                if (loadDrawable instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) loadDrawable).getBitmap();
                }
            }
            bitmap = null;
        }
        this.mBitmap = bitmap;
    }

    public Bitmap build() {
        return this.mBitmap;
    }

    public BitmapBuilder scale(Rect rect) {
        return scale(rect.width(), rect.height());
    }

    public BitmapBuilder scale(int i, int i2) {
        return scale(i, i2, false);
    }

    public BitmapBuilder scale(int i, int i2, boolean z) {
        if (this.mBitmap != null) {
            if (DEBUG) {
                Log.d("CLI-QSMV-BitmapBuilder", "will scale bitmap - originalWidth: " + this.mBitmap.getWidth() + " finalWidth: " + i + " originalHeight: " + this.mBitmap.getHeight() + " finalHeight: " + i2);
            }
            try {
                this.mBitmap = Bitmap.createScaledBitmap(this.mBitmap, i, i2, z);
            } catch (OutOfMemoryError unused) {
                Log.e("CLI-QSMV-BitmapBuilder", "Failed to allocate memory to create a scaled bitmap.");
            }
        }
        return this;
    }

    private Bitmap createBitmap(int i, int i2, Bitmap.Config config) {
        try {
            return Bitmap.createBitmap(i, i2, config);
        } catch (OutOfMemoryError unused) {
            Log.e("CLI-QSMV-BitmapBuilder", "Failed to allocate memory to create bitmap.");
            return null;
        }
    }
}
