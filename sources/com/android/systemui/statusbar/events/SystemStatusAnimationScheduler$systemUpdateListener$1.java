package com.android.systemui.statusbar.events;

import android.animation.ValueAnimator;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SystemStatusAnimationScheduler.kt */
final class SystemStatusAnimationScheduler$systemUpdateListener$1 implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ SystemStatusAnimationScheduler this$0;

    SystemStatusAnimationScheduler$systemUpdateListener$1(SystemStatusAnimationScheduler systemStatusAnimationScheduler) {
        this.this$0 = systemStatusAnimationScheduler;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        SystemStatusAnimationScheduler systemStatusAnimationScheduler = this.this$0;
        Intrinsics.checkNotNullExpressionValue(valueAnimator, "anim");
        systemStatusAnimationScheduler.notifySystemAnimationUpdate(valueAnimator);
    }
}
