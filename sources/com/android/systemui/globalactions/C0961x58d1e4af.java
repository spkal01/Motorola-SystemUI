package com.android.systemui.globalactions;

import android.animation.ValueAnimator;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C0961x58d1e4af implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GlobalActionsDialogLite.ActionsDialogLite f$0;

    public /* synthetic */ C0961x58d1e4af(GlobalActionsDialogLite.ActionsDialogLite actionsDialogLite) {
        this.f$0 = actionsDialogLite;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$dismissInternal$8(valueAnimator);
    }
}
