package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;

public final /* synthetic */ class NotificationPanelViewController$$ExternalSyntheticLambda3 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ NotificationPanelViewController f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ NotificationPanelViewController$$ExternalSyntheticLambda3(NotificationPanelViewController notificationPanelViewController, boolean z) {
        this.f$0 = notificationPanelViewController;
        this.f$1 = z;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$switchAnimation$12(this.f$1, valueAnimator);
    }
}
