package com.android.p011wm.shell.bubbles;

import android.graphics.PointF;
import android.view.View;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.animation.Interpolators;

/* renamed from: com.android.wm.shell.bubbles.StackEducationView$show$1 */
/* compiled from: StackEducationView.kt */
final class StackEducationView$show$1 implements Runnable {
    final /* synthetic */ PointF $stackPosition;
    final /* synthetic */ StackEducationView this$0;

    StackEducationView$show$1(StackEducationView stackEducationView, PointF pointF) {
        this.this$0 = stackEducationView;
        this.$stackPosition = pointF;
    }

    public final void run() {
        View access$getView = this.this$0.getView();
        access$getView.setTranslationY((this.$stackPosition.y + ((float) (access$getView.getContext().getResources().getDimensionPixelSize(C2219R.dimen.bubble_size) / 2))) - ((float) (access$getView.getHeight() / 2)));
        this.this$0.animate().setDuration(this.this$0.ANIMATE_DURATION).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(1.0f);
    }
}
