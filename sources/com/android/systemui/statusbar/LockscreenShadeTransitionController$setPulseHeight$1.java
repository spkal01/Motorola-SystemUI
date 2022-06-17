package com.android.systemui.statusbar;

import android.animation.ValueAnimator;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LockscreenShadeTransitionController.kt */
final class LockscreenShadeTransitionController$setPulseHeight$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    LockscreenShadeTransitionController$setPulseHeight$1(LockscreenShadeTransitionController lockscreenShadeTransitionController) {
        this.this$0 = lockscreenShadeTransitionController;
    }

    public final void onAnimationUpdate(@NotNull ValueAnimator valueAnimator) {
        Intrinsics.checkNotNullParameter(valueAnimator, "animation");
        LockscreenShadeTransitionController lockscreenShadeTransitionController = this.this$0;
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        LockscreenShadeTransitionController.setPulseHeight$default(lockscreenShadeTransitionController, ((Float) animatedValue).floatValue(), false, 2, (Object) null);
    }
}
