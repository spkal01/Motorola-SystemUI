package com.android.keyguard;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import java.util.List;

public class PresentationKgPatternView extends PtKgSecurityView {
    protected static boolean DEBUG = KeyguardConstants.DEBUG;
    /* access modifiers changed from: private */
    public Runnable mCancelPatternRunnable;
    private KeyguardManager mKeyguardManager;
    private KpMessageUpdateListener mKpMessageUpdateListener;
    /* access modifiers changed from: private */
    public LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public LockPatternView mLockPatternView;
    /* access modifiers changed from: private */
    public AsyncTask<?, ?, ?> mPendingLockCheck;
    /* access modifiers changed from: private */
    public KeyguardUpdateMonitor mUpdateMonitor;

    public PresentationKgPatternView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PresentationKgPatternView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -1);
    }

    public PresentationKgPatternView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCancelPatternRunnable = new Runnable() {
            public void run() {
                PresentationKgPatternView.this.mLockPatternView.clearPattern();
            }
        };
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService("keyguard");
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
        if (lockPatternUtils == null) {
            lockPatternUtils = new LockPatternUtils(this.mContext);
        }
        this.mLockPatternUtils = lockPatternUtils;
        LockPatternView findViewById = findViewById(R$id.lockPatternView);
        this.mLockPatternView = findViewById;
        findViewById.setOnPatternListener(new UnlockPatternListener());
        this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()));
        this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateInputState();
        updateBottomMessage(false);
        this.mLockPatternUtils = this.mUpdateMonitor.getLockPatternUtils();
    }

    /* access modifiers changed from: private */
    public void updateInputState() {
        PtKDMCallback ptKDMCallback = this.mPtKDMCallback;
        if (ptKDMCallback == null) {
            Log.d("PKgPatternView", "RDP: updateInputState()  mPtKDMCallback is null.");
        } else if (ptKDMCallback.getFailedCount() >= 5 || this.mPtKDMCallback.onceLockout()) {
            this.mLockPatternView.disableInput();
        }
    }

    /* access modifiers changed from: private */
    public void updateBottomMessage(boolean z) {
        int i;
        PtKDMCallback ptKDMCallback = this.mPtKDMCallback;
        if (ptKDMCallback == null) {
            Log.d("PKgPatternView", "RDP: updateBottomMessage()  mPtKDMCallback is null.");
            return;
        }
        if (ptKDMCallback.getFailedCount() >= 5 || this.mPtKDMCallback.onceLockout()) {
            i = R$string.zz_moto_rdp_pattern_max_wrong_tip;
        } else if (z) {
            i = R$string.kg_wrong_pattern;
        } else {
            i = R$string.zz_moto_rdp_pattern_tip;
        }
        this.mKpMessageUpdateListener.update(getResources().getString(i));
    }

    public void setKpMessageUpdateListener(KpMessageUpdateListener kpMessageUpdateListener) {
        this.mKpMessageUpdateListener = kpMessageUpdateListener;
    }

    private class UnlockPatternListener implements LockPatternView.OnPatternListener {
        public void onPatternCellAdded(List<LockPatternView.Cell> list) {
        }

        public void onPatternCleared() {
        }

        private UnlockPatternListener() {
        }

        public void onPatternStart() {
            PresentationKgPatternView.this.mLockPatternView.removeCallbacks(PresentationKgPatternView.this.mCancelPatternRunnable);
            PresentationKgPatternView.this.updateBottomMessage(false);
        }

        public void onPatternDetected(List<LockPatternView.Cell> list) {
            if (PresentationKgPatternView.DEBUG) {
                Log.d("PKgPatternView", "RDP: onPatternDetected");
            }
            PresentationKgPatternView.this.mLockPatternView.disableInput();
            if (PresentationKgPatternView.this.mPendingLockCheck != null) {
                PresentationKgPatternView.this.mPendingLockCheck.cancel(false);
            }
            final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (list.size() < 4) {
                PresentationKgPatternView.this.mLockPatternView.enableInput();
                onPatternChecked(currentUser, false, 0, false);
                return;
            }
            PresentationKgPatternView presentationKgPatternView = PresentationKgPatternView.this;
            AsyncTask unused = presentationKgPatternView.mPendingLockCheck = LockPatternChecker.checkCredential(presentationKgPatternView.mLockPatternUtils, LockscreenCredential.createPattern(list), currentUser, new LockPatternChecker.OnCheckCallback() {
                public void onCancelled() {
                }

                public void onEarlyMatched() {
                    UnlockPatternListener.this.onPatternChecked(currentUser, true, 0, true);
                }

                public void onChecked(boolean z, int i) {
                    PresentationKgPatternView.this.mLockPatternView.enableInput();
                    AsyncTask unused = PresentationKgPatternView.this.mPendingLockCheck = null;
                    if (!z) {
                        UnlockPatternListener.this.onPatternChecked(currentUser, false, i, true);
                        PresentationKgPatternView.this.updateInputState();
                    }
                }
            });
        }

        /* access modifiers changed from: private */
        public void onPatternChecked(int i, boolean z, int i2, boolean z2) {
            if (PresentationKgPatternView.DEBUG) {
                Log.d("PKgPatternView", "RDP: matched: " + z);
            }
            PresentationKgPatternView.this.mUpdateMonitor.reportBackupUnlock(z);
            boolean z3 = KeyguardUpdateMonitor.getCurrentUser() == i;
            if (z) {
                PresentationKgPatternView.this.mPtKDMCallback.setFailedCount(0);
                PresentationKgPatternView.this.mPtKDMCallback.setOnceLockout(false);
                if (z3) {
                    PresentationKgPatternView.this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    PresentationKgPatternView.this.mUpdateMonitor.handleExternalAuthenticated();
                    return;
                }
                return;
            }
            PresentationKgPatternView.this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            if (z2) {
                PtKDMCallback ptKDMCallback = PresentationKgPatternView.this.mPtKDMCallback;
                ptKDMCallback.setFailedCount(ptKDMCallback.getFailedCount() + 1);
                if (i2 > 0) {
                    PresentationKgPatternView.this.mPtKDMCallback.setOnceLockout(true);
                    PresentationKgPatternView.this.mLockPatternUtils.setLockoutAttemptDeadline(i, i2);
                }
            }
            PresentationKgPatternView.this.updateBottomMessage(true);
            if (z2 && i2 == 0) {
                PresentationKgPatternView.this.mLockPatternView.postDelayed(PresentationKgPatternView.this.mCancelPatternRunnable, 2000);
            }
        }
    }
}
