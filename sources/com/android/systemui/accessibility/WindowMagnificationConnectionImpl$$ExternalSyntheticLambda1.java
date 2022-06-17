package com.android.systemui.accessibility;

public final /* synthetic */ class WindowMagnificationConnectionImpl$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ WindowMagnificationConnectionImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ WindowMagnificationConnectionImpl$$ExternalSyntheticLambda1(WindowMagnificationConnectionImpl windowMagnificationConnectionImpl, int i, float f) {
        this.f$0 = windowMagnificationConnectionImpl;
        this.f$1 = i;
        this.f$2 = f;
    }

    public final void run() {
        this.f$0.lambda$setScale$1(this.f$1, this.f$2);
    }
}
