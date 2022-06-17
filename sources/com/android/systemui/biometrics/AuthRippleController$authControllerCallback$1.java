package com.android.systemui.biometrics;

import com.android.systemui.biometrics.AuthController;

/* compiled from: AuthRippleController.kt */
final class AuthRippleController$authControllerCallback$1 implements AuthController.Callback {
    final /* synthetic */ AuthRippleController this$0;

    AuthRippleController$authControllerCallback$1(AuthRippleController authRippleController) {
        this.this$0 = authRippleController;
    }

    public final void onAllAuthenticatorsRegistered() {
        this.this$0.updateSensorLocation();
    }
}
