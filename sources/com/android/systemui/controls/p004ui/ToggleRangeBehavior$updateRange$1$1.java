package com.android.systemui.controls.p004ui;

import android.animation.ValueAnimator;
import android.graphics.drawable.ClipDrawable;
import java.util.Objects;

/* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior$updateRange$1$1 */
/* compiled from: ToggleRangeBehavior.kt */
final class ToggleRangeBehavior$updateRange$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ ToggleRangeBehavior this$0;

    ToggleRangeBehavior$updateRange$1$1(ToggleRangeBehavior toggleRangeBehavior) {
        this.this$0 = toggleRangeBehavior;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        ClipDrawable clipLayer = this.this$0.getCvh().getClipLayer();
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Int");
        clipLayer.setLevel(((Integer) animatedValue).intValue());
    }
}
