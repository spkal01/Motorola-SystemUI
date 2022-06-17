package com.android.keyguard;

import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityModel;

public class KeyguardPAKSViewController extends KeyguardInputViewController<KeyguardPAKSView> {
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private KeyguardPAKSView mKeyguardPAKSView = null;

    public boolean needsInput() {
        return false;
    }

    protected KeyguardPAKSViewController(KeyguardPAKSView keyguardPAKSView, KeyguardSecurityModel.SecurityMode securityMode, LockPatternUtils lockPatternUtils, KeyguardSecurityCallback keyguardSecurityCallback, EmergencyButtonController emergencyButtonController) {
        super(keyguardPAKSView, securityMode, keyguardSecurityCallback, emergencyButtonController);
        this.mKeyguardPAKSView = keyguardPAKSView;
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        this.mKeyguardPAKSView.updateLockMessage();
    }
}
