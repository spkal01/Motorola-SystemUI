package com.android.systemui.biometrics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.systemui.R$color;
import com.android.systemui.R$id;

public class UdfpsFpmOtherView extends UdfpsAnimationView {
    private final UdfpsFpDrawable mFingerprintDrawable;
    private ImageView mFingerprintView;

    public /* bridge */ /* synthetic */ void onExpansionChanged(float f, boolean z) {
        super.onExpansionChanged(f, z);
    }

    public UdfpsFpmOtherView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        UdfpsFpDrawable udfpsFpDrawable = new UdfpsFpDrawable(context);
        this.mFingerprintDrawable = udfpsFpDrawable;
        udfpsFpDrawable.setPaintColor(context.getColor(R$color.udfps_fpm_other_icon_color));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        ImageView imageView = (ImageView) findViewById(R$id.udfps_fpm_other_fp_view);
        this.mFingerprintView = imageView;
        imageView.setImageDrawable(this.mFingerprintDrawable);
    }

    /* access modifiers changed from: package-private */
    public UdfpsDrawable getDrawable() {
        return this.mFingerprintDrawable;
    }
}
