package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Binder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$integer;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.navigationbar.NavigationBarView;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowControllerImpl;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class CliStatusBarWindowController implements RemoteInputController.Callback, Dumpable {
    private final State mBarCurrentState;
    private final ArrayList<WeakReference<StatusBarWindowCallback>> mCallbacks;
    private int mCliBarHeight = -1;
    private WindowManager.LayoutParams mCliBarLp;
    private final WindowManager.LayoutParams mCliBarLpChanged;
    private final Context mCliContext;
    private WindowManager.LayoutParams mCliLp;
    private final WindowManager.LayoutParams mCliLpChanged;
    private View mCliPanelView;
    private View mCliStatusBarView;
    private final State mCurrentState;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final long mLockScreenDisplayTimeout;
    private float mScreenBrightnessDoze;
    private final WindowManager mWindowManager;

    public interface ICliChildView {
        int getPulseTime() {
            return 6000;
        }

        void resetCliKeyguard() {
        }

        void setCliViewRequestListener(OnCliViewRequestListener onCliViewRequestListener) {
        }
    }

    public interface OnCliViewRequestListener {
        void onVisibleChanged(View view, int i, boolean z) {
        }
    }

    public CliStatusBarWindowController(Context context, KeyguardViewMediator keyguardViewMediator) {
        Context context2 = null;
        this.mBarCurrentState = new State();
        this.mCurrentState = new State();
        this.mCallbacks = Lists.newArrayList();
        this.mLockScreenDisplayTimeout = (long) context.getResources().getInteger(R$integer.config_lockScreenDisplayTimeout);
        Context cliBaseContext = MotoFeature.getInstance(context).getCliBaseContext(context);
        this.mKeyguardViewMediator = keyguardViewMediator;
        if (cliBaseContext == context) {
            Log.e("Cli_StatusBarWindowController", "Can't create CLI context!");
        } else {
            context2 = cliBaseContext;
        }
        this.mCliContext = context2;
        this.mCliBarLpChanged = new WindowManager.LayoutParams();
        this.mCliLpChanged = new WindowManager.LayoutParams();
        this.mWindowManager = (WindowManager) context2.getSystemService("window");
    }

    public void addCli(View view, int i) {
        this.mCliStatusBarView = view;
        this.mCliBarHeight = i;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, this.mCliBarHeight, 2000, -2139095032, -3);
        this.mCliBarLp = layoutParams;
        layoutParams.privateFlags |= 16777216;
        layoutParams.token = new Binder();
        WindowManager.LayoutParams layoutParams2 = this.mCliBarLp;
        layoutParams2.gravity = 48;
        layoutParams2.setFitInsetsTypes(0);
        this.mCliBarLp.setTitle("CliStatusBar");
        this.mCliBarLp.packageName = this.mCliContext.getPackageName();
        WindowManager.LayoutParams layoutParams3 = this.mCliBarLp;
        layoutParams3.layoutInDisplayCutoutMode = 3;
        this.mWindowManager.addView(this.mCliStatusBarView, layoutParams3);
        this.mCliBarLpChanged.copyFrom(this.mCliBarLp);
    }

    public void setForceStatusBarVisible(boolean z) {
        State state = this.mBarCurrentState;
        state.mForceStatusBarVisible = z;
        applyBar(state);
    }

    private void applyHeight() {
        this.mCliBarLpChanged.height = this.mCliBarHeight;
    }

    private void applyBar(State state) {
        applyForceStatusBarVisibleFlag(state);
        applyHeight();
        WindowManager.LayoutParams layoutParams = this.mCliBarLp;
        if (layoutParams != null && layoutParams.copyFrom(this.mCliBarLpChanged) != 0) {
            this.mWindowManager.updateViewLayout(this.mCliStatusBarView, this.mCliBarLp);
        }
    }

    private void applyForceStatusBarVisibleFlag(State state) {
        if (state.mForceStatusBarVisible) {
            this.mCliBarLpChanged.privateFlags |= 4096;
            return;
        }
        this.mCliBarLpChanged.privateFlags &= -4097;
    }

    private static class State {
        boolean mBouncerShowing;
        boolean mDetailVisible;
        boolean mDozing;
        boolean mForceCollapsed;
        boolean mForceDozeBrightness;
        boolean mForceStatusBarVisible;
        boolean mForceUserActivity;
        boolean mHeadsUpShowing;
        boolean mKeyguardNeedsInput;
        boolean mKeyguardOccluded;
        boolean mKeyguardShowing;
        boolean mLogoVisible;
        boolean mNotificationCardShowing;
        boolean mPanelVisible;
        boolean mRemoteInputActive;
        int mStatusBarState;
        boolean mUnlockCollapsing;

        private State() {
        }

        /* access modifiers changed from: private */
        public boolean isKeyguardShowingAndNotOccluded() {
            return this.mKeyguardShowing && !this.mKeyguardOccluded;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Window State {");
            sb.append("\n");
            for (Field field : State.class.getDeclaredFields()) {
                sb.append("  ");
                try {
                    sb.append(field.getName());
                    sb.append(": ");
                    sb.append(field.get(this));
                } catch (IllegalAccessException unused) {
                }
                sb.append("\n");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public void addCliPanel(View view) {
        this.mCliPanelView = view;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2040, -2138832824, -3);
        this.mCliLp = layoutParams;
        layoutParams.token = new Binder();
        WindowManager.LayoutParams layoutParams2 = this.mCliLp;
        layoutParams2.gravity = 48;
        layoutParams2.setFitInsetsTypes(0);
        WindowManager.LayoutParams layoutParams3 = this.mCliLp;
        layoutParams3.softInputMode = 16;
        layoutParams3.setTitle("CliPanel");
        this.mCliLp.packageName = this.mCliContext.getPackageName();
        WindowManager.LayoutParams layoutParams4 = this.mCliLp;
        layoutParams4.layoutInDisplayCutoutMode = 3;
        layoutParams4.privateFlags |= 134217728;
        layoutParams4.insetsFlags.behavior = 2;
        this.mWindowManager.addView(this.mCliPanelView, layoutParams4);
        this.mCliLpChanged.copyFrom(this.mCliLp);
        if (this.mKeyguardViewMediator.isShowingAndNotOccluded()) {
            setKeyguardShowing(true);
        }
    }

    private void apply(State state) {
        applyKeyguardFlags(state);
        applyModalFlag(state);
        applyForceShowNavigationFlag(state);
        applyVisibility(state);
        applyFocusableFlag(state);
        applyUserActivityTimeout(state);
        applyBrightness(state);
        applyInputFeatures(state);
        WindowManager.LayoutParams layoutParams = this.mCliLp;
        if (!(layoutParams == null || layoutParams.copyFrom(this.mCliLpChanged) == 0)) {
            Log.v("Cli_StatusBarWindowController", "state=" + state.toString());
            this.mWindowManager.updateViewLayout(this.mCliPanelView, this.mCliLp);
            updateNavigationBar();
        }
        notifyStateChangedCallbacks();
    }

    private boolean isExpanded(State state) {
        return !state.mForceCollapsed && (state.isKeyguardShowingAndNotOccluded() || state.mPanelVisible || state.mBouncerShowing || state.mDozing || state.mUnlockCollapsing || state.mHeadsUpShowing || ((state.mLogoVisible && !state.mKeyguardOccluded) || state.mNotificationCardShowing));
    }

    public void setPanelVisible(boolean z) {
        State state = this.mCurrentState;
        state.mPanelVisible = z;
        apply(state);
    }

    public boolean isPanelVisible() {
        return this.mCurrentState.mPanelVisible;
    }

    public void setDetailVisible(boolean z) {
        this.mCurrentState.mDetailVisible = z;
    }

    public boolean isDetailVisible() {
        return this.mCurrentState.mDetailVisible;
    }

    public void setLogoVisible(boolean z) {
        State state = this.mCurrentState;
        state.mLogoVisible = z;
        apply(state);
    }

    public void setHeadsUpShowing(boolean z) {
        State state = this.mCurrentState;
        state.mHeadsUpShowing = z;
        apply(state);
    }

    public void onRemoteInputActive(boolean z) {
        State state = this.mCurrentState;
        state.mRemoteInputActive = z;
        apply(state);
    }

    public boolean isDozing() {
        return this.mCurrentState.mDozing;
    }

    public boolean isKeyguardShowingAndOccluded() {
        State state = this.mCurrentState;
        return state.mKeyguardShowing && state.mKeyguardOccluded;
    }

    public boolean shouldSetTouchRegion() {
        State state = this.mCurrentState;
        if (!state.mPanelVisible && !state.mBouncerShowing && !state.isKeyguardShowingAndNotOccluded()) {
            State state2 = this.mCurrentState;
            return !state2.mLogoVisible && state2.mHeadsUpShowing;
        }
    }

    public boolean getNotificationCardShowing() {
        return this.mCurrentState.mNotificationCardShowing;
    }

    public void setNotificationCardShowing(boolean z) {
        State state = this.mCurrentState;
        state.mNotificationCardShowing = z;
        apply(state);
    }

    public void registerCallback(StatusBarWindowCallback statusBarWindowCallback) {
        int i = 0;
        while (i < this.mCallbacks.size()) {
            if (this.mCallbacks.get(i).get() != statusBarWindowCallback) {
                i++;
            } else {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference(statusBarWindowCallback));
    }

    public void setKeyguardShowing(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardShowing = z;
        apply(state);
    }

    public void updateStatusBarHeight(int i) {
        this.mCliBarHeight = i;
        applyBar(this.mCurrentState);
    }

    public void updateNavigationBar() {
        NavigationBarView navigationBarView = ((NavigationBarController) Dependency.get(NavigationBarController.class)).getNavigationBarView(1);
        if (navigationBarView != null) {
            int i = this.mCliLp.flags;
            if ((i & 8) != 0 || (i & 131072) == 0) {
                State state = this.mCurrentState;
                if (state.mBouncerShowing) {
                    navigationBarView.getRootView().setVisibility(8);
                } else if (state.mDozing) {
                    navigationBarView.setVisibility(8);
                } else {
                    navigationBarView.getRootView().setVisibility(0);
                    navigationBarView.setVisibility(0);
                }
            } else {
                navigationBarView.getRootView().setVisibility(8);
            }
        }
    }

    private void applyKeyguardFlags(State state) {
        if (state.mDozing) {
            this.mCliLpChanged.privateFlags |= 524288;
            return;
        }
        this.mCliLpChanged.privateFlags &= -524289;
    }

    private void applyModalFlag(State state) {
        if (state.mHeadsUpShowing) {
            this.mCliLpChanged.flags |= 32;
            return;
        }
        this.mCliLpChanged.flags &= -33;
    }

    private void applyForceShowNavigationFlag(State state) {
        if (state.mBouncerShowing) {
            this.mCliLpChanged.privateFlags |= 8388608;
            return;
        }
        this.mCliLpChanged.privateFlags &= -8388609;
    }

    private void applyVisibility(State state) {
        if (isExpanded(state)) {
            this.mCliPanelView.setVisibility(0);
        } else {
            this.mCliPanelView.setVisibility(4);
        }
    }

    private void applyFocusableFlag(State state) {
        boolean z = state.mPanelVisible | state.mLogoVisible;
        if ((state.mBouncerShowing && (state.mKeyguardOccluded || state.mKeyguardNeedsInput)) || (NotificationRemoteInputManager.ENABLE_REMOTE_INPUT && state.mRemoteInputActive)) {
            WindowManager.LayoutParams layoutParams = this.mCliLpChanged;
            int i = layoutParams.flags & -9;
            layoutParams.flags = i;
            layoutParams.flags = i & -131073;
        } else if (state.isKeyguardShowingAndNotOccluded() || z) {
            WindowManager.LayoutParams layoutParams2 = this.mCliLpChanged;
            int i2 = layoutParams2.flags & -9;
            layoutParams2.flags = i2;
            layoutParams2.flags = i2 | 131072;
        } else {
            WindowManager.LayoutParams layoutParams3 = this.mCliLpChanged;
            int i3 = layoutParams3.flags | 8;
            layoutParams3.flags = i3;
            layoutParams3.flags = i3 & -131073;
        }
        this.mCliLpChanged.softInputMode = 16;
    }

    private void applyUserActivityTimeout(State state) {
        long j;
        if (!state.isKeyguardShowingAndNotOccluded() || state.mStatusBarState != 1 || state.mPanelVisible) {
            this.mCliLpChanged.userActivityTimeout = -1;
        } else if (state.mHeadsUpShowing) {
            this.mCliLpChanged.userActivityTimeout = 15000;
        } else {
            WindowManager.LayoutParams layoutParams = this.mCliLpChanged;
            if (state.mBouncerShowing) {
                j = 10000;
            } else {
                j = this.mLockScreenDisplayTimeout;
            }
            layoutParams.userActivityTimeout = j;
        }
    }

    private void applyBrightness(State state) {
        if (state.mForceDozeBrightness) {
            this.mCliLpChanged.screenBrightness = this.mScreenBrightnessDoze;
            return;
        }
        this.mCliLpChanged.screenBrightness = -1.0f;
    }

    private void applyInputFeatures(State state) {
        if (!state.isKeyguardShowingAndNotOccluded() || state.mStatusBarState != 1 || state.mPanelVisible || state.mForceUserActivity || state.mRemoteInputActive) {
            this.mCliLpChanged.inputFeatures &= -5;
            return;
        }
        this.mCliLpChanged.inputFeatures |= 4;
    }

    public void notifyStateChangedCallbacks() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            StatusBarWindowCallback statusBarWindowCallback = (StatusBarWindowCallback) this.mCallbacks.get(i).get();
            if (statusBarWindowCallback != null) {
                State state = this.mCurrentState;
                statusBarWindowCallback.onStateChangedForCli(state.mKeyguardShowing, state.mKeyguardOccluded, state.mBouncerShowing, state.mDozing, state.mUnlockCollapsing);
                statusBarWindowCallback.onViewStateChanged(this.mCurrentState.mPanelVisible);
            }
        }
    }

    public void applyToCli(NotificationShadeWindowControllerImpl.State state, float f) {
        this.mScreenBrightnessDoze = f;
        State state2 = this.mCurrentState;
        state2.mBouncerShowing = state.mBouncerShowing;
        state2.mKeyguardShowing = state.mKeyguardShowing;
        state2.mKeyguardOccluded = state.mKeyguardOccluded;
        state2.mForceUserActivity = state.mForceUserActivity;
        state2.mForceDozeBrightness = state.mForceDozeBrightness;
        state2.mStatusBarState = state.mStatusBarState;
        state2.mDozing = state.mDozing;
        if (this.mCliLp != null) {
            apply(state2);
        }
    }

    public void setBiometricUnlockCollapsing(boolean z) {
        State state = this.mCurrentState;
        state.mUnlockCollapsing = z;
        apply(state);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("CliStatusBarWindowController state:");
        printWriter.println(this.mCurrentState);
    }
}
