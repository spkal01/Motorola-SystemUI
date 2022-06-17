package com.android.systemui.controls.p004ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$reload$1 */
/* compiled from: ControlsUiControllerImpl.kt */
public final class ControlsUiControllerImpl$reload$1 extends AnimatorListenerAdapter {
    final /* synthetic */ ViewGroup $parent;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$reload$1(ControlsUiControllerImpl controlsUiControllerImpl, ViewGroup viewGroup) {
        this.this$0 = controlsUiControllerImpl;
        this.$parent = viewGroup;
    }

    public void onAnimationEnd(@NotNull Animator animator) {
        Intrinsics.checkNotNullParameter(animator, "animation");
        this.this$0.controlViewsById.clear();
        this.this$0.controlsById.clear();
        ControlsUiControllerImpl controlsUiControllerImpl = this.this$0;
        ViewGroup viewGroup = this.$parent;
        Runnable access$getOnDismiss$p = controlsUiControllerImpl.onDismiss;
        if (access$getOnDismiss$p != null) {
            Context access$getActivityContext$p = this.this$0.activityContext;
            if (access$getActivityContext$p != null) {
                controlsUiControllerImpl.show(viewGroup, access$getOnDismiss$p, access$getActivityContext$p);
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.$parent, "alpha", new float[]{0.0f, 1.0f});
                ofFloat.setInterpolator(new DecelerateInterpolator(1.0f));
                ofFloat.setDuration(200);
                ofFloat.start();
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("activityContext");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("onDismiss");
        throw null;
    }
}
