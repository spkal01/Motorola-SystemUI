package com.android.systemui.statusbar.phone;

import android.view.IRemoteAnimationRunner;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ StatusBar f$0;
    public final /* synthetic */ IRemoteAnimationRunner f$1;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda32(StatusBar statusBar, IRemoteAnimationRunner iRemoteAnimationRunner) {
        this.f$0 = statusBar;
        this.f$1 = iRemoteAnimationRunner;
    }

    public final void run() {
        this.f$0.lambda$hideKeyguardWithAnimation$15(this.f$1);
    }
}
