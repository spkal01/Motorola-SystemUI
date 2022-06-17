package com.android.systemui.statusbar.phone;

import android.app.IActivityManager;
import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.os.Trace;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$integer;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationBar;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NotificationShadeWindowControllerImpl implements NotificationShadeWindowController, Dumpable, ConfigurationController.ConfigurationListener {
    private final IActivityManager mActivityManager;
    private final AuthController mAuthController;
    private final ArrayList<WeakReference<StatusBarWindowCallback>> mCallbacks = new ArrayList<>();
    private CliStatusBarWindowController mCliStatusBarWindowController;
    private final SysuiColorExtractor mColorExtractor;
    private final Context mContext;
    private final State mCurrentState = new State();
    private final DozeParameters mDozeParameters;
    private float mFaceAuthDisplayBrightness;
    private final Set<Object> mForceOpenTokens;
    private NotificationShadeWindowController.ForcePluginOpenListener mForcePluginOpenListener;
    private boolean mHasTopUi;
    private boolean mHasTopUiChanged;
    private final KeyguardBypassController mKeyguardBypassController;
    private final float mKeyguardMaxRefreshRate;
    private final float mKeyguardPreferredRefreshRate;
    private final boolean mKeyguardScreenRotation;
    private final KeyguardViewMediator mKeyguardViewMediator;
    private NotificationShadeWindowController.OtherwisedCollapsedListener mListener;
    private final long mLockScreenDisplayTimeout;
    private WindowManager.LayoutParams mLp;
    private final WindowManager.LayoutParams mLpChanged;
    private ViewGroup mNotificationShadeView;
    private float mScreenBrightnessDoze;
    private Consumer<Integer> mScrimsVisibilityListener;
    private final StatusBarStateController.StateListener mStateListener;
    private final UnlockedScreenOffAnimationController mUnlockedScreenOffAnimationController;
    private final WindowManager mWindowManager;

    public NotificationShadeWindowControllerImpl(Context context, WindowManager windowManager, IActivityManager iActivityManager, DozeParameters dozeParameters, StatusBarStateController statusBarStateController, ConfigurationController configurationController, KeyguardViewMediator keyguardViewMediator, KeyguardBypassController keyguardBypassController, SysuiColorExtractor sysuiColorExtractor, DumpManager dumpManager, KeyguardStateController keyguardStateController, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController, AuthController authController) {
        float f = -1.0f;
        this.mFaceAuthDisplayBrightness = -1.0f;
        this.mForceOpenTokens = new HashSet();
        C18671 r1 = new StatusBarStateController.StateListener() {
            public void onStateChanged(int i) {
                NotificationShadeWindowControllerImpl.this.setStatusBarState(i);
            }

            public void onDozingChanged(boolean z) {
                NotificationShadeWindowControllerImpl.this.setDozing(z);
            }
        };
        this.mStateListener = r1;
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mActivityManager = iActivityManager;
        this.mKeyguardScreenRotation = keyguardStateController.isKeyguardScreenRotationAllowed();
        this.mDozeParameters = dozeParameters;
        this.mScreenBrightnessDoze = dozeParameters.getScreenBrightnessDoze();
        this.mLpChanged = new WindowManager.LayoutParams();
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mColorExtractor = sysuiColorExtractor;
        this.mUnlockedScreenOffAnimationController = unlockedScreenOffAnimationController;
        dumpManager.registerDumpable(NotificationShadeWindowControllerImpl.class.getName(), this);
        this.mAuthController = authController;
        this.mLockScreenDisplayTimeout = (long) context.getResources().getInteger(R$integer.config_lockScreenDisplayTimeout);
        ((SysuiStatusBarStateController) statusBarStateController).addCallback(r1, 1);
        configurationController.addCallback(this);
        float integer = (float) context.getResources().getInteger(R$integer.config_keyguardRefreshRate);
        if (integer > -1.0f) {
            Display.Mode[] supportedModes = context.getDisplay().getSupportedModes();
            int length = supportedModes.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Display.Mode mode = supportedModes[i];
                if (((double) Math.abs(mode.getRefreshRate() - integer)) <= 0.1d) {
                    f = mode.getRefreshRate();
                    break;
                }
                i++;
            }
        }
        this.mKeyguardPreferredRefreshRate = f;
        this.mKeyguardMaxRefreshRate = (float) context.getResources().getInteger(R$integer.config_keyguardMaxRefreshRate);
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            this.mCliStatusBarWindowController = (CliStatusBarWindowController) Dependency.get(CliStatusBarWindowController.class);
        }
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

    public void setScrimsVisibilityListener(Consumer<Integer> consumer) {
        if (consumer != null && this.mScrimsVisibilityListener != consumer) {
            this.mScrimsVisibilityListener = consumer;
        }
    }

    public void attach() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2040, -2138832824, -3);
        this.mLp = layoutParams;
        layoutParams.token = new Binder();
        WindowManager.LayoutParams layoutParams2 = this.mLp;
        layoutParams2.gravity = 48;
        layoutParams2.setFitInsetsTypes(0);
        WindowManager.LayoutParams layoutParams3 = this.mLp;
        layoutParams3.softInputMode = 16;
        layoutParams3.setTitle("NotificationShade");
        this.mLp.packageName = this.mContext.getPackageName();
        WindowManager.LayoutParams layoutParams4 = this.mLp;
        layoutParams4.layoutInDisplayCutoutMode = 3;
        layoutParams4.privateFlags |= 134217728;
        layoutParams4.insetsFlags.behavior = 2;
        this.mWindowManager.addView(this.mNotificationShadeView, layoutParams4);
        this.mLpChanged.copyFrom(this.mLp);
        onThemeChanged();
        if (this.mKeyguardViewMediator.isShowingAndNotOccluded()) {
            setKeyguardShowing(true);
        }
    }

    public void setNotificationShadeView(ViewGroup viewGroup) {
        this.mNotificationShadeView = viewGroup;
    }

    public void setDozeScreenBrightness(int i) {
        this.mScreenBrightnessDoze = ((float) i) / 255.0f;
    }

    public void setFaceAuthDisplayBrightness(float f) {
        this.mFaceAuthDisplayBrightness = f;
        apply(this.mCurrentState);
    }

    private void setKeyguardDark(boolean z) {
        int systemUiVisibility = this.mNotificationShadeView.getSystemUiVisibility();
        this.mNotificationShadeView.setSystemUiVisibility(z ? systemUiVisibility | 16 | 8192 : systemUiVisibility & -17 & -8193);
    }

    private void applyKeyguardFlags(State state) {
        boolean z = false;
        boolean z2 = state.mScrimsVisibility == 2 || state.mLightRevealScrimOpaque;
        if ((!(state.mKeyguardShowing || (state.mDozing && this.mDozeParameters.getAlwaysOn())) || state.mBackdropShowing || z2) && !this.mKeyguardViewMediator.isAnimatingBetweenKeyguardAndSurfaceBehindOrWillBe()) {
            this.mLpChanged.flags &= -1048577;
        } else {
            this.mLpChanged.flags |= 1048576;
        }
        if (state.mDozing) {
            this.mLpChanged.privateFlags |= 524288;
        } else {
            this.mLpChanged.privateFlags &= -524289;
        }
        if (this.mKeyguardPreferredRefreshRate > 0.0f) {
            if (state.mStatusBarState == 1 && !state.mKeyguardFadingAway && !state.mKeyguardGoingAway) {
                z = true;
            }
            if (!z || !this.mAuthController.isUdfpsEnrolled(KeyguardUpdateMonitor.getCurrentUser())) {
                WindowManager.LayoutParams layoutParams = this.mLpChanged;
                layoutParams.preferredMaxDisplayRefreshRate = 0.0f;
                layoutParams.preferredMinDisplayRefreshRate = 0.0f;
            } else {
                WindowManager.LayoutParams layoutParams2 = this.mLpChanged;
                float f = this.mKeyguardPreferredRefreshRate;
                layoutParams2.preferredMaxDisplayRefreshRate = f;
                layoutParams2.preferredMinDisplayRefreshRate = f;
            }
            Trace.setCounter("display_set_preferred_refresh_rate", (long) this.mKeyguardPreferredRefreshRate);
        } else if (this.mKeyguardMaxRefreshRate > 0.0f) {
            if (this.mKeyguardBypassController.getBypassEnabled() && state.mStatusBarState == 1 && !state.mKeyguardFadingAway && !state.mKeyguardGoingAway) {
                z = true;
            }
            if (state.mDozing || z) {
                this.mLpChanged.preferredMaxDisplayRefreshRate = this.mKeyguardMaxRefreshRate;
            } else {
                this.mLpChanged.preferredMaxDisplayRefreshRate = 0.0f;
            }
            Trace.setCounter("display_max_refresh_rate", (long) this.mLpChanged.preferredMaxDisplayRefreshRate);
        }
    }

    private void adjustScreenOrientation(State state) {
        if (!state.isKeyguardShowingAndNotOccluded() && !state.mDozing) {
            this.mLpChanged.screenOrientation = -1;
        } else if (this.mKeyguardScreenRotation) {
            this.mLpChanged.screenOrientation = 2;
        } else {
            this.mLpChanged.screenOrientation = 5;
        }
    }

    private void applyFocusableFlag(State state) {
        boolean z = state.mNotificationShadeFocusable && state.mPanelExpanded;
        if ((state.mBouncerShowing && (state.mKeyguardOccluded || state.mKeyguardNeedsInput)) || ((NotificationRemoteInputManager.ENABLE_REMOTE_INPUT && state.mRemoteInputActive) || this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying())) {
            WindowManager.LayoutParams layoutParams = this.mLpChanged;
            int i = layoutParams.flags & -9;
            layoutParams.flags = i;
            layoutParams.flags = i & -131073;
        } else if (state.isKeyguardShowingAndNotOccluded() || z) {
            this.mLpChanged.flags &= -9;
            if (!state.mKeyguardNeedsInput || !state.isKeyguardShowingAndNotOccluded()) {
                this.mLpChanged.flags |= 131072;
            } else {
                this.mLpChanged.flags &= -131073;
            }
        } else {
            WindowManager.LayoutParams layoutParams2 = this.mLpChanged;
            int i2 = layoutParams2.flags | 8;
            layoutParams2.flags = i2;
            layoutParams2.flags = i2 & -131073;
        }
        this.mLpChanged.softInputMode = 16;
    }

    private void applyForceShowNavigationFlag(State state) {
        if (state.mDozing || (!state.mPanelExpanded && !state.mBouncerShowing && (!NotificationRemoteInputManager.ENABLE_REMOTE_INPUT || !state.mRemoteInputActive))) {
            this.mLpChanged.privateFlags &= -8388609;
            return;
        }
        this.mLpChanged.privateFlags |= 8388608;
    }

    private void applyVisibility(State state) {
        boolean isExpanded = isExpanded(state);
        if (state.mForcePluginOpen) {
            NotificationShadeWindowController.OtherwisedCollapsedListener otherwisedCollapsedListener = this.mListener;
            if (otherwisedCollapsedListener != null) {
                otherwisedCollapsedListener.setWouldOtherwiseCollapse(isExpanded);
            }
            isExpanded = true;
        }
        ViewGroup viewGroup = this.mNotificationShadeView;
        if (viewGroup == null) {
            return;
        }
        if (isExpanded) {
            viewGroup.setVisibility(0);
        } else {
            viewGroup.setVisibility(4);
        }
    }

    private boolean isExpanded(State state) {
        return (!state.mForceCollapsed && (state.isKeyguardShowingAndNotOccluded() || state.mPanelVisible || state.mKeyguardFadingAway || state.mBouncerShowing || state.mHeadsUpShowing || state.mScrimsVisibility != 0)) || state.mBackgroundBlurRadius > 0 || state.mLaunchingActivity;
    }

    private void applyFitsSystemWindows(State state) {
        boolean z = !state.isKeyguardShowingAndNotOccluded();
        ViewGroup viewGroup = this.mNotificationShadeView;
        if (viewGroup != null && viewGroup.getFitsSystemWindows() != z) {
            this.mNotificationShadeView.setFitsSystemWindows(z);
            this.mNotificationShadeView.requestApplyInsets();
        }
    }

    private void applyUserActivityTimeout(State state) {
        long j;
        if (!state.isKeyguardShowingAndNotOccluded() || state.mStatusBarState != 1 || state.mQsExpanded) {
            this.mLpChanged.userActivityTimeout = -1;
            return;
        }
        WindowManager.LayoutParams layoutParams = this.mLpChanged;
        if (state.mBouncerShowing) {
            j = 10000;
        } else {
            j = this.mLockScreenDisplayTimeout;
        }
        layoutParams.userActivityTimeout = j;
    }

    private void applyInputFeatures(State state) {
        if (!state.isKeyguardShowingAndNotOccluded() || state.mStatusBarState != 1 || state.mQsExpanded || state.mForceUserActivity) {
            this.mLpChanged.inputFeatures &= -5;
            return;
        }
        this.mLpChanged.inputFeatures |= 4;
    }

    private void applyStatusBarColorSpaceAgnosticFlag(State state) {
        if (!isExpanded(state)) {
            this.mLpChanged.privateFlags |= 16777216;
            return;
        }
        this.mLpChanged.privateFlags &= -16777217;
    }

    private void applyNavigationBarStyle(State state) {
        NavigationBar defaultNavigationBar = ((NavigationBarController) Dependency.get(NavigationBarController.class)).getDefaultNavigationBar();
        if (defaultNavigationBar != null) {
            if (state.mPanelExpanded || state.mBouncerShowing) {
                defaultNavigationBar.setForceSampling(true);
            } else {
                defaultNavigationBar.setForceSampling(false);
            }
        }
    }

    private void apply(State state) {
        applyKeyguardFlags(state);
        applyFocusableFlag(state);
        applyForceShowNavigationFlag(state);
        adjustScreenOrientation(state);
        applyVisibility(state);
        applyUserActivityTimeout(state);
        applyInputFeatures(state);
        applyFitsSystemWindows(state);
        applyModalFlag(state);
        applyBrightness(state);
        applyHasTopUi(state);
        applyNotTouchable(state);
        applyStatusBarColorSpaceAgnosticFlag(state);
        applyNavigationBarStyle(state);
        WindowManager.LayoutParams layoutParams = this.mLp;
        if (!(layoutParams == null || layoutParams.copyFrom(this.mLpChanged) == 0)) {
            this.mWindowManager.updateViewLayout(this.mNotificationShadeView, this.mLp);
        }
        if (this.mHasTopUi != this.mHasTopUiChanged) {
            DejankUtils.whitelistIpcs((Runnable) new NotificationShadeWindowControllerImpl$$ExternalSyntheticLambda0(this));
        }
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            this.mCliStatusBarWindowController.applyToCli(state, this.mScreenBrightnessDoze);
        }
        notifyStateChangedCallbacks();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$apply$0() {
        try {
            this.mActivityManager.setHasTopUi(this.mHasTopUiChanged);
        } catch (RemoteException e) {
            Log.e("NotificationShadeWindowController", "Failed to call setHasTopUi", e);
        }
        this.mHasTopUi = this.mHasTopUiChanged;
    }

    public void notifyStateChangedCallbacks() {
        for (StatusBarWindowCallback onStateChanged : (List) this.mCallbacks.stream().map(NotificationShadeWindowControllerImpl$$ExternalSyntheticLambda1.INSTANCE).filter(NotificationShadeWindowControllerImpl$$ExternalSyntheticLambda2.INSTANCE).collect(Collectors.toList())) {
            State state = this.mCurrentState;
            onStateChanged.onStateChanged(state.mKeyguardShowing, state.mKeyguardOccluded, state.mBouncerShowing);
        }
    }

    private void applyModalFlag(State state) {
        if (state.mHeadsUpShowing) {
            this.mLpChanged.flags |= 32;
            return;
        }
        this.mLpChanged.flags &= -33;
    }

    private void applyBrightness(State state) {
        if (state.mForceDozeBrightness) {
            this.mLpChanged.screenBrightness = this.mScreenBrightnessDoze;
            return;
        }
        this.mLpChanged.screenBrightness = this.mFaceAuthDisplayBrightness;
    }

    private void applyHasTopUi(State state) {
        this.mHasTopUiChanged = !state.mComponentsForcingTopUi.isEmpty() || isExpanded(state);
    }

    private void applyNotTouchable(State state) {
        if (state.mNotTouchable) {
            this.mLpChanged.flags |= 16;
            return;
        }
        this.mLpChanged.flags &= -17;
    }

    public void setKeyguardShowing(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardShowing = z;
        apply(state);
    }

    public void setKeyguardOccluded(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardOccluded = z;
        apply(state);
    }

    public void setKeyguardNeedsInput(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardNeedsInput = z;
        apply(state);
    }

    public void setPanelVisible(boolean z) {
        State state = this.mCurrentState;
        state.mPanelVisible = z;
        state.mNotificationShadeFocusable = z;
        apply(state);
    }

    public void setNotificationShadeFocusable(boolean z) {
        State state = this.mCurrentState;
        state.mNotificationShadeFocusable = z;
        apply(state);
    }

    public void setBouncerShowing(boolean z) {
        State state = this.mCurrentState;
        state.mBouncerShowing = z;
        apply(state);
    }

    public void setBackdropShowing(boolean z) {
        State state = this.mCurrentState;
        state.mBackdropShowing = z;
        apply(state);
    }

    public void setKeyguardFadingAway(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardFadingAway = z;
        apply(state);
    }

    public void setQsExpanded(boolean z) {
        State state = this.mCurrentState;
        state.mQsExpanded = z;
        apply(state);
    }

    public void setLaunchingActivity(boolean z) {
        State state = this.mCurrentState;
        state.mLaunchingActivity = z;
        apply(state);
    }

    public boolean isLaunchingActivity() {
        return this.mCurrentState.mLaunchingActivity;
    }

    public void setScrimsVisibility(int i) {
        State state = this.mCurrentState;
        state.mScrimsVisibility = i;
        apply(state);
        this.mScrimsVisibilityListener.accept(Integer.valueOf(i));
    }

    public void setBackgroundBlurRadius(int i) {
        State state = this.mCurrentState;
        if (state.mBackgroundBlurRadius != i) {
            state.mBackgroundBlurRadius = i;
            apply(state);
        }
    }

    public void setHeadsUpShowing(boolean z) {
        State state = this.mCurrentState;
        state.mHeadsUpShowing = z;
        apply(state);
    }

    public void setLightRevealScrimAmount(float f) {
        boolean z = f == 0.0f;
        State state = this.mCurrentState;
        if (state.mLightRevealScrimOpaque != z) {
            state.mLightRevealScrimOpaque = z;
            apply(state);
        }
    }

    public void setWallpaperSupportsAmbientMode(boolean z) {
        State state = this.mCurrentState;
        state.mWallpaperSupportsAmbientMode = z;
        apply(state);
    }

    /* access modifiers changed from: private */
    public void setStatusBarState(int i) {
        State state = this.mCurrentState;
        state.mStatusBarState = i;
        apply(state);
    }

    public void setForceWindowCollapsed(boolean z) {
        State state = this.mCurrentState;
        state.mForceCollapsed = z;
        apply(state);
    }

    public void setPanelExpanded(boolean z) {
        State state = this.mCurrentState;
        state.mPanelExpanded = z;
        apply(state);
    }

    public void onRemoteInputActive(boolean z) {
        State state = this.mCurrentState;
        state.mRemoteInputActive = z;
        apply(state);
    }

    public void setForceDozeBrightness(boolean z) {
        State state = this.mCurrentState;
        state.mForceDozeBrightness = z;
        apply(state);
    }

    public void setDozing(boolean z) {
        State state = this.mCurrentState;
        state.mDozing = z;
        apply(state);
    }

    public void setForcePluginOpen(boolean z, Object obj) {
        if (z) {
            this.mForceOpenTokens.add(obj);
        } else {
            this.mForceOpenTokens.remove(obj);
        }
        State state = this.mCurrentState;
        boolean z2 = state.mForcePluginOpen;
        state.mForcePluginOpen = !this.mForceOpenTokens.isEmpty();
        State state2 = this.mCurrentState;
        if (z2 != state2.mForcePluginOpen) {
            apply(state2);
            NotificationShadeWindowController.ForcePluginOpenListener forcePluginOpenListener = this.mForcePluginOpenListener;
            if (forcePluginOpenListener != null) {
                forcePluginOpenListener.onChange(this.mCurrentState.mForcePluginOpen);
            }
        }
    }

    public boolean getForcePluginOpen() {
        return this.mCurrentState.mForcePluginOpen;
    }

    public void setNotTouchable(boolean z) {
        State state = this.mCurrentState;
        state.mNotTouchable = z;
        apply(state);
    }

    public boolean getPanelExpanded() {
        return this.mCurrentState.mPanelExpanded;
    }

    public void setStateListener(NotificationShadeWindowController.OtherwisedCollapsedListener otherwisedCollapsedListener) {
        this.mListener = otherwisedCollapsedListener;
    }

    public void setForcePluginOpenListener(NotificationShadeWindowController.ForcePluginOpenListener forcePluginOpenListener) {
        this.mForcePluginOpenListener = forcePluginOpenListener;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationShadeWindowController:");
        printWriter.println("  mKeyguardMaxRefreshRate=" + this.mKeyguardMaxRefreshRate);
        printWriter.println("  mKeyguardPreferredRefreshRate=" + this.mKeyguardPreferredRefreshRate);
        printWriter.println(this.mCurrentState);
        ViewGroup viewGroup = this.mNotificationShadeView;
        if (viewGroup != null && viewGroup.getViewRootImpl() != null) {
            this.mNotificationShadeView.getViewRootImpl().dump("  ", printWriter);
        }
    }

    public boolean isShowingWallpaper() {
        return !this.mCurrentState.mBackdropShowing;
    }

    public void onThemeChanged() {
        if (this.mNotificationShadeView != null) {
            setKeyguardDark(this.mColorExtractor.getNeutralColors().supportsDarkText());
        }
    }

    public void setKeyguardGoingAway(boolean z) {
        State state = this.mCurrentState;
        state.mKeyguardGoingAway = z;
        apply(state);
    }

    public void setRequestTopUi(boolean z, String str) {
        if (z) {
            this.mCurrentState.mComponentsForcingTopUi.add(str);
        } else {
            this.mCurrentState.mComponentsForcingTopUi.remove(str);
        }
        apply(this.mCurrentState);
    }

    public static class State {
        boolean mBackdropShowing;
        int mBackgroundBlurRadius;
        boolean mBouncerShowing;
        Set<String> mComponentsForcingTopUi = new HashSet();
        boolean mDozing;
        boolean mForceCollapsed;
        boolean mForceDozeBrightness;
        boolean mForcePluginOpen;
        boolean mForceUserActivity;
        boolean mHeadsUpShowing;
        boolean mKeyguardFadingAway;
        boolean mKeyguardGoingAway;
        boolean mKeyguardNeedsInput;
        boolean mKeyguardOccluded;
        boolean mKeyguardShowing;
        boolean mLaunchingActivity;
        boolean mLightRevealScrimOpaque;
        boolean mNotTouchable;
        boolean mNotificationShadeFocusable;
        boolean mPanelExpanded;
        boolean mPanelVisible;
        boolean mQsExpanded;
        boolean mRemoteInputActive;
        int mScrimsVisibility;
        int mStatusBarState;
        boolean mWallpaperSupportsAmbientMode;

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
}
