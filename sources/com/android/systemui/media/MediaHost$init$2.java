package com.android.systemui.media;

import android.view.View;
import com.android.systemui.util.animation.MeasurementInput;
import com.android.systemui.util.animation.MeasurementOutput;
import com.android.systemui.util.animation.UniqueObjectHostView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MediaHost.kt */
public final class MediaHost$init$2 implements UniqueObjectHostView.MeasurementManager {
    final /* synthetic */ int $location;
    final /* synthetic */ MediaHost this$0;

    MediaHost$init$2(MediaHost mediaHost, int i) {
        this.this$0 = mediaHost;
        this.$location = i;
    }

    @NotNull
    public MeasurementOutput onMeasure(@NotNull MeasurementInput measurementInput) {
        Intrinsics.checkNotNullParameter(measurementInput, "input");
        if (View.MeasureSpec.getMode(measurementInput.getWidthMeasureSpec()) == Integer.MIN_VALUE) {
            measurementInput.setWidthMeasureSpec(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measurementInput.getWidthMeasureSpec()), 1073741824));
        }
        this.this$0.state.setMeasurementInput(measurementInput);
        return this.this$0.mediaHostStatesManager.updateCarouselDimensions(this.$location, this.this$0.state);
    }
}
