package com.motorola.systemui.cli.navgesture.notifier;

public final /* synthetic */ class SystemUIGestureNotifier$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SystemUIGestureNotifier f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SystemUIGestureNotifier$$ExternalSyntheticLambda0(SystemUIGestureNotifier systemUIGestureNotifier, int i) {
        this.f$0 = systemUIGestureNotifier;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$notifyGestureEndTargetChanged$0(this.f$1);
    }
}
