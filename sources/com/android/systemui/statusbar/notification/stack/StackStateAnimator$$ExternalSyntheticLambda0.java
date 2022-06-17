package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.notification.row.ExpandableView;

public final /* synthetic */ class StackStateAnimator$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ExpandableView f$0;

    public /* synthetic */ StackStateAnimator$$ExternalSyntheticLambda0(ExpandableView expandableView) {
        this.f$0 = expandableView;
    }

    public final void run() {
        StackStateAnimator.removeTransientView(this.f$0);
    }
}
