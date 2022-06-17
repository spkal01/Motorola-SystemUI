package com.android.systemui.volume;

import android.animation.ValueAnimator;

public final /* synthetic */ class VolumeDialogImpl$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ VolumeDialogImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ VolumeDialogImpl$$ExternalSyntheticLambda1(VolumeDialogImpl volumeDialogImpl, int i, int i2) {
        this.f$0 = volumeDialogImpl;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setupRingerDrawer$8(this.f$1, this.f$2, valueAnimator);
    }
}
