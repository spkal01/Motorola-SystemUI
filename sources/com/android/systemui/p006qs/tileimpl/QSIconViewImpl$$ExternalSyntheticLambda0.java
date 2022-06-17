package com.android.systemui.p006qs.tileimpl;

import android.animation.ValueAnimator;
import android.widget.ImageView;

/* renamed from: com.android.systemui.qs.tileimpl.QSIconViewImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class QSIconViewImpl$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ float f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ ImageView f$4;

    public /* synthetic */ QSIconViewImpl$$ExternalSyntheticLambda0(float f, float f2, float f3, float f4, ImageView imageView) {
        this.f$0 = f;
        this.f$1 = f2;
        this.f$2 = f3;
        this.f$3 = f4;
        this.f$4 = imageView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        QSIconViewImpl.lambda$animateGrayScale$1(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}
