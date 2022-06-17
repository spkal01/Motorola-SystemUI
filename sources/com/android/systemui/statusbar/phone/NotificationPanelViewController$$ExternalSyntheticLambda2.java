package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;

public final /* synthetic */ class NotificationPanelViewController$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ NotificationPanelViewController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ NotificationPanelViewController$$ExternalSyntheticLambda2(NotificationPanelViewController notificationPanelViewController, int i, int i2, int i3, int i4, boolean z) {
        this.f$0 = notificationPanelViewController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = i4;
        this.f$5 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$applyQSClippingBounds$9(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, valueAnimator);
    }
}
