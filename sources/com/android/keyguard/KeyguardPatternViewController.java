package com.android.keyguard;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.keyguard.EmergencyButtonController;
import com.android.keyguard.KeyguardMessageAreaController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.settingslib.Utils;
import com.android.systemui.R$id;
import com.android.systemui.R$plurals;
import com.android.systemui.R$string;
import com.android.systemui.classifier.FalsingClassifier;
import com.android.systemui.classifier.FalsingCollector;
import java.util.List;

public class KeyguardPatternViewController extends KeyguardInputViewController<KeyguardPatternView> {
    /* access modifiers changed from: private */
    public Runnable mCancelPatternRunnable = new Runnable() {
        public void run() {
            KeyguardPatternViewController.this.mLockPatternView.clearPattern();
        }
    };
    private CountDownTimer mCountdownTimer;
    private EmergencyButtonController.EmergencyButtonCallback mEmergencyButtonCallback = new EmergencyButtonController.EmergencyButtonCallback() {
        public void onEmergencyButtonClickedWhenInCall() {
            KeyguardPatternViewController.this.getKeyguardSecurityCallback().reset();
        }
    };
    private final EmergencyButtonController mEmergencyButtonController;
    private final FalsingCollector mFalsingCollector;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public final LatencyTracker mLatencyTracker;
    private final LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public LockPatternView mLockPatternView;
    /* access modifiers changed from: private */
    public KeyguardMessageAreaController mMessageAreaController;
    private final KeyguardMessageAreaController.Factory mMessageAreaControllerFactory;
    /* access modifiers changed from: private */
    public List<LockPatternView.Cell> mPattern;
    /* access modifiers changed from: private */
    public AsyncTask<?, ?, ?> mPendingLockCheck;
    /* access modifiers changed from: private */
    public boolean mRetryIfNeeded;

    public boolean needsInput() {
        return false;
    }

    private class UnlockPatternListener implements LockPatternView.OnPatternListener {
        public void onPatternCleared() {
        }

        private UnlockPatternListener() {
        }

        public void onPatternStart() {
            KeyguardPatternViewController.this.mLockPatternView.removeCallbacks(KeyguardPatternViewController.this.mCancelPatternRunnable);
            KeyguardPatternViewController.this.mMessageAreaController.setMessage((CharSequence) "");
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            KeyguardPatternViewController.this.getKeyguardSecurityCallback().userActivity();
            KeyguardPatternViewController.this.getKeyguardSecurityCallback().onUserInput();
        }

        public void onPatternDetected(List<LockPatternView.Cell> list) {
            List unused = KeyguardPatternViewController.this.mPattern = list;
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            boolean hasPrivacySpace = KeyguardPatternViewController.this.hasPrivacySpace();
            boolean isEncrypted = KeyguardPatternViewController.this.mKeyguardUpdateMonitor.isEncrypted(0);
            boolean isPrivacySpaceNormal = KeyguardPatternViewController.this.isPrivacySpaceNormal();
            if (KeyguardConstants.DEBUG) {
                Log.d("KeyguardPatternViewController", "hasPrivacySpace: " + hasPrivacySpace + "  isEncrypted: " + isEncrypted + "  isPrivacySpaceNormal: " + isPrivacySpaceNormal + "  currentUserId: " + currentUser);
            }
            if (hasPrivacySpace && !isEncrypted && isPrivacySpaceNormal && (currentUser == 0 || currentUser == KeyguardPatternViewController.this.getPrivacySpaceUserId())) {
                boolean unused2 = KeyguardPatternViewController.this.mRetryIfNeeded = true;
            }
            KeyguardPatternViewController.this.verifyPatternAndUnlock(false);
        }
    }

    protected KeyguardPatternViewController(KeyguardPatternView keyguardPatternView, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardSecurityModel.SecurityMode securityMode, LockPatternUtils lockPatternUtils, KeyguardSecurityCallback keyguardSecurityCallback, LatencyTracker latencyTracker, FalsingCollector falsingCollector, EmergencyButtonController emergencyButtonController, KeyguardMessageAreaController.Factory factory) {
        super(keyguardPatternView, securityMode, keyguardSecurityCallback, emergencyButtonController);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mLockPatternUtils = lockPatternUtils;
        this.mLatencyTracker = latencyTracker;
        this.mFalsingCollector = falsingCollector;
        this.mEmergencyButtonController = emergencyButtonController;
        this.mMessageAreaControllerFactory = factory;
        this.mMessageAreaController = factory.create(KeyguardMessageArea.findSecurityMessageDisplay(this.mView));
        this.mLockPatternView = ((KeyguardPatternView) this.mView).findViewById(R$id.lockPatternView);
    }

    public void onInit() {
        super.onInit();
        this.mMessageAreaController.init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        super.onViewAttached();
        this.mLockPatternView.setOnPatternListener(new UnlockPatternListener());
        this.mLockPatternView.setSaveEnabled(false);
        this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()));
        this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
        this.mLockPatternView.setOnTouchListener(new KeyguardPatternViewController$$ExternalSyntheticLambda1(this));
        this.mEmergencyButtonController.setEmergencyButtonCallback(this.mEmergencyButtonCallback);
        View findViewById = ((KeyguardPatternView) this.mView).findViewById(R$id.cancel_button);
        if (findViewById != null) {
            findViewById.setOnClickListener(new KeyguardPatternViewController$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onViewAttached$0(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0) {
            return false;
        }
        this.mFalsingCollector.avoidGesture();
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewAttached$1(View view) {
        getKeyguardSecurityCallback().reset();
        getKeyguardSecurityCallback().onCancelClicked();
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        super.onViewDetached();
        this.mLockPatternView.setOnPatternListener((LockPatternView.OnPatternListener) null);
        this.mLockPatternView.setOnTouchListener((View.OnTouchListener) null);
        this.mEmergencyButtonController.setEmergencyButtonCallback((EmergencyButtonController.EmergencyButtonCallback) null);
        View findViewById = ((KeyguardPatternView) this.mView).findViewById(R$id.cancel_button);
        if (findViewById != null) {
            findViewById.setOnClickListener((View.OnClickListener) null);
        }
    }

    public void reset() {
        this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()));
        this.mLockPatternView.enableInput();
        this.mLockPatternView.setEnabled(true);
        this.mLockPatternView.clearPattern();
        long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
        if (lockoutAttemptDeadline != 0) {
            handleAttemptLockout(lockoutAttemptDeadline);
        } else {
            displayDefaultSecurityMessage();
        }
    }

    public void reloadColors() {
        super.reloadColors();
        this.mMessageAreaController.reloadColors();
        int defaultColor = Utils.getColorAttr(this.mLockPatternView.getContext(), 16842806).getDefaultColor();
        this.mLockPatternView.setColors(defaultColor, defaultColor, Utils.getColorError(this.mLockPatternView.getContext()).getDefaultColor());
    }

    public void onPause() {
        super.onPause();
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
        displayDefaultSecurityMessage();
        reset();
    }

    public void showPromptReason(int i) {
        if (i == 0) {
            return;
        }
        if (i == 1) {
            this.mMessageAreaController.setMessage(R$string.kg_prompt_reason_restart_pattern);
        } else if (i == 2) {
            this.mMessageAreaController.setMessage(R$string.kg_prompt_reason_timeout_pattern);
        } else if (i == 3) {
            this.mMessageAreaController.setMessage(R$string.kg_prompt_reason_device_admin);
        } else if (i == 4) {
            this.mMessageAreaController.setMessage(R$string.kg_prompt_reason_user_request);
        } else if (i != 6) {
            this.mMessageAreaController.setMessage(R$string.kg_prompt_reason_timeout_pattern);
        } else {
            this.mMessageAreaController.setMessage(R$string.kg_prompt_reason_timeout_pattern);
        }
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
        if (colorStateList != null) {
            this.mMessageAreaController.setNextMessageColor(colorStateList);
        }
        this.mMessageAreaController.setMessage(charSequence);
    }

    public void startAppearAnimation() {
        super.startAppearAnimation();
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        return ((KeyguardPatternView) this.mView).startDisappearAnimation(this.mKeyguardUpdateMonitor.needsSlowUnlockTransition(), runnable);
    }

    /* access modifiers changed from: private */
    public void displayDefaultSecurityMessage() {
        this.mMessageAreaController.setMessage((CharSequence) "");
    }

    private void handleAttemptLockout(long j) {
        this.mLockPatternView.clearPattern();
        this.mLockPatternView.setEnabled(false);
        this.mCountdownTimer = new CountDownTimer(((long) Math.ceil(((double) (j - SystemClock.elapsedRealtime())) / 1000.0d)) * 1000, 1000) {
            public void onTick(long j) {
                int round = (int) Math.round(((double) j) / 1000.0d);
                KeyguardPatternViewController.this.mMessageAreaController.setMessage((CharSequence) ((KeyguardPatternView) KeyguardPatternViewController.this.mView).getResources().getQuantityString(R$plurals.kg_too_many_failed_attempts_countdown, round, new Object[]{Integer.valueOf(round)}));
            }

            public void onFinish() {
                KeyguardPatternViewController.this.mLockPatternView.setEnabled(true);
                KeyguardPatternViewController.this.displayDefaultSecurityMessage();
            }
        }.start();
    }

    /* access modifiers changed from: private */
    public void verifyPatternAndUnlock(boolean z) {
        final int i;
        boolean z2 = KeyguardConstants.DEBUG;
        if (z2) {
            Log.d("KeyguardPatternViewController", "verifyPasswordAndUnlock: isRetry = " + z);
        }
        this.mKeyguardUpdateMonitor.setCredentialAttempted();
        this.mLockPatternView.disableInput();
        AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
        if (asyncTask != null) {
            asyncTask.cancel(false);
        }
        if (!hasPrivacySpace() || !z) {
            i = KeyguardUpdateMonitor.getCurrentUser();
        } else {
            i = wantToSwitchToUserId();
            if (i < 0) {
                Log.w("KeyguardPatternViewController", "Want switch to userId:" + i + ", it's abnormal.");
                return;
            }
        }
        if (z2) {
            Log.d("KeyguardPatternViewController", "verify Pattern for userId = " + i);
        }
        if (this.mPattern.size() < 4) {
            if (this.mPattern.size() == 1) {
                this.mFalsingCollector.updateFalseConfidence(FalsingClassifier.Result.falsed(0.7d, KeyguardPatternViewController.class.getSimpleName(), "empty pattern input"));
            }
            this.mLockPatternView.enableInput();
            onPatternChecked(i, false, 0, false);
            return;
        }
        this.mLatencyTracker.onActionStart(3);
        this.mLatencyTracker.onActionStart(4);
        this.mPendingLockCheck = LockPatternChecker.checkCredential(this.mLockPatternUtils, LockscreenCredential.createPattern(this.mPattern), i, new LockPatternChecker.OnCheckCallback() {
            public void onEarlyMatched() {
                KeyguardPatternViewController.this.mLatencyTracker.onActionEnd(3);
                KeyguardPatternViewController.this.onPatternChecked(i, true, 0, true);
            }

            public void onChecked(boolean z, int i) {
                KeyguardPatternViewController.this.mLatencyTracker.onActionEnd(4);
                KeyguardPatternViewController.this.mLockPatternView.enableInput();
                AsyncTask unused = KeyguardPatternViewController.this.mPendingLockCheck = null;
                if (!z) {
                    KeyguardPatternViewController.this.onPatternChecked(i, false, i, true);
                }
            }

            public void onCancelled() {
                KeyguardPatternViewController.this.mLatencyTracker.onActionEnd(4);
            }
        });
        if (this.mPattern.size() > 2) {
            getKeyguardSecurityCallback().userActivity();
            getKeyguardSecurityCallback().onUserInput();
        }
    }

    /* access modifiers changed from: private */
    public void onPatternChecked(int i, boolean z, int i2, boolean z2) {
        this.mKeyguardUpdateMonitor.reportBackupUnlock(z);
        boolean z3 = KeyguardUpdateMonitor.getCurrentUser() == i;
        if (z) {
            getKeyguardSecurityCallback().reportUnlockAttempt(i, true, 0);
            if (hasPrivacySpace() && KeyguardUpdateMonitor.getCurrentUser() != i) {
                this.mUserSwitcherController.switchToUserId(i);
                z3 = true;
            }
            if (z3) {
                this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                this.mLatencyTracker.onActionStart(11);
                getKeyguardSecurityCallback().dismiss(true, i);
            }
            if (hasPrivacySpace()) {
                this.mRetryIfNeeded = false;
                return;
            }
            return;
        }
        if (!this.mRetryIfNeeded) {
            this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        }
        if (z2) {
            getKeyguardSecurityCallback().reportUnlockAttempt(i, false, i2);
            if (i2 > 0) {
                handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(i, i2));
            }
        }
        if (i2 != 0) {
            return;
        }
        if (!hasPrivacySpace() || !this.mRetryIfNeeded) {
            this.mMessageAreaController.setMessage(R$string.kg_wrong_pattern);
            this.mLockPatternView.postDelayed(this.mCancelPatternRunnable, 2000);
            return;
        }
        this.mRetryIfNeeded = false;
        verifyPatternAndUnlock(true);
    }
}
