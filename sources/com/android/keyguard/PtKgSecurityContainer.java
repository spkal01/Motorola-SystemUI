package com.android.keyguard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class PtKgSecurityContainer extends LinearLayout {
    public PtKgSecurityContainer(Context context) {
        this(context, (AttributeSet) null);
    }

    public PtKgSecurityContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PtKgSecurityContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }
}
