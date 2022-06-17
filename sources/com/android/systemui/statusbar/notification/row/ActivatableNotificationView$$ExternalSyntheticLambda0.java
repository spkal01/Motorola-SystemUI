package com.android.systemui.statusbar.notification.row;

import android.animation.ValueAnimator;

public final /* synthetic */ class ActivatableNotificationView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ ActivatableNotificationView f$0;

    public /* synthetic */ ActivatableNotificationView$$ExternalSyntheticLambda0(ActivatableNotificationView activatableNotificationView) {
        this.f$0 = activatableNotificationView;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updateBackgroundTint$0(valueAnimator);
    }
}
