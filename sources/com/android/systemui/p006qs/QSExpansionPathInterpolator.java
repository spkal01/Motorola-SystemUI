package com.android.systemui.p006qs;

import android.view.animation.Interpolator;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.QSExpansionPathInterpolator */
/* compiled from: QSExpansionPathInterpolator.kt */
public final class QSExpansionPathInterpolator {
    @NotNull
    private PathInterpolatorBuilder pathInterpolatorBuilder = new PathInterpolatorBuilder(0.0f, 0.0f, 0.0f, 1.0f);

    @NotNull
    public final Interpolator getXInterpolator() {
        Interpolator xInterpolator = this.pathInterpolatorBuilder.getXInterpolator();
        Intrinsics.checkNotNullExpressionValue(xInterpolator, "pathInterpolatorBuilder.xInterpolator");
        return xInterpolator;
    }

    @NotNull
    public final Interpolator getYInterpolator() {
        Interpolator yInterpolator = this.pathInterpolatorBuilder.getYInterpolator();
        Intrinsics.checkNotNullExpressionValue(yInterpolator, "pathInterpolatorBuilder.yInterpolator");
        return yInterpolator;
    }
}
