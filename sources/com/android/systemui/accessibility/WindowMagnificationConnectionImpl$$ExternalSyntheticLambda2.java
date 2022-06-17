package com.android.systemui.accessibility;

public final /* synthetic */ class WindowMagnificationConnectionImpl$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ WindowMagnificationConnectionImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;

    public /* synthetic */ WindowMagnificationConnectionImpl$$ExternalSyntheticLambda2(WindowMagnificationConnectionImpl windowMagnificationConnectionImpl, int i, float f, float f2) {
        this.f$0 = windowMagnificationConnectionImpl;
        this.f$1 = i;
        this.f$2 = f;
        this.f$3 = f2;
    }

    public final void run() {
        this.f$0.lambda$moveWindowMagnifier$3(this.f$1, this.f$2, this.f$3);
    }
}
