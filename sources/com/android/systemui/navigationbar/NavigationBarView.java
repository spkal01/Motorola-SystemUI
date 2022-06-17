package com.android.systemui.navigationbar;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.pip.Pip;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.R$attr;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.model.SysUiState;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.navigationbar.buttons.ButtonDispatcher;
import com.android.systemui.navigationbar.buttons.ContextualButton;
import com.android.systemui.navigationbar.buttons.ContextualButtonGroup;
import com.android.systemui.navigationbar.buttons.DeadZone;
import com.android.systemui.navigationbar.buttons.KeyButtonDrawable;
import com.android.systemui.navigationbar.buttons.NearestTouchFrame;
import com.android.systemui.navigationbar.buttons.RotationContextButton;
import com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler;
import com.android.systemui.navigationbar.gestural.FloatingRotationButton;
import com.android.systemui.navigationbar.gestural.RegionSamplingHelper;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.Recents;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.LightBarTransitionsController;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.motorola.systemui.cli.navgesture.CliNavGestureController;
import com.motorola.systemui.desktop.widget.DesktopNavGuideView;
import com.motorola.taskbar.MotoTaskBarController;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class NavigationBarView extends FrameLayout implements NavigationModeController.ModeChangedListener {
    private AutoHideController mAutoHideController;
    private float mBackDegrees;
    private KeyButtonDrawable mBackIcon;
    private final NavigationBarTransitions mBarTransitions;
    private final SparseArray<ButtonDispatcher> mButtonDispatchers;
    private Map<View, Rect> mButtonFullTouchableRegions = new HashMap();
    private ViewGroup mCliNavArea;
    private CliNavGestureController mCliNavGestureController;
    private Configuration mConfiguration;
    private final ContextualButtonGroup mContextualButtonGroup;
    private int mCurrentRotation = -1;
    View mCurrentView = null;
    private int mDarkIconColor;
    private final DeadZone mDeadZone;
    private boolean mDeadZoneConsuming = false;
    private DesktopNavGuideView mDesktopNavGuideView;
    int mDisabledFlags = 0;
    private KeyButtonDrawable mDockedIcon;
    private final Consumer<Boolean> mDockedListener;
    private boolean mDockedStackExists;
    private EdgeBackGestureHandler mEdgeBackGestureHandler;
    private FloatingRotationButton mFloatingRotationButton;
    /* access modifiers changed from: private */
    public boolean mForceSampling = false;
    private KeyButtonDrawable mHomeDefaultIcon;
    private View mHorizontal;
    private boolean mImeVisible;
    private boolean mInCarMode = false;
    private boolean mIsMotoTaskBarAvailable;
    private boolean mIsVertical;
    private boolean mLayoutTransitionsEnabled = true;
    private Context mLightContext;
    private int mLightIconColor;
    boolean mLongClickableAccessibilityButton;
    /* access modifiers changed from: private */
    public int mNavBarMode;
    private NavigationBarOverlayController mNavBarOverlayController;
    private final int mNavColorSampleMargin;
    private final Consumer<Boolean> mNavbarOverlayVisibilityChangeCallback;
    int mNavigationIconHints = 0;
    private NavigationBarInflaterView mNavigationInflaterView;
    private final ViewTreeObserver.OnComputeInternalInsetsListener mOnComputeInternalInsetsListener;
    private OnVerticalChangedListener mOnVerticalChangedListener;
    /* access modifiers changed from: private */
    public Rect mOrientedHandleSamplingRegion;
    private final OverviewProxyService mOverviewProxyService;
    private int mPaddingBottom;
    private NotificationPanelViewController mPanelView;
    private final Consumer<Rect> mPipListener;
    private final View.AccessibilityDelegate mQuickStepAccessibilityDelegate;
    private KeyButtonDrawable mRecentIcon;
    private final RegionSamplingHelper mRegionSamplingHelper;
    private RotationButtonController mRotationButtonController;
    private final Consumer<Boolean> mRotationButtonListener;
    private RotationContextButton mRotationContextButton;
    /* access modifiers changed from: private */
    public Rect mSamplingBounds;
    private boolean mScreenOn = true;
    private ScreenPinningNotify mScreenPinningNotify;
    private final SysUiState mSysUiFlagContainer;
    private Rect mTmpBounds = new Rect();
    private Configuration mTmpLastConfiguration;
    private final int[] mTmpPosition = new int[2];
    private final Region mTmpRegion = new Region();
    private final NavTransitionListener mTransitionListener = new NavTransitionListener();
    private boolean mUseCarModeUi = false;
    private View mVertical;
    private boolean mWakeAndUnlocking;

    public interface OnVerticalChangedListener {
        void onVerticalChanged(boolean z);
    }

    private static String visibilityToString(int i) {
        return i != 4 ? i != 8 ? "VISIBLE" : "GONE" : "INVISIBLE";
    }

    private class NavTransitionListener implements LayoutTransition.TransitionListener {
        private boolean mBackTransitioning;
        private long mDuration;
        private boolean mHomeAppearing;
        private TimeInterpolator mInterpolator;
        private long mStartDelay;

        private NavTransitionListener() {
        }

        public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            if (view.getId() == R$id.back) {
                this.mBackTransitioning = true;
            } else if (view.getId() == R$id.home && i == 2) {
                this.mHomeAppearing = true;
                this.mStartDelay = layoutTransition.getStartDelay(i);
                this.mDuration = layoutTransition.getDuration(i);
                this.mInterpolator = layoutTransition.getInterpolator(i);
            }
        }

        public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            if (view.getId() == R$id.back) {
                this.mBackTransitioning = false;
            } else if (view.getId() == R$id.home && i == 2) {
                this.mHomeAppearing = false;
            }
        }

        public void onBackAltCleared() {
            ButtonDispatcher backButton = NavigationBarView.this.getBackButton();
            if (!this.mBackTransitioning && backButton.getVisibility() == 0 && this.mHomeAppearing && NavigationBarView.this.getHomeButton().getAlpha() == 0.0f) {
                NavigationBarView.this.getBackButton().setAlpha(0.0f);
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(backButton, "alpha", new float[]{0.0f, 1.0f});
                ofFloat.setStartDelay(this.mStartDelay);
                ofFloat.setDuration(this.mDuration);
                ofFloat.setInterpolator(this.mInterpolator);
                ofFloat.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
        boolean z = true;
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || getContext().getDisplayId() != 1) {
            z = false;
        }
        if (!this.mEdgeBackGestureHandler.isHandlingGestures() || (z && this.mImeVisible)) {
            internalInsetsInfo.setTouchableInsets(0);
            return;
        }
        internalInsetsInfo.setTouchableInsets(3);
        internalInsetsInfo.touchableRegion.set(getButtonLocations(false, false, false));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Boolean bool) {
        if (bool.booleanValue()) {
            this.mAutoHideController.touchAutoHide();
        }
        notifyActiveTouchRegions();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(Boolean bool) {
        if (bool.booleanValue()) {
            this.mAutoHideController.touchAutoHide();
        }
        notifyActiveTouchRegions();
    }

    public NavigationBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        SparseArray<ButtonDispatcher> sparseArray = new SparseArray<>();
        this.mButtonDispatchers = sparseArray;
        this.mSamplingBounds = new Rect();
        this.mQuickStepAccessibilityDelegate = new View.AccessibilityDelegate() {
            private AccessibilityNodeInfo.AccessibilityAction mToggleOverviewAction;

            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                if (this.mToggleOverviewAction == null) {
                    this.mToggleOverviewAction = new AccessibilityNodeInfo.AccessibilityAction(R$id.action_toggle_overview, NavigationBarView.this.getContext().getString(R$string.quick_step_accessibility_toggle_overview));
                }
                accessibilityNodeInfo.addAction(this.mToggleOverviewAction);
            }

            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                if (i != R$id.action_toggle_overview) {
                    return super.performAccessibilityAction(view, i, bundle);
                }
                ((Recents) Dependency.get(Recents.class)).toggleRecentApps();
                return true;
            }
        };
        this.mOnComputeInternalInsetsListener = new NavigationBarView$$ExternalSyntheticLambda0(this);
        this.mRotationButtonListener = new NavigationBarView$$ExternalSyntheticLambda7(this);
        NavigationBarView$$ExternalSyntheticLambda9 navigationBarView$$ExternalSyntheticLambda9 = new NavigationBarView$$ExternalSyntheticLambda9(this);
        this.mNavbarOverlayVisibilityChangeCallback = navigationBarView$$ExternalSyntheticLambda9;
        this.mCliNavArea = null;
        this.mDockedListener = new NavigationBarView$$ExternalSyntheticLambda8(this);
        this.mPipListener = new NavigationBarView$$ExternalSyntheticLambda6(this);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.darkIconTheme));
        ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R$attr.lightIconTheme));
        this.mLightContext = contextThemeWrapper2;
        int i = R$attr.singleToneColor;
        this.mLightIconColor = Utils.getColorAttrDefaultColor(contextThemeWrapper2, i);
        this.mDarkIconColor = Utils.getColorAttrDefaultColor(contextThemeWrapper, i);
        this.mIsVertical = false;
        this.mLongClickableAccessibilityButton = false;
        this.mNavBarMode = ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this);
        this.mIsMotoTaskBarAvailable = ((MotoTaskBarController) Dependency.get(MotoTaskBarController.class)).isMotoTaskBarAvailable();
        this.mSysUiFlagContainer = (SysUiState) Dependency.get(SysUiState.class);
        int i2 = R$id.menu_container;
        ContextualButtonGroup contextualButtonGroup = new ContextualButtonGroup(i2);
        this.mContextualButtonGroup = contextualButtonGroup;
        int i3 = R$id.ime_switcher;
        ContextualButton contextualButton = new ContextualButton(i3, this.mLightContext, R$drawable.ic_ime_switcher_default);
        int i4 = R$id.accessibility_button;
        ContextualButton contextualButton2 = new ContextualButton(i4, this.mLightContext, R$drawable.ic_sysbar_accessibility_button);
        contextualButtonGroup.addButton(contextualButton);
        contextualButtonGroup.addButton(contextualButton2);
        this.mRotationContextButton = new RotationContextButton(R$id.rotate_suggestion, this.mLightContext, R$drawable.ic_sysbar_rotate_button_ccw_start_0);
        this.mFloatingRotationButton = new FloatingRotationButton(context);
        this.mRotationButtonController = new RotationButtonController(this.mLightContext, this.mLightIconColor, this.mDarkIconColor);
        updateRotationButton();
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
        this.mConfiguration = new Configuration();
        this.mTmpLastConfiguration = new Configuration();
        this.mConfiguration.updateFrom(context.getResources().getConfiguration());
        this.mScreenPinningNotify = new ScreenPinningNotify(this.mContext);
        this.mBarTransitions = new NavigationBarTransitions(this, (CommandQueue) Dependency.get(CommandQueue.class));
        int i5 = R$id.back;
        sparseArray.put(i5, new ButtonDispatcher(i5));
        int i6 = R$id.home;
        sparseArray.put(i6, new ButtonDispatcher(i6));
        int i7 = R$id.home_handle;
        sparseArray.put(i7, new ButtonDispatcher(i7));
        int i8 = R$id.recent_apps;
        sparseArray.put(i8, new ButtonDispatcher(i8));
        sparseArray.put(i3, contextualButton);
        sparseArray.put(i4, contextualButton2);
        sparseArray.put(i2, contextualButtonGroup);
        if (this.mIsMotoTaskBarAvailable) {
            int i9 = R$id.trackpad;
            ButtonDispatcher buttonDispatcher = new ButtonDispatcher(i9);
            buttonDispatcher.setImageDrawable(getDrawable(R$drawable.zz_moto_ic_trackpad));
            sparseArray.put(i9, buttonDispatcher);
        }
        this.mDeadZone = new DeadZone(this);
        this.mNavColorSampleMargin = getResources().getDimensionPixelSize(R$dimen.navigation_handle_sample_horizontal_margin);
        EdgeBackGestureHandler create = ((EdgeBackGestureHandler.Factory) Dependency.get(EdgeBackGestureHandler.Factory.class)).create(this.mContext);
        this.mEdgeBackGestureHandler = create;
        create.setStateChangeCallback(new NavigationBarView$$ExternalSyntheticLambda1(this));
        this.mRegionSamplingHelper = new RegionSamplingHelper(this, new RegionSamplingHelper.SamplingCallback() {
            public void onRegionDarknessChanged(boolean z) {
                NavigationBarView.this.getLightTransitionsController().setIconsDark(!z, true);
            }

            public Rect getSampledRegion(View view) {
                if (NavigationBarView.this.mOrientedHandleSamplingRegion != null) {
                    return NavigationBarView.this.mOrientedHandleSamplingRegion;
                }
                NavigationBarView.this.updateSamplingRect();
                return NavigationBarView.this.mSamplingBounds;
            }

            public boolean isSamplingEnabled() {
                return com.android.systemui.util.Utils.isGesturalModeOnDefaultDisplay(NavigationBarView.this.getContext(), NavigationBarView.this.mNavBarMode) || NavigationBarView.this.mForceSampling;
            }
        });
        this.mPaddingBottom = getResources().getDimensionPixelSize(R$dimen.navigation_bar_padding_bottom);
        NavigationBarOverlayController navigationBarOverlayController = (NavigationBarOverlayController) Dependency.get(NavigationBarOverlayController.class);
        this.mNavBarOverlayController = navigationBarOverlayController;
        if (navigationBarOverlayController.isNavigationBarOverlayEnabled()) {
            NavigationBarOverlayController navigationBarOverlayController2 = this.mNavBarOverlayController;
            EdgeBackGestureHandler edgeBackGestureHandler = this.mEdgeBackGestureHandler;
            Objects.requireNonNull(edgeBackGestureHandler);
            navigationBarOverlayController2.init(navigationBarView$$ExternalSyntheticLambda9, new NavigationBarView$$ExternalSyntheticLambda10(edgeBackGestureHandler));
        }
        if (MotoFeature.getInstance(this.mContext).isSupportCli() && getContext().getDisplayId() == 1) {
            this.mCliNavGestureController = (CliNavGestureController) Dependency.get(CliNavGestureController.class);
        }
    }

    /* access modifiers changed from: package-private */
    public void setForceSampling(boolean z) {
        this.mForceSampling = z;
        setNavigationBarLumaSamplingEnabled(z || QuickStepContract.isGesturalMode(this.mNavBarMode));
    }

    public void setAutoHideController(AutoHideController autoHideController) {
        this.mAutoHideController = autoHideController;
    }

    public NavigationBarTransitions getBarTransitions() {
        return this.mBarTransitions;
    }

    public LightBarTransitionsController getLightTransitionsController() {
        return this.mBarTransitions.getLightTransitionsController();
    }

    public void setComponents(NotificationPanelViewController notificationPanelViewController) {
        this.mPanelView = notificationPanelViewController;
        updatePanelSystemUiStateFlags();
    }

    public void setOnVerticalChangedListener(OnVerticalChangedListener onVerticalChangedListener) {
        this.mOnVerticalChangedListener = onVerticalChangedListener;
        notifyVerticalChangedListener(this.mIsVertical);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (QuickStepContract.isGesturalMode(this.mNavBarMode) && this.mImeVisible && motionEvent.getAction() == 0) {
            SysUiStatsLog.write(304, (int) motionEvent.getX(), (int) motionEvent.getY());
        }
        return shouldDeadZoneConsumeTouchEvents(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        shouldDeadZoneConsumeTouchEvents(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    public void setWindowHasBlurs(boolean z) {
        this.mRegionSamplingHelper.setWindowHasBlurs(z);
    }

    /* access modifiers changed from: package-private */
    public void onTransientStateChanged(boolean z) {
        this.mEdgeBackGestureHandler.onNavBarTransientStateChanged(z);
        if (this.mNavBarOverlayController.isNavigationBarOverlayEnabled()) {
            this.mNavBarOverlayController.setButtonState(z, false);
        }
    }

    /* access modifiers changed from: package-private */
    public void onBarTransition(int i) {
        if (i == 4) {
            this.mRegionSamplingHelper.stop();
            getLightTransitionsController().setIconsDark(false, true);
            return;
        }
        this.mRegionSamplingHelper.start(this.mSamplingBounds);
    }

    private boolean shouldDeadZoneConsumeTouchEvents(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mDeadZoneConsuming = false;
        }
        if (!this.mDeadZone.onTouchEvent(motionEvent) && !this.mDeadZoneConsuming) {
            return false;
        }
        if (actionMasked == 0) {
            setSlippery(true);
            this.mDeadZoneConsuming = true;
        } else if (actionMasked == 1 || actionMasked == 3) {
            updateSlippery();
            this.mDeadZoneConsuming = false;
        }
        return true;
    }

    public void abortCurrentGesture() {
        getHomeButton().abortCurrentGesture();
    }

    public View getCurrentView() {
        return this.mCurrentView;
    }

    public RotationButtonController getRotationButtonController() {
        return this.mRotationButtonController;
    }

    public ButtonDispatcher getRecentsButton() {
        return this.mButtonDispatchers.get(R$id.recent_apps);
    }

    public ButtonDispatcher getBackButton() {
        return this.mButtonDispatchers.get(R$id.back);
    }

    public ButtonDispatcher getHomeButton() {
        return this.mButtonDispatchers.get(R$id.home);
    }

    public ButtonDispatcher getImeSwitchButton() {
        return this.mButtonDispatchers.get(R$id.ime_switcher);
    }

    public ButtonDispatcher getAccessibilityButton() {
        return this.mButtonDispatchers.get(R$id.accessibility_button);
    }

    public RotationContextButton getRotateSuggestionButton() {
        return (RotationContextButton) this.mButtonDispatchers.get(R$id.rotate_suggestion);
    }

    public ButtonDispatcher getHomeHandle() {
        return this.mButtonDispatchers.get(R$id.home_handle);
    }

    public SparseArray<ButtonDispatcher> getButtonDispatchers() {
        return this.mButtonDispatchers;
    }

    public boolean isRecentsButtonVisible() {
        return getRecentsButton().getVisibility() == 0;
    }

    public boolean isOverviewEnabled() {
        return (this.mDisabledFlags & 16777216) == 0;
    }

    public boolean isQuickStepSwipeUpEnabled() {
        return this.mOverviewProxyService.shouldShowSwipeUpUI() && isOverviewEnabled();
    }

    private void reloadNavIcons() {
        updateIcons(Configuration.EMPTY);
    }

    private void updateIcons(Configuration configuration) {
        int i = configuration.orientation;
        Configuration configuration2 = this.mConfiguration;
        boolean z = true;
        boolean z2 = i != configuration2.orientation;
        boolean z3 = configuration.densityDpi != configuration2.densityDpi;
        if (configuration.getLayoutDirection() == this.mConfiguration.getLayoutDirection()) {
            z = false;
        }
        if (z2 || z3) {
            this.mDockedIcon = getDrawable(R$drawable.ic_sysbar_docked);
            this.mHomeDefaultIcon = getHomeDrawable();
        }
        if (z3 || z) {
            this.mRecentIcon = getDrawable(R$drawable.ic_sysbar_recent);
            this.mContextualButtonGroup.updateIcons(this.mLightIconColor, this.mDarkIconColor);
        }
        if (z2 || z3 || z) {
            this.mBackIcon = getBackDrawable();
        }
    }

    private void updateRotationButton() {
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            ContextualButtonGroup contextualButtonGroup = this.mContextualButtonGroup;
            int i = R$id.rotate_suggestion;
            contextualButtonGroup.removeButton(i);
            this.mButtonDispatchers.remove(i);
            this.mRotationButtonController.setRotationButton(this.mFloatingRotationButton, this.mRotationButtonListener);
            return;
        }
        ContextualButtonGroup contextualButtonGroup2 = this.mContextualButtonGroup;
        int i2 = R$id.rotate_suggestion;
        if (contextualButtonGroup2.getContextButton(i2) == null) {
            this.mContextualButtonGroup.addButton(this.mRotationContextButton);
            this.mButtonDispatchers.put(i2, this.mRotationContextButton);
            this.mRotationButtonController.setRotationButton(this.mRotationContextButton, this.mRotationButtonListener);
        }
    }

    public KeyButtonDrawable getBackDrawable() {
        KeyButtonDrawable drawable = getDrawable(getBackDrawableRes());
        this.mBackDegrees = 0.0f;
        orientBackButton(drawable);
        return drawable;
    }

    public int getBackDrawableRes() {
        return chooseNavigationIconDrawableRes(R$drawable.ic_sysbar_back, R$drawable.ic_sysbar_back_quick_step);
    }

    public KeyButtonDrawable getHomeDrawable() {
        KeyButtonDrawable keyButtonDrawable;
        if (this.mOverviewProxyService.shouldShowSwipeUpUI()) {
            keyButtonDrawable = getDrawable(R$drawable.ic_sysbar_home_quick_step);
        } else {
            keyButtonDrawable = getDrawable(R$drawable.ic_sysbar_home);
        }
        orientHomeButton(keyButtonDrawable);
        return keyButtonDrawable;
    }

    private void orientBackButton(KeyButtonDrawable keyButtonDrawable) {
        float f;
        boolean z = (this.mNavigationIconHints & 1) != 0;
        boolean z2 = this.mConfiguration.getLayoutDirection() == 1;
        float f2 = 0.0f;
        if (z) {
            f = (float) (z2 ? 90 : -90);
        } else {
            f = 0.0f;
        }
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            keyButtonDrawable.setRotation(f);
        } else if (this.mBackDegrees != f) {
            this.mBackDegrees = f;
            if (!this.mOverviewProxyService.shouldShowSwipeUpUI() && !this.mIsVertical && z) {
                f2 = -getResources().getDimension(R$dimen.navbar_back_button_ime_offset);
            }
            ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(keyButtonDrawable, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_ROTATE, new float[]{f}), PropertyValuesHolder.ofFloat(KeyButtonDrawable.KEY_DRAWABLE_TRANSLATE_Y, new float[]{f2})});
            ofPropertyValuesHolder.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            ofPropertyValuesHolder.setDuration(200);
            ofPropertyValuesHolder.start();
        }
    }

    private void orientHomeButton(KeyButtonDrawable keyButtonDrawable) {
        keyButtonDrawable.setRotation(this.mIsVertical ? 90.0f : 0.0f);
    }

    private int chooseNavigationIconDrawableRes(int i, int i2) {
        return this.mOverviewProxyService.shouldShowSwipeUpUI() ? i2 : i;
    }

    private KeyButtonDrawable getDrawable(int i) {
        return KeyButtonDrawable.create(this.mLightContext, this.mLightIconColor, this.mDarkIconColor, i, true, (Color) null);
    }

    public void onScreenStateChanged(boolean z) {
        this.mScreenOn = z;
        if (!z) {
            this.mRegionSamplingHelper.stop();
        } else if (com.android.systemui.util.Utils.isGesturalModeOnDefaultDisplay(getContext(), this.mNavBarMode)) {
            this.mRegionSamplingHelper.start(this.mSamplingBounds);
        }
    }

    public void setWindowVisible(boolean z) {
        this.mRegionSamplingHelper.setWindowVisible(z);
        this.mRotationButtonController.onNavigationBarWindowVisibilityChange(z);
    }

    public void setBehavior(int i) {
        this.mRotationButtonController.onBehaviorChanged(i);
    }

    public void setLayoutDirection(int i) {
        reloadNavIcons();
        super.setLayoutDirection(i);
    }

    public void setNavigationIconHints(int i) {
        int i2 = this.mNavigationIconHints;
        if (i != i2) {
            boolean z = false;
            boolean z2 = (i & 1) != 0;
            if ((i2 & 1) != 0) {
                z = true;
            }
            if (z2 != z) {
                onImeVisibilityChanged(z2);
            }
            this.mNavigationIconHints = i;
            if (!MotoFeature.getInstance(this.mContext).isSupportCli() || getContext().getDisplayId() != 1) {
                updateNavButtonIcons();
            } else if (!updateCliImeNav(z2)) {
                updateNavButtonIcons();
            }
        }
    }

    private void onImeVisibilityChanged(boolean z) {
        if (!z) {
            this.mTransitionListener.onBackAltCleared();
        }
        this.mImeVisible = z;
        this.mRotationButtonController.getRotationButton().setCanShowRotationButton(!this.mImeVisible);
        if (this.mNavBarOverlayController.isNavigationBarOverlayEnabled()) {
            this.mNavBarOverlayController.setCanShow(!this.mImeVisible);
        }
    }

    public void setDisabledFlags(int i) {
        if (this.mDisabledFlags != i) {
            boolean isOverviewEnabled = isOverviewEnabled();
            this.mDisabledFlags = i;
            if (!isOverviewEnabled && isOverviewEnabled()) {
                reloadNavIcons();
            }
            updateNavButtonIcons();
            updateSlippery();
            updateDisabledSystemUiStateFlags();
        }
    }

    public void updateNavButtonIcons() {
        LayoutTransition layoutTransition;
        Class cls = MotoTaskBarController.class;
        boolean z = true;
        int i = 0;
        boolean z2 = (this.mNavigationIconHints & 1) != 0;
        KeyButtonDrawable keyButtonDrawable = this.mBackIcon;
        orientBackButton(keyButtonDrawable);
        KeyButtonDrawable keyButtonDrawable2 = this.mHomeDefaultIcon;
        if (!this.mUseCarModeUi) {
            orientHomeButton(keyButtonDrawable2);
        }
        getHomeButton().setImageDrawable(keyButtonDrawable2);
        getBackButton().setImageDrawable(keyButtonDrawable);
        updateRecentsIcon();
        this.mContextualButtonGroup.setButtonVisibility(R$id.ime_switcher, (this.mNavigationIconHints & 2) != 0);
        this.mBarTransitions.reapplyDarkIntensity();
        boolean z3 = QuickStepContract.isGesturalMode(this.mNavBarMode) || (this.mDisabledFlags & 2097152) != 0;
        boolean isRecentsButtonDisabled = isRecentsButtonDisabled();
        boolean z4 = isRecentsButtonDisabled && (2097152 & this.mDisabledFlags) != 0;
        boolean z5 = !z2 && (this.mEdgeBackGestureHandler.isHandlingGestures() || (this.mDisabledFlags & 4194304) != 0);
        boolean isScreenPinningActive = ActivityManagerWrapper.getInstance().isScreenPinningActive();
        if (this.mOverviewProxyService.isEnabled()) {
            isRecentsButtonDisabled |= !QuickStepContract.isLegacyMode(this.mNavBarMode);
            if (isScreenPinningActive && !QuickStepContract.isGesturalMode(this.mNavBarMode)) {
                z5 = false;
                z3 = false;
            }
        } else if (isScreenPinningActive) {
            z5 = false;
            isRecentsButtonDisabled = false;
        }
        ViewGroup viewGroup = (ViewGroup) getCurrentView().findViewById(R$id.nav_buttons);
        if (!(viewGroup == null || (layoutTransition = viewGroup.getLayoutTransition()) == null || layoutTransition.getTransitionListeners().contains(this.mTransitionListener))) {
            layoutTransition.addTransitionListener(this.mTransitionListener);
        }
        getBackButton().setVisibility(z5 ? 4 : 0);
        getHomeButton().setVisibility(z3 ? 4 : 0);
        getRecentsButton().setVisibility(isRecentsButtonDisabled ? 4 : 0);
        getHomeHandle().setVisibility(z4 ? 4 : 0);
        if (this.mIsMotoTaskBarAvailable) {
            boolean isKeyguardLocked = ((KeyguardManager) getContext().getSystemService("keyguard")).isKeyguardLocked();
            boolean isTrackpadIconShow = ((MotoTaskBarController) Dependency.get(cls)).isTrackpadIconShow();
            if (!QuickStepContract.isGesturalMode(this.mNavBarMode) ? !isTrackpadIconShow || isKeyguardLocked : !isTrackpadIconShow || !z5 || isKeyguardLocked) {
                z = false;
            }
            ButtonDispatcher trackpadButton = getTrackpadButton();
            if (!z) {
                i = 4;
            }
            trackpadButton.setVisibility(i);
            ((MotoTaskBarController) Dependency.get(cls)).handleTrackpadGuideShow(z);
        }
        notifyActiveTouchRegions();
    }

    /* access modifiers changed from: private */
    /* renamed from: updateCliImeNavSync */
    public boolean lambda$updateCliImeNav$3(boolean z) {
        ViewGroup viewGroup = (ViewGroup) getCurrentView().findViewById(R$id.nav_buttons);
        if (this.mCliNavArea == null) {
            this.mCliNavArea = (ViewGroup) LayoutInflater.from(this.mContext).inflate(R$layout.cli_ime_navigation_layout, (ViewGroup) null);
            ((ViewGroup) viewGroup.getParent()).addView(this.mCliNavArea);
            View findViewById = this.mCliNavArea.findViewById(R$id.zz_moto_cli_hideAction);
            if (findViewById != null) {
                ((ImageView) findViewById).setImageDrawable(getDrawable(R$drawable.zz_moto_cli_hide));
            }
        }
        int i = 4;
        viewGroup.setVisibility(z ? 4 : 0);
        ViewGroup viewGroup2 = this.mCliNavArea;
        if (z) {
            i = 0;
        }
        viewGroup2.setVisibility(i);
        return z;
    }

    private boolean updateCliImeNav(boolean z) {
        if (z) {
            lambda$updateCliImeNav$3(z);
        } else {
            postDelayed(new NavigationBarView$$ExternalSyntheticLambda5(this, z), 100);
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isRecentsButtonDisabled() {
        return this.mUseCarModeUi || !isOverviewEnabled() || (getContext().getDisplayId() != 0 && !MotoFeature.getInstance(this.mContext).isSupportCli());
    }

    private Display getContextDisplay() {
        return getContext().getDisplay();
    }

    public void setLayoutTransitionsEnabled(boolean z) {
        this.mLayoutTransitionsEnabled = z;
        updateLayoutTransitionsEnabled();
    }

    public void setWakeAndUnlocking(boolean z) {
        setUseFadingAnimations(z);
        this.mWakeAndUnlocking = z;
        updateLayoutTransitionsEnabled();
    }

    private void updateLayoutTransitionsEnabled() {
        boolean z = !this.mWakeAndUnlocking && this.mLayoutTransitionsEnabled;
        LayoutTransition layoutTransition = ((ViewGroup) getCurrentView().findViewById(R$id.nav_buttons)).getLayoutTransition();
        if (layoutTransition == null) {
            return;
        }
        if (z) {
            layoutTransition.enableTransitionType(2);
            layoutTransition.enableTransitionType(3);
            layoutTransition.enableTransitionType(0);
            layoutTransition.enableTransitionType(1);
            return;
        }
        layoutTransition.disableTransitionType(2);
        layoutTransition.disableTransitionType(3);
        layoutTransition.disableTransitionType(0);
        layoutTransition.disableTransitionType(1);
    }

    private void setUseFadingAnimations(boolean z) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) ((ViewGroup) getParent()).getLayoutParams();
        if (layoutParams != null) {
            boolean z2 = layoutParams.windowAnimations != 0;
            if (!z2 && z) {
                layoutParams.windowAnimations = R$style.Animation_NavigationBarFadeIn;
            } else if (z2 && !z) {
                layoutParams.windowAnimations = 0;
            } else {
                return;
            }
            ((WindowManager) getContext().getSystemService(WindowManager.class)).updateViewLayout((View) getParent(), layoutParams);
        }
    }

    public void onStatusBarPanelStateChanged() {
        updateSlippery();
        updatePanelSystemUiStateFlags();
    }

    public void updateDisabledSystemUiStateFlags() {
        int displayId = this.mContext.getDisplayId();
        CliNavGestureController cliNavGestureController = this.mCliNavGestureController;
        boolean z = false;
        if (cliNavGestureController != null && displayId == 1) {
            cliNavGestureController.setSystemUiFlag(1, ActivityManagerWrapper.getInstance().isScreenPinningActive());
            this.mCliNavGestureController.setSystemUiFlag(128, (this.mDisabledFlags & 16777216) != 0);
            this.mCliNavGestureController.setSystemUiFlag(256, (this.mDisabledFlags & 2097152) != 0);
            this.mCliNavGestureController.setSystemUiFlag(1024, (this.mDisabledFlags & 33554432) != 0);
        }
        SysUiState flag = this.mSysUiFlagContainer.setFlag(1, ActivityManagerWrapper.getInstance().isScreenPinningActive()).setFlag(128, (16777216 & this.mDisabledFlags) != 0).setFlag(256, (2097152 & this.mDisabledFlags) != 0);
        if ((this.mDisabledFlags & 33554432) != 0) {
            z = true;
        }
        flag.setFlag(1024, z).commitUpdate(displayId);
    }

    public void updatePanelSystemUiStateFlags() {
        int displayId = this.mContext.getDisplayId();
        NotificationPanelViewController notificationPanelViewController = this.mPanelView;
        if (notificationPanelViewController != null) {
            this.mSysUiFlagContainer.setFlag(4, notificationPanelViewController.isFullyExpanded() && !this.mPanelView.isInSettings()).setFlag(2048, this.mPanelView.isInSettings()).commitUpdate(displayId);
        }
    }

    public void updateStates() {
        boolean shouldShowSwipeUpUI = this.mOverviewProxyService.shouldShowSwipeUpUI();
        NavigationBarInflaterView navigationBarInflaterView = this.mNavigationInflaterView;
        if (navigationBarInflaterView != null) {
            navigationBarInflaterView.onLikelyDefaultLayoutChange();
        }
        updateSlippery();
        reloadNavIcons();
        updateNavButtonIcons();
        WindowManagerWrapper.getInstance().setNavBarVirtualKeyHapticFeedbackEnabled(!shouldShowSwipeUpUI);
        getHomeButton().setAccessibilityDelegate(shouldShowSwipeUpUI ? this.mQuickStepAccessibilityDelegate : null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.mPanelView;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSlippery() {
        /*
            r1 = this;
            boolean r0 = r1.isQuickStepSwipeUpEnabled()
            if (r0 == 0) goto L_0x001b
            com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = r1.mPanelView
            if (r0 == 0) goto L_0x0019
            boolean r0 = r0.isFullyExpanded()
            if (r0 == 0) goto L_0x0019
            com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = r1.mPanelView
            boolean r0 = r0.isCollapsing()
            if (r0 != 0) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            r0 = 0
            goto L_0x001c
        L_0x001b:
            r0 = 1
        L_0x001c:
            r1.setSlippery(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.navigationbar.NavigationBarView.updateSlippery():void");
    }

    private void setSlippery(boolean z) {
        setWindowFlag(536870912, z);
    }

    private void setWindowFlag(int i, boolean z) {
        WindowManager.LayoutParams layoutParams;
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup != null && (layoutParams = (WindowManager.LayoutParams) viewGroup.getLayoutParams()) != null) {
            int i2 = layoutParams.flags;
            if (z != ((i2 & i) != 0)) {
                if (z) {
                    layoutParams.flags = i | i2;
                } else {
                    layoutParams.flags = (~i) & i2;
                }
                ((WindowManager) getContext().getSystemService(WindowManager.class)).updateViewLayout(viewGroup, layoutParams);
            }
        }
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
        this.mBarTransitions.onNavigationModeChanged(i);
        this.mEdgeBackGestureHandler.onNavigationModeChanged(this.mNavBarMode);
        updateRotationButton();
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            this.mRegionSamplingHelper.start(this.mSamplingBounds);
        } else {
            this.mRegionSamplingHelper.stop();
        }
    }

    public void setAccessibilityButtonState(boolean z, boolean z2) {
        this.mLongClickableAccessibilityButton = z2;
        getAccessibilityButton().setLongClickable(z2);
        this.mContextualButtonGroup.setButtonVisibility(R$id.accessibility_button, z);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        NavigationBarInflaterView navigationBarInflaterView = (NavigationBarInflaterView) findViewById(R$id.navigation_inflater);
        this.mNavigationInflaterView = navigationBarInflaterView;
        navigationBarInflaterView.setButtonDispatchers(this.mButtonDispatchers);
        updateOrientationViews();
        reloadNavIcons();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mDeadZone.onDraw(canvas);
        super.onDraw(canvas);
    }

    /* access modifiers changed from: private */
    public void updateSamplingRect() {
        this.mSamplingBounds.setEmpty();
        View currentView = getHomeHandle().getCurrentView();
        if (currentView != null) {
            int[] iArr = new int[2];
            currentView.getLocationOnScreen(iArr);
            Point point = new Point();
            currentView.getContext().getDisplay().getRealSize(point);
            this.mSamplingBounds.set(new Rect(iArr[0] - this.mNavColorSampleMargin, point.y - getNavBarHeight(), iArr[0] + currentView.getWidth() + this.mNavColorSampleMargin, point.y));
            return;
        }
        int[] iArr2 = new int[2];
        Point point2 = new Point();
        getLocationOnScreen(iArr2);
        getContext().getDisplay().getRealSize(point2);
        this.mSamplingBounds.set(new Rect(iArr2[0], point2.y - getNavBarHeight(), iArr2[0] + getWidth(), point2.y));
    }

    /* access modifiers changed from: package-private */
    public void setOrientedHandleSamplingRegion(Rect rect) {
        this.mOrientedHandleSamplingRegion = rect;
        this.mRegionSamplingHelper.updateSamplingRect();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = this.mCurrentRotation;
        notifyActiveTouchRegions();
    }

    public void notifyActiveTouchRegions() {
        this.mOverviewProxyService.onActiveNavBarRegionChanges(getButtonLocations(true, true, true));
    }

    private void updateButtonTouchRegionCache() {
        FrameLayout frameLayout;
        if (this.mIsVertical) {
            frameLayout = this.mNavigationInflaterView.mVertical;
        } else {
            frameLayout = this.mNavigationInflaterView.mHorizontal;
        }
        this.mButtonFullTouchableRegions = ((NearestTouchFrame) frameLayout.findViewById(R$id.nav_buttons)).getFullTouchableChildRegions();
    }

    private Region getButtonLocations(boolean z, boolean z2, boolean z3) {
        if (z3 && !z2) {
            z3 = false;
        }
        this.mTmpRegion.setEmpty();
        updateButtonTouchRegionCache();
        updateButtonLocation(getBackButton(), z2, z3);
        updateButtonLocation(getHomeButton(), z2, z3);
        updateButtonLocation(getRecentsButton(), z2, z3);
        updateButtonLocation(getImeSwitchButton(), z2, z3);
        updateButtonLocation(getAccessibilityButton(), z2, z3);
        if (this.mIsMotoTaskBarAvailable) {
            updateButtonLocation(getTrackpadButton(), z2, z3);
        }
        if (!z || !this.mFloatingRotationButton.isVisible()) {
            updateButtonLocation(getRotateSuggestionButton(), z2, z3);
        } else {
            updateButtonLocation(this.mFloatingRotationButton.getCurrentView(), z2);
        }
        if (z && this.mNavBarOverlayController.isNavigationBarOverlayEnabled() && this.mNavBarOverlayController.isVisible()) {
            updateButtonLocation(this.mNavBarOverlayController.getCurrentView(), z2);
        }
        return this.mTmpRegion;
    }

    private void updateButtonLocation(ButtonDispatcher buttonDispatcher, boolean z, boolean z2) {
        View currentView;
        if (buttonDispatcher != null && (currentView = buttonDispatcher.getCurrentView()) != null && buttonDispatcher.isVisible()) {
            if (!z2 || !this.mButtonFullTouchableRegions.containsKey(currentView)) {
                updateButtonLocation(currentView, z);
            } else {
                this.mTmpRegion.op(this.mButtonFullTouchableRegions.get(currentView), Region.Op.UNION);
            }
        }
    }

    private void updateButtonLocation(View view, boolean z) {
        if (z) {
            view.getBoundsOnScreen(this.mTmpBounds);
        } else {
            view.getLocationInWindow(this.mTmpPosition);
            Rect rect = this.mTmpBounds;
            int[] iArr = this.mTmpPosition;
            rect.set(iArr[0], iArr[1], iArr[0] + view.getWidth(), this.mTmpPosition[1] + view.getHeight());
        }
        this.mTmpRegion.op(this.mTmpBounds, Region.Op.UNION);
    }

    private void updateOrientationViews() {
        this.mHorizontal = findViewById(R$id.horizontal);
        this.mVertical = findViewById(R$id.vertical);
        updateCurrentView();
    }

    /* access modifiers changed from: package-private */
    public boolean needsReorient(int i) {
        return this.mCurrentRotation != i;
    }

    private void updateCurrentView() {
        resetViews();
        View view = this.mIsVertical ? this.mVertical : this.mHorizontal;
        this.mCurrentView = view;
        boolean z = false;
        view.setVisibility(0);
        this.mNavigationInflaterView.setVertical(this.mIsVertical);
        int rotation = getContextDisplay().getRotation();
        this.mCurrentRotation = rotation;
        NavigationBarInflaterView navigationBarInflaterView = this.mNavigationInflaterView;
        if (rotation == 1) {
            z = true;
        }
        navigationBarInflaterView.setAlternativeOrder(z);
        this.mNavigationInflaterView.updateButtonDispatchersCurrentView();
        updateLayoutTransitionsEnabled();
        post(new NavigationBarView$$ExternalSyntheticLambda2(this));
    }

    private void resetViews() {
        this.mHorizontal.setVisibility(8);
        this.mVertical.setVisibility(8);
    }

    private void updateRecentsIcon() {
        this.mDockedIcon.setRotation((!this.mDockedStackExists || !this.mIsVertical) ? 0.0f : 90.0f);
        getRecentsButton().setImageDrawable(this.mDockedStackExists ? this.mDockedIcon : this.mRecentIcon);
        this.mBarTransitions.reapplyDarkIntensity();
    }

    public void showPinningEnterExitToast(boolean z) {
        if (z) {
            this.mScreenPinningNotify.showPinningStartToast();
        } else {
            this.mScreenPinningNotify.showPinningExitToast();
        }
    }

    public void showPinningEscapeToast() {
        this.mScreenPinningNotify.showEscapeToast(this.mNavBarMode == 2, isRecentsButtonVisible());
    }

    public void reorient() {
        updateCurrentView();
        ((NavigationBarFrame) getRootView()).setDeadZone(this.mDeadZone);
        this.mDeadZone.onConfigurationChanged(this.mCurrentRotation);
        this.mBarTransitions.init();
        if (!isLayoutDirectionResolved()) {
            resolveLayoutDirection();
        }
        updateNavButtonIcons();
        getHomeButton().setVertical(this.mIsVertical);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int dimensionPixelSize;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        boolean z = size > 0 && size2 > size && !QuickStepContract.isGesturalMode(this.mNavBarMode);
        if (z != this.mIsVertical) {
            this.mIsVertical = z;
            reorient();
            notifyVerticalChangedListener(z);
        }
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) {
                if (this.mIsVertical) {
                    dimensionPixelSize = getResources().getDimensionPixelSize(17105364);
                } else {
                    dimensionPixelSize = getResources().getDimensionPixelSize(17105362);
                }
                i3 = getResources().getDimensionPixelSize(17105358);
            } else {
                if (this.mIsVertical) {
                    i4 = getResources().getDimensionPixelSize(17105062);
                } else {
                    i4 = getResources().getDimensionPixelSize(17105061);
                }
                i3 = getResources().getDimensionPixelSize(17105058);
            }
            this.mBarTransitions.setBackgroundFrame(new Rect(0, i3 - i4, size, size2));
        } else {
            this.mBarTransitions.setBackgroundFrame((Rect) null);
        }
        super.onMeasure(i, i2);
    }

    private int getNavBarHeight() {
        Context currentUserContext = ((NavigationModeController) Dependency.get(NavigationModeController.class)).getCurrentUserContext();
        if (this.mIsVertical) {
            return currentUserContext.getResources().getDimensionPixelSize(17105364);
        }
        return currentUserContext.getResources().getDimensionPixelSize(17105362);
    }

    private void notifyVerticalChangedListener(boolean z) {
        OnVerticalChangedListener onVerticalChangedListener = this.mOnVerticalChangedListener;
        if (onVerticalChangedListener != null) {
            onVerticalChangedListener.onVerticalChanged(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mTmpLastConfiguration.updateFrom(this.mConfiguration);
        this.mConfiguration.updateFrom(configuration);
        boolean updateCarMode = updateCarMode();
        updateIcons(this.mTmpLastConfiguration);
        updateRecentsIcon();
        this.mEdgeBackGestureHandler.onConfigurationChanged(this.mConfiguration);
        if (!updateCarMode) {
            Configuration configuration2 = this.mTmpLastConfiguration;
            if (configuration2.densityDpi == this.mConfiguration.densityDpi && configuration2.getLayoutDirection() == this.mConfiguration.getLayoutDirection()) {
                return;
            }
        }
        updateNavButtonIcons();
    }

    private boolean updateCarMode() {
        Configuration configuration = this.mConfiguration;
        if (configuration != null) {
            boolean z = (configuration.uiMode & 15) == 3;
            if (z != this.mInCarMode) {
                this.mInCarMode = z;
                this.mUseCarModeUi = false;
            }
        }
        return false;
    }

    private String getResourceName(int i) {
        if (i == 0) {
            return "(null)";
        }
        try {
            return getContext().getResources().getResourceName(i);
        } catch (Resources.NotFoundException unused) {
            return "(unknown)";
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mEdgeBackGestureHandler.onNavBarAttached();
        requestApplyInsets();
        reorient();
        onNavigationModeChanged(this.mNavBarMode);
        RotationButtonController rotationButtonController = this.mRotationButtonController;
        if (rotationButtonController != null) {
            rotationButtonController.registerListeners();
        }
        if (this.mNavBarOverlayController.isNavigationBarOverlayEnabled()) {
            this.mNavBarOverlayController.registerListeners();
        }
        getViewTreeObserver().addOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
        updateNavButtonIcons();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).removeListener(this);
        for (int i = 0; i < this.mButtonDispatchers.size(); i++) {
            this.mButtonDispatchers.valueAt(i).onDestroy();
        }
        RotationButtonController rotationButtonController = this.mRotationButtonController;
        if (rotationButtonController != null) {
            rotationButtonController.unregisterListeners();
        }
        if (this.mNavBarOverlayController.isNavigationBarOverlayEnabled()) {
            this.mNavBarOverlayController.unregisterListeners();
        }
        this.mEdgeBackGestureHandler.onNavBarDetached();
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mOnComputeInternalInsetsListener);
    }

    public AutoHideController getAutoHideController() {
        return this.mAutoHideController;
    }

    public void dump(PrintWriter printWriter) {
        Rect rect = new Rect();
        Point point = new Point();
        getContextDisplay().getRealSize(point);
        printWriter.println("NavigationBarView:");
        printWriter.println(String.format("      this: " + StatusBar.viewInfo(this) + " " + visibilityToString(getVisibility()), new Object[0]));
        getWindowVisibleDisplayFrame(rect);
        boolean z = rect.right > point.x || rect.bottom > point.y;
        StringBuilder sb = new StringBuilder();
        sb.append("      window: ");
        sb.append(rect.toShortString());
        sb.append(" ");
        sb.append(visibilityToString(getWindowVisibility()));
        sb.append(z ? " OFFSCREEN!" : "");
        printWriter.println(sb.toString());
        printWriter.println(String.format("      mCurrentView: id=%s (%dx%d) %s %f", new Object[]{getResourceName(getCurrentView().getId()), Integer.valueOf(getCurrentView().getWidth()), Integer.valueOf(getCurrentView().getHeight()), visibilityToString(getCurrentView().getVisibility()), Float.valueOf(getCurrentView().getAlpha())}));
        Object[] objArr = new Object[3];
        objArr[0] = Integer.valueOf(this.mDisabledFlags);
        objArr[1] = this.mIsVertical ? "true" : "false";
        objArr[2] = Float.valueOf(getLightTransitionsController().getCurrentDarkIntensity());
        printWriter.println(String.format("      disabled=0x%08x vertical=%s darkIntensity=%.2f", objArr));
        printWriter.println("      mOrientedHandleSamplingRegion: " + this.mOrientedHandleSamplingRegion);
        printWriter.println("    mScreenOn: " + this.mScreenOn);
        dumpButton(printWriter, "back", getBackButton());
        dumpButton(printWriter, "home", getHomeButton());
        dumpButton(printWriter, "rcnt", getRecentsButton());
        dumpButton(printWriter, "rota", getRotateSuggestionButton());
        dumpButton(printWriter, "a11y", getAccessibilityButton());
        dumpButton(printWriter, "ime", getImeSwitchButton());
        NavigationBarInflaterView navigationBarInflaterView = this.mNavigationInflaterView;
        if (navigationBarInflaterView != null) {
            navigationBarInflaterView.dump(printWriter);
        }
        this.mBarTransitions.dump(printWriter);
        this.mContextualButtonGroup.dump(printWriter);
        this.mRegionSamplingHelper.dump(printWriter);
        this.mEdgeBackGestureHandler.dump(printWriter);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        int systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
        int systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
        int systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
        int systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
        int i = this.mCurrentRotation;
        boolean z = true;
        if (i == 1) {
            systemWindowInsetRight += this.mPaddingBottom;
        } else if (i == 3) {
            systemWindowInsetLeft += this.mPaddingBottom;
        } else {
            systemWindowInsetBottom += this.mPaddingBottom;
        }
        setPadding(systemWindowInsetLeft, systemWindowInsetTop, systemWindowInsetRight, systemWindowInsetBottom);
        this.mEdgeBackGestureHandler.setInsets(systemWindowInsetLeft, systemWindowInsetRight);
        if (QuickStepContract.isGesturalMode(this.mNavBarMode) && windowInsets.getSystemWindowInsetBottom() != 0) {
            z = false;
        }
        setClipChildren(z);
        setClipToPadding(z);
        return super.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: package-private */
    public void registerDockedListener(LegacySplitScreen legacySplitScreen) {
        legacySplitScreen.registerInSplitScreenListener(this.mDockedListener);
    }

    /* access modifiers changed from: package-private */
    public void registerPipExclusionBoundsChangeListener(Pip pip) {
        pip.setPipExclusionBoundsChangeListener(this.mPipListener);
    }

    private static void dumpButton(PrintWriter printWriter, String str, ButtonDispatcher buttonDispatcher) {
        printWriter.print("      " + str + ": ");
        if (buttonDispatcher == null) {
            printWriter.print("null");
        } else {
            printWriter.print(visibilityToString(buttonDispatcher.getVisibility()) + " alpha=" + buttonDispatcher.getAlpha());
        }
        printWriter.println();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(Boolean bool) {
        post(new NavigationBarView$$ExternalSyntheticLambda4(this, bool));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(Boolean bool) {
        this.mDockedStackExists = bool.booleanValue();
        updateRecentsIcon();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(Rect rect) {
        post(new NavigationBarView$$ExternalSyntheticLambda3(this, rect));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(Rect rect) {
        this.mEdgeBackGestureHandler.setPipStashExclusionBounds(rect);
    }

    /* access modifiers changed from: package-private */
    public void setNavigationBarLumaSamplingEnabled(boolean z) {
        if (z) {
            this.mRegionSamplingHelper.start(this.mSamplingBounds);
        } else {
            this.mRegionSamplingHelper.stop();
        }
    }

    public ButtonDispatcher getTrackpadButton() {
        return this.mButtonDispatchers.get(R$id.trackpad);
    }

    public void setTrackpadIconShow(boolean z) {
        ButtonDispatcher trackpadButton = getTrackpadButton();
        int i = z ? 0 : 4;
        if (trackpadButton != null && this.mIsMotoTaskBarAvailable && i != trackpadButton.getVisibility()) {
            updateNavButtonIcons();
        }
    }

    public void requestNavTrackpadGuide(boolean z) {
        if (z) {
            ButtonDispatcher trackpadButton = getTrackpadButton();
            if (trackpadButton != null && trackpadButton.isVisible()) {
                if (this.mDesktopNavGuideView == null) {
                    this.mDesktopNavGuideView = new DesktopNavGuideView(getContext(), R$layout.zz_moto_trackpad_guide);
                }
                this.mDesktopNavGuideView.show(trackpadButton.getCurrentView());
                return;
            }
            return;
        }
        DesktopNavGuideView desktopNavGuideView = this.mDesktopNavGuideView;
        if (desktopNavGuideView != null) {
            desktopNavGuideView.close(true, false);
        }
    }

    /* renamed from: updateTrackpadGuideLocation */
    public void lambda$updateCurrentView$4() {
        ButtonDispatcher trackpadButton = getTrackpadButton();
        DesktopNavGuideView desktopNavGuideView = this.mDesktopNavGuideView;
        if (desktopNavGuideView != null && trackpadButton != null) {
            desktopNavGuideView.updateLocation(trackpadButton.getCurrentView());
        }
    }
}
