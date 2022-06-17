package com.motorola.systemui.screenshot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;

public class BitmapArrayDrawable extends Drawable {
    private List<Bitmap> mBitmapList = new ArrayList();
    private Paint mPaint = new Paint(3);

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void draw(Canvas canvas) {
        canvas.save();
        Rect bounds = getBounds();
        if (bounds != null) {
            canvas.translate((float) bounds.left, (float) bounds.top);
        }
        for (int i = 0; i < this.mBitmapList.size(); i++) {
            Bitmap bitmap = this.mBitmapList.get(i);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mPaint);
            canvas.translate(0.0f, (float) bitmap.getHeight());
        }
        canvas.restore();
    }

    public void addBitmap(Bitmap bitmap) {
        this.mBitmapList.add(bitmap);
    }

    public Bitmap removeLastBitmap() {
        if (this.mBitmapList.size() <= 0) {
            return null;
        }
        List<Bitmap> list = this.mBitmapList;
        return list.remove(list.size() - 1);
    }

    public void clearAllBitmaps() {
        this.mBitmapList.clear();
    }

    public List<Bitmap> getBitmapLists() {
        return this.mBitmapList;
    }
}
