package com.android.systemui.biometrics;

import android.content.Context;
import android.util.AttributeSet;

public class UdfpsBpView extends UdfpsAnimationView {
    private UdfpsFpDrawable mFingerprintDrawable = new UdfpsFpDrawable(this.mContext);

    public /* bridge */ /* synthetic */ void onExpansionChanged(float f, boolean z) {
        super.onExpansionChanged(f, z);
    }

    public UdfpsBpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public UdfpsDrawable getDrawable() {
        return this.mFingerprintDrawable;
    }
}
