package com.android.systemui.statusbar;

import android.animation.ValueAnimator;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.NotificationShadeDepthController$keyguardStateCallback$1$onKeyguardFadingAwayChanged$1$1 */
/* compiled from: NotificationShadeDepthController.kt */
final class C1477xd4eb1c54 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ NotificationShadeDepthController this$0;

    C1477xd4eb1c54(NotificationShadeDepthController notificationShadeDepthController) {
        this.this$0 = notificationShadeDepthController;
    }

    public final void onAnimationUpdate(@NotNull ValueAnimator valueAnimator) {
        Intrinsics.checkNotNullParameter(valueAnimator, "animation");
        NotificationShadeDepthController notificationShadeDepthController = this.this$0;
        BlurUtils access$getBlurUtils$p = notificationShadeDepthController.blurUtils;
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        notificationShadeDepthController.setWakeAndUnlockBlurRadius(access$getBlurUtils$p.blurRadiusOfRatio(((Float) animatedValue).floatValue()));
    }
}
