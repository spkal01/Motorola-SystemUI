package com.motorola.systemui.cli.navgesture.animation;

public final /* synthetic */ class MultiStateCallback$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MultiStateCallback f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MultiStateCallback$$ExternalSyntheticLambda0(MultiStateCallback multiStateCallback, int i) {
        this.f$0 = multiStateCallback;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$setStateOnUiThread$0(this.f$1);
    }
}
