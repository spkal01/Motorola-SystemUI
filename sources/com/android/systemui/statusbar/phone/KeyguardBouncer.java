package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowInsets;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardHostViewController;
import com.android.keyguard.KeyguardRootViewController;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.keyguard.dagger.KeyguardBouncerComponent;
import com.android.systemui.DejankUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.moto.DisplayLayoutInflater;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyguardBouncer {
    /* access modifiers changed from: private */
    public int mBouncerPromptReason;
    protected final ViewMediatorCallback mCallback;
    protected final ViewGroup mContainer;
    protected final Context mContext;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    /* access modifiers changed from: private */
    public float mExpansion;
    private final List<BouncerExpansionCallback> mExpansionCallbacks;
    private final FalsingCollector mFalsingCollector;
    private final Handler mHandler;
    private boolean mIsAnimatingAway;
    private boolean mIsScrimmed;
    private final KeyguardBouncerComponent.Factory mKeyguardBouncerComponentFactory;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardSecurityModel mKeyguardSecurityModel;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public KeyguardHostViewController mKeyguardViewController;
    /* access modifiers changed from: private */
    public LockPatternUtils mLockPatternUtils;
    private final Runnable mRemoveViewRunnable;
    private final List<KeyguardResetCallback> mResetCallbacks;
    private final Runnable mResetRunnable;
    protected ViewGroup mRoot;
    private KeyguardRootViewController mRootViewController;
    private final Runnable mShowRunnable;
    /* access modifiers changed from: private */
    public boolean mShowingSoon;
    /* access modifiers changed from: private */
    public int mStatusBarHeight;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;

    public interface BouncerExpansionCallback {
        void hideBouncerFromCli() {
        }

        void onExpansionChanged(float f) {
        }

        void onFullyHidden();

        void onFullyShown();

        void onStartingToHide();

        void onStartingToShow();

        void onVisibilityChanged(boolean z) {
        }
    }

    public interface KeyguardResetCallback {
        void onKeyguardReset();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null) {
            keyguardHostViewController.resetSecurityContainer();
            Iterator it = new ArrayList(this.mResetCallbacks).iterator();
            while (it.hasNext()) {
                ((KeyguardResetCallback) it.next()).onKeyguardReset();
            }
        }
    }

    private KeyguardBouncer(Context context, ViewMediatorCallback viewMediatorCallback, ViewGroup viewGroup, DismissCallbackRegistry dismissCallbackRegistry, FalsingCollector falsingCollector, BouncerExpansionCallback bouncerExpansionCallback, KeyguardStateController keyguardStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardBypassController keyguardBypassController, Handler handler, KeyguardSecurityModel keyguardSecurityModel, LockPatternUtils lockPatternUtils, KeyguardBouncerComponent.Factory factory) {
        ArrayList arrayList = new ArrayList();
        this.mExpansionCallbacks = arrayList;
        C18121 r3 = new KeyguardUpdateMonitorCallback() {
            public void onStrongAuthStateChanged(int i) {
                KeyguardBouncer keyguardBouncer = KeyguardBouncer.this;
                int unused = keyguardBouncer.mBouncerPromptReason = keyguardBouncer.mCallback.getBouncerPromptReason();
            }

            public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
                if (!KeyguardBouncer.this.mLockPatternUtils.isBiometricAllowedForUser(i)) {
                    KeyguardBouncer keyguardBouncer = KeyguardBouncer.this;
                    keyguardBouncer.showPromptReason(keyguardBouncer.mBouncerPromptReason);
                }
            }
        };
        this.mUpdateMonitorCallback = r3;
        this.mRemoveViewRunnable = new KeyguardBouncer$$ExternalSyntheticLambda0(this);
        this.mResetCallbacks = new ArrayList();
        this.mResetRunnable = new KeyguardBouncer$$ExternalSyntheticLambda1(this);
        this.mExpansion = 1.0f;
        this.mShowRunnable = new Runnable() {
            public void run() {
                KeyguardBouncer.this.setVisibility(0);
                KeyguardBouncer keyguardBouncer = KeyguardBouncer.this;
                keyguardBouncer.showPromptReason(keyguardBouncer.mBouncerPromptReason);
                CharSequence consumeCustomMessage = KeyguardBouncer.this.mCallback.consumeCustomMessage();
                if (consumeCustomMessage != null) {
                    KeyguardBouncer.this.mKeyguardViewController.showErrorMessage(consumeCustomMessage);
                }
                KeyguardBouncer.this.mKeyguardViewController.appear(KeyguardBouncer.this.mStatusBarHeight);
                boolean unused = KeyguardBouncer.this.mShowingSoon = false;
                if (KeyguardBouncer.this.mExpansion == 0.0f) {
                    KeyguardBouncer.this.mKeyguardViewController.onResume();
                    KeyguardBouncer.this.mKeyguardViewController.resetSecurityContainer();
                    KeyguardBouncer keyguardBouncer2 = KeyguardBouncer.this;
                    keyguardBouncer2.showPromptReason(keyguardBouncer2.mBouncerPromptReason);
                }
                SysUiStatsLog.write(63, 2);
            }
        };
        this.mContext = context;
        this.mCallback = viewMediatorCallback;
        this.mContainer = viewGroup;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mFalsingCollector = falsingCollector;
        this.mDismissCallbackRegistry = dismissCallbackRegistry;
        this.mHandler = handler;
        this.mKeyguardStateController = keyguardStateController;
        this.mKeyguardSecurityModel = keyguardSecurityModel;
        this.mKeyguardBouncerComponentFactory = factory;
        keyguardUpdateMonitor.registerCallback(r3);
        this.mKeyguardBypassController = keyguardBypassController;
        this.mLockPatternUtils = lockPatternUtils;
        BouncerExpansionCallback bouncerExpansionCallback2 = bouncerExpansionCallback;
        arrayList.add(bouncerExpansionCallback);
    }

    public KeyguardBouncerComponent getKeyguardBouncerComponent() {
        return this.mKeyguardBouncerComponentFactory.create(DisplayLayoutInflater.create(this.mContext));
    }

    public void show(boolean z) {
        show(z, true);
    }

    public void show(boolean z, boolean z2) {
        int currentUser = KeyguardUpdateMonitor.getCurrentUser();
        if (currentUser != 0 || !UserManager.isSplitSystemUser()) {
            ensureView();
            this.mIsScrimmed = z2;
            if (z2) {
                setExpansion(0.0f);
            }
            if (z) {
                showPrimarySecurityScreen();
            }
            if (this.mRoot.getVisibility() != 0 && !this.mShowingSoon) {
                int currentUser2 = KeyguardUpdateMonitor.getCurrentUser();
                boolean z3 = false;
                if (!(UserManager.isSplitSystemUser() && currentUser2 == 0) && currentUser2 == currentUser) {
                    z3 = true;
                }
                if (!z3 || !this.mKeyguardViewController.dismiss(currentUser2)) {
                    if (!z3) {
                        Log.w("KeyguardBouncer", "User can't dismiss keyguard: " + currentUser2 + " != " + currentUser);
                    }
                    this.mShowingSoon = true;
                    DejankUtils.removeCallbacks(this.mResetRunnable);
                    if (!this.mKeyguardStateController.isFaceAuthEnabled() || needsFullscreenBouncer() || this.mKeyguardUpdateMonitor.userNeedsStrongAuth() || this.mKeyguardBypassController.getBypassEnabled()) {
                        DejankUtils.postAfterTraversal(this.mShowRunnable);
                    } else {
                        this.mHandler.postDelayed(this.mShowRunnable, 1200);
                    }
                    this.mCallback.onBouncerVisiblityChanged(true);
                    dispatchStartingToShow();
                }
            }
        }
    }

    public boolean isScrimmed() {
        return this.mIsScrimmed;
    }

    private void onFullyShown() {
        FalsingCollector falsingCollector = this.mFalsingCollector;
        if (falsingCollector != null) {
            falsingCollector.onBouncerShown();
        }
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController == null) {
            Log.wtf("KeyguardBouncer", "onFullyShown when view was null");
            return;
        }
        keyguardHostViewController.onResume();
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            viewGroup.announceForAccessibility(this.mKeyguardViewController.getAccessibilityTitleForCurrentMode());
        }
    }

    private void onFullyHidden() {
        cancelShowRunnable();
        setVisibility(4);
        FalsingCollector falsingCollector = this.mFalsingCollector;
        if (falsingCollector != null) {
            falsingCollector.onBouncerHidden();
        }
        DejankUtils.postAfterTraversal(this.mResetRunnable);
    }

    /* access modifiers changed from: private */
    public void setVisibility(int i) {
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            viewGroup.setVisibility(i);
            dispatchVisibilityChanged();
        }
    }

    public void showPromptReason(int i) {
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null) {
            keyguardHostViewController.showPromptReason(i);
        } else {
            Log.w("KeyguardBouncer", "Trying to show prompt reason on empty bouncer");
        }
    }

    public void showMessage(String str, ColorStateList colorStateList) {
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null) {
            keyguardHostViewController.showMessage(str, colorStateList);
        } else {
            Log.w("KeyguardBouncer", "Trying to show message on empty bouncer");
        }
    }

    private void cancelShowRunnable() {
        DejankUtils.removeCallbacks(this.mShowRunnable);
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mShowingSoon = false;
    }

    public void showWithDismissAction(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable) {
        ensureView();
        setDismissAction(onDismissAction, runnable);
        show(false);
    }

    public void setDismissAction(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable) {
        this.mKeyguardViewController.setOnDismissAction(onDismissAction, runnable);
    }

    public void hide(boolean z, boolean z2) {
        if (isShowing()) {
            SysUiStatsLog.write(63, 1);
            DismissCallbackRegistry dismissCallbackRegistry = this.mDismissCallbackRegistry;
            if (dismissCallbackRegistry != null) {
                dismissCallbackRegistry.notifyDismissCancelled();
            }
        }
        this.mIsScrimmed = false;
        FalsingCollector falsingCollector = this.mFalsingCollector;
        if (falsingCollector != null) {
            falsingCollector.onBouncerHidden();
        }
        this.mCallback.onBouncerVisiblityChanged(false);
        cancelShowRunnable();
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null) {
            if (z2) {
                keyguardHostViewController.cancelDismissAction();
            }
            this.mKeyguardViewController.cleanUp();
        }
        this.mIsAnimatingAway = false;
        if (this.mRoot != null) {
            setVisibility(4);
            if (z) {
                this.mHandler.postDelayed(this.mRemoveViewRunnable, 50);
            }
        }
    }

    public void hide(boolean z) {
        hide(z, true);
    }

    public void startPreHideAnimation(Runnable runnable) {
        this.mIsAnimatingAway = true;
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null) {
            keyguardHostViewController.startDisappearAnimation(runnable);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public void onScreenTurnedOff() {
        ViewGroup viewGroup;
        if (this.mKeyguardViewController != null && (viewGroup = this.mRoot) != null && viewGroup.getVisibility() == 0) {
            this.mKeyguardViewController.onPause();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r2.mRoot;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isShowing() {
        /*
            r2 = this;
            boolean r0 = r2.mShowingSoon
            if (r0 != 0) goto L_0x000e
            android.view.ViewGroup r0 = r2.mRoot
            if (r0 == 0) goto L_0x001d
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x001d
        L_0x000e:
            float r0 = r2.mExpansion
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x001d
            boolean r2 = r2.isAnimatingAway()
            if (r2 != 0) goto L_0x001d
            r2 = 1
            goto L_0x001e
        L_0x001d:
            r2 = 0
        L_0x001e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.KeyguardBouncer.isShowing():boolean");
    }

    public boolean getShowingSoon() {
        return this.mShowingSoon;
    }

    public boolean isAnimatingAway() {
        return this.mIsAnimatingAway;
    }

    public void prepare() {
        boolean z = this.mRoot != null;
        ensureView();
        if (z) {
            showPrimarySecurityScreen();
        }
        this.mBouncerPromptReason = this.mCallback.getBouncerPromptReason();
    }

    private void showPrimarySecurityScreen() {
        this.mKeyguardViewController.showPrimarySecurityScreen();
    }

    public void setExpansion(float f) {
        float f2 = this.mExpansion;
        boolean z = f2 != f;
        this.mExpansion = f;
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null && !this.mIsAnimatingAway) {
            keyguardHostViewController.setExpansion(f);
        }
        int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        if (i == 0 && f2 != 0.0f) {
            onFullyShown();
            dispatchFullyShown();
        } else if (f == 1.0f && f2 != 1.0f) {
            onFullyHidden();
            dispatchFullyHidden();
        } else if (i != 0 && f2 == 0.0f) {
            dispatchStartingToHide();
            KeyguardHostViewController keyguardHostViewController2 = this.mKeyguardViewController;
            if (keyguardHostViewController2 != null) {
                keyguardHostViewController2.onStartingToHide();
            }
        }
        if (z) {
            dispatchExpansionChanged();
        }
    }

    public boolean willDismissWithAction() {
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        return keyguardHostViewController != null && keyguardHostViewController.hasDismissActions();
    }

    /* access modifiers changed from: protected */
    public void ensureView() {
        boolean hasCallbacks = this.mHandler.hasCallbacks(this.mRemoveViewRunnable);
        if (this.mRoot == null || hasCallbacks) {
            inflateView();
        }
    }

    /* access modifiers changed from: protected */
    public void inflateView() {
        removeView();
        this.mHandler.removeCallbacks(this.mRemoveViewRunnable);
        KeyguardBouncerComponent create = this.mKeyguardBouncerComponentFactory.create(DisplayLayoutInflater.create(this.mContext));
        KeyguardRootViewController keyguardRootViewController = create.getKeyguardRootViewController();
        this.mRootViewController = keyguardRootViewController;
        keyguardRootViewController.init();
        this.mRoot = this.mRootViewController.getView();
        KeyguardHostViewController keyguardHostViewController = create.getKeyguardHostViewController();
        this.mKeyguardViewController = keyguardHostViewController;
        keyguardHostViewController.init();
        ViewGroup viewGroup = this.mContainer;
        viewGroup.addView(this.mRoot, viewGroup.getChildCount());
        this.mStatusBarHeight = this.mRoot.getResources().getDimensionPixelOffset(R$dimen.status_bar_height);
        setVisibility(4);
        WindowInsets rootWindowInsets = this.mRoot.getRootWindowInsets();
        if (rootWindowInsets != null) {
            this.mRoot.dispatchApplyWindowInsets(rootWindowInsets);
        }
    }

    /* access modifiers changed from: protected */
    public void removeView() {
        ViewGroup viewGroup;
        ViewGroup viewGroup2 = this.mRoot;
        if (viewGroup2 != null && viewGroup2.getParent() == (viewGroup = this.mContainer)) {
            viewGroup.removeView(this.mRoot);
            this.mRoot = null;
        }
    }

    public boolean needsFullscreenBouncer() {
        KeyguardSecurityModel.SecurityMode securityMode = this.mKeyguardSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
        return securityMode == KeyguardSecurityModel.SecurityMode.SimPin || securityMode == KeyguardSecurityModel.SecurityMode.SimPuk || securityMode == KeyguardSecurityModel.SecurityMode.PAKS;
    }

    public boolean isFullscreenBouncer() {
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController == null) {
            return false;
        }
        KeyguardSecurityModel.SecurityMode currentSecurityMode = keyguardHostViewController.getCurrentSecurityMode();
        if (currentSecurityMode == KeyguardSecurityModel.SecurityMode.SimPin || currentSecurityMode == KeyguardSecurityModel.SecurityMode.SimPuk || currentSecurityMode == KeyguardSecurityModel.SecurityMode.PAKS) {
            return true;
        }
        return false;
    }

    public boolean isSecure() {
        return this.mKeyguardSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser()) != KeyguardSecurityModel.SecurityMode.None;
    }

    public boolean shouldDismissOnMenuPressed() {
        return this.mKeyguardViewController.shouldEnableMenuKey();
    }

    public boolean interceptMediaKey(KeyEvent keyEvent) {
        ensureView();
        return this.mKeyguardViewController.interceptMediaKey(keyEvent);
    }

    public boolean dispatchBackKeyEventPreIme() {
        ensureView();
        return this.mKeyguardViewController.dispatchBackKeyEventPreIme();
    }

    public void notifyKeyguardAuthenticated(boolean z) {
        ensureView();
        this.mKeyguardViewController.finish(z, KeyguardUpdateMonitor.getCurrentUser());
    }

    private void dispatchFullyShown() {
        for (BouncerExpansionCallback onFullyShown : this.mExpansionCallbacks) {
            onFullyShown.onFullyShown();
        }
    }

    private void dispatchStartingToHide() {
        for (BouncerExpansionCallback onStartingToHide : this.mExpansionCallbacks) {
            onStartingToHide.onStartingToHide();
        }
    }

    private void dispatchStartingToShow() {
        for (BouncerExpansionCallback onStartingToShow : this.mExpansionCallbacks) {
            onStartingToShow.onStartingToShow();
        }
    }

    private void dispatchFullyHidden() {
        for (BouncerExpansionCallback onFullyHidden : this.mExpansionCallbacks) {
            onFullyHidden.onFullyHidden();
        }
    }

    private void dispatchExpansionChanged() {
        for (BouncerExpansionCallback onExpansionChanged : this.mExpansionCallbacks) {
            onExpansionChanged.onExpansionChanged(this.mExpansion);
        }
    }

    private void dispatchVisibilityChanged() {
        for (BouncerExpansionCallback onVisibilityChanged : this.mExpansionCallbacks) {
            onVisibilityChanged.onVisibilityChanged(this.mRoot.getVisibility() == 0);
        }
    }

    public void updateResources() {
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null) {
            keyguardHostViewController.updateResources();
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("KeyguardBouncer");
        printWriter.println("  isShowing(): " + isShowing());
        printWriter.println("  mStatusBarHeight: " + this.mStatusBarHeight);
        printWriter.println("  mExpansion: " + this.mExpansion);
        printWriter.println("  mKeyguardViewController; " + this.mKeyguardViewController);
        printWriter.println("  mShowingSoon: " + this.mShowingSoon);
        printWriter.println("  mBouncerPromptReason: " + this.mBouncerPromptReason);
        printWriter.println("  mIsAnimatingAway: " + this.mIsAnimatingAway);
    }

    public void updateKeyguardPosition(float f) {
        KeyguardHostViewController keyguardHostViewController = this.mKeyguardViewController;
        if (keyguardHostViewController != null) {
            keyguardHostViewController.updateKeyguardPosition(f);
        }
    }

    public KeyguardSecurityModel.SecurityMode getSecurityMode() {
        KeyguardSecurityModel keyguardSecurityModel = this.mKeyguardSecurityModel;
        if (keyguardSecurityModel != null) {
            return keyguardSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
        }
        return KeyguardSecurityModel.SecurityMode.None;
    }

    public static class Factory {
        private final ViewMediatorCallback mCallback;
        private final Context mContext;
        private final DismissCallbackRegistry mDismissCallbackRegistry;
        private final FalsingCollector mFalsingCollector;
        private final Handler mHandler;
        private final KeyguardBouncerComponent.Factory mKeyguardBouncerComponentFactory;
        private final KeyguardBypassController mKeyguardBypassController;
        private final KeyguardSecurityModel mKeyguardSecurityModel;
        private final KeyguardStateController mKeyguardStateController;
        private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
        private final LockPatternUtils mLockPatternUtils;

        public Factory(Context context, ViewMediatorCallback viewMediatorCallback, DismissCallbackRegistry dismissCallbackRegistry, FalsingCollector falsingCollector, KeyguardStateController keyguardStateController, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardBypassController keyguardBypassController, Handler handler, KeyguardSecurityModel keyguardSecurityModel, LockPatternUtils lockPatternUtils, KeyguardBouncerComponent.Factory factory) {
            this.mContext = context;
            this.mCallback = viewMediatorCallback;
            this.mDismissCallbackRegistry = dismissCallbackRegistry;
            this.mFalsingCollector = falsingCollector;
            this.mKeyguardStateController = keyguardStateController;
            this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
            this.mKeyguardBypassController = keyguardBypassController;
            this.mHandler = handler;
            this.mKeyguardSecurityModel = keyguardSecurityModel;
            this.mKeyguardBouncerComponentFactory = factory;
            this.mLockPatternUtils = lockPatternUtils;
        }

        public KeyguardBouncer create(ViewGroup viewGroup, BouncerExpansionCallback bouncerExpansionCallback) {
            return new KeyguardBouncer(this.mContext, this.mCallback, viewGroup, this.mDismissCallbackRegistry, this.mFalsingCollector, bouncerExpansionCallback, this.mKeyguardStateController, this.mKeyguardUpdateMonitor, this.mKeyguardBypassController, this.mHandler, this.mKeyguardSecurityModel, this.mLockPatternUtils, this.mKeyguardBouncerComponentFactory);
        }

        public KeyguardBouncer createForCli(Context context, ViewGroup viewGroup, ViewMediatorCallback viewMediatorCallback, BouncerExpansionCallback bouncerExpansionCallback) {
            return new KeyguardBouncer(context, viewMediatorCallback, viewGroup, this.mDismissCallbackRegistry, this.mFalsingCollector, bouncerExpansionCallback, this.mKeyguardStateController, this.mKeyguardUpdateMonitor, this.mKeyguardBypassController, this.mHandler, this.mKeyguardSecurityModel, this.mLockPatternUtils, this.mKeyguardBouncerComponentFactory);
        }
    }
}
