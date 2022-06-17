package com.android.keyguard;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.EmergencyButtonController;
import com.android.keyguard.KeyguardInputView;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.util.ViewController;
import com.android.systemui.util.concurrency.DelayableExecutor;

public abstract class KeyguardInputViewController<T extends KeyguardInputView> extends ViewController<T> implements KeyguardSecurityView {
    private final EmergencyButton mEmergencyButton;
    private final EmergencyButtonController mEmergencyButtonController;
    private final KeyguardSecurityCallback mKeyguardSecurityCallback;
    private KeyguardSecurityCallback mNullCallback = new KeyguardSecurityCallback() {
        public void dismiss(boolean z, int i) {
        }

        public void dismiss(boolean z, int i, boolean z2) {
        }

        public void onUserInput() {
        }

        public void reportUnlockAttempt(int i, boolean z, int i2) {
        }

        public void reset() {
        }

        public void userActivity() {
        }
    };
    private boolean mPaused;
    private final KeyguardSecurityModel.SecurityMode mSecurityMode;
    protected UserManager mUserManager;
    protected UserSwitcherController mUserSwitcherController;

    /* access modifiers changed from: protected */
    public void onViewAttached() {
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
    }

    public void reset() {
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
    }

    public void showPromptReason(int i) {
    }

    protected KeyguardInputViewController(T t, KeyguardSecurityModel.SecurityMode securityMode, KeyguardSecurityCallback keyguardSecurityCallback, EmergencyButtonController emergencyButtonController) {
        super(t);
        EmergencyButton emergencyButton;
        this.mSecurityMode = securityMode;
        this.mKeyguardSecurityCallback = keyguardSecurityCallback;
        if (t == null) {
            emergencyButton = null;
        } else {
            emergencyButton = (EmergencyButton) t.findViewById(R$id.emergency_call_button);
        }
        this.mEmergencyButton = emergencyButton;
        this.mEmergencyButtonController = emergencyButtonController;
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        this.mEmergencyButtonController.init();
        if (MotoFeature.getInstance(((KeyguardInputView) this.mView).getContext()).isSupportPrivacySpace()) {
            this.mUserManager = (UserManager) ((KeyguardInputView) this.mView).getContext().getSystemService(UserManager.class);
            this.mUserSwitcherController = (UserSwitcherController) Dependency.get(UserSwitcherController.class);
        }
    }

    /* access modifiers changed from: package-private */
    public KeyguardSecurityModel.SecurityMode getSecurityMode() {
        return this.mSecurityMode;
    }

    /* access modifiers changed from: protected */
    public KeyguardSecurityCallback getKeyguardSecurityCallback() {
        if (this.mPaused) {
            return this.mNullCallback;
        }
        return this.mKeyguardSecurityCallback;
    }

    public void onPause() {
        this.mPaused = true;
    }

    public void onResume(int i) {
        this.mPaused = false;
    }

    public void reloadColors() {
        EmergencyButton emergencyButton = this.mEmergencyButton;
        if (emergencyButton != null) {
            emergencyButton.reloadColors();
        }
    }

    public void startAppearAnimation() {
        ((KeyguardInputView) this.mView).startAppearAnimation();
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        return ((KeyguardInputView) this.mView).startDisappearAnimation(runnable);
    }

    public int getIndexIn(KeyguardSecurityViewFlipper keyguardSecurityViewFlipper) {
        return keyguardSecurityViewFlipper.indexOfChild(this.mView);
    }

    /* access modifiers changed from: protected */
    public void pauseCallback(boolean z) {
        this.mPaused = z;
    }

    public static class Factory {
        private final EmergencyButtonController.Factory mEmergencyButtonControllerFactory;
        private final FalsingCollector mFalsingCollector;
        private final InputMethodManager mInputMethodManager;
        private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
        private final LatencyTracker mLatencyTracker;
        private final LiftToActivateListener mLiftToActivateListener;
        private final LockPatternUtils mLockPatternUtils;
        private final DelayableExecutor mMainExecutor;
        private final KeyguardMessageAreaController.Factory mMessageAreaControllerFactory;
        private final Resources mResources;
        private final TelephonyManager mTelephonyManager;

        public Factory(KeyguardUpdateMonitor keyguardUpdateMonitor, LockPatternUtils lockPatternUtils, LatencyTracker latencyTracker, KeyguardMessageAreaController.Factory factory, InputMethodManager inputMethodManager, DelayableExecutor delayableExecutor, Resources resources, LiftToActivateListener liftToActivateListener, TelephonyManager telephonyManager, FalsingCollector falsingCollector, EmergencyButtonController.Factory factory2) {
            this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
            this.mLockPatternUtils = lockPatternUtils;
            this.mLatencyTracker = latencyTracker;
            this.mMessageAreaControllerFactory = factory;
            this.mInputMethodManager = inputMethodManager;
            this.mMainExecutor = delayableExecutor;
            this.mResources = resources;
            this.mLiftToActivateListener = liftToActivateListener;
            this.mTelephonyManager = telephonyManager;
            this.mEmergencyButtonControllerFactory = factory2;
            this.mFalsingCollector = falsingCollector;
        }

        public KeyguardInputViewController create(KeyguardInputView keyguardInputView, KeyguardSecurityModel.SecurityMode securityMode, KeyguardSecurityCallback keyguardSecurityCallback) {
            KeyguardInputView keyguardInputView2 = keyguardInputView;
            EmergencyButtonController create = this.mEmergencyButtonControllerFactory.create((EmergencyButton) keyguardInputView2.findViewById(R$id.emergency_call_button));
            if (keyguardInputView2 instanceof KeyguardPatternView) {
                return new KeyguardPatternViewController((KeyguardPatternView) keyguardInputView2, this.mKeyguardUpdateMonitor, securityMode, this.mLockPatternUtils, keyguardSecurityCallback, this.mLatencyTracker, this.mFalsingCollector, create, this.mMessageAreaControllerFactory);
            } else if (keyguardInputView2 instanceof KeyguardPasswordView) {
                return new KeyguardPasswordViewController((KeyguardPasswordView) keyguardInputView2, this.mKeyguardUpdateMonitor, securityMode, this.mLockPatternUtils, keyguardSecurityCallback, this.mMessageAreaControllerFactory, this.mLatencyTracker, this.mInputMethodManager, create, this.mMainExecutor, this.mResources, this.mFalsingCollector);
            } else if (keyguardInputView2 instanceof KeyguardPINView) {
                return new KeyguardPinViewController((KeyguardPINView) keyguardInputView2, this.mKeyguardUpdateMonitor, securityMode, this.mLockPatternUtils, keyguardSecurityCallback, this.mMessageAreaControllerFactory, this.mLatencyTracker, this.mLiftToActivateListener, create, this.mFalsingCollector);
            } else if (keyguardInputView2 instanceof KeyguardSimPinView) {
                return new KeyguardSimPinViewController((KeyguardSimPinView) keyguardInputView2, this.mKeyguardUpdateMonitor, securityMode, this.mLockPatternUtils, keyguardSecurityCallback, this.mMessageAreaControllerFactory, this.mLatencyTracker, this.mLiftToActivateListener, this.mTelephonyManager, this.mFalsingCollector, create);
            } else if (keyguardInputView2 instanceof KeyguardSimPukView) {
                return new KeyguardSimPukViewController((KeyguardSimPukView) keyguardInputView2, this.mKeyguardUpdateMonitor, securityMode, this.mLockPatternUtils, keyguardSecurityCallback, this.mMessageAreaControllerFactory, this.mLatencyTracker, this.mLiftToActivateListener, this.mTelephonyManager, this.mFalsingCollector, create);
            } else if (keyguardInputView2 instanceof KeyguardPAKSView) {
                return new KeyguardPAKSViewController((KeyguardPAKSView) keyguardInputView2, securityMode, this.mLockPatternUtils, keyguardSecurityCallback, create);
            } else {
                throw new RuntimeException("Unable to find controller for " + keyguardInputView2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isPrivacySpaceNormal() {
        return Settings.System.getIntForUser(((KeyguardInputView) this.mView).getContext().getContentResolver(), "privacy_space_remove_user_status", 0, 0) == 0;
    }

    /* access modifiers changed from: protected */
    public boolean hasPrivacySpace() {
        UserManager userManager;
        if (!MotoFeature.getInstance(((KeyguardInputView) this.mView).getContext()).isSupportPrivacySpace() || (userManager = this.mUserManager) == null || userManager.getPrivacySpaceUserId() <= 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int wantToSwitchToUserId() {
        if (KeyguardUpdateMonitor.getCurrentUser() == 0) {
            return getPrivacySpaceUserId();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getPrivacySpaceUserId() {
        UserManager userManager = this.mUserManager;
        if (userManager != null) {
            return userManager.getPrivacySpaceUserId();
        }
        return -1;
    }
}
