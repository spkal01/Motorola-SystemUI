package com.android.keyguard;

import com.android.keyguard.AnimatableClockView;
import com.android.systemui.plugins.statusbar.StatusBarStateController;

public final /* synthetic */ class AnimatableClockController$1$$ExternalSyntheticLambda0 implements AnimatableClockView.DozeStateGetter {
    public final /* synthetic */ StatusBarStateController f$0;

    public /* synthetic */ AnimatableClockController$1$$ExternalSyntheticLambda0(StatusBarStateController statusBarStateController) {
        this.f$0 = statusBarStateController;
    }

    public final boolean isDozing() {
        return this.f$0.isDozing();
    }
}
