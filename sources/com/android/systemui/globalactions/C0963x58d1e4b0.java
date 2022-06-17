package com.android.systemui.globalactions;

import android.animation.ValueAnimator;
import com.android.systemui.globalactions.GlobalActionsDialogLite;

/* renamed from: com.android.systemui.globalactions.GlobalActionsDialogLite$ActionsDialogLite$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C0963x58d1e4b0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ GlobalActionsDialogLite.ActionsDialogLite f$0;

    public /* synthetic */ C0963x58d1e4b0(GlobalActionsDialogLite.ActionsDialogLite actionsDialogLite) {
        this.f$0 = actionsDialogLite;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$showDialog$6(valueAnimator);
    }
}
