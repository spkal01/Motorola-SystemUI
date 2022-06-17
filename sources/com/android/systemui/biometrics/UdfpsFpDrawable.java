package com.android.systemui.biometrics;

import android.content.Context;
import android.graphics.Canvas;

public class UdfpsFpDrawable extends UdfpsDrawable {
    UdfpsFpDrawable(Context context) {
        super(context);
    }

    public void draw(Canvas canvas) {
        if (!isIlluminationShowing()) {
            this.mFingerprintDrawable.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void setPaintColor(int i) {
        this.mPaint.setColor(i);
    }
}
