package com.android.systemui.biometrics;

/* compiled from: AuthRippleController.kt */
final class AuthRippleController$showRipple$1 implements Runnable {
    final /* synthetic */ AuthRippleController this$0;

    AuthRippleController$showRipple$1(AuthRippleController authRippleController) {
        this.this$0 = authRippleController;
    }

    public final void run() {
        this.this$0.notificationShadeWindowController.setForcePluginOpen(false, this.this$0);
    }
}
