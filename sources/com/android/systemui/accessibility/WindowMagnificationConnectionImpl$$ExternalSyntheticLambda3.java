package com.android.systemui.accessibility;

import android.view.accessibility.IRemoteMagnificationAnimationCallback;

public final /* synthetic */ class WindowMagnificationConnectionImpl$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ WindowMagnificationConnectionImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ float f$4;
    public final /* synthetic */ IRemoteMagnificationAnimationCallback f$5;

    public /* synthetic */ WindowMagnificationConnectionImpl$$ExternalSyntheticLambda3(WindowMagnificationConnectionImpl windowMagnificationConnectionImpl, int i, float f, float f2, float f3, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) {
        this.f$0 = windowMagnificationConnectionImpl;
        this.f$1 = i;
        this.f$2 = f;
        this.f$3 = f2;
        this.f$4 = f3;
        this.f$5 = iRemoteMagnificationAnimationCallback;
    }

    public final void run() {
        this.f$0.lambda$enableWindowMagnification$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
