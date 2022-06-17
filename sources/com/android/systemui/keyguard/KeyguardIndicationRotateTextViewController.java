package com.android.systemui.keyguard;

import android.content.res.ColorStateList;
import android.text.TextUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.keyguard.KeyguardIndication;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import com.android.systemui.util.ViewController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KeyguardIndicationRotateTextViewController extends ViewController<KeyguardIndicationTextView> implements Dumpable {
    private int mCurrIndicationType = -1;
    /* access modifiers changed from: private */
    public final DelayableExecutor mExecutor;
    private final Map<Integer, KeyguardIndication> mIndicationMessages = new HashMap();
    /* access modifiers changed from: private */
    public final List<Integer> mIndicationQueue = new LinkedList();
    private final ColorStateList mInitialTextColorState;
    /* access modifiers changed from: private */
    public boolean mIsDozing;
    /* access modifiers changed from: private */
    public final float mMaxAlpha;
    private ShowNextIndication mShowNextIndicationRunnable;
    private final StatusBarStateController mStatusBarStateController;
    private StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() {
        public void onDozeAmountChanged(float f, float f2) {
            ((KeyguardIndicationTextView) KeyguardIndicationRotateTextViewController.this.mView).setAlpha((1.0f - f) * KeyguardIndicationRotateTextViewController.this.mMaxAlpha);
        }

        public void onDozingChanged(boolean z) {
            if (z != KeyguardIndicationRotateTextViewController.this.mIsDozing) {
                boolean unused = KeyguardIndicationRotateTextViewController.this.mIsDozing = z;
                if (KeyguardIndicationRotateTextViewController.this.mIsDozing) {
                    KeyguardIndicationRotateTextViewController.this.showIndication(-1);
                } else if (KeyguardIndicationRotateTextViewController.this.mIndicationQueue.size() > 0) {
                    KeyguardIndicationRotateTextViewController keyguardIndicationRotateTextViewController = KeyguardIndicationRotateTextViewController.this;
                    keyguardIndicationRotateTextViewController.showIndication(((Integer) keyguardIndicationRotateTextViewController.mIndicationQueue.remove(0)).intValue());
                }
            }
        }
    };

    public KeyguardIndicationRotateTextViewController(KeyguardIndicationTextView keyguardIndicationTextView, DelayableExecutor delayableExecutor, StatusBarStateController statusBarStateController) {
        super(keyguardIndicationTextView);
        this.mMaxAlpha = keyguardIndicationTextView.getAlpha();
        this.mExecutor = delayableExecutor;
        T t = this.mView;
        this.mInitialTextColorState = t != null ? ((KeyguardIndicationTextView) t).getTextColors() : ColorStateList.valueOf(-1);
        this.mStatusBarStateController = statusBarStateController;
        init();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        this.mStatusBarStateController.addCallback(this.mStatusBarStateListener);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mStatusBarStateController.removeCallback(this.mStatusBarStateListener);
        cancelScheduledIndication();
    }

    public void updateIndication(int i, KeyguardIndication keyguardIndication, boolean z) {
        int i2;
        if (i != 9 && i != 10) {
            boolean z2 = true;
            boolean z3 = this.mIndicationMessages.get(Integer.valueOf(i)) != null;
            boolean z4 = keyguardIndication != null;
            if (!z4) {
                this.mIndicationMessages.remove(Integer.valueOf(i));
                this.mIndicationQueue.removeIf(new C0977x419da85a(i));
            } else {
                if (!z3) {
                    this.mIndicationQueue.add(Integer.valueOf(i));
                }
                this.mIndicationMessages.put(Integer.valueOf(i), keyguardIndication);
            }
            if (!this.mIsDozing) {
                if (!(z || (i2 = this.mCurrIndicationType) == -1 || i2 == i)) {
                    z2 = false;
                }
                if (z4) {
                    if (z2) {
                        showIndication(i);
                    } else if (!isNextIndicationScheduled()) {
                        scheduleShowNextIndication();
                    }
                } else if (this.mCurrIndicationType == i && !z4 && z) {
                    ShowNextIndication showNextIndication = this.mShowNextIndicationRunnable;
                    if (showNextIndication != null) {
                        showNextIndication.runImmediately();
                    } else {
                        showIndication(-1);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateIndication$0(int i, Integer num) {
        return num.intValue() == i;
    }

    public void hideIndication(int i) {
        if (this.mIndicationMessages.containsKey(Integer.valueOf(i)) && !TextUtils.isEmpty(this.mIndicationMessages.get(Integer.valueOf(i)).getMessage())) {
            updateIndication(i, (KeyguardIndication) null, true);
        }
    }

    public void showTransient(CharSequence charSequence) {
        updateIndication(5, new KeyguardIndication.Builder().setMessage(charSequence).setMinVisibilityMillis(2600L).setTextColor(this.mInitialTextColorState).build(), true);
    }

    public void hideTransient() {
        hideIndication(5);
    }

    public boolean hasIndications() {
        return this.mIndicationMessages.keySet().size() > 0;
    }

    /* access modifiers changed from: private */
    public void showIndication(int i) {
        cancelScheduledIndication();
        this.mCurrIndicationType = i;
        this.mIndicationQueue.removeIf(new C0978x419da85b(i));
        if (this.mCurrIndicationType != -1) {
            this.mIndicationQueue.add(Integer.valueOf(i));
        }
        ((KeyguardIndicationTextView) this.mView).switchIndication(this.mIndicationMessages.get(Integer.valueOf(i)));
        if (this.mCurrIndicationType != -1 && this.mIndicationQueue.size() > 1) {
            scheduleShowNextIndication();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showIndication$1(int i, Integer num) {
        return num.intValue() == i;
    }

    /* access modifiers changed from: protected */
    public boolean isNextIndicationScheduled() {
        return this.mShowNextIndicationRunnable != null;
    }

    private void scheduleShowNextIndication() {
        cancelScheduledIndication();
        this.mShowNextIndicationRunnable = new ShowNextIndication(3500);
    }

    private void cancelScheduledIndication() {
        ShowNextIndication showNextIndication = this.mShowNextIndicationRunnable;
        if (showNextIndication != null) {
            showNextIndication.cancelDelayedExecution();
            this.mShowNextIndicationRunnable = null;
        }
    }

    class ShowNextIndication {
        private Runnable mCancelDelayedRunnable;
        private final Runnable mShowIndicationRunnable;

        ShowNextIndication(long j) {
            C0979x49118fb4 keyguardIndicationRotateTextViewController$ShowNextIndication$$ExternalSyntheticLambda0 = new C0979x49118fb4(this);
            this.mShowIndicationRunnable = keyguardIndicationRotateTextViewController$ShowNextIndication$$ExternalSyntheticLambda0;
            this.mCancelDelayedRunnable = KeyguardIndicationRotateTextViewController.this.mExecutor.executeDelayed(keyguardIndicationRotateTextViewController$ShowNextIndication$$ExternalSyntheticLambda0, j);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            int i;
            if (KeyguardIndicationRotateTextViewController.this.mIndicationQueue.size() == 0) {
                i = -1;
            } else {
                i = ((Integer) KeyguardIndicationRotateTextViewController.this.mIndicationQueue.remove(0)).intValue();
            }
            KeyguardIndicationRotateTextViewController.this.showIndication(i);
        }

        public void runImmediately() {
            cancelDelayedExecution();
            this.mShowIndicationRunnable.run();
        }

        public void cancelDelayedExecution() {
            Runnable runnable = this.mCancelDelayedRunnable;
            if (runnable != null) {
                runnable.run();
                this.mCancelDelayedRunnable = null;
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("KeyguardIndicationRotatingTextViewController:");
        printWriter.println("    currentMessage=" + ((KeyguardIndicationTextView) this.mView).getText());
        printWriter.println("    dozing:" + this.mIsDozing);
        printWriter.println("    queue:" + this.mIndicationQueue.toString());
        printWriter.println("    showNextIndicationRunnable:" + this.mShowNextIndicationRunnable);
        if (hasIndications()) {
            printWriter.println("    All messages:");
            for (Integer intValue : this.mIndicationMessages.keySet()) {
                int intValue2 = intValue.intValue();
                printWriter.println("        type=" + intValue2 + " " + this.mIndicationMessages.get(Integer.valueOf(intValue2)));
            }
        }
    }
}
