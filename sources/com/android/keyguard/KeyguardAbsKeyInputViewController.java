package com.android.keyguard;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.keyguard.EmergencyButtonController;
import com.android.keyguard.KeyguardAbsKeyInputView;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.systemui.R$plurals;
import com.android.systemui.classifier.FalsingClassifier;
import com.android.systemui.classifier.FalsingCollector;

public abstract class KeyguardAbsKeyInputViewController<T extends KeyguardAbsKeyInputView> extends KeyguardInputViewController<T> {
    private CountDownTimer mCountdownTimer;
    private boolean mDismissing;
    private final EmergencyButtonController.EmergencyButtonCallback mEmergencyButtonCallback = new EmergencyButtonController.EmergencyButtonCallback() {
        public void onEmergencyButtonClickedWhenInCall() {
            KeyguardAbsKeyInputViewController.this.getKeyguardSecurityCallback().reset();
        }
    };
    private final EmergencyButtonController mEmergencyButtonController;
    private final FalsingCollector mFalsingCollector;
    private final KeyguardAbsKeyInputView.KeyDownListener mKeyDownListener = new KeyguardAbsKeyInputViewController$$ExternalSyntheticLambda0(this);
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public final LatencyTracker mLatencyTracker;
    private final LockPatternUtils mLockPatternUtils;
    protected KeyguardMessageAreaController mMessageAreaController;
    protected AsyncTask<?, ?, ?> mPendingLockCheck;
    protected boolean mResumed;
    private boolean mRetryIfNeeded;

    public boolean needsInput() {
        return false;
    }

    /* access modifiers changed from: package-private */
    public abstract void resetState();

    /* access modifiers changed from: protected */
    public boolean shouldLockout(long j) {
        return j != 0;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(int i, KeyEvent keyEvent) {
        if (i == 0) {
            return false;
        }
        onUserInput();
        return false;
    }

    protected KeyguardAbsKeyInputViewController(T t, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel.SecurityMode securityMode, LockPatternUtils lockPatternUtils, KeyguardSecurityCallback keyguardSecurityCallback, KeyguardMessageAreaController.Factory factory, LatencyTracker latencyTracker, FalsingCollector falsingCollector, EmergencyButtonController emergencyButtonController) {
        super(t, securityMode, keyguardSecurityCallback, emergencyButtonController);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mLockPatternUtils = lockPatternUtils;
        this.mLatencyTracker = latencyTracker;
        this.mFalsingCollector = falsingCollector;
        this.mEmergencyButtonController = emergencyButtonController;
        this.mMessageAreaController = factory.create(KeyguardMessageArea.findSecurityMessageDisplay(this.mView));
    }

    public void onInit() {
        super.onInit();
        this.mMessageAreaController.init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        ((KeyguardAbsKeyInputView) this.mView).setKeyDownListener(this.mKeyDownListener);
        ((KeyguardAbsKeyInputView) this.mView).setEnableHaptics(this.mLockPatternUtils.isTactileFeedbackEnabled());
        this.mEmergencyButtonController.setEmergencyButtonCallback(this.mEmergencyButtonCallback);
    }

    public void reset() {
        this.mDismissing = false;
        ((KeyguardAbsKeyInputView) this.mView).resetPasswordText(false, false);
        long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
        if (shouldLockout(lockoutAttemptDeadline)) {
            handleAttemptLockout(lockoutAttemptDeadline);
        } else {
            resetState();
        }
    }

    public void reloadColors() {
        super.reloadColors();
        this.mMessageAreaController.reloadColors();
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
        if (colorStateList != null) {
            this.mMessageAreaController.setNextMessageColor(colorStateList);
        }
        this.mMessageAreaController.setMessage(charSequence);
    }

    /* access modifiers changed from: protected */
    public void handleAttemptLockout(long j) {
        ((KeyguardAbsKeyInputView) this.mView).setPasswordEntryEnabled(false);
        this.mCountdownTimer = new CountDownTimer(((long) Math.ceil(((double) (j - SystemClock.elapsedRealtime())) / 1000.0d)) * 1000, 1000) {
            public void onTick(long j) {
                int round = (int) Math.round(((double) j) / 1000.0d);
                KeyguardAbsKeyInputViewController keyguardAbsKeyInputViewController = KeyguardAbsKeyInputViewController.this;
                keyguardAbsKeyInputViewController.mMessageAreaController.setMessage((CharSequence) ((KeyguardAbsKeyInputView) keyguardAbsKeyInputViewController.mView).getResources().getQuantityString(R$plurals.kg_too_many_failed_attempts_countdown, round, new Object[]{Integer.valueOf(round)}));
            }

            public void onFinish() {
                KeyguardAbsKeyInputViewController.this.mMessageAreaController.setMessage((CharSequence) "");
                KeyguardAbsKeyInputViewController.this.resetState();
            }
        }.start();
    }

    /* access modifiers changed from: package-private */
    public void onPasswordChecked(int i, boolean z, int i2, boolean z2) {
        this.mKeyguardUpdateMonitor.reportBackupUnlock(z);
        boolean z3 = KeyguardUpdateMonitor.getCurrentUser() == i;
        if (z) {
            getKeyguardSecurityCallback().reportUnlockAttempt(i, true, 0);
            if (hasPrivacySpace() && KeyguardUpdateMonitor.getCurrentUser() != i) {
                this.mUserSwitcherController.switchToUserId(i);
                z3 = true;
            }
            if (z3) {
                this.mDismissing = true;
                this.mLatencyTracker.onActionStart(11);
                getKeyguardSecurityCallback().dismiss(true, i);
            }
            if (hasPrivacySpace()) {
                this.mRetryIfNeeded = false;
                return;
            }
            return;
        }
        if (z2 && i == KeyguardUpdateMonitor.getCurrentUser()) {
            getKeyguardSecurityCallback().reportUnlockAttempt(i, false, i2);
            if (i2 > 0) {
                handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(i, i2));
            }
        }
        if (i2 == 0) {
            if (!hasPrivacySpace() || !this.mRetryIfNeeded) {
                this.mMessageAreaController.setMessage(((KeyguardAbsKeyInputView) this.mView).getWrongPasswordStringId());
            } else {
                this.mRetryIfNeeded = false;
                verifyPasswordAndUnlock(true);
                return;
            }
        }
        ((KeyguardAbsKeyInputView) this.mView).resetPasswordText(true, false);
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        boolean hasPrivacySpace = hasPrivacySpace();
        boolean isEncrypted = this.mKeyguardUpdateMonitor.isEncrypted(0);
        boolean isPrivacySpaceNormal = isPrivacySpaceNormal();
        if (KeyguardConstants.DEBUG) {
            Log.d("KeyguardAbsKeyInputViewController", "hasPrivacySpace: " + hasPrivacySpace + "  isEncrypted: " + isEncrypted + "  isPrivacySpaceNormal: " + isPrivacySpaceNormal + "  currentUserId: " + currentUser);
        }
        if (hasPrivacySpace && !isEncrypted && isPrivacySpaceNormal && (currentUser == 0 || currentUser == getPrivacySpaceUserId())) {
            this.mRetryIfNeeded = true;
        }
        verifyPasswordAndUnlock(false);
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock(boolean z) {
        final int i;
        boolean z2 = KeyguardConstants.DEBUG;
        if (z2) {
            Log.d("KeyguardAbsKeyInputViewController", "verifyPasswordAndUnlock: isRetry = " + z);
        }
        if (!this.mDismissing) {
            final LockscreenCredential enteredCredential = ((KeyguardAbsKeyInputView) this.mView).getEnteredCredential();
            ((KeyguardAbsKeyInputView) this.mView).setPasswordEntryInputEnabled(false);
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
            }
            if (!hasPrivacySpace() || !z) {
                i = KeyguardUpdateMonitor.getCurrentUser();
            } else {
                i = wantToSwitchToUserId();
                if (i < 0) {
                    Log.w("KeyguardAbsKeyInputViewController", "Want switch to userId:" + i + ", it's abnormal.");
                }
            }
            if (z2) {
                Log.d("KeyguardAbsKeyInputViewController", "verify Password for userId = " + i);
            }
            if (enteredCredential.size() <= 3) {
                ((KeyguardAbsKeyInputView) this.mView).setPasswordEntryInputEnabled(true);
                onPasswordChecked(i, false, 0, false);
                enteredCredential.zeroize();
                return;
            }
            this.mLatencyTracker.onActionStart(3);
            this.mLatencyTracker.onActionStart(4);
            this.mKeyguardUpdateMonitor.setCredentialAttempted();
            this.mPendingLockCheck = LockPatternChecker.checkCredential(this.mLockPatternUtils, enteredCredential, i, new LockPatternChecker.OnCheckCallback() {
                public void onEarlyMatched() {
                    KeyguardAbsKeyInputViewController.this.mLatencyTracker.onActionEnd(3);
                    KeyguardAbsKeyInputViewController.this.onPasswordChecked(i, true, 0, true);
                    enteredCredential.zeroize();
                }

                public void onChecked(boolean z, int i) {
                    KeyguardAbsKeyInputViewController.this.mLatencyTracker.onActionEnd(4);
                    ((KeyguardAbsKeyInputView) KeyguardAbsKeyInputViewController.this.mView).setPasswordEntryInputEnabled(true);
                    KeyguardAbsKeyInputViewController keyguardAbsKeyInputViewController = KeyguardAbsKeyInputViewController.this;
                    keyguardAbsKeyInputViewController.mPendingLockCheck = null;
                    if (!z) {
                        keyguardAbsKeyInputViewController.onPasswordChecked(i, false, i, true);
                    }
                    enteredCredential.zeroize();
                }

                public void onCancelled() {
                    KeyguardAbsKeyInputViewController.this.mLatencyTracker.onActionEnd(4);
                    enteredCredential.zeroize();
                }
            });
        }
    }

    public void showPromptReason(int i) {
        int promptReasonStringRes;
        if (i != 0 && (promptReasonStringRes = ((KeyguardAbsKeyInputView) this.mView).getPromptReasonStringRes(i)) != 0) {
            this.mMessageAreaController.setMessage(promptReasonStringRes);
        }
    }

    /* access modifiers changed from: protected */
    public void onUserInput() {
        this.mFalsingCollector.updateFalseConfidence(FalsingClassifier.Result.passed(0.6d));
        getKeyguardSecurityCallback().userActivity();
        getKeyguardSecurityCallback().onUserInput();
        this.mMessageAreaController.setMessage((CharSequence) "");
    }

    public void onResume(int i) {
        this.mResumed = true;
    }

    public void onPause() {
        this.mResumed = false;
        CountDownTimer countDownTimer = this.mCountdownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.mCountdownTimer = null;
        }
        AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
        if (asyncTask != null) {
            asyncTask.cancel(false);
            this.mPendingLockCheck = null;
        }
        reset();
    }

    public void startAppearAnimation() {
        this.mMessageAreaController.restoreMessageArea();
        super.startAppearAnimation();
    }
}
