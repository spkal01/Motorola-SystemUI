package com.android.systemui.controls.p004ui;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.util.MathUtils;
import com.android.internal.graphics.ColorUtils;
import java.util.Objects;

/* renamed from: com.android.systemui.controls.ui.ControlViewHolder$startBackgroundAnimation$1$1 */
/* compiled from: ControlViewHolder.kt */
final class ControlViewHolder$startBackgroundAnimation$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ Drawable $clipDrawable;
    final /* synthetic */ int $newBaseColor;
    final /* synthetic */ int $newClipColor;
    final /* synthetic */ float $oldAlpha;
    final /* synthetic */ int $oldBaseColor;
    final /* synthetic */ int $oldClipColor;
    final /* synthetic */ ControlViewHolder this$0;

    ControlViewHolder$startBackgroundAnimation$1$1(int i, int i2, int i3, int i4, float f, ControlViewHolder controlViewHolder, Drawable drawable) {
        this.$oldClipColor = i;
        this.$newClipColor = i2;
        this.$oldBaseColor = i3;
        this.$newBaseColor = i4;
        this.$oldAlpha = f;
        this.this$0 = controlViewHolder;
        this.$clipDrawable = drawable;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Int");
        this.this$0.applyBackgroundChange(this.$clipDrawable, ((Integer) animatedValue).intValue(), ColorUtils.blendARGB(this.$oldClipColor, this.$newClipColor, valueAnimator.getAnimatedFraction()), ColorUtils.blendARGB(this.$oldBaseColor, this.$newBaseColor, valueAnimator.getAnimatedFraction()), MathUtils.lerp(this.$oldAlpha, 1.0f, valueAnimator.getAnimatedFraction()));
    }
}
