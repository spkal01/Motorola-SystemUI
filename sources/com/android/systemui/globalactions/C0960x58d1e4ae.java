package com.android.systemui.globalactions;

import android.animation.ValueAnimator;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C0960x58d1e4ae implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GlobalActionsDialogLite.ActionsDialogLite f$0;

    public /* synthetic */ C0960x58d1e4ae(GlobalActionsDialogLite.ActionsDialogLite actionsDialogLite) {
        this.f$0 = actionsDialogLite;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$dismissInternal$9(valueAnimator);
    }
}
