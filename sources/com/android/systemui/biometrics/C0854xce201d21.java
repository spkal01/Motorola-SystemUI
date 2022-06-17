package com.android.systemui.biometrics;

import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.systemui.biometrics.AuthCredentialPatternView;

/* renamed from: com.android.systemui.biometrics.AuthCredentialPatternView$UnlockPatternListener$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C0854xce201d21 implements LockPatternChecker.OnVerifyCallback {
    public final /* synthetic */ AuthCredentialPatternView.UnlockPatternListener f$0;

    public /* synthetic */ C0854xce201d21(AuthCredentialPatternView.UnlockPatternListener unlockPatternListener) {
        this.f$0 = unlockPatternListener;
    }

    public final void onVerified(VerifyCredentialResponse verifyCredentialResponse, int i) {
        this.f$0.onPatternVerified(verifyCredentialResponse, i);
    }
}
