package com.android.systemui.statusbar;

import android.animation.ValueAnimator;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LockscreenShadeTransitionController.kt */
final class LockscreenShadeTransitionController$setDragDownAmountAnimated$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    LockscreenShadeTransitionController$setDragDownAmountAnimated$1(LockscreenShadeTransitionController lockscreenShadeTransitionController) {
        this.this$0 = lockscreenShadeTransitionController;
    }

    public final void onAnimationUpdate(@NotNull ValueAnimator valueAnimator) {
        Intrinsics.checkNotNullParameter(valueAnimator, "animation");
        LockscreenShadeTransitionController lockscreenShadeTransitionController = this.this$0;
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        lockscreenShadeTransitionController.mo18462xcfc05636(((Float) animatedValue).floatValue());
    }
}
