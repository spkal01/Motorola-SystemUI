package com.android.systemui.statusbar;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LightRevealScrim.kt */
public final class CircleReveal implements LightRevealEffect {
    private final float centerX;
    private final float centerY;
    private final float endRadius;
    private final float startRadius;

    public CircleReveal(float f, float f2, float f3, float f4) {
        this.centerX = f;
        this.centerY = f2;
        this.startRadius = f3;
        this.endRadius = f4;
    }

    public void setRevealAmountOnScrim(float f, @NotNull LightRevealScrim lightRevealScrim) {
        Intrinsics.checkNotNullParameter(lightRevealScrim, "scrim");
        float percentPastThreshold = LightRevealEffect.Companion.getPercentPastThreshold(f, 0.5f);
        float f2 = this.startRadius;
        float f3 = f2 + ((this.endRadius - f2) * f);
        lightRevealScrim.setRevealGradientEndColorAlpha(1.0f - percentPastThreshold);
        float f4 = this.centerX;
        float f5 = this.centerY;
        lightRevealScrim.setRevealGradientBounds(f4 - f3, f5 - f3, f4 + f3, f5 + f3);
    }
}
