package com.android.systemui.accessibility;

import java.util.function.Consumer;

public final /* synthetic */ class WindowMagnification$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ WindowMagnification$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((WindowMagnificationAnimationController) obj).onConfigurationChanged(this.f$0);
    }
}
