package com.android.systemui.controls.p004ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior$updateRange$1$2 */
/* compiled from: ToggleRangeBehavior.kt */
public final class ToggleRangeBehavior$updateRange$1$2 extends AnimatorListenerAdapter {
    final /* synthetic */ ToggleRangeBehavior this$0;

    ToggleRangeBehavior$updateRange$1$2(ToggleRangeBehavior toggleRangeBehavior) {
        this.this$0 = toggleRangeBehavior;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        this.this$0.rangeAnimator = null;
    }
}
