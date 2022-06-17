package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LockscreenShadeTransitionController.kt */
public final class DragDownHelper$cancelChildExpansion$1 extends AnimatorListenerAdapter {
    final /* synthetic */ ExpandableView $child;
    final /* synthetic */ DragDownHelper this$0;

    DragDownHelper$cancelChildExpansion$1(DragDownHelper dragDownHelper, ExpandableView expandableView) {
        this.this$0 = dragDownHelper;
        this.$child = expandableView;
    }

    public void onAnimationEnd(@NotNull Animator animator) {
        Intrinsics.checkNotNullParameter(animator, "animation");
        this.this$0.getExpandCallback().setUserLockedChild(this.$child, false);
    }
}
