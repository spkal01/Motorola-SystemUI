package com.android.systemui.media;

import android.animation.ValueAnimator;
import com.android.internal.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.Objects;

/* compiled from: IlluminationDrawable.kt */
final class IlluminationDrawable$animateBackground$1$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ int $finalHighlight;
    final /* synthetic */ int $initialBackground;
    final /* synthetic */ int $initialHighlight;
    final /* synthetic */ IlluminationDrawable this$0;

    IlluminationDrawable$animateBackground$1$1(IlluminationDrawable illuminationDrawable, int i, int i2, int i3) {
        this.this$0 = illuminationDrawable;
        this.$initialBackground = i;
        this.$initialHighlight = i2;
        this.$finalHighlight = i3;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        Object animatedValue = valueAnimator.getAnimatedValue();
        Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        float floatValue = ((Float) animatedValue).floatValue();
        this.this$0.paint.setColor(ColorUtils.blendARGB(this.$initialBackground, this.this$0.backgroundColor, floatValue));
        this.this$0.highlightColor = ColorUtils.blendARGB(this.$initialHighlight, this.$finalHighlight, floatValue);
        ArrayList<LightSourceDrawable> access$getLightSources$p = this.this$0.lightSources;
        IlluminationDrawable illuminationDrawable = this.this$0;
        for (LightSourceDrawable highlightColor : access$getLightSources$p) {
            highlightColor.setHighlightColor(illuminationDrawable.highlightColor);
        }
        this.this$0.invalidateSelf();
    }
}
