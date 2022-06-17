package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.statusbar.notification.ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$animator$1$2 */
/* compiled from: ViewGroupFadeHelper.kt */
public final class C1531xbb47cb27 extends AnimatorListenerAdapter {
    final /* synthetic */ Runnable $endRunnable;

    C1531xbb47cb27(Runnable runnable) {
        this.$endRunnable = runnable;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        Runnable runnable = this.$endRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }
}
