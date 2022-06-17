package com.android.systemui.scrim;

import com.android.internal.colorextraction.ColorExtractor;

public final /* synthetic */ class ScrimView$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ScrimView f$0;
    public final /* synthetic */ ColorExtractor.GradientColors f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ScrimView$$ExternalSyntheticLambda4(ScrimView scrimView, ColorExtractor.GradientColors gradientColors, boolean z) {
        this.f$0 = scrimView;
        this.f$1 = gradientColors;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$setColors$3(this.f$1, this.f$2);
    }
}
