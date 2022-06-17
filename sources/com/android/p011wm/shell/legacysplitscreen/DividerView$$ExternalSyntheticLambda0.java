package com.android.p011wm.shell.legacysplitscreen;

import android.animation.ValueAnimator;
import com.android.internal.policy.DividerSnapAlgorithm;

/* renamed from: com.android.wm.shell.legacysplitscreen.DividerView$$ExternalSyntheticLambda0 */
public final /* synthetic */ class DividerView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ DividerView f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ DividerSnapAlgorithm.SnapTarget f$2;

    public /* synthetic */ DividerView$$ExternalSyntheticLambda0(DividerView dividerView, boolean z, DividerSnapAlgorithm.SnapTarget snapTarget) {
        this.f$0 = dividerView;
        this.f$1 = z;
        this.f$2 = snapTarget;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$getFlingAnimator$2(this.f$1, this.f$2, valueAnimator);
    }
}
