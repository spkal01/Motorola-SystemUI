package com.motorola.systemui.biometrics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint({"AppCompatCustomView"})
public class MotoUdfpsAnimationView extends ImageView {
    public MotoUdfpsAnimationView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MotoUdfpsAnimationView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public MotoUdfpsAnimationView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
