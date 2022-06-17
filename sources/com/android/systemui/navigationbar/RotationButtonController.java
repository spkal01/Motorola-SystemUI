package com.android.systemui.navigationbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.IRotationWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManagerGlobal;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.navigationbar.buttons.KeyButtonDrawable;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.RotationLockController;
import java.util.Optional;
import java.util.function.Consumer;

public class RotationButtonController {
    private AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    private int mBehavior = 1;
    private final Runnable mCancelPendingRotationProposal = new RotationButtonController$$ExternalSyntheticLambda2(this);
    private final Context mContext;
    private int mDarkIconColor;
    private boolean mHomeRotationEnabled;
    private boolean mHoveringRotationSuggestion;
    private int mIconResId = R$drawable.ic_sysbar_rotate_button_ccw_start_90;
    private boolean mIsNavigationBarShowing;
    private boolean mIsRecentsAnimationRunning;
    private int mLastRotationSuggestion;
    private int mLightIconColor;
    private boolean mListenersRegistered = false;
    /* access modifiers changed from: private */
    public final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private boolean mPendingRotationSuggestion;
    private final Runnable mRemoveRotationProposal = new RotationButtonController$$ExternalSyntheticLambda3(this);
    /* access modifiers changed from: private */
    public Consumer<Integer> mRotWatcherListener;
    private Animator mRotateHideAnimator;
    /* access modifiers changed from: private */
    public RotationButton mRotationButton;
    /* access modifiers changed from: private */
    public RotationLockController mRotationLockController;
    private final IRotationWatcher.Stub mRotationWatcher = new IRotationWatcher.Stub() {
        public void onRotationChanged(int i) throws RemoteException {
            RotationButtonController.this.mMainThreadHandler.postAtFrontOfQueue(new RotationButtonController$1$$ExternalSyntheticLambda0(this, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onRotationChanged$0(int i) {
            if (RotationButtonController.this.mRotationLockController.isRotationLocked()) {
                if (RotationButtonController.this.shouldOverrideUserLockPrefs(i)) {
                    RotationButtonController.this.setRotationLockedAtAngle(i);
                }
                RotationButtonController.this.setRotateSuggestionButtonState(false, true);
            }
            if (RotationButtonController.this.mRotWatcherListener != null) {
                RotationButtonController.this.mRotWatcherListener.accept(Integer.valueOf(i));
            }
        }
    };
    private boolean mSkipOverrideUserLockPrefsOnce;
    private TaskStackListenerImpl mTaskStackListener;
    private final UiEventLogger mUiEventLogger = new UiEventLoggerImpl();
    private final ViewRippler mViewRippler = new ViewRippler();

    static boolean hasDisable2RotateSuggestionFlag(int i) {
        return (i & 16) != 0;
    }

    private boolean isRotationAnimationCCW(int i, int i2) {
        if (i == 0 && i2 == 1) {
            return false;
        }
        if (i == 0 && i2 == 2) {
            return true;
        }
        if (i == 0 && i2 == 3) {
            return true;
        }
        if (i == 1 && i2 == 0) {
            return true;
        }
        if (i == 1 && i2 == 2) {
            return false;
        }
        if (i == 1 && i2 == 3) {
            return true;
        }
        if (i == 2 && i2 == 0) {
            return true;
        }
        if (i == 2 && i2 == 1) {
            return true;
        }
        if (i == 2 && i2 == 3) {
            return false;
        }
        if (i == 3 && i2 == 0) {
            return false;
        }
        if (i == 3 && i2 == 1) {
            return true;
        }
        return i == 3 && i2 == 2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        setRotateSuggestionButtonState(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.mPendingRotationSuggestion = false;
    }

    RotationButtonController(Context context, int i, int i2) {
        this.mContext = context;
        this.mLightIconColor = i;
        this.mDarkIconColor = i2;
        this.mIsNavigationBarShowing = true;
        this.mRotationLockController = (RotationLockController) Dependency.get(RotationLockController.class);
        this.mAccessibilityManagerWrapper = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);
        this.mTaskStackListener = new TaskStackListenerImpl();
    }

    /* access modifiers changed from: package-private */
    public void setRotationButton(RotationButton rotationButton, Consumer<Boolean> consumer) {
        this.mRotationButton = rotationButton;
        rotationButton.setRotationButtonController(this);
        this.mRotationButton.setOnClickListener(new RotationButtonController$$ExternalSyntheticLambda0(this));
        this.mRotationButton.setOnHoverListener(new RotationButtonController$$ExternalSyntheticLambda1(this));
        this.mRotationButton.setVisibilityChangedCallback(consumer);
    }

    /* access modifiers changed from: package-private */
    public void registerListeners() {
        if (!this.mListenersRegistered) {
            this.mListenersRegistered = true;
            try {
                WindowManagerGlobal.getWindowManagerService().watchRotation(this.mRotationWatcher, this.mContext.getDisplay().getDisplayId());
            } catch (IllegalArgumentException unused) {
                this.mListenersRegistered = false;
                Log.w("StatusBar/RotationButtonController", "RegisterListeners for the display failed");
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
            TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mTaskStackListener);
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterListeners() {
        if (this.mListenersRegistered) {
            this.mListenersRegistered = false;
            try {
                WindowManagerGlobal.getWindowManagerService().removeRotationWatcher(this.mRotationWatcher);
                TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addRotationCallback(Consumer<Integer> consumer) {
        this.mRotWatcherListener = consumer;
    }

    /* access modifiers changed from: package-private */
    public void setRotationLockedAtAngle(int i) {
        this.mRotationLockController.setRotationLockedAtAngle(true, i);
    }

    public boolean isRotationLocked() {
        return this.mRotationLockController.isRotationLocked();
    }

    /* access modifiers changed from: package-private */
    public void setRotateSuggestionButtonState(boolean z) {
        setRotateSuggestionButtonState(z, false);
    }

    /* access modifiers changed from: package-private */
    public void setRotateSuggestionButtonState(boolean z, boolean z2) {
        View currentView;
        KeyButtonDrawable imageDrawable;
        if ((z || this.mRotationButton.isVisible()) && (currentView = this.mRotationButton.getCurrentView()) != null && (imageDrawable = this.mRotationButton.getImageDrawable()) != null) {
            this.mPendingRotationSuggestion = false;
            this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
            if (z) {
                Animator animator = this.mRotateHideAnimator;
                if (animator != null && animator.isRunning()) {
                    this.mRotateHideAnimator.cancel();
                }
                this.mRotateHideAnimator = null;
                currentView.setAlpha(1.0f);
                if (imageDrawable.canAnimate()) {
                    imageDrawable.resetAnimation();
                    imageDrawable.startAnimation();
                }
                if (!isRotateSuggestionIntroduced()) {
                    this.mViewRippler.start(currentView);
                }
                this.mRotationButton.show();
                return;
            }
            this.mViewRippler.stop();
            if (z2) {
                Animator animator2 = this.mRotateHideAnimator;
                if (animator2 != null && animator2.isRunning()) {
                    this.mRotateHideAnimator.pause();
                }
                this.mRotationButton.hide();
                return;
            }
            Animator animator3 = this.mRotateHideAnimator;
            if (animator3 == null || !animator3.isRunning()) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(currentView, "alpha", new float[]{0.0f});
                ofFloat.setDuration(100);
                ofFloat.setInterpolator(Interpolators.LINEAR);
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        RotationButtonController.this.mRotationButton.hide();
                    }
                });
                this.mRotateHideAnimator = ofFloat;
                ofFloat.start();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setRecentsAnimationRunning(boolean z) {
        this.mIsRecentsAnimationRunning = z;
        updateRotationButtonStateInOverview();
    }

    /* access modifiers changed from: package-private */
    public void setHomeRotationEnabled(boolean z) {
        this.mHomeRotationEnabled = z;
        updateRotationButtonStateInOverview();
    }

    private void updateRotationButtonStateInOverview() {
        if (this.mIsRecentsAnimationRunning && !this.mHomeRotationEnabled) {
            setRotateSuggestionButtonState(false, true);
        }
    }

    /* access modifiers changed from: package-private */
    public void setDarkIntensity(float f) {
        this.mRotationButton.setDarkIntensity(f);
    }

    /* access modifiers changed from: package-private */
    public void onRotationProposal(int i, int i2, boolean z) {
        int i3;
        int i4;
        if (!this.mRotationButton.acceptRotationProposal()) {
            return;
        }
        if (!this.mHomeRotationEnabled && this.mIsRecentsAnimationRunning) {
            return;
        }
        if (!z) {
            setRotateSuggestionButtonState(false);
        } else if (i == i2) {
            this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
            setRotateSuggestionButtonState(false);
        } else {
            this.mLastRotationSuggestion = i;
            boolean isRotationAnimationCCW = isRotationAnimationCCW(i2, i);
            if (i2 == 0 || i2 == 2) {
                if (isRotationAnimationCCW) {
                    i3 = R$drawable.ic_sysbar_rotate_button_ccw_start_90;
                } else {
                    i3 = R$drawable.ic_sysbar_rotate_button_cw_start_90;
                }
                this.mIconResId = i3;
            } else {
                if (isRotationAnimationCCW) {
                    i4 = R$drawable.ic_sysbar_rotate_button_ccw_start_0;
                } else {
                    i4 = R$drawable.ic_sysbar_rotate_button_ccw_start_0;
                }
                this.mIconResId = i4;
            }
            this.mRotationButton.updateIcon(this.mLightIconColor, this.mDarkIconColor);
            if (canShowRotationButton()) {
                showAndLogRotationSuggestion();
                return;
            }
            this.mPendingRotationSuggestion = true;
            this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
            this.mMainThreadHandler.postDelayed(this.mCancelPendingRotationProposal, 20000);
        }
    }

    /* access modifiers changed from: package-private */
    public void onDisable2FlagChanged(int i) {
        if (hasDisable2RotateSuggestionFlag(i)) {
            onRotationSuggestionsDisabled();
        }
    }

    /* access modifiers changed from: package-private */
    public void onNavigationBarWindowVisibilityChange(boolean z) {
        if (this.mIsNavigationBarShowing != z) {
            this.mIsNavigationBarShowing = z;
            showPendingRotationButtonIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    public void onBehaviorChanged(int i) {
        if (this.mBehavior != i) {
            this.mBehavior = i;
            showPendingRotationButtonIfNeeded();
        }
    }

    private void showPendingRotationButtonIfNeeded() {
        if (canShowRotationButton() && this.mPendingRotationSuggestion) {
            showAndLogRotationSuggestion();
        }
    }

    private boolean canShowRotationButton() {
        return this.mIsNavigationBarShowing || this.mBehavior == 1;
    }

    public Context getContext() {
        return this.mContext;
    }

    /* access modifiers changed from: package-private */
    public RotationButton getRotationButton() {
        return this.mRotationButton;
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public int getLightIconColor() {
        return this.mLightIconColor;
    }

    public int getDarkIconColor() {
        return this.mDarkIconColor;
    }

    /* access modifiers changed from: private */
    public void onRotateSuggestionClick(View view) {
        this.mUiEventLogger.log(RotationButtonEvent.ROTATION_SUGGESTION_ACCEPTED);
        incrementNumAcceptedRotationSuggestionsIfNeeded();
        setRotationLockedAtAngle(this.mLastRotationSuggestion);
    }

    /* access modifiers changed from: private */
    public boolean onRotateSuggestionHover(View view, MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        this.mHoveringRotationSuggestion = actionMasked == 9 || actionMasked == 7;
        rescheduleRotationTimeout(true);
        return false;
    }

    private void onRotationSuggestionsDisabled() {
        setRotateSuggestionButtonState(false, true);
        this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
    }

    private void showAndLogRotationSuggestion() {
        setRotateSuggestionButtonState(true);
        rescheduleRotationTimeout(false);
        this.mUiEventLogger.log(RotationButtonEvent.ROTATION_SUGGESTION_SHOWN);
    }

    /* access modifiers changed from: package-private */
    public void setSkipOverrideUserLockPrefsOnce() {
        this.mSkipOverrideUserLockPrefsOnce = true;
    }

    /* access modifiers changed from: private */
    public boolean shouldOverrideUserLockPrefs(int i) {
        if (this.mSkipOverrideUserLockPrefsOnce) {
            this.mSkipOverrideUserLockPrefsOnce = false;
            return false;
        } else if (i == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void rescheduleRotationTimeout(boolean z) {
        Animator animator;
        if (!z || (((animator = this.mRotateHideAnimator) == null || !animator.isRunning()) && this.mRotationButton.isVisible())) {
            this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
            this.mMainThreadHandler.postDelayed(this.mRemoveRotationProposal, (long) computeRotationProposalTimeout());
        }
    }

    private int computeRotationProposalTimeout() {
        return this.mAccessibilityManagerWrapper.getRecommendedTimeoutMillis(this.mHoveringRotationSuggestion ? 16000 : 5000, 4);
    }

    private boolean isRotateSuggestionIntroduced() {
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "num_rotation_suggestions_accepted", 0) >= 3) {
            return true;
        }
        return false;
    }

    private void incrementNumAcceptedRotationSuggestionsIfNeeded() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int i = Settings.Secure.getInt(contentResolver, "num_rotation_suggestions_accepted", 0);
        if (i < 3) {
            Settings.Secure.putInt(contentResolver, "num_rotation_suggestions_accepted", i + 1);
        }
    }

    private class TaskStackListenerImpl extends TaskStackChangeListener {
        private TaskStackListenerImpl() {
        }

        public void onTaskStackChanged() {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        public void onTaskRemoved(int i) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        public void onTaskMovedToFront(int i) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        public void onActivityRequestedOrientationChanged(int i, int i2) {
            Optional.ofNullable(ActivityManagerWrapper.getInstance()).map(C1091x8e6fda0f.INSTANCE).ifPresent(new C1090x8e6fda0e(this, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onActivityRequestedOrientationChanged$0(int i, ActivityManager.RunningTaskInfo runningTaskInfo) {
            if (runningTaskInfo.id == i) {
                RotationButtonController.this.setRotateSuggestionButtonState(false);
            }
        }
    }

    private class ViewRippler {
        private final Runnable mRipple;
        /* access modifiers changed from: private */
        public View mRoot;

        private ViewRippler() {
            this.mRipple = new Runnable() {
                public void run() {
                    if (ViewRippler.this.mRoot.isAttachedToWindow()) {
                        ViewRippler.this.mRoot.setPressed(true);
                        ViewRippler.this.mRoot.setPressed(false);
                    }
                }
            };
        }

        public void start(View view) {
            stop();
            this.mRoot = view;
            view.postOnAnimationDelayed(this.mRipple, 50);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 2000);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 4000);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 6000);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 8000);
        }

        public void stop() {
            View view = this.mRoot;
            if (view != null) {
                view.removeCallbacks(this.mRipple);
            }
        }
    }

    enum RotationButtonEvent implements UiEventLogger.UiEventEnum {
        ROTATION_SUGGESTION_SHOWN(206),
        ROTATION_SUGGESTION_ACCEPTED(207);
        
        private final int mId;

        private RotationButtonEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }
}
