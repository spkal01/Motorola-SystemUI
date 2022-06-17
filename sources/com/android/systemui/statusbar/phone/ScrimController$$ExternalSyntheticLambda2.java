package com.android.systemui.statusbar.phone;

import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.util.function.TriConsumer;

public final /* synthetic */ class ScrimController$$ExternalSyntheticLambda2 implements TriConsumer {
    public final /* synthetic */ LightBarController f$0;

    public /* synthetic */ ScrimController$$ExternalSyntheticLambda2(LightBarController lightBarController) {
        this.f$0 = lightBarController;
    }

    public final void accept(Object obj, Object obj2, Object obj3) {
        this.f$0.setScrimState((ScrimState) obj, ((Float) obj2).floatValue(), (ColorExtractor.GradientColors) obj3);
    }
}
