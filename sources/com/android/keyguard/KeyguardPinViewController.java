package com.android.keyguard;

import android.util.Log;
import android.view.View;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.PasswordTextView;
import com.android.systemui.R$id;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.moto.MotoFeature;

public class KeyguardPinViewController extends KeyguardPinBasedInputViewController<KeyguardPINView> {
    private PasswordTextView.OnInputPinChangedListener mInputPinChangedListener = new KeyguardPinViewController$$ExternalSyntheticLambda1(this);
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private boolean mUsePrcSixPin;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mPasswordEntry.getText().length() == 6) {
            verifyPasswordAndUnlock();
        }
    }

    protected KeyguardPinViewController(KeyguardPINView keyguardPINView, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel.SecurityMode securityMode, LockPatternUtils lockPatternUtils, KeyguardSecurityCallback keyguardSecurityCallback, KeyguardMessageAreaController.Factory factory, LatencyTracker latencyTracker, LiftToActivateListener liftToActivateListener, EmergencyButtonController emergencyButtonController, FalsingCollector falsingCollector) {
        super(keyguardPINView, keyguardUpdateMonitor, securityMode, lockPatternUtils, keyguardSecurityCallback, factory, latencyTracker, liftToActivateListener, emergencyButtonController, falsingCollector);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        boolean isSixBitForPinLock = lockPatternUtils.isSixBitForPinLock(KeyguardUpdateMonitor.getCurrentUser());
        if (KeyguardConstants.DEBUG) {
            Log.i("KeyguardPinViewController", "User set six pin lock: " + isSixBitForPinLock);
        }
        if (MotoFeature.isPrcProduct() && isSixBitForPinLock) {
            this.mUsePrcSixPin = true;
            keyguardPINView.setUsePrcSixPin();
        }
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        View findViewById = ((KeyguardPINView) this.mView).findViewById(R$id.cancel_button);
        if (findViewById != null) {
            findViewById.setOnClickListener(new KeyguardPinViewController$$ExternalSyntheticLambda0(this));
        }
        if (this.mUsePrcSixPin) {
            this.mPasswordEntry.setOnInputPinChanged(this.mInputPinChangedListener);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$1(View view) {
        getKeyguardSecurityCallback().reset();
        getKeyguardSecurityCallback().onCancelClicked();
    }

    public void reloadColors() {
        super.reloadColors();
        ((KeyguardPINView) this.mView).reloadColors();
    }

    /* access modifiers changed from: package-private */
    public void resetState() {
        super.resetState();
        this.mMessageAreaController.setMessage((CharSequence) "");
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        return ((KeyguardPINView) this.mView).startDisappearAnimation(this.mKeyguardUpdateMonitor.needsSlowUnlockTransition(), runnable);
    }
}
