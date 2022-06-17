package com.android.systemui.biometrics;

import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.VerifyCredentialResponse;

public final /* synthetic */ class AuthCredentialPasswordView$$ExternalSyntheticLambda1 implements LockPatternChecker.OnVerifyCallback {
    public final /* synthetic */ AuthCredentialPasswordView f$0;

    public /* synthetic */ AuthCredentialPasswordView$$ExternalSyntheticLambda1(AuthCredentialPasswordView authCredentialPasswordView) {
        this.f$0 = authCredentialPasswordView;
    }

    public final void onVerified(VerifyCredentialResponse verifyCredentialResponse, int i) {
        this.f$0.onCredentialVerified(verifyCredentialResponse, i);
    }
}
