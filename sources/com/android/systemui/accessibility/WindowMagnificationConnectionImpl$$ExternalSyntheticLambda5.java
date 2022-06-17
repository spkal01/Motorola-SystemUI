package com.android.systemui.accessibility;

import android.view.accessibility.IRemoteMagnificationAnimationCallback;

public final /* synthetic */ class WindowMagnificationConnectionImpl$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ WindowMagnificationConnectionImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ IRemoteMagnificationAnimationCallback f$2;

    public /* synthetic */ WindowMagnificationConnectionImpl$$ExternalSyntheticLambda5(WindowMagnificationConnectionImpl windowMagnificationConnectionImpl, int i, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) {
        this.f$0 = windowMagnificationConnectionImpl;
        this.f$1 = i;
        this.f$2 = iRemoteMagnificationAnimationCallback;
    }

    public final void run() {
        this.f$0.lambda$disableWindowMagnification$2(this.f$1, this.f$2);
    }
}
