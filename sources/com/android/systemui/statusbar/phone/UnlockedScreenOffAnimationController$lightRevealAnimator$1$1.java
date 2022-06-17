package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;
import com.android.systemui.statusbar.LightRevealScrim;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UnlockedScreenOffAnimationController.kt */
final class UnlockedScreenOffAnimationController$lightRevealAnimator$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ UnlockedScreenOffAnimationController this$0;

    UnlockedScreenOffAnimationController$lightRevealAnimator$1$1(UnlockedScreenOffAnimationController unlockedScreenOffAnimationController) {
        this.this$0 = unlockedScreenOffAnimationController;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        LightRevealScrim access$getLightRevealScrim$p = this.this$0.lightRevealScrim;
        if (access$getLightRevealScrim$p != null) {
            Object animatedValue = valueAnimator.getAnimatedValue();
            Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
            access$getLightRevealScrim$p.setRevealAmount(((Float) animatedValue).floatValue());
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("lightRevealScrim");
        throw null;
    }
}
