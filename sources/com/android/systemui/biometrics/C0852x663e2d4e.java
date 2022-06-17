package com.android.systemui.biometrics;

/* renamed from: com.android.systemui.biometrics.AuthController$BiometricTaskStackListener$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C0852x663e2d4e implements Runnable {
    public final /* synthetic */ AuthController f$0;

    public /* synthetic */ C0852x663e2d4e(AuthController authController) {
        this.f$0 = authController;
    }

    public final void run() {
        this.f$0.handleTaskStackChanged();
    }
}
