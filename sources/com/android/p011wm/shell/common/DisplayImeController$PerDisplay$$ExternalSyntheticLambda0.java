package com.android.p011wm.shell.common;

import android.animation.ValueAnimator;
import com.android.p011wm.shell.common.DisplayImeController;

/* renamed from: com.android.wm.shell.common.DisplayImeController$PerDisplay$$ExternalSyntheticLambda0 */
public final /* synthetic */ class DisplayImeController$PerDisplay$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ DisplayImeController.PerDisplay f$0;
    public final /* synthetic */ float f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;

    public /* synthetic */ DisplayImeController$PerDisplay$$ExternalSyntheticLambda0(DisplayImeController.PerDisplay perDisplay, float f, boolean z, float f2, float f3) {
        this.f$0 = perDisplay;
        this.f$1 = f;
        this.f$2 = z;
        this.f$3 = f2;
        this.f$4 = f3;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$startAnimation$0(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}
