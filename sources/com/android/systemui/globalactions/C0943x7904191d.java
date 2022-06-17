package com.android.systemui.globalactions;

import android.animation.ValueAnimator;
import com.android.systemui.globalactions.GlobalActionsDialogFolio;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogFolio$ActionDialogFolio$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C0943x7904191d implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GlobalActionsDialogFolio.ActionDialogFolio f$0;

    public /* synthetic */ C0943x7904191d(GlobalActionsDialogFolio.ActionDialogFolio actionDialogFolio) {
        this.f$0 = actionDialogFolio;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$showDialog$1(valueAnimator);
    }
}
