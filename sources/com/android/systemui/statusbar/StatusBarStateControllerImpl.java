package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.policy.CallbackController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class StatusBarStateControllerImpl implements SysuiStatusBarStateController, CallbackController<StatusBarStateController.StateListener>, Dumpable {
    private static final FloatProperty<StatusBarStateControllerImpl> SET_DARK_AMOUNT_PROPERTY = new FloatProperty<StatusBarStateControllerImpl>("mDozeAmount") {
        public void setValue(StatusBarStateControllerImpl statusBarStateControllerImpl, float f) {
            statusBarStateControllerImpl.setDozeAmountInternal(f);
        }

        public Float get(StatusBarStateControllerImpl statusBarStateControllerImpl) {
            return Float.valueOf(statusBarStateControllerImpl.mDozeAmount);
        }
    };
    private static final Comparator<SysuiStatusBarStateController.RankedListener> sComparator = Comparator.comparingInt(StatusBarStateControllerImpl$$ExternalSyntheticLambda2.INSTANCE);
    private ValueAnimator mDarkAnimator;
    /* access modifiers changed from: private */
    public float mDozeAmount;
    private float mDozeAmountTarget;
    private Interpolator mDozeInterpolator;
    private final Handler mHandler;
    private HistoricalState[] mHistoricalRecords;
    private int mHistoryIndex;
    private boolean mIsDozing;
    private boolean mIsExpanded;
    private boolean mIsFullscreen;
    private boolean mKeyguardRequested;
    private int mLastState;
    private boolean mLeaveOpenOnKeyguardHide;
    private final ArrayList<SysuiStatusBarStateController.RankedListener> mListeners = new ArrayList<>();
    private boolean mPulsing;
    private int mState;
    private final UiEventLogger mUiEventLogger;
    private int mUpcomingState;
    private View mView;

    public StatusBarStateControllerImpl(UiEventLogger uiEventLogger) {
        this.mHistoryIndex = 0;
        this.mHistoricalRecords = new HistoricalState[32];
        this.mIsFullscreen = false;
        this.mDozeInterpolator = Interpolators.FAST_OUT_SLOW_IN;
        this.mHandler = (Handler) Dependency.get(Dependency.MAIN_HANDLER);
        this.mUiEventLogger = uiEventLogger;
        for (int i = 0; i < 32; i++) {
            this.mHistoricalRecords[i] = new HistoricalState();
        }
    }

    public int getState() {
        return this.mState;
    }

    public boolean setState(int i, boolean z) {
        if (i > 3 || i < 0) {
            throw new IllegalArgumentException("Invalid state " + i);
        } else if (!z && i == this.mState) {
            return false;
        } else {
            recordHistoricalState(i, this.mState);
            if (this.mState == 0 && i == 2) {
                Log.e("SbStateController", "Invalid state transition: SHADE -> SHADE_LOCKED", new Throwable());
            }
            synchronized (this.mListeners) {
                String str = StatusBarStateControllerImpl.class.getSimpleName() + "#setState(" + i + ")";
                DejankUtils.startDetectingBlockingIpcs(str);
                Iterator it = new ArrayList(this.mListeners).iterator();
                while (it.hasNext()) {
                    ((SysuiStatusBarStateController.RankedListener) it.next()).mListener.onStatePreChange(this.mState, i);
                }
                this.mLastState = this.mState;
                this.mState = i;
                this.mUpcomingState = i;
                this.mUiEventLogger.log(StatusBarStateEvent.fromState(i));
                Iterator it2 = new ArrayList(this.mListeners).iterator();
                while (it2.hasNext()) {
                    ((SysuiStatusBarStateController.RankedListener) it2.next()).mListener.onStateChanged(this.mState);
                }
                Iterator it3 = new ArrayList(this.mListeners).iterator();
                while (it3.hasNext()) {
                    ((SysuiStatusBarStateController.RankedListener) it3.next()).mListener.onStatePostChange();
                }
                DejankUtils.stopDetectingBlockingIpcs(str);
            }
            return true;
        }
    }

    public void setUpcomingState(int i) {
        this.mUpcomingState = i;
    }

    public int getCurrentOrUpcomingState() {
        return this.mUpcomingState;
    }

    public boolean isDozing() {
        return this.mIsDozing;
    }

    public boolean isPulsing() {
        return this.mPulsing;
    }

    public float getDozeAmount() {
        return this.mDozeAmount;
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public boolean setPanelExpanded(boolean z) {
        if (this.mIsExpanded == z) {
            return false;
        }
        this.mIsExpanded = z;
        String str = StatusBarStateControllerImpl.class.getSimpleName() + "#setIsExpanded";
        DejankUtils.startDetectingBlockingIpcs(str);
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((SysuiStatusBarStateController.RankedListener) it.next()).mListener.onExpandedChanged(this.mIsExpanded);
        }
        DejankUtils.stopDetectingBlockingIpcs(str);
        return true;
    }

    public float getInterpolatedDozeAmount() {
        return this.mDozeInterpolator.getInterpolation(this.mDozeAmount);
    }

    public boolean setIsDozing(boolean z) {
        if (this.mIsDozing == z) {
            return false;
        }
        this.mIsDozing = z;
        synchronized (this.mListeners) {
            String str = StatusBarStateControllerImpl.class.getSimpleName() + "#setIsDozing";
            DejankUtils.startDetectingBlockingIpcs(str);
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                ((SysuiStatusBarStateController.RankedListener) it.next()).mListener.onDozingChanged(z);
            }
            DejankUtils.stopDetectingBlockingIpcs(str);
        }
        return true;
    }

    public void setAndInstrumentDozeAmount(View view, float f, boolean z) {
        ValueAnimator valueAnimator = this.mDarkAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            if (!z || this.mDozeAmountTarget != f) {
                this.mDarkAnimator.cancel();
            } else {
                return;
            }
        }
        View view2 = this.mView;
        if ((view2 == null || !view2.isAttachedToWindow()) && view != null && view.isAttachedToWindow()) {
            this.mView = view;
        }
        this.mDozeAmountTarget = f;
        if (z) {
            startDozeAnimation();
        } else {
            setDozeAmountInternal(f);
        }
    }

    private void startDozeAnimation() {
        Interpolator interpolator;
        float f = this.mDozeAmount;
        if (f == 0.0f || f == 1.0f) {
            if (this.mIsDozing) {
                interpolator = Interpolators.FAST_OUT_SLOW_IN;
            } else {
                interpolator = Interpolators.TOUCH_RESPONSE_REVERSE;
            }
            this.mDozeInterpolator = interpolator;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, SET_DARK_AMOUNT_PROPERTY, new float[]{this.mDozeAmountTarget});
        this.mDarkAnimator = ofFloat;
        ofFloat.setInterpolator(Interpolators.LINEAR);
        this.mDarkAnimator.setDuration(500);
        this.mDarkAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                StatusBarStateControllerImpl.this.cancelInteractionJankMonitor();
            }

            public void onAnimationEnd(Animator animator) {
                StatusBarStateControllerImpl.this.endInteractionJankMonitor();
            }

            public void onAnimationStart(Animator animator) {
                StatusBarStateControllerImpl.this.beginInteractionJankMonitor();
            }
        });
        this.mDarkAnimator.start();
    }

    /* access modifiers changed from: private */
    public void setDozeAmountInternal(float f) {
        this.mDozeAmount = f;
        float interpolation = this.mDozeInterpolator.getInterpolation(f);
        synchronized (this.mListeners) {
            String str = StatusBarStateControllerImpl.class.getSimpleName() + "#setDozeAmount";
            DejankUtils.startDetectingBlockingIpcs(str);
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                ((SysuiStatusBarStateController.RankedListener) it.next()).mListener.onDozeAmountChanged(this.mDozeAmount, interpolation);
            }
            DejankUtils.stopDetectingBlockingIpcs(str);
        }
    }

    /* access modifiers changed from: private */
    public void beginInteractionJankMonitor() {
        View view = this.mView;
        if (view != null && view.isAttachedToWindow()) {
            InteractionJankMonitor.getInstance().begin(this.mView, getCujType());
        }
    }

    /* access modifiers changed from: private */
    public void endInteractionJankMonitor() {
        InteractionJankMonitor.getInstance().end(getCujType());
    }

    /* access modifiers changed from: private */
    public void cancelInteractionJankMonitor() {
        InteractionJankMonitor.getInstance().cancel(getCujType());
    }

    private int getCujType() {
        return this.mIsDozing ? 24 : 23;
    }

    public boolean goingToFullShade() {
        return this.mState == 0 && this.mLeaveOpenOnKeyguardHide;
    }

    public void setLeaveOpenOnKeyguardHide(boolean z) {
        this.mLeaveOpenOnKeyguardHide = z;
    }

    public boolean leaveOpenOnKeyguardHide() {
        return this.mLeaveOpenOnKeyguardHide;
    }

    public boolean fromShadeLocked() {
        return this.mLastState == 2;
    }

    public void addCallback(StatusBarStateController.StateListener stateListener) {
        synchronized (this.mListeners) {
            addListenerInternalLocked(stateListener, Integer.MAX_VALUE);
        }
    }

    @Deprecated
    public void addCallback(StatusBarStateController.StateListener stateListener, int i) {
        synchronized (this.mListeners) {
            addListenerInternalLocked(stateListener, i);
        }
    }

    @GuardedBy({"mListeners"})
    private void addListenerInternalLocked(StatusBarStateController.StateListener stateListener, int i) {
        Iterator<SysuiStatusBarStateController.RankedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            if (it.next().mListener.equals(stateListener)) {
                return;
            }
        }
        this.mListeners.add(new SysuiStatusBarStateController.RankedListener(stateListener, i));
        this.mListeners.sort(sComparator);
        this.mHandler.post(new StatusBarStateControllerImpl$$ExternalSyntheticLambda0(this, stateListener));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addListenerInternalLocked$1(StatusBarStateController.StateListener stateListener) {
        int i = this.mState;
        stateListener.onStatePreChange(i, i);
        stateListener.onStateChanged(this.mState);
        stateListener.onStatePostChange();
    }

    public void removeCallback(StatusBarStateController.StateListener stateListener) {
        synchronized (this.mListeners) {
            this.mListeners.removeIf(new StatusBarStateControllerImpl$$ExternalSyntheticLambda1(stateListener));
        }
    }

    public void setKeyguardRequested(boolean z) {
        this.mKeyguardRequested = z;
    }

    public boolean isKeyguardRequested() {
        return this.mKeyguardRequested;
    }

    public void setFullscreenState(boolean z) {
        if (this.mIsFullscreen != z) {
            this.mIsFullscreen = z;
            synchronized (this.mListeners) {
                Iterator it = new ArrayList(this.mListeners).iterator();
                while (it.hasNext()) {
                    ((SysuiStatusBarStateController.RankedListener) it.next()).mListener.onFullscreenStateChanged(z, true);
                }
            }
        }
    }

    public void setPulsing(boolean z) {
        if (this.mPulsing != z) {
            this.mPulsing = z;
            synchronized (this.mListeners) {
                Iterator it = new ArrayList(this.mListeners).iterator();
                while (it.hasNext()) {
                    ((SysuiStatusBarStateController.RankedListener) it.next()).mListener.onPulsingChanged(z);
                }
            }
        }
    }

    public static String describe(int i) {
        return StatusBarState.toShortString(i);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("StatusBarStateController: ");
        printWriter.println(" mState=" + this.mState + " (" + describe(this.mState) + ")");
        printWriter.println(" mLastState=" + this.mLastState + " (" + describe(this.mLastState) + ")");
        StringBuilder sb = new StringBuilder();
        sb.append(" mLeaveOpenOnKeyguardHide=");
        sb.append(this.mLeaveOpenOnKeyguardHide);
        printWriter.println(sb.toString());
        printWriter.println(" mKeyguardRequested=" + this.mKeyguardRequested);
        printWriter.println(" mIsDozing=" + this.mIsDozing);
        printWriter.println(" Historical states:");
        int i = 0;
        for (int i2 = 0; i2 < 32; i2++) {
            if (this.mHistoricalRecords[i2].mTimestamp != 0) {
                i++;
            }
        }
        for (int i3 = this.mHistoryIndex + 32; i3 >= ((this.mHistoryIndex + 32) - i) + 1; i3 += -1) {
            printWriter.println("  (" + (((this.mHistoryIndex + 32) - i3) + 1) + ")" + this.mHistoricalRecords[i3 & 31]);
        }
    }

    private void recordHistoricalState(int i, int i2) {
        int i3 = (this.mHistoryIndex + 1) % 32;
        this.mHistoryIndex = i3;
        HistoricalState historicalState = this.mHistoricalRecords[i3];
        historicalState.mState = i;
        historicalState.mLastState = i2;
        historicalState.mTimestamp = System.currentTimeMillis();
    }

    private static class HistoricalState {
        int mLastState;
        int mState;
        long mTimestamp;

        private HistoricalState() {
        }

        public String toString() {
            if (this.mTimestamp != 0) {
                return "state=" + this.mState + " (" + StatusBarStateControllerImpl.describe(this.mState) + ")" + "lastState=" + this.mLastState + " (" + StatusBarStateControllerImpl.describe(this.mLastState) + ")" + "timestamp=" + DateFormat.format("MM-dd HH:mm:ss", this.mTimestamp);
            }
            return "Empty " + HistoricalState.class.getSimpleName();
        }
    }
}
