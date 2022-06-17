package com.android.systemui.statusbar;

import com.android.systemui.plugins.statusbar.StatusBarStateController;

public final /* synthetic */ class StatusBarStateControllerImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StatusBarStateControllerImpl f$0;
    public final /* synthetic */ StatusBarStateController.StateListener f$1;

    public /* synthetic */ StatusBarStateControllerImpl$$ExternalSyntheticLambda0(StatusBarStateControllerImpl statusBarStateControllerImpl, StatusBarStateController.StateListener stateListener) {
        this.f$0 = statusBarStateControllerImpl;
        this.f$1 = stateListener;
    }

    public final void run() {
        this.f$0.lambda$addListenerInternalLocked$1(this.f$1);
    }
}
