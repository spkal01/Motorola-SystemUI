package com.android.keyguard;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.systemui.Dependency;

public abstract class PresentationKgAbsKeyInputView extends PtKgSecurityView implements KeyguardSecurityView {
    protected static boolean DEBUG = KeyguardConstants.DEBUG;
    protected KeyguardSecurityCallback mCallback;
    private CountDownTimer mCountdownTimer;
    private boolean mDismissing;
    protected boolean mEnableHaptics;
    private KeyguardManager mKeyguardManager;
    protected KpMessageUpdateListener mKpMessageUpdateListener;
    protected LockPatternUtils mLockPatternUtils;
    protected AsyncTask<?, ?, ?> mPendingLockCheck;
    private KeyguardUpdateMonitor mUpdateMonitor;

    /* access modifiers changed from: protected */
    public abstract LockscreenCredential getEnteredCredential();

    /* access modifiers changed from: protected */
    public abstract void resetPasswordText(boolean z, boolean z2);

    /* access modifiers changed from: protected */
    public abstract void setPasswordEntryInputEnabled(boolean z);

    /* access modifiers changed from: protected */
    public abstract void updateBottomMessage(boolean z);

    /* access modifiers changed from: protected */
    public abstract void updateInputState();

    public PresentationKgAbsKeyInputView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PresentationKgAbsKeyInputView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCountdownTimer = null;
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService("keyguard");
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLockPatternUtils = this.mUpdateMonitor.getLockPatternUtils();
    }

    public void setKpMessageUpdateListener(KpMessageUpdateListener kpMessageUpdateListener) {
        this.mKpMessageUpdateListener = kpMessageUpdateListener;
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        if (DEBUG) {
            Log.d("PresentationKgAbsKeyInputView", "RDP: verifyPasswordAndUnlock()");
        }
        if (this.mDismissing) {
            if (DEBUG) {
                Log.i("PresentationKgAbsKeyInputView", "RDP: already verified. Return.");
            }
        } else if (this.mPtKDMCallback.getFailedCount() >= 5) {
            Log.i("PresentationKgAbsKeyInputView", "RDP: Enter wrong password reached 5 times.");
        } else {
            final LockscreenCredential enteredCredential = getEnteredCredential();
            setPasswordEntryInputEnabled(false);
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
            }
            final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (enteredCredential.size() <= 3) {
                setPasswordEntryInputEnabled(true);
                onPasswordChecked(currentUser, false, 0, false);
                enteredCredential.zeroize();
                return;
            }
            this.mPendingLockCheck = LockPatternChecker.checkCredential(this.mLockPatternUtils, enteredCredential, currentUser, new LockPatternChecker.OnCheckCallback() {
                public void onEarlyMatched() {
                    PresentationKgAbsKeyInputView.this.onPasswordChecked(currentUser, true, 0, true);
                    enteredCredential.zeroize();
                }

                public void onChecked(boolean z, int i) {
                    PresentationKgAbsKeyInputView.this.setPasswordEntryInputEnabled(true);
                    PresentationKgAbsKeyInputView presentationKgAbsKeyInputView = PresentationKgAbsKeyInputView.this;
                    presentationKgAbsKeyInputView.mPendingLockCheck = null;
                    if (!z) {
                        presentationKgAbsKeyInputView.onPasswordChecked(currentUser, false, i, true);
                        PresentationKgAbsKeyInputView.this.updateInputState();
                    }
                    enteredCredential.zeroize();
                }

                public void onCancelled() {
                    enteredCredential.zeroize();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onPasswordChecked(int i, boolean z, int i2, boolean z2) {
        this.mUpdateMonitor.reportBackupUnlock(z);
        boolean z3 = KeyguardUpdateMonitor.getCurrentUser() == i;
        if (z) {
            this.mPtKDMCallback.setFailedCount(0);
            this.mPtKDMCallback.setOnceLockout(false);
            if (z3) {
                this.mDismissing = true;
                this.mUpdateMonitor.handleExternalAuthenticated();
            }
        } else {
            if (z2) {
                PtKDMCallback ptKDMCallback = this.mPtKDMCallback;
                ptKDMCallback.setFailedCount(ptKDMCallback.getFailedCount() + 1);
                if (i2 > 0) {
                    this.mPtKDMCallback.setOnceLockout(true);
                    this.mLockPatternUtils.setLockoutAttemptDeadline(i, i2);
                }
            }
            updateBottomMessage(true);
        }
        resetPasswordText(true, !z);
    }

    /* access modifiers changed from: protected */
    public void onUserInput() {
        KeyguardSecurityCallback keyguardSecurityCallback = this.mCallback;
        if (keyguardSecurityCallback != null) {
            keyguardSecurityCallback.userActivity();
        }
        updateBottomMessage(false);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 0) {
            return false;
        }
        onUserInput();
        return false;
    }

    public void doHapticKeyClick() {
        if (this.mEnableHaptics) {
            performHapticFeedback(1, 3);
        }
    }
}
