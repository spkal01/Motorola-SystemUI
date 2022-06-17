package com.motorola.systemui.desktop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.android.settingslib.Utils;
import com.android.systemui.R$styleable;

public class RoundRectFrameLayout extends FrameLayout {
    private boolean mChipChildRect;
    private int mOutlineRadius;

    public RoundRectFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundRectFrameLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public RoundRectFrameLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mChipChildRect = true;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RoundRectFrameLayout, 0, 0);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i3 = 0; i3 < indexCount; i3++) {
                int index = obtainStyledAttributes.getIndex(i3);
                if (index == R$styleable.RoundRectFrameLayout_outlineRadius) {
                    this.mOutlineRadius = obtainStyledAttributes.getDimensionPixelSize(i3, 0);
                } else if (index == R$styleable.RoundRectFrameLayout_chipChildRect) {
                    this.mChipChildRect = obtainStyledAttributes.getBoolean(i3, true);
                }
            }
            obtainStyledAttributes.recycle();
        }
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        setVerticalScrollBarEnabled(false);
        setWillNotDraw(false);
        if (this.mOutlineRadius == 0) {
            this.mOutlineRadius = getResources().getDimensionPixelSize(Utils.getThemeAttr(this.mContext, 16844145));
        }
    }

    public void draw(Canvas canvas) {
        canvas.save();
        if (!this.mChipChildRect || getChildCount() <= 0) {
            Path path = new Path();
            RectF rectF = new RectF(0.0f, (float) getScrollY(), (float) getWidth(), ((float) getHeight()) + ((float) getScrollY()));
            int i = this.mOutlineRadius;
            path.addRoundRect(rectF, (float) i, (float) i, Path.Direction.CW);
            canvas.clipPath(path);
        } else {
            int min = Math.min(getChildAt(0).getHeight(), getHeight());
            Path path2 = new Path();
            RectF rectF2 = new RectF(0.0f, (float) getScrollY(), (float) getWidth(), ((float) min) + ((float) getScrollY()));
            int i2 = this.mOutlineRadius;
            path2.addRoundRect(rectF2, (float) i2, (float) i2, Path.Direction.CW);
            canvas.clipPath(path2);
        }
        super.draw(canvas);
        canvas.restore();
    }
}
