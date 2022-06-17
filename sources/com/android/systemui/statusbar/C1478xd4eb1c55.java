package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.statusbar.NotificationShadeDepthController$keyguardStateCallback$1$onKeyguardFadingAwayChanged$1$2 */
/* compiled from: NotificationShadeDepthController.kt */
public final class C1478xd4eb1c55 extends AnimatorListenerAdapter {
    final /* synthetic */ NotificationShadeDepthController this$0;

    C1478xd4eb1c55(NotificationShadeDepthController notificationShadeDepthController) {
        this.this$0 = notificationShadeDepthController;
    }

    public void onAnimationEnd(@Nullable Animator animator) {
        this.this$0.keyguardAnimator = null;
        NotificationShadeDepthController.scheduleUpdate$default(this.this$0, (View) null, 1, (Object) null);
    }
}
