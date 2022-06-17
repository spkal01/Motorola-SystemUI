package com.android.p011wm.shell.onehanded;

import android.animation.ValueAnimator;

/* renamed from: com.android.wm.shell.onehanded.OneHandedBackgroundPanelOrganizer$$ExternalSyntheticLambda0 */
public final /* synthetic */ class OneHandedBackgroundPanelOrganizer$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ OneHandedBackgroundPanelOrganizer f$0;

    public /* synthetic */ OneHandedBackgroundPanelOrganizer$$ExternalSyntheticLambda0(OneHandedBackgroundPanelOrganizer oneHandedBackgroundPanelOrganizer) {
        this.f$0 = oneHandedBackgroundPanelOrganizer;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$createBackgroundSurface$0(valueAnimator);
    }
}
