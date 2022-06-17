package com.android.systemui.accessibility;

public final /* synthetic */ class MagnificationModeSwitch$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ MagnificationModeSwitch f$0;

    public /* synthetic */ MagnificationModeSwitch$$ExternalSyntheticLambda7(MagnificationModeSwitch magnificationModeSwitch) {
        this.f$0 = magnificationModeSwitch;
    }

    public final void run() {
        this.f$0.onWindowInsetChanged();
    }
}
