package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Insets;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.util.MathUtils;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardStatusView;
import com.android.keyguard.KeyguardStatusViewController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.LockIconViewController;
import com.android.keyguard.dagger.KeyguardQsUserSwitchComponent;
import com.android.keyguard.dagger.KeyguardStatusBarViewComponent;
import com.android.keyguard.dagger.KeyguardStatusViewComponent;
import com.android.keyguard.dagger.KeyguardUserSwitcherComponent;
import com.android.p011wm.shell.animation.FlingAnimationUtils;
import com.android.systemui.DejankUtils;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.controls.dagger.ControlsComponent;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.media.KeyguardMediaController;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.media.MediaHierarchyManager;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.moto.NetworkStateTracker;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.p006qs.QSDetailDisplayer;
import com.android.systemui.p006qs.QuickStatusBarHeader;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.C1129QS;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.NotificationShelfController;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.events.PrivacyDotViewController;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.MediaHeaderView;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import com.android.systemui.statusbar.phone.KeyguardAffordanceHelper;
import com.android.systemui.statusbar.phone.KeyguardClockPositionAlgorithm;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.PanelViewController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardQsUserSwitchController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcherController;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcherView;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.util.Utils;
import com.android.systemui.util.settings.SecureSettings;
import com.android.systemui.wallet.controller.QuickAccessWalletController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.inject.Provider;

public class NotificationPanelViewController extends PanelViewController {
    private static final Rect EMPTY_RECT = new Rect();
    private static final AnimationProperties KEYGUARD_HUN_PROPERTIES = new AnimationProperties().setDuration(360);
    /* access modifiers changed from: private */
    public static final Rect M_DUMMY_DIRTY_RECT = new Rect(0, 0, 1, 1);
    private final AnimatableProperty KEYGUARD_HEADS_UP_SHOWING_AMOUNT = AnimatableProperty.from("KEYGUARD_HEADS_UP_SHOWING_AMOUNT", new NotificationPanelViewController$$ExternalSyntheticLambda10(this), new NotificationPanelViewController$$ExternalSyntheticLambda16(this), R$id.keyguard_hun_animator_tag, R$id.keyguard_hun_animator_end_tag, R$id.keyguard_hun_animator_start_tag);
    private View.AccessibilityDelegate mAccessibilityDelegate;
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final ActivityManager mActivityManager;
    private boolean mAffordanceHasPreview;
    /* access modifiers changed from: private */
    public KeyguardAffordanceHelper mAffordanceHelper;
    private boolean mAllowExpandForSmallExpansion;
    private int mAmbientIndicationBottomPadding;
    private final Runnable mAnimateKeyguardBottomAreaInvisibleEndRunnable;
    /* access modifiers changed from: private */
    public final Runnable mAnimateKeyguardStatusBarInvisibleEndRunnable;
    private boolean mAnimateNextNotificationBounds;
    /* access modifiers changed from: private */
    public boolean mAnimateNextPositionUpdate;
    /* access modifiers changed from: private */
    public boolean mAnimatingQS;
    private final AuthController mAuthController;
    /* access modifiers changed from: private */
    public int mBarState;
    private ViewGroup mBigClockContainer;
    /* access modifiers changed from: private */
    public final BiometricUnlockController mBiometricUnlockController;
    /* access modifiers changed from: private */
    public boolean mBlockTouches;
    /* access modifiers changed from: private */
    public boolean mBlockingExpansionForCurrentTouch;
    private float mBottomAreaShadeAlpha;
    private final ValueAnimator mBottomAreaShadeAlphaAnimator;
    /* access modifiers changed from: private */
    public View mCarrierLabel;
    private final KeyguardClockPositionAlgorithm mClockPositionAlgorithm = new KeyguardClockPositionAlgorithm();
    /* access modifiers changed from: private */
    public final KeyguardClockPositionAlgorithm.Result mClockPositionResult = new KeyguardClockPositionAlgorithm.Result();
    private boolean mClosingWithAlphaFadeOut;
    private boolean mCollapsedOnDown;
    private final CommandQueue mCommandQueue;
    /* access modifiers changed from: private */
    public final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public final ConfigurationListener mConfigurationListener = new ConfigurationListener();
    private boolean mConflictingQsExpansionGesture;
    private final ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public Context mContext;
    private final ControlsComponent mControlsComponent;
    private final ConversationNotificationManager mConversationNotificationManager;
    private int mDarkIconSize;
    /* access modifiers changed from: private */
    public boolean mDelayShowingKeyguardStatusBar;
    private NotificationShadeDepthController mDepthController;
    private int mDisplayId;
    /* access modifiers changed from: private */
    public int mDisplayRightInset = 0;
    /* access modifiers changed from: private */
    public int mDisplayTopInset = 0;
    private int mDistanceForQSFullShadeTransition;
    /* access modifiers changed from: private */
    public float mDownX;
    /* access modifiers changed from: private */
    public float mDownY;
    /* access modifiers changed from: private */
    public final DozeParameters mDozeParameters;
    /* access modifiers changed from: private */
    public boolean mDozing;
    private boolean mDozingOnDown;
    private final NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public Runnable mExpandAfterLayoutRunnable;
    private boolean mExpandingFromHeadsUp;
    private boolean mExpectingSynthesizedDown;
    /* access modifiers changed from: private */
    public final FalsingCollector mFalsingCollector;
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    /* access modifiers changed from: private */
    public final FalsingManager.FalsingTapListener mFalsingTapListener;
    private final FeatureFlags mFeatureFlags;
    /* access modifiers changed from: private */
    public boolean mFirstBypassAttempt;
    private FlingAnimationUtils mFlingAnimationUtils;
    private final Provider<FlingAnimationUtils.Builder> mFlingAnimationUtilsBuilder;
    /* access modifiers changed from: private */
    public final FragmentHostManager.FragmentListener mFragmentListener;
    /* access modifiers changed from: private */
    public final FragmentService mFragmentService;
    private NotificationGroupManagerLegacy mGroupManager;
    private int mHeaderHeight;
    private boolean mHeadsUpAnimatingAway;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    /* access modifiers changed from: private */
    public Runnable mHeadsUpExistenceChangedRunnable = new NotificationPanelViewController$$ExternalSyntheticLambda7(this);
    private int mHeadsUpInset;
    /* access modifiers changed from: private */
    public boolean mHeadsUpPinnedMode;
    /* access modifiers changed from: private */
    public HeadsUpTouchHelper mHeadsUpTouchHelper;
    /* access modifiers changed from: private */
    public final HeightListener mHeightListener = new HeightListener();
    private boolean mHideIconsDuringLaunchAnimation = true;
    private int mIndicationBottomPadding;
    private float mInitialHeightOnTouch;
    private float mInitialSwitchX;
    private float mInitialSwitchY;
    /* access modifiers changed from: private */
    public float mInitialTouchX;
    private float mInitialTouchY;
    /* access modifiers changed from: private */
    public float mInterpolatedDarkAmount;
    /* access modifiers changed from: private */
    public boolean mIsBellowCarrierName;
    /* access modifiers changed from: private */
    public boolean mIsExpanding;
    private boolean mIsFullWidth;
    private boolean mIsGestureNavigation;
    /* access modifiers changed from: private */
    public boolean mIsLaunchTransitionFinished;
    /* access modifiers changed from: private */
    public boolean mIsLaunchTransitionRunning;
    private boolean mIsPanelCollapseOnQQS;
    /* access modifiers changed from: private */
    public boolean mIsPulseExpansionResetAnimator;
    /* access modifiers changed from: private */
    public boolean mIsQsTranslationResetAnimator;
    /* access modifiers changed from: private */
    public boolean mIsSwitching;
    private KeyguardStatusBarViewController mKeyguarStatusBarViewController;
    private final KeyguardAffordanceHelperCallback mKeyguardAffordanceHelperCallback = new KeyguardAffordanceHelperCallback();
    /* access modifiers changed from: private */
    public final KeyguardBypassController mKeyguardBypassController;
    private float mKeyguardHeadsUpShowingAmount;
    /* access modifiers changed from: private */
    public KeyguardIndicationController mKeyguardIndicationController;
    private KeyguardMediaController mKeyguardMediaController;
    private float mKeyguardOnlyContentAlpha;
    private final KeyguardQsUserSwitchComponent.Factory mKeyguardQsUserSwitchComponentFactory;
    private KeyguardQsUserSwitchController mKeyguardQsUserSwitchController;
    private boolean mKeyguardQsUserSwitchEnabled;
    /* access modifiers changed from: private */
    public boolean mKeyguardShowing;
    private final Rect mKeyguardStatusAreaClipBounds;
    /* access modifiers changed from: private */
    public KeyguardStatusBarView mKeyguardStatusBar;
    /* access modifiers changed from: private */
    public float mKeyguardStatusBarAnimateAlpha = 1.0f;
    private final KeyguardStatusBarViewComponent.Factory mKeyguardStatusBarViewComponentFactory;
    private final KeyguardStatusViewComponent.Factory mKeyguardStatusViewComponentFactory;
    /* access modifiers changed from: private */
    public KeyguardStatusViewController mKeyguardStatusViewController;
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateCallback = new KeyguardUpdateMonitorCallback() {
        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            if (NotificationPanelViewController.this.mFirstBypassAttempt && NotificationPanelViewController.this.mUpdateMonitor.isUnlockingWithBiometricAllowed(z)) {
                boolean unused = NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = true;
            }
        }

        public void onBiometricRunningStateChanged(boolean z, BiometricSourceType biometricSourceType) {
            boolean z2 = true;
            if (!(NotificationPanelViewController.this.mBarState == 1 || NotificationPanelViewController.this.mBarState == 2)) {
                z2 = false;
            }
            if (!z && NotificationPanelViewController.this.mFirstBypassAttempt && z2 && !NotificationPanelViewController.this.mDozing && !NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar && !NotificationPanelViewController.this.mBiometricUnlockController.isBiometricUnlock()) {
                boolean unused = NotificationPanelViewController.this.mFirstBypassAttempt = false;
                NotificationPanelViewController.this.animateKeyguardStatusBarIn(360);
            }
        }

        public void onFinishedGoingToSleep(int i) {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            boolean unused = notificationPanelViewController.mFirstBypassAttempt = notificationPanelViewController.mKeyguardBypassController.getBypassEnabled();
            boolean unused2 = NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = false;
        }
    };
    private final KeyguardUserSwitcherComponent.Factory mKeyguardUserSwitcherComponentFactory;
    private KeyguardUserSwitcherController mKeyguardUserSwitcherController;
    private boolean mKeyguardUserSwitcherEnabled;
    /* access modifiers changed from: private */
    public String mLastCameraLaunchSource = "lockscreen_affordance";
    /* access modifiers changed from: private */
    public boolean mLastEventSynthesizedDown;
    /* access modifiers changed from: private */
    public int mLastOrientation = -1;
    /* access modifiers changed from: private */
    public float mLastOverscroll;
    /* access modifiers changed from: private */
    public Runnable mLaunchAnimationEndRunnable;
    private boolean mLaunchingAffordance;
    private final LayoutInflater mLayoutInflater;
    /* access modifiers changed from: private */
    public float mLinearDarkAmount;
    /* access modifiers changed from: private */
    public boolean mListenForHeadsUp;
    /* access modifiers changed from: private */
    public LockIconViewController mLockIconViewController;
    /* access modifiers changed from: private */
    public LockscreenGestureLogger mLockscreenGestureLogger = new LockscreenGestureLogger();
    private int mLockscreenNotificationQSPadding;
    /* access modifiers changed from: private */
    public final LockscreenShadeTransitionController mLockscreenShadeTransitionController;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private int mMaxAllowedKeyguardNotifications;
    private final int mMaxKeyguardNotifications;
    private int mMaxOverscrollAmountForPulse;
    private final MediaDataManager mMediaDataManager;
    private final MediaHierarchyManager mMediaHierarchyManager;
    /* access modifiers changed from: private */
    public final MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public int mNavigationBarBottomHeight;
    private long mNotificationBoundsAnimationDelay;
    private long mNotificationBoundsAnimationDuration;
    /* access modifiers changed from: private */
    public NotificationsQuickSettingsContainer mNotificationContainerParent;
    private final NotificationIconAreaController mNotificationIconAreaController;
    private NotificationShelfController mNotificationShelfController;
    /* access modifiers changed from: private */
    public final NotificationStackScrollLayoutController mNotificationStackScrollLayoutController;
    private int mNotificationsHeaderCollideDistance;
    private int mOldLayoutDirection;
    /* access modifiers changed from: private */
    public final OnClickListener mOnClickListener = new OnClickListener();
    private final OnEmptySpaceClickListener mOnEmptySpaceClickListener = new OnEmptySpaceClickListener();
    private final MyOnHeadsUpChangedListener mOnHeadsUpChangedListener = new MyOnHeadsUpChangedListener();
    private final OnHeightChangedListener mOnHeightChangedListener = new OnHeightChangedListener();
    private final OnOverscrollTopChangedListener mOnOverscrollTopChangedListener = new OnOverscrollTopChangedListener();
    /* access modifiers changed from: private */
    public boolean mOnlyAffordanceInThisMotion;
    private float mOverStretchAmount;
    private int mPanelAlpha;
    private final AnimatableProperty mPanelAlphaAnimator;
    private Runnable mPanelAlphaEndAction;
    private final AnimationProperties mPanelAlphaInPropertiesAnimator;
    private final AnimationProperties mPanelAlphaOutPropertiesAnimator;
    private boolean mPanelExpanded;
    private NetworkStateTracker.PanelViewExpansionListener mPanelViewExpansionListener;
    private int mPositionMinSideMargin;
    private final PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public View mPrcContainer;
    private ViewGroup mPreviewContainer;
    private final PrivacyDotViewController mPrivacyDotViewController;
    /* access modifiers changed from: private */
    public final PulseExpansionHandler mPulseExpansionHandler;
    /* access modifiers changed from: private */
    public boolean mPulsing;
    /* access modifiers changed from: private */
    public boolean mQSAnimatingHiddenFromCollapsed;
    private final QSDetailDisplayer mQSDetailDisplayer;
    @VisibleForTesting
    C1129QS mQs;
    /* access modifiers changed from: private */
    public boolean mQsAnimatorExpand;
    private int mQsClipBottom;
    private int mQsClipTop;
    /* access modifiers changed from: private */
    public ValueAnimator mQsClippingAnimation;
    private final Rect mQsClippingAnimationEndBounds;
    /* access modifiers changed from: private */
    public boolean mQsExpandImmediate;
    /* access modifiers changed from: private */
    public boolean mQsExpanded;
    private boolean mQsExpandedWhenExpandingStarted;
    /* access modifiers changed from: private */
    public ValueAnimator mQsExpansionAnimator;
    private boolean mQsExpansionEnabledAmbient = true;
    private boolean mQsExpansionEnabledPolicy = true;
    /* access modifiers changed from: private */
    public boolean mQsExpansionFromOverscroll;
    /* access modifiers changed from: private */
    public float mQsExpansionHeight;
    private int mQsFalsingThreshold;
    /* access modifiers changed from: private */
    public FrameLayout mQsFrame;
    /* access modifiers changed from: private */
    public boolean mQsFullyExpanded;
    private final Region mQsInterceptRegion;
    /* access modifiers changed from: private */
    public int mQsMaxExpansionHeight;
    /* access modifiers changed from: private */
    public int mQsMinExpansionHeight;
    private int mQsNotificationTopPadding;
    private int mQsPeekHeight;
    private boolean mQsScrimEnabled = true;
    /* access modifiers changed from: private */
    public ValueAnimator mQsSizeChangeAnimator;
    private boolean mQsTouchAboveFalsingThreshold;
    /* access modifiers changed from: private */
    public boolean mQsTracking;
    private float mQsTranslationForFullShadeTransition;
    private VelocityTracker mQsVelocityTracker;
    private boolean mQsVisible;
    private final QuickAccessWalletController mQuickAccessWalletController;
    private float mQuickQsOffsetHeight;
    private final RecordingController mRecordingController;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private int mScreenCornerRadius;
    /* access modifiers changed from: private */
    public final ScrimController mScrimController;
    private int mScrimCornerRadius;
    public final C1129QS.ScrollListener mScrollListener;
    private final SecureSettings mSecureSettings;
    private final SettingsChangeObserver mSettingsChangeObserver;
    private int mShelfHeight;
    /* access modifiers changed from: private */
    public boolean mShouldUseSplitNotificationShade;
    private boolean mShowIconsWhenExpanded;
    private boolean mShowingKeyguardHeadsUp;
    private int mSplitShadeNotificationsTopPadding;
    private int mStackScrollerMeasuringPass;
    /* access modifiers changed from: private */
    public boolean mStackScrollerOverscrolling;
    /* access modifiers changed from: private */
    public boolean mStartSwitchDetect;
    private final ValueAnimator.AnimatorUpdateListener mStatusBarAnimateAlphaListener;
    private int mStatusBarHeaderHeightKeyguard;
    /* access modifiers changed from: private */
    public final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private int mStatusBarMinHeight;
    @VisibleForTesting
    final StatusBarStateListener mStatusBarStateListener = new StatusBarStateListener();
    private Region mSwitchRegion;
    private int mSwitchRegionHeight;
    private int mSwitchState;
    private int mSwitchThreshold;
    /* access modifiers changed from: private */
    public final TapAgainViewController mTapAgainViewController;
    /* access modifiers changed from: private */
    public int mThemeResId;
    private ArrayList<Consumer<ExpandableNotificationRow>> mTrackingHeadsUpListeners = new ArrayList<>();
    private int mTrackingPointer;
    private int mTransitionToFullShadeQSPosition;
    private float mTransitioningToFullShadeProgress;
    private boolean mTwoFingerQsExpandPossible;
    private final Executor mUiExecutor;
    private UnlockedScreenOffAnimationController mUnlockedScreenOffAnimationController;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mUpdateMonitor;
    /* access modifiers changed from: private */
    public boolean mUseFrontCamera;
    private final UserManager mUserManager;
    private boolean mUserSetupComplete;
    private Runnable mVerticalTranslationListener;
    /* access modifiers changed from: private */
    public final VibratorHelper mVibratorHelper;
    /* access modifiers changed from: private */
    public final NotificationPanelView mView;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(NotificationPanelView notificationPanelView, Float f) {
        setKeyguardHeadsUpShowingAmount(f.floatValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Float lambda$new$1(NotificationPanelView notificationPanelView) {
        return Float.valueOf(getKeyguardHeadsUpShowingAmount());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        setHeadsUpAnimatingAway(false);
        notifyBarPanelExpansionChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(Property property) {
        Runnable runnable = this.mPanelAlphaEndAction;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NotificationPanelViewController(NotificationPanelView notificationPanelView, Resources resources, Handler handler, LayoutInflater layoutInflater, NotificationWakeUpCoordinator notificationWakeUpCoordinator, PulseExpansionHandler pulseExpansionHandler, DynamicPrivacyController dynamicPrivacyController, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager, FalsingCollector falsingCollector, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationEntryManager notificationEntryManager, KeyguardStateController keyguardStateController, StatusBarStateController statusBarStateController, DozeLog dozeLog, DozeParameters dozeParameters, CommandQueue commandQueue, VibratorHelper vibratorHelper, LatencyTracker latencyTracker, PowerManager powerManager, AccessibilityManager accessibilityManager, int i, KeyguardUpdateMonitor keyguardUpdateMonitor, MetricsLogger metricsLogger, ActivityManager activityManager, ConfigurationController configurationController, Provider<FlingAnimationUtils.Builder> provider, StatusBarTouchableRegionManager statusBarTouchableRegionManager, ConversationNotificationManager conversationNotificationManager, MediaHierarchyManager mediaHierarchyManager, BiometricUnlockController biometricUnlockController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, NotificationStackScrollLayoutController notificationStackScrollLayoutController, KeyguardStatusViewComponent.Factory factory, KeyguardQsUserSwitchComponent.Factory factory2, KeyguardUserSwitcherComponent.Factory factory3, KeyguardStatusBarViewComponent.Factory factory4, LockscreenShadeTransitionController lockscreenShadeTransitionController, QSDetailDisplayer qSDetailDisplayer, NotificationGroupManagerLegacy notificationGroupManagerLegacy, NotificationIconAreaController notificationIconAreaController, AuthController authController, ScrimController scrimController, UserManager userManager, MediaDataManager mediaDataManager, NotificationShadeDepthController notificationShadeDepthController, AmbientState ambientState, LockIconViewController lockIconViewController, FeatureFlags featureFlags, KeyguardMediaController keyguardMediaController, PrivacyDotViewController privacyDotViewController, TapAgainViewController tapAgainViewController, NavigationModeController navigationModeController, FragmentService fragmentService, ContentResolver contentResolver, QuickAccessWalletController quickAccessWalletController, RecordingController recordingController, Executor executor, SecureSettings secureSettings, UnlockedScreenOffAnimationController unlockedScreenOffAnimationController, NotificationRemoteInputManager notificationRemoteInputManager, ControlsComponent controlsComponent) {
        super(notificationPanelView, falsingManager, dozeLog, keyguardStateController, (SysuiStatusBarStateController) statusBarStateController, vibratorHelper, statusBarKeyguardViewManager, latencyTracker, provider.get(), statusBarTouchableRegionManager, ambientState);
        NotificationPanelView notificationPanelView2 = notificationPanelView;
        PulseExpansionHandler pulseExpansionHandler2 = pulseExpansionHandler;
        LockscreenShadeTransitionController lockscreenShadeTransitionController2 = lockscreenShadeTransitionController;
        ScrimController scrimController2 = scrimController;
        FeatureFlags featureFlags2 = featureFlags;
        AnimatableProperty from = AnimatableProperty.from("panelAlpha", NotificationPanelViewController$$ExternalSyntheticLambda11.INSTANCE, NotificationPanelViewController$$ExternalSyntheticLambda17.INSTANCE, R$id.panel_alpha_animator_tag, R$id.panel_alpha_animator_start_tag, R$id.panel_alpha_animator_end_tag);
        this.mPanelAlphaAnimator = from;
        AnimationProperties duration = new AnimationProperties().setDuration(150);
        Property property = from.getProperty();
        Interpolator interpolator = Interpolators.ALPHA_OUT;
        this.mPanelAlphaOutPropertiesAnimator = duration.setCustomInterpolator(property, interpolator);
        this.mPanelAlphaInPropertiesAnimator = new AnimationProperties().setDuration(200).setAnimationEndAction(new NotificationPanelViewController$$ExternalSyntheticLambda13(this)).setCustomInterpolator(from.getProperty(), Interpolators.ALPHA_IN);
        this.mKeyguardHeadsUpShowingAmount = 0.0f;
        this.mQsClippingAnimationEndBounds = new Rect();
        this.mQsClippingAnimation = null;
        this.mKeyguardStatusAreaClipBounds = new Rect();
        this.mQsInterceptRegion = new Region();
        this.mKeyguardOnlyContentAlpha = 1.0f;
        this.mSwitchRegion = new Region();
        this.mAccessibilityDelegate = new View.AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
            }

            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                if (i != AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.getId() && i != AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP.getId()) {
                    return super.performAccessibilityAction(view, i, bundle);
                }
                NotificationPanelViewController.this.mStatusBarKeyguardViewManager.showBouncer(true);
                return true;
            }
        };
        this.mFalsingTapListener = new FalsingManager.FalsingTapListener() {
            public void onDoubleTapRequired() {
                if (NotificationPanelViewController.this.mStatusBarStateController.getState() == 2) {
                    NotificationPanelViewController.this.mTapAgainViewController.show();
                } else {
                    NotificationPanelViewController.this.mKeyguardIndicationController.showTransientIndication(R$string.notification_tap_again);
                }
                NotificationPanelViewController.this.mVibratorHelper.vibrate(1);
            }
        };
        this.mAnimateKeyguardStatusBarInvisibleEndRunnable = new Runnable() {
            public void run() {
                NotificationPanelViewController.this.mKeyguardStatusBar.setVisibility(4);
                NotificationPanelViewController.this.mKeyguardStatusBar.setAlpha(1.0f);
                float unused = NotificationPanelViewController.this.mKeyguardStatusBarAnimateAlpha = 1.0f;
                if (NotificationPanelViewController.this.mIsBellowCarrierName) {
                    NotificationPanelViewController.this.mCarrierLabel.setVisibility(4);
                    NotificationPanelViewController.this.mCarrierLabel.setAlpha(1.0f);
                }
            }
        };
        this.mStatusBarAnimateAlphaListener = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float unused = NotificationPanelViewController.this.mKeyguardStatusBarAnimateAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                NotificationPanelViewController.this.updateHeaderKeyguardAlpha();
            }
        };
        this.mAnimateKeyguardBottomAreaInvisibleEndRunnable = new Runnable() {
            public void run() {
                NotificationPanelViewController.this.mKeyguardBottomArea.setVisibility(8);
            }
        };
        this.mScrollListener = new NotificationPanelViewController$$ExternalSyntheticLambda5(this);
        this.mFragmentListener = new FragmentHostManager.FragmentListener() {
            public void onFragmentViewCreated(String str, Fragment fragment) {
                NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                C1129QS qs = (C1129QS) fragment;
                notificationPanelViewController.mQs = qs;
                qs.setPanelView(notificationPanelViewController.mHeightListener);
                NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                notificationPanelViewController2.mQs.setExpandClickListener(notificationPanelViewController2.mOnClickListener);
                NotificationPanelViewController notificationPanelViewController3 = NotificationPanelViewController.this;
                notificationPanelViewController3.mQs.setHeaderClickable(notificationPanelViewController3.isQsExpansionEnabled());
                NotificationPanelViewController notificationPanelViewController4 = NotificationPanelViewController.this;
                notificationPanelViewController4.mQs.setOverscrolling(notificationPanelViewController4.mStackScrollerOverscrolling);
                NotificationPanelViewController notificationPanelViewController5 = NotificationPanelViewController.this;
                notificationPanelViewController5.mQs.setTranslateWhileExpanding(notificationPanelViewController5.mShouldUseSplitNotificationShade);
                NotificationPanelViewController.this.mQs.getView().addOnLayoutChangeListener(new NotificationPanelViewController$16$$ExternalSyntheticLambda0(this));
                NotificationPanelViewController.this.mQs.setCollapsedMediaVisibilityChangedListener(new NotificationPanelViewController$16$$ExternalSyntheticLambda1(this));
                NotificationPanelViewController.this.mLockscreenShadeTransitionController.setQS(NotificationPanelViewController.this.mQs);
                NotificationPanelViewController.this.mNotificationStackScrollLayoutController.setQsContainer((ViewGroup) NotificationPanelViewController.this.mQs.getView());
                NotificationPanelViewController notificationPanelViewController6 = NotificationPanelViewController.this;
                notificationPanelViewController6.mQs.setScrollListener(notificationPanelViewController6.mScrollListener);
                NotificationPanelViewController notificationPanelViewController7 = NotificationPanelViewController.this;
                if (notificationPanelViewController7.mIsPrcCustom) {
                    View unused = notificationPanelViewController7.mPrcContainer = notificationPanelViewController7.mQsFrame.findViewById(R$id.qs_prc_panel_container);
                }
                NotificationPanelViewController.this.updateQsExpansion();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onFragmentViewCreated$0(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (i4 - i2 != i8 - i6) {
                    NotificationPanelViewController.this.mHeightListener.onQsHeightChanged();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onFragmentViewCreated$1(Boolean bool) {
                if (NotificationPanelViewController.this.mQs.getHeader().isShown()) {
                    NotificationPanelViewController.this.animateNextNotificationBounds(360, 0);
                    NotificationPanelViewController.this.mNotificationStackScrollLayoutController.animateNextTopPaddingChange();
                }
            }

            public void onFragmentViewDestroyed(String str, Fragment fragment) {
                NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                if (fragment == notificationPanelViewController.mQs) {
                    notificationPanelViewController.mQs = null;
                }
            }
        };
        this.mContext = notificationPanelView.getContext();
        this.mView = notificationPanelView2;
        this.mVibratorHelper = vibratorHelper;
        this.mKeyguardMediaController = keyguardMediaController;
        this.mPrivacyDotViewController = privacyDotViewController;
        this.mQuickAccessWalletController = quickAccessWalletController;
        this.mControlsComponent = controlsComponent;
        this.mMetricsLogger = metricsLogger;
        this.mActivityManager = activityManager;
        this.mConfigurationController = configurationController;
        this.mFlingAnimationUtilsBuilder = provider;
        this.mMediaHierarchyManager = mediaHierarchyManager;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mNotificationStackScrollLayoutController = notificationStackScrollLayoutController;
        this.mGroupManager = notificationGroupManagerLegacy;
        this.mNotificationIconAreaController = notificationIconAreaController;
        this.mKeyguardStatusViewComponentFactory = factory;
        this.mKeyguardStatusBarViewComponentFactory = factory4;
        this.mDepthController = notificationShadeDepthController;
        this.mFeatureFlags = featureFlags2;
        this.mContentResolver = contentResolver;
        this.mKeyguardQsUserSwitchComponentFactory = factory2;
        this.mKeyguardUserSwitcherComponentFactory = factory3;
        this.mQSDetailDisplayer = qSDetailDisplayer;
        this.mFragmentService = fragmentService;
        this.mSettingsChangeObserver = new SettingsChangeObserver(handler);
        this.mShouldUseSplitNotificationShade = Utils.shouldUseSplitNotificationShade(featureFlags2, this.mResources);
        notificationPanelView2.setWillNotDraw(true);
        this.mLayoutInflater = layoutInflater;
        this.mFalsingManager = falsingManager;
        this.mFalsingCollector = falsingCollector;
        this.mPowerManager = powerManager;
        this.mWakeUpCoordinator = notificationWakeUpCoordinator;
        this.mAccessibilityManager = accessibilityManager;
        notificationPanelView2.setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        setPanelAlpha(255, false);
        this.mCommandQueue = commandQueue;
        this.mRecordingController = recordingController;
        this.mDisplayId = i;
        this.mPulseExpansionHandler = pulseExpansionHandler2;
        this.mDozeParameters = dozeParameters;
        this.mBiometricUnlockController = biometricUnlockController;
        ScrimController scrimController3 = scrimController;
        this.mScrimController = scrimController3;
        scrimController3.setClipsQsScrim(true ^ this.mShouldUseSplitNotificationShade);
        this.mUserManager = userManager;
        this.mMediaDataManager = mediaDataManager;
        this.mTapAgainViewController = tapAgainViewController;
        this.mUiExecutor = executor;
        this.mSecureSettings = secureSettings;
        pulseExpansionHandler2.setPulseExpandAbortListener(new NotificationPanelViewController$$ExternalSyntheticLambda9(this));
        this.mThemeResId = notificationPanelView.getContext().getThemeResId();
        this.mKeyguardBypassController = keyguardBypassController;
        this.mUpdateMonitor = keyguardUpdateMonitor;
        this.mFirstBypassAttempt = keyguardBypassController.getBypassEnabled();
        C18574 r0 = new KeyguardStateController.Callback() {
            public void onKeyguardFadingAwayChanged() {
                if (!NotificationPanelViewController.this.mKeyguardStateController.isKeyguardFadingAway()) {
                    boolean unused = NotificationPanelViewController.this.mFirstBypassAttempt = false;
                    boolean unused2 = NotificationPanelViewController.this.mDelayShowingKeyguardStatusBar = false;
                }
            }
        };
        LockscreenShadeTransitionController lockscreenShadeTransitionController3 = lockscreenShadeTransitionController;
        this.mLockscreenShadeTransitionController = lockscreenShadeTransitionController3;
        lockscreenShadeTransitionController3.setNotificationPanelController(this);
        this.mKeyguardStateController.addCallback(r0);
        dynamicPrivacyController.addListener(new DynamicPrivacyControlListener());
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        this.mBottomAreaShadeAlphaAnimator = ofFloat;
        ofFloat.addUpdateListener(new NotificationPanelViewController$$ExternalSyntheticLambda0(this));
        ofFloat.setDuration(160);
        ofFloat.setInterpolator(interpolator);
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mEntryManager = notificationEntryManager;
        this.mConversationNotificationManager = conversationNotificationManager;
        this.mAuthController = authController;
        this.mLockIconViewController = lockIconViewController;
        this.mUnlockedScreenOffAnimationController = unlockedScreenOffAnimationController;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mIsGestureNavigation = QuickStepContract.isGesturalMode(navigationModeController.addListener(new NotificationPanelViewController$$ExternalSyntheticLambda4(this)));
        notificationPanelView2.setBackgroundColor(0);
        OnAttachStateChangeListener onAttachStateChangeListener = new OnAttachStateChangeListener();
        notificationPanelView2.addOnAttachStateChangeListener(onAttachStateChangeListener);
        if (notificationPanelView.isAttachedToWindow()) {
            onAttachStateChangeListener.onViewAttachedToWindow(notificationPanelView2);
        }
        notificationPanelView2.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener());
        this.mMaxKeyguardNotifications = resources.getInteger(R$integer.keyguard_max_notification_count);
        updateUserSwitcherFlags();
        onFinishInflate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        C1129QS qs = this.mQs;
        if (qs != null) {
            qs.animateHeaderSlidingOut();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(ValueAnimator valueAnimator) {
        this.mBottomAreaShadeAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateKeyguardBottomAreaAlpha();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(int i) {
        this.mIsGestureNavigation = QuickStepContract.isGesturalMode(i);
    }

    private void onFinishInflate() {
        KeyguardUserSwitcherView keyguardUserSwitcherView;
        loadDimens();
        boolean isBelowCarrierName = MotoFeature.getInstance(this.mContext).isBelowCarrierName();
        this.mIsBellowCarrierName = isBelowCarrierName;
        if (isBelowCarrierName) {
            this.mCarrierLabel = this.mView.findViewById(R$id.keyguard_carrier_text);
        }
        this.mKeyguardStatusBar = (KeyguardStatusBarView) this.mView.findViewById(R$id.keyguard_header);
        this.mBigClockContainer = (ViewGroup) this.mView.findViewById(R$id.big_clock_container);
        UserAvatarView userAvatarView = null;
        if (!this.mKeyguardUserSwitcherEnabled || !this.mUserManager.isUserSwitcherEnabled()) {
            keyguardUserSwitcherView = null;
        } else if (this.mKeyguardQsUserSwitchEnabled) {
            userAvatarView = (UserAvatarView) ((ViewStub) this.mView.findViewById(R$id.keyguard_qs_user_switch_stub)).inflate();
            keyguardUserSwitcherView = null;
        } else {
            keyguardUserSwitcherView = (KeyguardUserSwitcherView) ((ViewStub) this.mView.findViewById(R$id.keyguard_user_switcher_stub)).inflate();
        }
        updateViewControllers((KeyguardStatusView) this.mView.findViewById(R$id.keyguard_status_view), userAvatarView, this.mKeyguardStatusBar, keyguardUserSwitcherView);
        this.mNotificationContainerParent = (NotificationsQuickSettingsContainer) this.mView.findViewById(R$id.notification_container_parent);
        this.mNotificationStackScrollLayoutController.attach((NotificationStackScrollLayout) this.mView.findViewById(R$id.notification_stack_scroller));
        this.mNotificationStackScrollLayoutController.setOnHeightChangedListener(this.mOnHeightChangedListener);
        this.mNotificationStackScrollLayoutController.setOverscrollTopChangedListener(this.mOnOverscrollTopChangedListener);
        this.mNotificationStackScrollLayoutController.setOnScrollListener(new NotificationPanelViewController$$ExternalSyntheticLambda15(this));
        this.mNotificationStackScrollLayoutController.setOnStackYChanged(new NotificationPanelViewController$$ExternalSyntheticLambda14(this));
        this.mNotificationStackScrollLayoutController.setOnEmptySpaceClickListener(this.mOnEmptySpaceClickListener);
        NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.mNotificationStackScrollLayoutController;
        Objects.requireNonNull(notificationStackScrollLayoutController);
        addTrackingHeadsUpListener(new NotificationPanelViewController$$ExternalSyntheticLambda12(notificationStackScrollLayoutController));
        this.mKeyguardBottomArea = (KeyguardBottomAreaView) this.mView.findViewById(R$id.keyguard_bottom_area);
        ViewGroup viewGroup = (ViewGroup) this.mView.findViewById(R$id.preview_container);
        this.mPreviewContainer = viewGroup;
        this.mKeyguardBottomArea.setPreviewContainer(viewGroup);
        this.mLastOrientation = this.mResources.getConfiguration().orientation;
        initBottomArea();
        this.mWakeUpCoordinator.setStackScroller(this.mNotificationStackScrollLayoutController);
        this.mQsFrame = (FrameLayout) this.mView.findViewById(R$id.qs_frame);
        this.mPulseExpansionHandler.setUp(this.mNotificationStackScrollLayoutController);
        this.mWakeUpCoordinator.addListener(new NotificationWakeUpCoordinator.WakeUpListener() {
            public void onFullyHiddenChanged(boolean z) {
                NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
            }

            public void onPulseExpansionChanged(boolean z) {
                if (NotificationPanelViewController.this.mKeyguardBypassController.getBypassEnabled()) {
                    NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                }
            }
        });
        this.mView.setRtlChangeListener(new NotificationPanelViewController$$ExternalSyntheticLambda6(this));
        this.mView.setAccessibilityDelegate(this.mAccessibilityDelegate);
        if (this.mShouldUseSplitNotificationShade) {
            updateResources();
        }
        this.mTapAgainViewController.init();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$7(int i) {
        if (i != this.mOldLayoutDirection) {
            this.mAffordanceHelper.onRtlPropertiesChanged();
            this.mOldLayoutDirection = i;
        }
    }

    /* access modifiers changed from: protected */
    public void loadDimens() {
        super.loadDimens();
        this.mFlingAnimationUtils = this.mFlingAnimationUtilsBuilder.get().setMaxLengthSeconds(0.4f).build();
        this.mStatusBarMinHeight = this.mResources.getDimensionPixelSize(17105533);
        this.mStatusBarHeaderHeightKeyguard = this.mResources.getDimensionPixelSize(R$dimen.status_bar_header_height_keyguard);
        this.mQsPeekHeight = this.mResources.getDimensionPixelSize(R$dimen.qs_peek_height);
        this.mNotificationsHeaderCollideDistance = this.mResources.getDimensionPixelSize(R$dimen.header_notifications_collide_distance);
        this.mClockPositionAlgorithm.loadDimens(this.mResources);
        this.mQsFalsingThreshold = this.mResources.getDimensionPixelSize(R$dimen.qs_falsing_threshold);
        this.mPositionMinSideMargin = this.mResources.getDimensionPixelSize(R$dimen.notification_panel_min_side_margin);
        this.mIndicationBottomPadding = this.mResources.getDimensionPixelSize(R$dimen.keyguard_indication_bottom_padding);
        this.mShelfHeight = this.mResources.getDimensionPixelSize(R$dimen.notification_shelf_height);
        this.mDarkIconSize = this.mResources.getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size_dark);
        this.mHeadsUpInset = this.mResources.getDimensionPixelSize(17105533) + this.mResources.getDimensionPixelSize(R$dimen.heads_up_status_bar_padding);
        this.mDistanceForQSFullShadeTransition = this.mResources.getDimensionPixelSize(R$dimen.lockscreen_shade_qs_transition_distance);
        this.mMaxOverscrollAmountForPulse = this.mResources.getDimensionPixelSize(R$dimen.pulse_expansion_max_top_overshoot);
        this.mScrimCornerRadius = this.mResources.getDimensionPixelSize(R$dimen.notification_scrim_corner_radius);
        this.mScreenCornerRadius = (int) ScreenDecorationsUtils.getWindowCornerRadius(this.mResources);
        this.mLockscreenNotificationQSPadding = this.mResources.getDimensionPixelSize(R$dimen.notification_side_paddings);
        if (this.mIsPrcCustom) {
            this.mHeaderHeight = this.mResources.getDimensionPixelSize(R$dimen.statusbar_header_height);
            this.mSwitchRegionHeight = this.mResources.getDimensionPixelSize(R$dimen.zz_moto_prc_switch_region_height);
            this.mSwitchThreshold = this.mResources.getDimensionPixelSize(R$dimen.zz_moto_prc_switch_threshold);
        }
    }

    private void updateViewControllers(KeyguardStatusView keyguardStatusView, UserAvatarView userAvatarView, KeyguardStatusBarView keyguardStatusBarView, KeyguardUserSwitcherView keyguardUserSwitcherView) {
        KeyguardStatusViewController keyguardStatusViewController = this.mKeyguardStatusViewComponentFactory.build(keyguardStatusView).getKeyguardStatusViewController();
        this.mKeyguardStatusViewController = keyguardStatusViewController;
        keyguardStatusViewController.init();
        KeyguardStatusBarViewController keyguardStatusBarViewController = this.mKeyguardStatusBarViewComponentFactory.build(keyguardStatusBarView).getKeyguardStatusBarViewController();
        this.mKeyguarStatusBarViewController = keyguardStatusBarViewController;
        keyguardStatusBarViewController.init();
        KeyguardUserSwitcherController keyguardUserSwitcherController = this.mKeyguardUserSwitcherController;
        if (keyguardUserSwitcherController != null) {
            keyguardUserSwitcherController.closeSwitcherIfOpenAndNotSimple(false);
        }
        this.mKeyguardQsUserSwitchController = null;
        this.mKeyguardUserSwitcherController = null;
        if (userAvatarView != null) {
            KeyguardQsUserSwitchController keyguardQsUserSwitchController = this.mKeyguardQsUserSwitchComponentFactory.build(userAvatarView).getKeyguardQsUserSwitchController();
            this.mKeyguardQsUserSwitchController = keyguardQsUserSwitchController;
            keyguardQsUserSwitchController.setNotificationPanelViewController(this);
            this.mKeyguardQsUserSwitchController.init();
            this.mKeyguardStatusBar.setKeyguardUserSwitcherEnabled(true);
        } else if (keyguardUserSwitcherView != null) {
            KeyguardUserSwitcherController keyguardUserSwitcherController2 = this.mKeyguardUserSwitcherComponentFactory.build(keyguardUserSwitcherView).getKeyguardUserSwitcherController();
            this.mKeyguardUserSwitcherController = keyguardUserSwitcherController2;
            keyguardUserSwitcherController2.init();
            this.mKeyguardStatusBar.setKeyguardUserSwitcherEnabled(true);
        } else {
            this.mKeyguardStatusBar.setKeyguardUserSwitcherEnabled(false);
        }
    }

    public boolean hasCustomClock() {
        return this.mKeyguardStatusViewController.hasCustomClock();
    }

    private void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
        this.mKeyguardBottomArea.setStatusBar(statusBar);
    }

    public void setPanelExpansionListener(NetworkStateTracker.PanelViewExpansionListener panelViewExpansionListener) {
        this.mPanelViewExpansionListener = panelViewExpansionListener;
    }

    public void updateResources() {
        this.mQuickQsOffsetHeight = (float) this.mResources.getDimensionPixelSize(17105483);
        this.mSplitShadeNotificationsTopPadding = this.mResources.getDimensionPixelSize(R$dimen.notifications_top_padding_split_shade);
        int dimensionPixelSize = this.mResources.getDimensionPixelSize(R$dimen.qs_panel_width);
        int dimensionPixelSize2 = this.mResources.getDimensionPixelSize(R$dimen.notification_panel_width);
        boolean shouldUseSplitNotificationShade = Utils.shouldUseSplitNotificationShade(this.mFeatureFlags, this.mResources);
        this.mShouldUseSplitNotificationShade = shouldUseSplitNotificationShade;
        this.mScrimController.setClipsQsScrim(!shouldUseSplitNotificationShade);
        C1129QS qs = this.mQs;
        if (qs != null) {
            qs.setTranslateWhileExpanding(this.mShouldUseSplitNotificationShade);
        }
        ensureAllViewsHaveIds(this.mNotificationContainerParent);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) this.mNotificationContainerParent);
        if (this.mShouldUseSplitNotificationShade) {
            int i = R$id.qs_frame;
            int i2 = R$id.qs_edge_guideline;
            constraintSet.connect(i, 7, i2, 7);
            constraintSet.connect(R$id.notification_stack_scroller, 6, i2, 6);
            constraintSet.connect(R$id.keyguard_status_view, 7, i2, 7);
            dimensionPixelSize = 0;
            dimensionPixelSize2 = 0;
        } else {
            constraintSet.connect(R$id.qs_frame, 7, 0, 7);
            constraintSet.connect(R$id.notification_stack_scroller, 6, 0, 6);
            constraintSet.connect(R$id.keyguard_status_view, 7, 0, 7);
        }
        constraintSet.getConstraint(R$id.notification_stack_scroller).layout.mWidth = dimensionPixelSize2;
        constraintSet.getConstraint(R$id.qs_frame).layout.mWidth = dimensionPixelSize;
        constraintSet.applyTo(this.mNotificationContainerParent);
        this.mKeyguardMediaController.refreshMediaPosition();
    }

    private static void ensureAllViewsHaveIds(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getId() == -1) {
                childAt.setId(View.generateViewId());
            }
        }
    }

    private View reInflateStub(int i, int i2, int i3, boolean z) {
        View findViewById = this.mView.findViewById(i);
        if (findViewById == null) {
            return z ? ((ViewStub) this.mView.findViewById(i2)).inflate() : findViewById;
        }
        int indexOfChild = this.mView.indexOfChild(findViewById);
        this.mView.removeView(findViewById);
        if (z) {
            View inflate = this.mLayoutInflater.inflate(i3, this.mView, false);
            this.mView.addView(inflate, indexOfChild);
            return inflate;
        }
        ViewStub viewStub = new ViewStub(this.mView.getContext(), i3);
        viewStub.setId(i2);
        this.mView.addView(viewStub, indexOfChild);
        return null;
    }

    /* access modifiers changed from: private */
    public void reInflateViews() {
        NotificationsQuickSettingsContainer notificationsQuickSettingsContainer = this.mNotificationContainerParent;
        int i = R$id.keyguard_status_view;
        KeyguardStatusView keyguardStatusView = (KeyguardStatusView) notificationsQuickSettingsContainer.findViewById(i);
        int indexOfChild = this.mNotificationContainerParent.indexOfChild(keyguardStatusView);
        this.mNotificationContainerParent.removeView(keyguardStatusView);
        KeyguardStatusView keyguardStatusView2 = (KeyguardStatusView) this.mLayoutInflater.inflate(R$layout.keyguard_status_view, this.mNotificationContainerParent, false);
        this.mNotificationContainerParent.addView(keyguardStatusView2, indexOfChild);
        attachSplitShadeMediaPlayerContainer((FrameLayout) keyguardStatusView2.findViewById(R$id.status_view_media_container));
        updateResources();
        updateUserSwitcherFlags();
        boolean isUserSwitcherEnabled = this.mUserManager.isUserSwitcherEnabled();
        boolean z = this.mKeyguardQsUserSwitchEnabled;
        boolean z2 = true;
        boolean z3 = z && isUserSwitcherEnabled;
        if (z || !this.mKeyguardUserSwitcherEnabled || !isUserSwitcherEnabled) {
            z2 = false;
        }
        this.mBigClockContainer.removeAllViews();
        updateViewControllers((KeyguardStatusView) this.mView.findViewById(i), (UserAvatarView) reInflateStub(R$id.keyguard_qs_user_switch_view, R$id.keyguard_qs_user_switch_stub, R$layout.keyguard_qs_user_switch, z3), this.mKeyguardStatusBar, (KeyguardUserSwitcherView) reInflateStub(R$id.keyguard_user_switcher_view, R$id.keyguard_user_switcher_stub, R$layout.keyguard_user_switcher, z2));
        int indexOfChild2 = this.mView.indexOfChild(this.mKeyguardBottomArea);
        this.mView.removeView(this.mKeyguardBottomArea);
        KeyguardBottomAreaView keyguardBottomAreaView = this.mKeyguardBottomArea;
        KeyguardBottomAreaView keyguardBottomAreaView2 = (KeyguardBottomAreaView) this.mLayoutInflater.inflate(R$layout.keyguard_bottom_area, this.mView, false);
        this.mKeyguardBottomArea = keyguardBottomAreaView2;
        keyguardBottomAreaView2.initFrom(keyguardBottomAreaView);
        this.mKeyguardBottomArea.setPreviewContainer(this.mPreviewContainer);
        this.mView.addView(this.mKeyguardBottomArea, indexOfChild2);
        initBottomArea();
        this.mKeyguardIndicationController.setIndicationArea(this.mKeyguardBottomArea);
        this.mStatusBarStateListener.onDozeAmountChanged(this.mStatusBarStateController.getDozeAmount(), this.mStatusBarStateController.getInterpolatedDozeAmount());
        KeyguardStatusBarView keyguardStatusBarView = this.mKeyguardStatusBar;
        if (keyguardStatusBarView != null) {
            keyguardStatusBarView.onThemeChanged();
        }
        KeyguardStatusViewController keyguardStatusViewController = this.mKeyguardStatusViewController;
        int i2 = this.mBarState;
        keyguardStatusViewController.setKeyguardStatusViewVisibility(i2, false, false, i2, this.mStatusBar.isForceShowBouncer());
        KeyguardQsUserSwitchController keyguardQsUserSwitchController = this.mKeyguardQsUserSwitchController;
        if (keyguardQsUserSwitchController != null) {
            int i3 = this.mBarState;
            keyguardQsUserSwitchController.setKeyguardQsUserSwitchVisibility(i3, false, false, i3);
        }
        KeyguardUserSwitcherController keyguardUserSwitcherController = this.mKeyguardUserSwitcherController;
        if (keyguardUserSwitcherController != null) {
            int i4 = this.mBarState;
            keyguardUserSwitcherController.setKeyguardUserSwitcherVisibility(i4, false, false, i4);
        }
        setKeyguardBottomAreaVisibility(this.mBarState, false);
    }

    private void attachSplitShadeMediaPlayerContainer(FrameLayout frameLayout) {
        this.mKeyguardMediaController.attachSplitShadeContainer(frameLayout);
    }

    private void initBottomArea() {
        KeyguardAffordanceHelper keyguardAffordanceHelper = new KeyguardAffordanceHelper(this.mKeyguardAffordanceHelperCallback, this.mView.getContext(), this.mFalsingManager);
        this.mAffordanceHelper = keyguardAffordanceHelper;
        this.mKeyguardBottomArea.setAffordanceHelper(keyguardAffordanceHelper);
        this.mKeyguardBottomArea.setStatusBar(this.mStatusBar);
        this.mKeyguardBottomArea.setUserSetupComplete(this.mUserSetupComplete);
        this.mKeyguardBottomArea.setFalsingManager(this.mFalsingManager);
        this.mKeyguardBottomArea.initWallet(this.mQuickAccessWalletController);
        this.mKeyguardBottomArea.initControls(this.mControlsComponent);
    }

    /* access modifiers changed from: private */
    public void updateMaxDisplayedNotifications(boolean z) {
        if (z) {
            this.mMaxAllowedKeyguardNotifications = Math.max(computeMaxKeyguardNotifications(), 1);
        }
        if (!this.mKeyguardShowing || this.mKeyguardBypassController.getBypassEnabled()) {
            this.mNotificationStackScrollLayoutController.setMaxDisplayedNotifications(-1);
        } else {
            this.mNotificationStackScrollLayoutController.setMaxDisplayedNotifications(this.mMaxAllowedKeyguardNotifications);
        }
    }

    public void setKeyguardIndicationController(KeyguardIndicationController keyguardIndicationController) {
        this.mKeyguardIndicationController = keyguardIndicationController;
        keyguardIndicationController.setIndicationArea(this.mKeyguardBottomArea);
    }

    /* access modifiers changed from: private */
    public void updateGestureExclusionRect() {
        List list;
        Rect calculateGestureExclusionRect = calculateGestureExclusionRect();
        NotificationPanelView notificationPanelView = this.mView;
        if (calculateGestureExclusionRect.isEmpty()) {
            list = Collections.EMPTY_LIST;
        } else {
            list = Collections.singletonList(calculateGestureExclusionRect);
        }
        notificationPanelView.setSystemGestureExclusionRects(list);
    }

    private Rect calculateGestureExclusionRect() {
        Region calculateTouchableRegion = this.mStatusBarTouchableRegionManager.calculateTouchableRegion();
        Rect bounds = (!isFullyCollapsed() || calculateTouchableRegion == null) ? null : calculateTouchableRegion.getBounds();
        return bounds != null ? bounds : EMPTY_RECT;
    }

    /* access modifiers changed from: private */
    public void setIsFullWidth(boolean z) {
        this.mIsFullWidth = z;
        this.mNotificationStackScrollLayoutController.setIsFullWidth(z);
    }

    /* access modifiers changed from: private */
    public void startQsSizeChangeAnimation(int i, int i2) {
        ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
        if (valueAnimator != null) {
            i = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            this.mQsSizeChangeAnimator.cancel();
        }
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i, i2});
        this.mQsSizeChangeAnimator = ofInt;
        ofInt.setDuration(300);
        this.mQsSizeChangeAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mQsSizeChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
                NotificationPanelViewController.this.mQs.setHeightOverride(((Integer) NotificationPanelViewController.this.mQsSizeChangeAnimator.getAnimatedValue()).intValue());
            }
        });
        this.mQsSizeChangeAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ValueAnimator unused = NotificationPanelViewController.this.mQsSizeChangeAnimator = null;
            }
        });
        this.mQsSizeChangeAnimator.start();
    }

    /* access modifiers changed from: private */
    public void positionClockAndNotifications() {
        positionClockAndNotifications(false);
    }

    private void positionClockAndNotifications(boolean z) {
        int i;
        boolean isAddOrRemoveAnimationPending = this.mNotificationStackScrollLayoutController.isAddOrRemoveAnimationPending();
        boolean isOnKeyguard = isOnKeyguard();
        if (isOnKeyguard || z) {
            updateClockAppearance();
        }
        if (!isOnKeyguard) {
            i = getUnlockedStackScrollerPadding();
        } else {
            i = this.mClockPositionResult.stackScrollerPaddingExpanded;
        }
        this.mNotificationStackScrollLayoutController.setIntrinsicPadding(i);
        this.mKeyguardBottomArea.setAntiBurnInOffsetX(this.mClockPositionResult.clockX);
        this.mStackScrollerMeasuringPass++;
        requestScrollerTopPaddingUpdate(isAddOrRemoveAnimationPending);
        this.mStackScrollerMeasuringPass = 0;
        this.mAnimateNextPositionUpdate = false;
    }

    private void updateClockAppearance() {
        float f;
        float f2;
        int height = this.mView.getHeight();
        int max = Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding);
        int i = this.mStatusBarHeaderHeightKeyguard;
        boolean bypassEnabled = this.mKeyguardBypassController.getBypassEnabled();
        boolean z = this.mNotificationStackScrollLayoutController.getVisibleNotificationCount() != 0 || this.mMediaDataManager.hasActiveMedia();
        this.mKeyguardStatusViewController.setHasVisibleNotifications(z);
        KeyguardQsUserSwitchController keyguardQsUserSwitchController = this.mKeyguardQsUserSwitchController;
        int userIconHeight = keyguardQsUserSwitchController != null ? keyguardQsUserSwitchController.getUserIconHeight() : 0;
        if (this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying()) {
            f = 1.0f;
        } else {
            f = getExpandedFraction();
        }
        if (this.mUnlockedScreenOffAnimationController.isScreenOffAnimationPlaying()) {
            f2 = 1.0f;
        } else {
            f2 = this.mInterpolatedDarkAmount;
        }
        this.mClockPositionAlgorithm.setup(this.mStatusBarHeaderHeightKeyguard, height - max, this.mNotificationStackScrollLayoutController.getIntrinsicContentHeight(), f, height, this.mKeyguardStatusViewController.getLockscreenHeight(), userIconHeight, i, hasCustomClock(), z, f2, this.mOverStretchAmount, bypassEnabled, getUnlockedStackScrollerPadding(), computeQsExpansionFraction(), this.mDisplayTopInset, this.mShouldUseSplitNotificationShade);
        this.mClockPositionAlgorithm.run(this.mClockPositionResult);
        boolean z2 = this.mNotificationStackScrollLayoutController.isAddOrRemoveAnimationPending() || this.mAnimateNextPositionUpdate;
        KeyguardStatusViewController keyguardStatusViewController = this.mKeyguardStatusViewController;
        KeyguardClockPositionAlgorithm.Result result = this.mClockPositionResult;
        keyguardStatusViewController.updatePosition(result.clockX, result.clockY, result.clockScale, z2);
        KeyguardQsUserSwitchController keyguardQsUserSwitchController2 = this.mKeyguardQsUserSwitchController;
        if (keyguardQsUserSwitchController2 != null) {
            KeyguardClockPositionAlgorithm.Result result2 = this.mClockPositionResult;
            keyguardQsUserSwitchController2.updatePosition(result2.clockX, result2.userSwitchY, z2);
        }
        KeyguardUserSwitcherController keyguardUserSwitcherController = this.mKeyguardUserSwitcherController;
        if (keyguardUserSwitcherController != null) {
            KeyguardClockPositionAlgorithm.Result result3 = this.mClockPositionResult;
            keyguardUserSwitcherController.updatePosition(result3.clockX, result3.userSwitchY, z2);
        }
        updateNotificationTranslucency();
        updateClock();
    }

    private int getUnlockedStackScrollerPadding() {
        C1129QS qs = this.mQs;
        return (qs != null ? qs.getHeader().getHeight() : 0) + this.mQsPeekHeight;
    }

    private int computeMaxKeyguardNotifications() {
        float f;
        int i;
        float minStackScrollerPadding = this.mClockPositionAlgorithm.getMinStackScrollerPadding();
        int max = Math.max(1, this.mResources.getDimensionPixelSize(R$dimen.notification_divider_height));
        if (this.mNotificationShelfController.getVisibility() == 8) {
            f = 0.0f;
        } else {
            f = (float) (this.mNotificationShelfController.getIntrinsicHeight() + max);
        }
        float height = ((((float) this.mNotificationStackScrollLayoutController.getHeight()) - minStackScrollerPadding) - f) - Math.max(this.mLockIconViewController.getTop() != 0.0f ? (this.mStatusBar.getDisplayHeight() - this.mLockIconViewController.getTop()) + ((float) this.mResources.getDimensionPixelSize(R$dimen.min_lock_icon_padding)) : 0.0f, (float) Math.max(this.mIndicationBottomPadding, this.mAmbientIndicationBottomPadding));
        ExpandableView expandableView = null;
        int i2 = 0;
        int i3 = 0;
        while (i2 < this.mNotificationStackScrollLayoutController.getChildCount()) {
            ExpandableView childAt = this.mNotificationStackScrollLayoutController.getChildAt(i2);
            if (childAt instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) childAt;
                NotificationGroupManagerLegacy notificationGroupManagerLegacy = this.mGroupManager;
                if (notificationGroupManagerLegacy != null && notificationGroupManagerLegacy.isSummaryOfSuppressedGroup(expandableNotificationRow.getEntry().getSbn())) {
                    continue;
                } else if (!canShowViewOnLockscreen(childAt)) {
                    continue;
                } else if (expandableNotificationRow.isRemoved()) {
                    continue;
                }
                i2++;
            } else {
                if (!(childAt instanceof MediaHeaderView)) {
                    continue;
                } else if (childAt.getVisibility() == 8) {
                    continue;
                } else if (childAt.getIntrinsicHeight() == 0) {
                    continue;
                }
                i2++;
            }
            height = ((height - ((float) childAt.getMinHeight(true))) - (i3 == 0 ? 0.0f : (float) max)) - this.mNotificationStackScrollLayoutController.calculateGapHeight(expandableView, childAt, i3);
            if (height >= 0.0f && ((i = this.mMaxKeyguardNotifications) == -1 || i3 < i)) {
                i3++;
                expandableView = childAt;
                i2++;
            } else if (height <= (-f)) {
                return i3;
            } else {
                int childCount = this.mNotificationStackScrollLayoutController.getChildCount();
                for (int i4 = i2 + 1; i4 < childCount; i4++) {
                    ExpandableView childAt2 = this.mNotificationStackScrollLayoutController.getChildAt(i4);
                    if ((childAt2 instanceof ExpandableNotificationRow) && canShowViewOnLockscreen(childAt2)) {
                        return i3;
                    }
                }
                return i3 + 1;
            }
        }
        return i3;
    }

    private boolean canShowViewOnLockscreen(ExpandableView expandableView) {
        if (expandableView.hasNoContentHeight()) {
            return false;
        }
        if ((!(expandableView instanceof ExpandableNotificationRow) || canShowRowOnLockscreen((ExpandableNotificationRow) expandableView)) && expandableView.getVisibility() != 8) {
            return true;
        }
        return false;
    }

    private boolean canShowRowOnLockscreen(ExpandableNotificationRow expandableNotificationRow) {
        NotificationGroupManagerLegacy notificationGroupManagerLegacy = this.mGroupManager;
        return !(notificationGroupManagerLegacy != null && notificationGroupManagerLegacy.isSummaryOfSuppressedGroup(expandableNotificationRow.getEntry().getSbn())) && this.mLockscreenUserManager.shouldShowOnKeyguard(expandableNotificationRow.getEntry()) && !expandableNotificationRow.isRemoved();
    }

    private void updateClock() {
        float f = this.mClockPositionResult.clockAlpha * this.mKeyguardOnlyContentAlpha;
        this.mKeyguardStatusViewController.setAlpha(f);
        KeyguardQsUserSwitchController keyguardQsUserSwitchController = this.mKeyguardQsUserSwitchController;
        if (keyguardQsUserSwitchController != null) {
            keyguardQsUserSwitchController.setAlpha(f);
        }
        KeyguardUserSwitcherController keyguardUserSwitcherController = this.mKeyguardUserSwitcherController;
        if (keyguardUserSwitcherController != null) {
            keyguardUserSwitcherController.setAlpha(f);
        }
    }

    public void animateToFullShade(long j) {
        this.mNotificationStackScrollLayoutController.goToFullShade(j);
        this.mView.requestLayout();
        this.mAnimateNextPositionUpdate = true;
    }

    private void setQsExpansionEnabled() {
        C1129QS qs = this.mQs;
        if (qs != null) {
            qs.setHeaderClickable(isQsExpansionEnabled());
        }
    }

    public void setQsExpansionEnabledPolicy(boolean z) {
        this.mQsExpansionEnabledPolicy = z;
        setQsExpansionEnabled();
    }

    public void resetViews(boolean z) {
        if (this.mIsPrcCustom && PanelViewController.DEBUG_PANEL) {
            String str = PanelViewController.TAG;
            Log.i(str, "PrcPanel resetViews  animate = " + z);
        }
        this.mIsLaunchTransitionFinished = false;
        this.mBlockTouches = false;
        this.mUseFrontCamera = false;
        if (!this.mLaunchingAffordance) {
            this.mAffordanceHelper.reset(false);
            this.mLastCameraLaunchSource = "lockscreen_affordance";
        }
        this.mStatusBar.getGutsManager().closeAndSaveGuts(true, true, true, -1, -1, true);
        if (!z || isFullyCollapsed()) {
            closeQs();
        } else {
            animateCloseQs(true);
        }
        this.mNotificationStackScrollLayoutController.setOverScrollAmount(0.0f, true, z, !z);
        this.mNotificationStackScrollLayoutController.resetScrollPosition();
    }

    public void collapse(boolean z, float f) {
        if (canPanelBeCollapsed()) {
            if (this.mQsExpanded) {
                this.mQsExpandImmediate = true;
                this.mNotificationStackScrollLayoutController.setShouldShowShelfOnly(true);
            }
            super.collapse(z, f);
        }
    }

    public void closeQs() {
        cancelQsAnimation();
        setQsExpansion((float) this.mQsMinExpansionHeight);
    }

    public void cancelAnimation() {
        this.mView.animate().cancel();
    }

    public void animateCloseQs(boolean z) {
        if (this.mIsPrcCustom && isOpenQSState() && !z) {
            collapse(false, 1.0f);
        }
        ValueAnimator valueAnimator = this.mQsExpansionAnimator;
        if (valueAnimator != null) {
            if (this.mQsAnimatorExpand) {
                float f = this.mQsExpansionHeight;
                valueAnimator.cancel();
                setQsExpansion(f);
            } else {
                return;
            }
        }
        flingSettings(0.0f, z ? 2 : 1);
    }

    public void animateCloseQs(boolean z, Runnable runnable) {
        ValueAnimator valueAnimator = this.mQsExpansionAnimator;
        if (valueAnimator != null) {
            if (this.mQsAnimatorExpand) {
                float f = this.mQsExpansionHeight;
                valueAnimator.cancel();
                setQsExpansion(f);
            } else {
                return;
            }
        }
        flingSettings(0.0f, z ? 2 : 1, runnable, false);
    }

    /* access modifiers changed from: private */
    public boolean isQsExpansionEnabled() {
        return this.mQsExpansionEnabledPolicy && this.mQsExpansionEnabledAmbient && !this.mRemoteInputManager.getController().isRemoteInputActive();
    }

    /* access modifiers changed from: protected */
    public void cancelExpandImmediate() {
        this.mQsExpandImmediate = false;
    }

    public void expandWithQs() {
        if (isQsExpansionEnabled()) {
            this.mQsExpandImmediate = true;
            this.mNotificationStackScrollLayoutController.setShouldShowShelfOnly(true);
        }
        if (isFullyCollapsed()) {
            expand(true);
            return;
        }
        traceQsJank(true, false);
        flingSettings(0.0f, 0);
    }

    public void expandWithQsDetail(DetailAdapter detailAdapter) {
        traceQsJank(true, false);
        flingSettings(0.0f, 0);
        this.mQSDetailDisplayer.showDetailAdapter(detailAdapter, this.mQsFrame.getWidth() / 2, -getHeight());
        if (this.mAccessibilityManager.isEnabled()) {
            this.mView.setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        }
    }

    public void expandWithoutQs() {
        if (isQsExpanded()) {
            flingSettings(0.0f, 1);
        } else {
            expand(true);
        }
    }

    public void fling(float f, boolean z) {
        ((PhoneStatusBarView) this.mBar).mBar.getGestureRecorder();
        super.fling(f, z);
    }

    /* access modifiers changed from: protected */
    public void flingToHeight(float f, boolean z, float f2, float f3, boolean z2) {
        this.mHeadsUpTouchHelper.notifyFling(!z);
        this.mKeyguardStateController.notifyPanelFlingStart(!z);
        setClosingWithAlphaFadeout(!z && !isOnKeyguard() && getFadeoutAlpha() == 1.0f);
        super.flingToHeight(f, z, f2, f3, z2);
    }

    /* access modifiers changed from: private */
    public boolean onQsIntercept(MotionEvent motionEvent) {
        int pointerId;
        int findPointerIndex = motionEvent.findPointerIndex(this.mTrackingPointer);
        if (findPointerIndex < 0) {
            this.mTrackingPointer = motionEvent.getPointerId(0);
            findPointerIndex = 0;
        }
        float x = motionEvent.getX(findPointerIndex);
        float y = motionEvent.getY(findPointerIndex);
        int actionMasked = motionEvent.getActionMasked();
        int i = 1;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    float f = y - this.mInitialTouchY;
                    trackMovement(motionEvent);
                    if (this.mQsTracking) {
                        setQsExpansion(f + this.mInitialHeightOnTouch);
                        trackMovement(motionEvent);
                        return true;
                    } else if ((f > getTouchSlop(motionEvent) || (f < (-getTouchSlop(motionEvent)) && this.mQsExpanded)) && Math.abs(f) > Math.abs(x - this.mInitialTouchX) && shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, f)) {
                        this.mView.getParent().requestDisallowInterceptTouchEvent(true);
                        this.mQsTracking = true;
                        traceQsJank(true, false);
                        onQsExpansionStarted();
                        notifyExpandingFinished();
                        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                        this.mInitialTouchY = y;
                        this.mInitialTouchX = x;
                        this.mNotificationStackScrollLayoutController.cancelLongPress();
                        return true;
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked == 6 && this.mTrackingPointer == (pointerId = motionEvent.getPointerId(motionEvent.getActionIndex()))) {
                        if (motionEvent.getPointerId(0) != pointerId) {
                            i = 0;
                        }
                        this.mTrackingPointer = motionEvent.getPointerId(i);
                        this.mInitialTouchX = motionEvent.getX(i);
                        this.mInitialTouchY = motionEvent.getY(i);
                    }
                }
            }
            trackMovement(motionEvent);
            this.mQsTracking = false;
        } else {
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            initVelocityTracker();
            trackMovement(motionEvent);
            if (this.mKeyguardShowing && shouldQuickSettingsIntercept(this.mInitialTouchX, this.mInitialTouchY, 0.0f)) {
                this.mView.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.mQsExpansionAnimator != null) {
                this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                this.mQsTracking = true;
                traceQsJank(true, false);
                this.mNotificationStackScrollLayoutController.cancelLongPress();
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isInContentBounds(float f, float f2) {
        float x = this.mNotificationStackScrollLayoutController.getX();
        return !this.mNotificationStackScrollLayoutController.isBelowLastNotification(f - x, f2) && x < f && f < x + this.mNotificationStackScrollLayoutController.getWidth();
    }

    /* access modifiers changed from: private */
    public void traceQsJank(boolean z, boolean z2) {
        InteractionJankMonitor instance = InteractionJankMonitor.getInstance();
        if (z) {
            instance.begin(this.mView, 5);
        } else if (z2) {
            instance.cancel(5);
        } else {
            instance.end(5);
        }
    }

    /* access modifiers changed from: private */
    public void initDownStates(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mOnlyAffordanceInThisMotion = false;
            this.mQsTouchAboveFalsingThreshold = this.mQsFullyExpanded;
            this.mDozingOnDown = isDozing();
            this.mDownX = motionEvent.getX();
            this.mDownY = motionEvent.getY();
            this.mCollapsedOnDown = isFullyCollapsed();
            this.mIsPanelCollapseOnQQS = canPanelCollapseOnQQS(this.mDownX, this.mDownY);
            this.mListenForHeadsUp = this.mCollapsedOnDown && this.mHeadsUpManager.hasPinnedHeadsUp();
            boolean z = this.mExpectingSynthesizedDown;
            this.mAllowExpandForSmallExpansion = z;
            this.mTouchSlopExceededBeforeDown = z;
            if (z) {
                this.mLastEventSynthesizedDown = true;
            } else {
                this.mLastEventSynthesizedDown = false;
            }
        } else {
            this.mLastEventSynthesizedDown = false;
        }
    }

    private boolean canPanelCollapseOnQQS(float f, float f2) {
        if (this.mCollapsedOnDown || this.mKeyguardShowing || this.mQsExpanded) {
            return false;
        }
        C1129QS qs = this.mQs;
        View header = qs == null ? this.mKeyguardStatusBar : qs.getHeader();
        if (f < this.mQsFrame.getX() || f > this.mQsFrame.getX() + ((float) this.mQsFrame.getWidth()) || f2 > ((float) header.getBottom())) {
            return false;
        }
        return true;
    }

    private void flingQsWithCurrentVelocity(float f, boolean z) {
        float currentQSVelocity = getCurrentQSVelocity();
        boolean flingExpandsQs = flingExpandsQs(currentQSVelocity);
        int i = 0;
        if (flingExpandsQs) {
            if (this.mFalsingManager.isUnlockingDisabled() || isFalseTouch(0)) {
                flingExpandsQs = false;
            } else {
                logQsSwipeDown(f);
            }
        } else if (currentQSVelocity < 0.0f) {
            this.mFalsingManager.isFalseTouch(12);
        }
        int i2 = (!flingExpandsQs || z) ? 1 : 0;
        if (!this.mIsPrcCustom || !isOpenQSState() || i2 != 1) {
            i = i2;
        } else if (z) {
            return;
        }
        flingSettings(currentQSVelocity, i);
    }

    private void logQsSwipeDown(float f) {
        this.mLockscreenGestureLogger.write(this.mBarState == 1 ? 193 : 194, (int) ((f - this.mInitialTouchY) / this.mStatusBar.getDisplayDensity()), (int) (getCurrentQSVelocity() / this.mStatusBar.getDisplayDensity()));
    }

    private boolean flingExpandsQs(float f) {
        if (Math.abs(f) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
            if (computeQsExpansionFraction() > 0.5f) {
                return true;
            }
            return false;
        } else if (f > 0.0f) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFalseTouch(int i) {
        if (this.mFalsingManager.isClassifierEnabled()) {
            return this.mFalsingManager.isFalseTouch(i);
        }
        return !this.mQsTouchAboveFalsingThreshold;
    }

    private float computeQsExpansionFraction() {
        if (this.mQSAnimatingHiddenFromCollapsed) {
            return 0.0f;
        }
        float f = this.mQsExpansionHeight;
        int i = this.mQsMinExpansionHeight;
        return Math.min(1.0f, (f - ((float) i)) / ((float) (this.mQsMaxExpansionHeight - i)));
    }

    /* access modifiers changed from: protected */
    public boolean shouldExpandWhenNotFlinging() {
        if (super.shouldExpandWhenNotFlinging()) {
            return true;
        }
        if (!this.mAllowExpandForSmallExpansion) {
            return false;
        }
        if (SystemClock.uptimeMillis() - this.mDownTime <= 300) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public float getOpeningHeight() {
        return this.mNotificationStackScrollLayoutController.getOpeningHeight();
    }

    /* access modifiers changed from: private */
    public boolean handleQsTouch(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0 && getExpandedFraction() == 1.0f && this.mBarState != 1 && !this.mQsExpanded && isQsExpansionEnabled()) {
            this.mQsTracking = true;
            traceQsJank(true, false);
            this.mConflictingQsExpansionGesture = true;
            onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = motionEvent.getX();
            this.mInitialTouchX = motionEvent.getY();
        }
        if (!isFullyCollapsed()) {
            handleQsDown(motionEvent);
        }
        if (!this.mQsExpandImmediate && this.mQsTracking) {
            onQsTouch(motionEvent);
            if (!this.mConflictingQsExpansionGesture) {
                return true;
            }
        }
        if (actionMasked == 3 || actionMasked == 1) {
            this.mConflictingQsExpansionGesture = false;
        }
        if (actionMasked == 0 && isFullyCollapsed() && isQsExpansionEnabled() && !this.mIsPrcCustom) {
            this.mTwoFingerQsExpandPossible = true;
        }
        if (this.mTwoFingerQsExpandPossible && isOpenQsEvent(motionEvent) && motionEvent.getY(motionEvent.getActionIndex()) < ((float) this.mStatusBarMinHeight)) {
            this.mMetricsLogger.count("panel_open_qs", 1);
            this.mQsExpandImmediate = true;
            this.mNotificationStackScrollLayoutController.setShouldShowShelfOnly(true);
            requestPanelHeightUpdate();
            setListening(true);
        }
        return false;
    }

    private boolean isInQsArea(float f, float f2) {
        if (f < this.mQsFrame.getX() || f > this.mQsFrame.getX() + ((float) this.mQsFrame.getWidth())) {
            return false;
        }
        if (this.mIsGestureNavigation && f2 > ((float) (this.mView.getHeight() - this.mNavigationBarBottomHeight))) {
            return false;
        }
        if (f2 <= this.mNotificationStackScrollLayoutController.getBottomMostNotificationBottom() || f2 <= this.mQs.getView().getY() + ((float) this.mQs.getView().getHeight())) {
            return true;
        }
        return false;
    }

    private boolean isOpenQsEvent(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        int actionMasked = motionEvent.getActionMasked();
        boolean z = actionMasked == 5 && pointerCount == 2;
        boolean z2 = actionMasked == 0 && (motionEvent.isButtonPressed(32) || motionEvent.isButtonPressed(64));
        boolean z3 = actionMasked == 0 && (motionEvent.isButtonPressed(2) || motionEvent.isButtonPressed(4));
        if (z || z2 || z3) {
            return true;
        }
        return false;
    }

    private void handleQsDown(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && shouldQuickSettingsIntercept(motionEvent.getX(), motionEvent.getY(), -1.0f)) {
            this.mFalsingCollector.onQsDown();
            this.mQsTracking = true;
            onQsExpansionStarted();
            this.mInitialHeightOnTouch = this.mQsExpansionHeight;
            this.mInitialTouchY = motionEvent.getX();
            this.mInitialTouchX = motionEvent.getY();
            notifyExpandingFinished();
        }
    }

    public void startWaitingForOpenPanelGesture() {
        if (isFullyCollapsed()) {
            if (this.mIsPrcCustom) {
                updatePanelViewState(2);
                this.mPrcContainer.setAlpha(0.0f);
            }
            this.mExpectingSynthesizedDown = true;
            onTrackingStarted();
            updatePanelExpanded();
        }
    }

    public void stopWaitingForOpenPanelGesture(boolean z, float f) {
        if (this.mExpectingSynthesizedDown) {
            this.mExpectingSynthesizedDown = false;
            if (z) {
                collapse(false, 1.0f);
            } else {
                maybeVibrateOnOpening();
                fling(f > 1.0f ? f * 1000.0f : 0.0f, true);
            }
            onTrackingStopped(false);
        }
    }

    /* access modifiers changed from: protected */
    public boolean flingExpands(float f, float f2, float f3, float f4) {
        boolean flingExpands = super.flingExpands(f, f2, f3, f4);
        if (this.mQsExpansionAnimator != null) {
            return true;
        }
        return flingExpands;
    }

    /* access modifiers changed from: protected */
    public boolean shouldGestureWaitForTouchSlop() {
        if (this.mExpectingSynthesizedDown) {
            this.mExpectingSynthesizedDown = false;
            return false;
        } else if (isFullyCollapsed() || this.mBarState != 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldGestureIgnoreXTouchSlop(float f, float f2) {
        return !this.mAffordanceHelper.isOnAffordanceIcon(f, f2);
    }

    private void onQsTouch(MotionEvent motionEvent) {
        int pointerId;
        int findPointerIndex = motionEvent.findPointerIndex(this.mTrackingPointer);
        boolean z = false;
        if (findPointerIndex < 0) {
            this.mTrackingPointer = motionEvent.getPointerId(0);
            findPointerIndex = 0;
        }
        float y = motionEvent.getY(findPointerIndex);
        float x = motionEvent.getX(findPointerIndex);
        float f = y - this.mInitialTouchY;
        float f2 = x - this.mInitialTouchX;
        int actionMasked = motionEvent.getActionMasked();
        boolean z2 = true;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    setQsExpansion(this.mInitialHeightOnTouch + f);
                    if (f >= ((float) getFalsingThreshold())) {
                        this.mQsTouchAboveFalsingThreshold = true;
                    }
                    trackMovement(motionEvent);
                    return;
                } else if (actionMasked != 3) {
                    if (actionMasked == 6 && this.mTrackingPointer == (pointerId = motionEvent.getPointerId(motionEvent.getActionIndex()))) {
                        if (motionEvent.getPointerId(0) == pointerId) {
                            z = true;
                        }
                        float y2 = motionEvent.getY(z ? 1 : 0);
                        float x2 = motionEvent.getX(z);
                        this.mTrackingPointer = motionEvent.getPointerId(z);
                        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
                        this.mInitialTouchY = y2;
                        this.mInitialTouchX = x2;
                        return;
                    }
                    return;
                }
            }
            this.mQsTracking = false;
            this.mTrackingPointer = -1;
            trackMovement(motionEvent);
            float computeQsExpansionFraction = computeQsExpansionFraction();
            if (this.mIsPrcCustom && isOpenQSState() && (isNeedCollapsePanleView(motionEvent, y, f, f2) || computeQsExpansionFraction == 0.0f)) {
                collapseWithCurrentVelocity();
            } else if (computeQsExpansionFraction != 0.0f || y >= this.mInitialTouchY) {
                if (motionEvent.getActionMasked() == 3) {
                    z = true;
                }
                flingQsWithCurrentVelocity(y, z);
            } else {
                if (motionEvent.getActionMasked() != 3) {
                    z2 = false;
                }
                traceQsJank(false, z2);
            }
            VelocityTracker velocityTracker = this.mQsVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.recycle();
                this.mQsVelocityTracker = null;
                return;
            }
            return;
        }
        this.mQsTracking = true;
        traceQsJank(true, false);
        this.mInitialTouchY = y;
        this.mInitialTouchX = x;
        onQsExpansionStarted();
        this.mInitialHeightOnTouch = this.mQsExpansionHeight;
        initVelocityTracker();
        trackMovement(motionEvent);
    }

    private void collapseWithCurrentVelocity() {
        flingSettings(getCurrentQSVelocity(), 2, new NotificationPanelViewController$$ExternalSyntheticLambda8(this), false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$collapseWithCurrentVelocity$8() {
        instantCollapse();
    }

    /* access modifiers changed from: package-private */
    public boolean isNeedCollapsePanleView(MotionEvent motionEvent, float f, float f2, float f3) {
        float touchSlop = getTouchSlop(motionEvent);
        if (f >= this.mInitialTouchY - touchSlop || Math.abs(f2) <= touchSlop || Math.abs(f2) <= Math.abs(f3) || getCurrentQSVelocity() >= 0.0f) {
            return false;
        }
        return true;
    }

    private int getFalsingThreshold() {
        return (int) (((float) this.mQsFalsingThreshold) * (this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f));
    }

    /* access modifiers changed from: private */
    public void setOverScrolling(boolean z) {
        this.mStackScrollerOverscrolling = z;
        C1129QS qs = this.mQs;
        if (qs != null) {
            qs.setOverscrolling(z);
        }
    }

    /* access modifiers changed from: private */
    public void onQsExpansionStarted() {
        onQsExpansionStarted(0);
    }

    /* access modifiers changed from: protected */
    public void onQsExpansionStarted(int i) {
        cancelQsAnimation();
        cancelHeightAnimator();
        float f = this.mQsExpansionHeight - ((float) i);
        setQsExpansion(f);
        requestPanelHeightUpdate();
        this.mNotificationStackScrollLayoutController.checkSnoozeLeavebehind();
        if (f == 0.0f) {
            this.mStatusBar.requestFaceAuth(false);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setQsExpanded(boolean z) {
        if (this.mQsExpanded != z) {
            if (PanelViewController.DEBUG_PANEL) {
                String str = PanelViewController.TAG;
                Log.i(str, "PrcPanel setQsExpanded = " + z);
            }
            this.mQsExpanded = z;
            updateQsState();
            requestPanelHeightUpdate();
            this.mFalsingCollector.setQsExpanded(z);
            this.mStatusBar.setQsExpanded(z);
            this.mNotificationContainerParent.setQsExpanded(z);
            this.mPulseExpansionHandler.setQsExpanded(z);
            this.mKeyguardBypassController.setQSExpanded(z);
            this.mStatusBarKeyguardViewManager.setQsExpanded(z);
            this.mLockIconViewController.setQsExpanded(z);
            this.mPrivacyDotViewController.setQsExpanded(z);
        }
    }

    /* access modifiers changed from: private */
    public void maybeAnimateBottomAreaAlpha() {
        this.mBottomAreaShadeAlphaAnimator.cancel();
        if (this.mBarState == 2) {
            this.mBottomAreaShadeAlphaAnimator.setFloatValues(new float[]{this.mBottomAreaShadeAlpha, 0.0f});
            this.mBottomAreaShadeAlphaAnimator.start();
            return;
        }
        this.mBottomAreaShadeAlpha = 1.0f;
    }

    /* access modifiers changed from: private */
    public void animateKeyguardStatusBarOut() {
        if (this.mIsBellowCarrierName) {
            this.mCarrierLabel.setVisibility(0);
            this.mCarrierLabel.setAlpha(0.0f);
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mKeyguardStatusBar.getAlpha(), 0.0f});
        ofFloat.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        ofFloat.setStartDelay(this.mKeyguardStateController.isKeyguardFadingAway() ? this.mKeyguardStateController.getKeyguardFadingAwayDelay() : 0);
        ofFloat.setDuration(this.mKeyguardStateController.isKeyguardFadingAway() ? this.mKeyguardStateController.getShortenedFadingAwayDuration() : 360);
        ofFloat.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                NotificationPanelViewController.this.mAnimateKeyguardStatusBarInvisibleEndRunnable.run();
            }
        });
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public void animateKeyguardStatusBarIn(long j) {
        this.mKeyguardStatusBar.setVisibility(0);
        this.mKeyguardStatusBar.setAlpha(0.0f);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(this.mStatusBarAnimateAlphaListener);
        ofFloat.setDuration(j);
        ofFloat.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public void setKeyguardBottomAreaVisibility(int i, boolean z) {
        this.mKeyguardBottomArea.animate().cancel();
        if (z) {
            this.mKeyguardBottomArea.animate().alpha(0.0f).setStartDelay(this.mKeyguardStateController.getKeyguardFadingAwayDelay()).setDuration(this.mKeyguardStateController.getShortenedFadingAwayDuration()).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(this.mAnimateKeyguardBottomAreaInvisibleEndRunnable).start();
        } else if (i == 1 || i == 2) {
            this.mKeyguardBottomArea.setVisibility(0);
            this.mKeyguardBottomArea.setAlpha(1.0f);
        } else {
            this.mKeyguardBottomArea.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void updateQsState() {
        this.mNotificationStackScrollLayoutController.setQsExpanded(this.mQsExpanded);
        this.mNotificationStackScrollLayoutController.setScrollingEnabled(this.mBarState != 1 && (!this.mQsExpanded || this.mQsExpansionFromOverscroll || this.mShouldUseSplitNotificationShade));
        KeyguardUserSwitcherController keyguardUserSwitcherController = this.mKeyguardUserSwitcherController;
        if (keyguardUserSwitcherController != null && this.mQsExpanded && !this.mStackScrollerOverscrolling) {
            keyguardUserSwitcherController.closeSwitcherIfOpenAndNotSimple(true);
        }
        C1129QS qs = this.mQs;
        if (qs != null) {
            qs.setExpanded(this.mQsExpanded);
        }
    }

    /* access modifiers changed from: private */
    public void setQsExpansion(float f) {
        float min = Math.min(Math.max(f, (float) this.mQsMinExpansionHeight), (float) this.mQsMaxExpansionHeight);
        if (this.mIsPrcCustom && (isOpenNotificationState() || isKeyguardNotificationState())) {
            min = (float) this.mQsMinExpansionHeight;
        }
        int i = this.mQsMaxExpansionHeight;
        this.mQsFullyExpanded = min == ((float) i) && i != 0;
        if (isOpenQSState()) {
            setQsExpanded(true);
        } else {
            int i2 = this.mQsMinExpansionHeight;
            if (min > ((float) i2) && !this.mQsExpanded && !this.mStackScrollerOverscrolling && !this.mDozing) {
                setQsExpanded(true);
            } else if (min <= ((float) i2) && this.mQsExpanded) {
                setQsExpanded(false);
            }
        }
        this.mQsExpansionHeight = min;
        updateQsExpansion();
        requestScrollerTopPaddingUpdate(false);
        updateHeaderKeyguardAlpha();
        int i3 = this.mBarState;
        if (i3 == 2 || i3 == 1) {
            updateKeyguardBottomAreaAlpha();
            positionClockAndNotifications();
            updateBigClockAlpha();
        }
        if (this.mAccessibilityManager.isEnabled()) {
            this.mView.setAccessibilityPaneTitle(determineAccessibilityPaneTitle());
        }
        if (!this.mFalsingManager.isUnlockingDisabled() && this.mQsFullyExpanded && this.mFalsingCollector.shouldEnforceBouncer()) {
            this.mStatusBar.executeRunnableDismissingKeyguard((Runnable) null, (Runnable) null, false, true, false);
        }
        for (int i4 = 0; i4 < this.mExpansionListeners.size(); i4++) {
            PanelExpansionListener panelExpansionListener = this.mExpansionListeners.get(i4);
            int i5 = this.mQsMaxExpansionHeight;
            panelExpansionListener.onQsExpansionChanged(i5 != 0 ? this.mQsExpansionHeight / ((float) i5) : 0.0f);
        }
        NetworkStateTracker.PanelViewExpansionListener panelViewExpansionListener = this.mPanelViewExpansionListener;
        if (panelViewExpansionListener != null) {
            panelViewExpansionListener.updateExpansion(min);
        }
    }

    /* access modifiers changed from: private */
    public void updateQsExpansion() {
        if (this.mQs != null) {
            float computeQsExpansionFraction = computeQsExpansionFraction();
            this.mQs.setQsExpansion(computeQsExpansionFraction, getHeaderTranslation());
            this.mMediaHierarchyManager.setQsExpansion(computeQsExpansionFraction);
            int calculateQsBottomPosition = calculateQsBottomPosition(computeQsExpansionFraction);
            if (this.mIsPrcCustom) {
                this.mScrimController.setPanelState(this.mPanelState);
            }
            this.mScrimController.setQsPosition(computeQsExpansionFraction, calculateQsBottomPosition);
            setQSClippingBounds();
            this.mNotificationStackScrollLayoutController.setQsExpansionFraction(computeQsExpansionFraction);
            this.mDepthController.setQsPanelExpansion(computeQsExpansionFraction);
        }
    }

    /* access modifiers changed from: private */
    public void onStackYChanged(boolean z) {
        if (this.mQs != null) {
            if (z) {
                animateNextNotificationBounds(360, 0);
                this.mNotificationBoundsAnimationDelay = 0;
            }
            setQSClippingBounds();
        }
    }

    /* access modifiers changed from: private */
    public void onNotificationScrolled(int i) {
        updateQSExpansionEnabledAmbient();
    }

    public void setIsShadeOpening(boolean z) {
        this.mAmbientState.setIsShadeOpening(z);
        updateQSExpansionEnabledAmbient();
    }

    private void updateQSExpansionEnabledAmbient() {
        this.mQsExpansionEnabledAmbient = ((float) this.mAmbientState.getScrollY()) <= this.mAmbientState.getTopPadding() - this.mQuickQsOffsetHeight;
        setQsExpansionEnabled();
    }

    private void setQSClippingBounds() {
        int i;
        int i2;
        int i3;
        int calculateQsBottomPosition = calculateQsBottomPosition(computeQsExpansionFraction());
        int i4 = 0;
        boolean z = computeQsExpansionFraction() > 0.0f || calculateQsBottomPosition > 0;
        if (!this.mShouldUseSplitNotificationShade) {
            if (this.mTransitioningToFullShadeProgress > 0.0f) {
                calculateQsBottomPosition = this.mTransitionToFullShadeQSPosition;
            } else {
                float qSEdgePosition = getQSEdgePosition();
                if (!isOnKeyguard()) {
                    calculateQsBottomPosition = (int) qSEdgePosition;
                } else if (!this.mKeyguardBypassController.getBypassEnabled()) {
                    calculateQsBottomPosition = (int) Math.min((float) calculateQsBottomPosition, qSEdgePosition);
                }
            }
            i3 = (int) (((float) calculateQsBottomPosition) + this.mOverStretchAmount);
            i2 = getView().getBottom();
            i = getView().getRight() + this.mDisplayRightInset;
        } else {
            i3 = Math.min(calculateQsBottomPosition, this.mSplitShadeNotificationsTopPadding);
            i2 = this.mNotificationStackScrollLayoutController.getHeight();
            i4 = this.mNotificationStackScrollLayoutController.getLeft();
            i = this.mNotificationStackScrollLayoutController.getRight();
        }
        int i5 = i2;
        applyQSClippingBounds(i4, Math.min(i3, i5), i, i5, z);
    }

    private void applyQSClippingBounds(int i, int i2, int i3, int i4, boolean z) {
        if (this.mAnimateNextNotificationBounds && !this.mKeyguardStatusAreaClipBounds.isEmpty()) {
            this.mQsClippingAnimationEndBounds.set(i, i2, i3, i4);
            Rect rect = this.mKeyguardStatusAreaClipBounds;
            int i5 = rect.left;
            int i6 = rect.top;
            int i7 = rect.right;
            int i8 = rect.bottom;
            ValueAnimator valueAnimator = this.mQsClippingAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.mQsClippingAnimation = ofFloat;
            ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mQsClippingAnimation.setDuration(this.mNotificationBoundsAnimationDuration);
            this.mQsClippingAnimation.setStartDelay(this.mNotificationBoundsAnimationDelay);
            this.mQsClippingAnimation.addUpdateListener(new NotificationPanelViewController$$ExternalSyntheticLambda2(this, i5, i6, i7, i8, z));
            this.mQsClippingAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ValueAnimator unused = NotificationPanelViewController.this.mQsClippingAnimation = null;
                    boolean unused2 = NotificationPanelViewController.this.mIsQsTranslationResetAnimator = false;
                    boolean unused3 = NotificationPanelViewController.this.mIsPulseExpansionResetAnimator = false;
                }
            });
            this.mQsClippingAnimation.start();
        } else if (this.mQsClippingAnimation != null) {
            this.mQsClippingAnimationEndBounds.set(i, i2, i3, i4);
        } else {
            applyQSClippingImmediately(i, i2, i3, i4, z);
        }
        this.mAnimateNextNotificationBounds = false;
        this.mNotificationBoundsAnimationDelay = 0;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$applyQSClippingBounds$9(int i, int i2, int i3, int i4, boolean z, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        applyQSClippingImmediately((int) MathUtils.lerp((float) i, (float) this.mQsClippingAnimationEndBounds.left, animatedFraction), (int) MathUtils.lerp((float) i2, (float) this.mQsClippingAnimationEndBounds.top, animatedFraction), (int) MathUtils.lerp((float) i3, (float) this.mQsClippingAnimationEndBounds.right, animatedFraction), (int) MathUtils.lerp((float) i4, (float) this.mQsClippingAnimationEndBounds.bottom, animatedFraction), z);
    }

    private void applyQSClippingImmediately(int i, int i2, int i3, int i4, boolean z) {
        int i5;
        int i6;
        boolean z2;
        float f;
        int i7;
        int i8 = i;
        int i9 = i2;
        int i10 = i3;
        int i11 = i4;
        boolean z3 = z;
        int i12 = this.mScrimCornerRadius;
        if (!this.mShouldUseSplitNotificationShade) {
            this.mKeyguardStatusAreaClipBounds.set(i8, i9, i10, i11);
            float f2 = this.mRecordingController.isRecording() ? 0.0f : (float) this.mScreenCornerRadius;
            int i13 = this.mScrimCornerRadius;
            i6 = i9 - this.mKeyguardStatusBar.getTop();
            i5 = (int) MathUtils.lerp(f2, (float) i13, Math.min(((float) i9) / ((float) i13), 1.0f));
            z2 = z3;
        } else {
            i5 = i12;
            z2 = false;
            i6 = 0;
        }
        if (this.mQs != null) {
            boolean isExpanding = this.mPulseExpansionHandler.isExpanding();
            if (this.mTransitioningToFullShadeProgress <= 0.0f && !isExpanding && (this.mQsClippingAnimation == null || (!this.mIsQsTranslationResetAnimator && !this.mIsPulseExpansionResetAnimator))) {
                f = 0.0f;
            } else if (isExpanding || this.mIsPulseExpansionResetAnimator) {
                f = Math.max(0.0f, ((float) (i9 - this.mQs.getHeader().getHeight())) / 2.0f);
            } else {
                f = ((float) (i9 - this.mQs.getHeader().getHeight())) * (this.mIsPrcCustom ? 0.0f : 0.175f);
            }
            this.mQsTranslationForFullShadeTransition = f;
            updateQsFrameTranslation();
            float translationY = this.mQsFrame.getTranslationY();
            this.mQsClipTop = (int) (((float) i9) - translationY);
            this.mQsClipBottom = (int) (((float) i11) - translationY);
            this.mQsVisible = z3;
            if (!this.mIsPrcCustom || !((i7 = this.mPanelState) == 2 || i7 == 4)) {
                boolean z4 = true;
                this.mScrimController.setClipsQsScrim(!this.mShouldUseSplitNotificationShade);
                C1129QS qs = this.mQs;
                int i14 = this.mQsClipTop;
                int i15 = this.mQsClipBottom;
                if (!z3 || this.mShouldUseSplitNotificationShade) {
                    z4 = false;
                }
                qs.setFancyClipping(i14, i15, i5, z4);
            } else {
                this.mScrimController.setClipsQsScrim(false);
                this.mQs.setFancyClipping(this.mQsClipTop, this.mQsClipBottom, i5, false);
            }
        }
        this.mKeyguardStatusViewController.setClipBounds(z2 ? this.mKeyguardStatusAreaClipBounds : null);
        if (z3 || !this.mShouldUseSplitNotificationShade) {
            this.mScrimController.setNotificationsBounds((float) i8, (float) i9, (float) i10, (float) i11);
        } else {
            this.mScrimController.setNotificationsBounds(0.0f, 0.0f, 0.0f, 0.0f);
        }
        this.mScrimController.setScrimCornerRadius(i5);
        this.mKeyguardStatusBar.setTopClipping(i6);
        this.mNotificationStackScrollLayoutController.setRoundedClippingBounds(i8 - this.mNotificationStackScrollLayoutController.getLeft(), i9 - this.mNotificationStackScrollLayoutController.getTop(), i10 - this.mNotificationStackScrollLayoutController.getLeft(), i11 - this.mNotificationStackScrollLayoutController.getTop(), i5, this.mShouldUseSplitNotificationShade ? i5 : 0);
    }

    private float getQSEdgePosition() {
        return Math.max(this.mQuickQsOffsetHeight * this.mAmbientState.getExpansionFraction(), this.mAmbientState.getStackY() - ((float) this.mAmbientState.getScrollY()));
    }

    private int calculateQsBottomPosition(float f) {
        if (this.mTransitioningToFullShadeProgress > 0.0f) {
            return this.mTransitionToFullShadeQSPosition;
        }
        int headerTranslation = ((int) getHeaderTranslation()) + this.mQs.getQsMinExpansionHeight();
        return ((double) f) != 0.0d ? (int) MathUtils.lerp((float) headerTranslation, (float) this.mQs.getDesiredHeight(), f) : headerTranslation;
    }

    /* access modifiers changed from: private */
    public String determineAccessibilityPaneTitle() {
        C1129QS qs = this.mQs;
        if (qs != null && qs.isCustomizing()) {
            return this.mResources.getString(R$string.accessibility_desc_quick_settings_edit);
        }
        if (this.mQsExpansionHeight != 0.0f && this.mQsFullyExpanded) {
            return this.mResources.getString(R$string.accessibility_desc_quick_settings);
        }
        if (this.mBarState == 1) {
            return this.mResources.getString(R$string.accessibility_desc_lock_screen);
        }
        return this.mResources.getString(R$string.accessibility_desc_notification_shade);
    }

    private float calculateNotificationsTopPadding() {
        if (this.mIsPrcCustom && (isOpenNotificationState() || this.mBarState == 2)) {
            return (float) (this.mHeaderHeight + this.mQsNotificationTopPadding);
        }
        if (this.mShouldUseSplitNotificationShade && !this.mKeyguardShowing) {
            return (float) this.mSplitShadeNotificationsTopPadding;
        }
        boolean z = this.mKeyguardShowing;
        if (!z || (!this.mQsExpandImmediate && (!this.mIsExpanding || !this.mQsExpandedWhenExpandingStarted))) {
            ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
            if (valueAnimator != null) {
                return (float) Math.max(((Integer) valueAnimator.getAnimatedValue()).intValue(), getKeyguardNotificationStaticPadding());
            }
            if (z) {
                return MathUtils.lerp((float) getKeyguardNotificationStaticPadding(), (float) this.mQsMaxExpansionHeight, computeQsExpansionFraction());
            }
            return this.mQsExpansionHeight;
        }
        int keyguardNotificationStaticPadding = getKeyguardNotificationStaticPadding();
        int i = this.mQsMaxExpansionHeight;
        if (this.mBarState == 1) {
            i = Math.max(keyguardNotificationStaticPadding, i);
        }
        return (float) ((int) MathUtils.lerp((float) this.mQsMinExpansionHeight, (float) i, getExpandedFraction()));
    }

    private int getKeyguardNotificationStaticPadding() {
        if (!this.mKeyguardShowing) {
            return 0;
        }
        if (!this.mKeyguardBypassController.getBypassEnabled()) {
            return this.mClockPositionResult.stackScrollerPadding;
        }
        int i = this.mHeadsUpInset;
        if (!this.mNotificationStackScrollLayoutController.isPulseExpanding()) {
            return i;
        }
        return (int) MathUtils.lerp((float) i, (float) this.mClockPositionResult.stackScrollerPadding, this.mNotificationStackScrollLayoutController.calculateAppearFractionBypass());
    }

    /* access modifiers changed from: protected */
    public void requestScrollerTopPaddingUpdate(boolean z) {
        this.mNotificationStackScrollLayoutController.updateTopPadding(calculateNotificationsTopPadding(), z);
        if (this.mKeyguardShowing && this.mKeyguardBypassController.getBypassEnabled()) {
            updateQsExpansion();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x007b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00a2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTransitionToFullShadeAmount(float r6, boolean r7, long r8) {
        /*
            r5 = this;
            r0 = 0
            r1 = 1
            r2 = 0
            if (r7 == 0) goto L_0x0019
            boolean r7 = r5.mShouldUseSplitNotificationShade
            if (r7 != 0) goto L_0x0019
            r3 = 448(0x1c0, double:2.213E-321)
            r5.animateNextNotificationBounds(r3, r8)
            float r7 = r5.mQsTranslationForFullShadeTransition
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 <= 0) goto L_0x0016
            r7 = r1
            goto L_0x0017
        L_0x0016:
            r7 = r0
        L_0x0017:
            r5.mIsQsTranslationResetAnimator = r7
        L_0x0019:
            int r7 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r7 <= 0) goto L_0x0061
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r7 = r5.mNotificationStackScrollLayoutController
            int r7 = r7.getVisibleNotificationCount()
            if (r7 != 0) goto L_0x004a
            com.android.systemui.media.MediaDataManager r7 = r5.mMediaDataManager
            boolean r7 = r7.hasActiveMedia()
            if (r7 != 0) goto L_0x004a
            com.android.systemui.plugins.qs.QS r7 = r5.mQs
            if (r7 == 0) goto L_0x0061
            float r7 = r5.getQSEdgePosition()
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r8 = r5.mNotificationStackScrollLayoutController
            int r8 = r8.getTopPadding()
            float r8 = (float) r8
            float r7 = r7 - r8
            com.android.systemui.plugins.qs.QS r8 = r5.mQs
            android.view.View r8 = r8.getHeader()
            int r8 = r8.getHeight()
            float r8 = (float) r8
            float r7 = r7 + r8
            goto L_0x0062
        L_0x004a:
            float r7 = r5.getQSEdgePosition()
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r8 = r5.mNotificationStackScrollLayoutController
            int r8 = r8.getFullShadeTransitionInset()
            float r8 = (float) r8
            float r7 = r7 + r8
            boolean r8 = r5.isOnKeyguard()
            if (r8 == 0) goto L_0x0062
            int r8 = r5.mLockscreenNotificationQSPadding
            float r8 = (float) r8
            float r7 = r7 - r8
            goto L_0x0062
        L_0x0061:
            r7 = r2
        L_0x0062:
            android.view.animation.Interpolator r8 = com.android.systemui.animation.Interpolators.FAST_OUT_SLOW_IN
            int r9 = r5.mDistanceForQSFullShadeTransition
            float r9 = (float) r9
            float r6 = r6 / r9
            float r6 = android.util.MathUtils.saturate(r6)
            float r6 = r8.getInterpolation(r6)
            r5.mTransitioningToFullShadeProgress = r6
            float r6 = android.util.MathUtils.lerp(r2, r7, r6)
            int r6 = (int) r6
            boolean r7 = r5.mIsPrcCustom
            if (r7 == 0) goto L_0x0093
            if (r6 <= 0) goto L_0x0093
            int r7 = r5.mBarState
            if (r7 != r1) goto L_0x0086
            r7 = 3
            r5.updatePanelViewState(r7)
            goto L_0x009c
        L_0x0086:
            boolean r7 = r5.mQsExpanded
            if (r7 == 0) goto L_0x008f
            r7 = 2
            r5.updatePanelViewState(r7)
            goto L_0x009c
        L_0x008f:
            r5.updatePanelViewState(r1)
            goto L_0x009c
        L_0x0093:
            if (r7 == 0) goto L_0x009c
            int r7 = r5.mBarState
            if (r7 != r1) goto L_0x009c
            r5.updatePanelViewState(r0)
        L_0x009c:
            float r7 = r5.mTransitioningToFullShadeProgress
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 <= 0) goto L_0x00a6
            int r6 = java.lang.Math.max(r1, r6)
        L_0x00a6:
            r5.mTransitionToFullShadeQSPosition = r6
            r5.updateQsExpansion()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationPanelViewController.setTransitionToFullShadeAmount(float, boolean, long):void");
    }

    public void onPulseExpansionFinished() {
        animateNextNotificationBounds(448, 0);
        this.mIsPulseExpansionResetAnimator = true;
    }

    public void setKeyguardOnlyContentAlpha(float f) {
        float interpolation = Interpolators.ALPHA_IN.getInterpolation(f);
        this.mKeyguardOnlyContentAlpha = interpolation;
        if (this.mBarState == 1) {
            this.mBottomAreaShadeAlpha = interpolation;
            updateKeyguardBottomAreaAlpha();
        }
        updateClock();
    }

    private void trackMovement(MotionEvent motionEvent) {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
        }
    }

    private void initVelocityTracker() {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mQsVelocityTracker = VelocityTracker.obtain();
    }

    private float getCurrentQSVelocity() {
        VelocityTracker velocityTracker = this.mQsVelocityTracker;
        if (velocityTracker == null) {
            return 0.0f;
        }
        velocityTracker.computeCurrentVelocity(1000);
        return this.mQsVelocityTracker.getYVelocity();
    }

    /* access modifiers changed from: private */
    public void cancelQsAnimation() {
        ValueAnimator valueAnimator = this.mQsExpansionAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void flingSettings(float f, int i) {
        flingSettings(f, i, (Runnable) null, false);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x001c  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0029  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void flingSettings(float r9, int r10, final java.lang.Runnable r11, boolean r12) {
        /*
            r8 = this;
            r0 = 0
            r1 = 1
            if (r10 == 0) goto L_0x0012
            if (r10 == r1) goto L_0x000f
            com.android.systemui.plugins.qs.QS r2 = r8.mQs
            if (r2 == 0) goto L_0x000d
            r2.closeDetail()
        L_0x000d:
            r2 = r0
            goto L_0x0015
        L_0x000f:
            int r2 = r8.mQsMinExpansionHeight
            goto L_0x0014
        L_0x0012:
            int r2 = r8.mQsMaxExpansionHeight
        L_0x0014:
            float r2 = (float) r2
        L_0x0015:
            float r3 = r8.mQsExpansionHeight
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            r5 = 0
            if (r4 != 0) goto L_0x0029
            if (r11 == 0) goto L_0x0021
            r11.run()
        L_0x0021:
            if (r10 == 0) goto L_0x0024
            goto L_0x0025
        L_0x0024:
            r1 = r5
        L_0x0025:
            r8.traceQsJank(r5, r1)
            return
        L_0x0029:
            if (r10 != 0) goto L_0x002d
            r10 = r1
            goto L_0x002e
        L_0x002d:
            r10 = r5
        L_0x002e:
            int r4 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r4 <= 0) goto L_0x0034
            if (r10 == 0) goto L_0x003a
        L_0x0034:
            int r4 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r4 >= 0) goto L_0x003d
            if (r10 == 0) goto L_0x003d
        L_0x003a:
            r9 = r0
            r4 = r1
            goto L_0x003e
        L_0x003d:
            r4 = r5
        L_0x003e:
            r6 = 2
            float[] r6 = new float[r6]
            r6[r5] = r3
            r6[r1] = r2
            android.animation.ValueAnimator r3 = android.animation.ValueAnimator.ofFloat(r6)
            if (r12 == 0) goto L_0x0056
            android.view.animation.Interpolator r9 = com.android.systemui.animation.Interpolators.TOUCH_RESPONSE
            r3.setInterpolator(r9)
            r6 = 368(0x170, double:1.82E-321)
            r3.setDuration(r6)
            goto L_0x005d
        L_0x0056:
            com.android.wm.shell.animation.FlingAnimationUtils r12 = r8.mFlingAnimationUtils
            float r6 = r8.mQsExpansionHeight
            r12.apply(r3, r6, r2, r9)
        L_0x005d:
            if (r4 == 0) goto L_0x0064
            r6 = 350(0x15e, double:1.73E-321)
            r3.setDuration(r6)
        L_0x0064:
            com.android.systemui.statusbar.phone.NotificationPanelViewController$$ExternalSyntheticLambda1 r9 = new com.android.systemui.statusbar.phone.NotificationPanelViewController$$ExternalSyntheticLambda1
            r9.<init>(r8)
            r3.addUpdateListener(r9)
            com.android.systemui.statusbar.phone.NotificationPanelViewController$13 r9 = new com.android.systemui.statusbar.phone.NotificationPanelViewController$13
            r9.<init>(r11)
            r3.addListener(r9)
            r8.mAnimatingQS = r1
            r3.start()
            r8.mQsExpansionAnimator = r3
            r8.mQsAnimatorExpand = r10
            float r9 = r8.computeQsExpansionFraction()
            int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r9 != 0) goto L_0x008a
            int r9 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r9 != 0) goto L_0x008a
            goto L_0x008b
        L_0x008a:
            r1 = r5
        L_0x008b:
            r8.mQSAnimatingHiddenFromCollapsed = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationPanelViewController.flingSettings(float, int, java.lang.Runnable, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$flingSettings$10(ValueAnimator valueAnimator) {
        setQsExpansion(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        r0 = r8.mQs;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldQuickSettingsIntercept(float r9, float r10, float r11) {
        /*
            r8 = this;
            boolean r0 = r8.isQsExpansionEnabled()
            r1 = 0
            if (r0 == 0) goto L_0x0086
            boolean r0 = r8.mCollapsedOnDown
            if (r0 != 0) goto L_0x0086
            boolean r0 = r8.mKeyguardShowing
            if (r0 == 0) goto L_0x0018
            com.android.systemui.statusbar.phone.KeyguardBypassController r0 = r8.mKeyguardBypassController
            boolean r0 = r0.getBypassEnabled()
            if (r0 == 0) goto L_0x0018
            goto L_0x0086
        L_0x0018:
            boolean r0 = r8.mKeyguardShowing
            if (r0 != 0) goto L_0x0026
            com.android.systemui.plugins.qs.QS r0 = r8.mQs
            if (r0 != 0) goto L_0x0021
            goto L_0x0026
        L_0x0021:
            android.view.View r0 = r0.getHeader()
            goto L_0x0028
        L_0x0026:
            com.android.systemui.statusbar.phone.KeyguardStatusBarView r0 = r8.mKeyguardStatusBar
        L_0x0028:
            android.widget.FrameLayout r2 = r8.mQsFrame
            float r2 = r2.getX()
            int r2 = (int) r2
            boolean r3 = r8.mIsPrcCustom
            r4 = 1
            if (r3 == 0) goto L_0x0048
            int r3 = r8.mBarState
            if (r3 != r4) goto L_0x0048
            android.widget.FrameLayout r2 = r8.mQsFrame
            float r2 = r2.getX()
            int r2 = (int) r2
            android.widget.FrameLayout r3 = r8.mQsFrame
            int r3 = r3.getWidth()
            int r3 = r3 / 2
            int r2 = r2 + r3
        L_0x0048:
            android.graphics.Region r3 = r8.mQsInterceptRegion
            int r5 = r0.getTop()
            android.widget.FrameLayout r6 = r8.mQsFrame
            float r6 = r6.getX()
            int r6 = (int) r6
            android.widget.FrameLayout r7 = r8.mQsFrame
            int r7 = r7.getWidth()
            int r6 = r6 + r7
            int r0 = r0.getBottom()
            r3.set(r2, r5, r6, r0)
            com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager r0 = r8.mStatusBarTouchableRegionManager
            android.graphics.Region r2 = r8.mQsInterceptRegion
            r0.updateRegionForNotch(r2)
            android.graphics.Region r0 = r8.mQsInterceptRegion
            int r2 = (int) r9
            int r3 = (int) r10
            boolean r0 = r0.contains(r2, r3)
            boolean r2 = r8.mQsExpanded
            if (r2 == 0) goto L_0x0085
            if (r0 != 0) goto L_0x0083
            r0 = 0
            int r11 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r11 >= 0) goto L_0x0084
            boolean r8 = r8.isInQsArea(r9, r10)
            if (r8 == 0) goto L_0x0084
        L_0x0083:
            r1 = r4
        L_0x0084:
            return r1
        L_0x0085:
            return r0
        L_0x0086:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationPanelViewController.shouldQuickSettingsIntercept(float, float, float):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean canCollapsePanelOnTouch() {
        if ((!isInSettings() && this.mBarState == 1) || this.mNotificationStackScrollLayoutController.isScrolledToBottom()) {
            return true;
        }
        if (this.mShouldUseSplitNotificationShade || (!isInSettings() && !this.mIsPanelCollapseOnQQS)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int getMaxPanelHeight() {
        int i;
        int i2 = this.mStatusBarMinHeight;
        if (this.mBarState != 1 && this.mNotificationStackScrollLayoutController.getNotGoneChildCount() == 0) {
            i2 = Math.max(i2, this.mQsMinExpansionHeight);
        }
        if (this.mQsExpandImmediate || this.mQsExpanded || ((this.mIsExpanding && this.mQsExpandedWhenExpandingStarted) || this.mPulsing)) {
            i = calculatePanelHeightQsExpanded();
        } else {
            i = calculatePanelHeightShade();
        }
        int max = Math.max(i2, i);
        if (max == 0 || Float.isNaN((float) max)) {
            String str = PanelViewController.TAG;
            Log.wtf(str, "maxPanelHeight is invalid. mOverExpansion: " + this.mOverExpansion + ", calculatePanelHeightQsExpanded: " + calculatePanelHeightQsExpanded() + ", calculatePanelHeightShade: " + calculatePanelHeightShade() + ", mStatusBarMinHeight = " + this.mStatusBarMinHeight + ", mQsMinExpansionHeight = " + this.mQsMinExpansionHeight);
        }
        return max;
    }

    public boolean isInSettings() {
        return this.mQsExpanded;
    }

    public boolean isExpanding() {
        return this.mIsExpanding;
    }

    /* access modifiers changed from: protected */
    public void onHeightUpdated(float f) {
        float f2;
        if ((!this.mQsExpanded || this.mQsExpandImmediate || (this.mIsExpanding && this.mQsExpandedWhenExpandingStarted)) && this.mStackScrollerMeasuringPass <= 2) {
            positionClockAndNotifications();
        }
        if (this.mQsExpandImmediate || (this.mQsExpanded && !this.mQsTracking && this.mQsExpansionAnimator == null && !this.mQsExpansionFromOverscroll)) {
            if (this.mKeyguardShowing) {
                f2 = f / ((float) getMaxPanelHeight());
            } else {
                float intrinsicPadding = this.mNotificationStackScrollLayoutController.getIntrinsicPadding() + this.mNotificationStackScrollLayoutController.getLayoutMinHeight();
                f2 = (f - intrinsicPadding) / (((float) calculatePanelHeightQsExpanded()) - intrinsicPadding);
            }
            int i = this.mQsMinExpansionHeight;
            setQsExpansion(((float) i) + (f2 * ((float) (this.mQsMaxExpansionHeight - i))));
        }
        updateExpandedHeight(f);
        updateHeader();
        updateNotificationTranslucency();
        updatePanelExpanded();
        updateGestureExclusionRect();
    }

    private void updatePanelExpanded() {
        boolean z = !isFullyCollapsed() || this.mExpectingSynthesizedDown;
        if (this.mPanelExpanded != z) {
            this.mHeadsUpManager.setIsPanelExpanded(z);
            this.mStatusBarTouchableRegionManager.setPanelExpanded(z);
            this.mStatusBar.setPanelExpanded(z);
            this.mPanelExpanded = z;
        }
    }

    private int calculatePanelHeightShade() {
        int height = this.mNotificationStackScrollLayoutController.getHeight() - this.mNotificationStackScrollLayoutController.getEmptyBottomMargin();
        return this.mBarState == 1 ? Math.max(height, this.mClockPositionAlgorithm.getLockscreenStatusViewHeight() + this.mNotificationStackScrollLayoutController.getIntrinsicContentHeight()) : height;
    }

    private int calculatePanelHeightQsExpanded() {
        float height = (float) ((this.mNotificationStackScrollLayoutController.getHeight() - this.mNotificationStackScrollLayoutController.getEmptyBottomMargin()) - this.mNotificationStackScrollLayoutController.getTopPadding());
        if (this.mNotificationStackScrollLayoutController.getNotGoneChildCount() == 0 && this.mNotificationStackScrollLayoutController.isShowingEmptyShadeView()) {
            height = this.mNotificationStackScrollLayoutController.getEmptyShadeViewHeight();
        }
        int i = this.mQsMaxExpansionHeight;
        ValueAnimator valueAnimator = this.mQsSizeChangeAnimator;
        if (valueAnimator != null) {
            i = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        }
        float max = ((float) Math.max(i, this.mBarState == 1 ? this.mClockPositionResult.stackScrollerPadding : 0)) + height + this.mNotificationStackScrollLayoutController.getTopPaddingOverflow();
        if (max > ((float) this.mNotificationStackScrollLayoutController.getHeight())) {
            max = Math.max(((float) i) + this.mNotificationStackScrollLayoutController.getLayoutMinHeight(), (float) this.mNotificationStackScrollLayoutController.getHeight());
        }
        return (int) max;
    }

    /* access modifiers changed from: private */
    public void updateNotificationTranslucency() {
        float fadeoutAlpha = (!this.mClosingWithAlphaFadeOut || this.mExpandingFromHeadsUp || this.mHeadsUpManager.hasPinnedHeadsUp()) ? 1.0f : getFadeoutAlpha();
        if (this.mBarState == 1 && !this.mHintAnimationRunning && !this.mKeyguardBypassController.getBypassEnabled()) {
            fadeoutAlpha *= this.mClockPositionResult.clockAlpha;
        }
        if (this.mIsPrcCustom && isOpenQSState()) {
            fadeoutAlpha = 0.0f;
        }
        this.mNotificationStackScrollLayoutController.setAlpha(fadeoutAlpha);
        updateNotificationHeaderTranslucency();
    }

    private void updateNotificationHeaderTranslucency() {
        float maxPanelHeight = (float) getMaxPanelHeight();
        float expandedHeight = getExpandedHeight();
        if (!Float.isNaN(expandedHeight) && !Float.isNaN(maxPanelHeight) && maxPanelHeight > 0.0f) {
            float max = Math.max(0.0f, Math.min(1.0f, expandedHeight / maxPanelHeight));
            View header = this.mQs.getHeader();
            if (header instanceof QuickStatusBarHeader) {
                ((QuickStatusBarHeader) header).setNotificationExpansion(max);
            }
        } else if (PanelViewController.DEBUG_PANEL) {
            String str = PanelViewController.TAG;
            Log.d(str, "update header with illegal value: expanded, maxHeight = " + expandedHeight + ", " + maxPanelHeight);
        }
    }

    private float getFadeoutAlpha() {
        if (this.mQsMinExpansionHeight == 0) {
            return 1.0f;
        }
        return (float) Math.pow((double) Math.max(0.0f, Math.min(getExpandedHeight() / ((float) this.mQsMinExpansionHeight), 1.0f)), 0.75d);
    }

    /* access modifiers changed from: private */
    public void updateHeader() {
        if (this.mBarState == 1) {
            updateHeaderKeyguardAlpha();
        }
        updateQsExpansion();
    }

    /* access modifiers changed from: protected */
    public float getHeaderTranslation() {
        if (this.mBarState == 1 && !this.mKeyguardBypassController.getBypassEnabled()) {
            return (float) (-this.mQs.getQsMinExpansionHeight());
        }
        if (this.mIsPrcCustom && this.mBarState == 2) {
            return 0.0f;
        }
        float calculateAppearFraction = this.mNotificationStackScrollLayoutController.calculateAppearFraction(this.mExpandedHeight);
        float f = this.mQsExpansionHeight;
        float f2 = -f;
        if (!this.mShouldUseSplitNotificationShade && this.mBarState == 0) {
            f2 = 0.175f * (-f);
        }
        if (this.mKeyguardBypassController.getBypassEnabled() && isOnKeyguard()) {
            calculateAppearFraction = this.mNotificationStackScrollLayoutController.calculateAppearFractionBypass();
            f2 = (float) (-this.mQs.getQsMinExpansionHeight());
        }
        return Math.min(0.0f, MathUtils.lerp(f2, 0.0f, Math.min(1.0f, calculateAppearFraction)));
    }

    private float getKeyguardContentsAlpha() {
        float f;
        float f2;
        if (this.mBarState == 1) {
            f2 = getExpandedHeight();
            f = (float) (this.mKeyguardStatusBar.getHeight() + this.mNotificationsHeaderCollideDistance);
        } else {
            f2 = getExpandedHeight();
            f = (float) this.mKeyguardStatusBar.getHeight();
        }
        return (float) Math.pow((double) MathUtils.saturate(f2 / f), 0.75d);
    }

    /* access modifiers changed from: private */
    public void updateHeaderKeyguardAlpha() {
        if (this.mKeyguardShowing) {
            float min = 1.0f - Math.min(1.0f, computeQsExpansionFraction() * 2.0f);
            float min2 = Math.min(getKeyguardContentsAlpha(), min) * this.mKeyguardStatusBarAnimateAlpha * (1.0f - this.mKeyguardHeadsUpShowingAmount);
            this.mKeyguardStatusBar.setAlpha(min2);
            int i = 0;
            this.mKeyguardStatusBar.setVisibility((min2 == 0.0f || this.mDozing || ((this.mFirstBypassAttempt && this.mUpdateMonitor.shouldListenForFace()) || this.mDelayShowingKeyguardStatusBar)) ? 4 : 0);
            if (this.mIsBellowCarrierName) {
                this.mCarrierLabel.setAlpha(Math.min(getKeyguardContentsAlpha(), min) * this.mKeyguardStatusBarAnimateAlpha);
                View view = this.mCarrierLabel;
                if (view.getAlpha() == 0.0f || this.mDozing) {
                    i = 4;
                }
                view.setVisibility(i);
            }
        }
    }

    private void updateKeyguardBottomAreaAlpha() {
        float min = Math.min(MathUtils.map(isUnlockHintRunning() ? 0.0f : 0.95f, 1.0f, 0.0f, 1.0f, getExpandedFraction()), 1.0f - computeQsExpansionFraction()) * this.mBottomAreaShadeAlpha;
        this.mKeyguardBottomArea.setAffordanceAlpha(min);
        this.mKeyguardBottomArea.setImportantForAccessibility(min == 0.0f ? 4 : 0);
        View ambientIndicationContainer = this.mStatusBar.getAmbientIndicationContainer();
        if (ambientIndicationContainer != null) {
            ambientIndicationContainer.setAlpha(min);
        }
        this.mLockIconViewController.setAlpha(min);
    }

    private void updateBigClockAlpha() {
        this.mBigClockContainer.setAlpha(Math.min(MathUtils.map(isUnlockHintRunning() ? 0.0f : 0.95f, 1.0f, 0.0f, 1.0f, getExpandedFraction()), 1.0f - computeQsExpansionFraction()));
    }

    /* access modifiers changed from: protected */
    public void onExpandingStarted() {
        super.onExpandingStarted();
        this.mNotificationStackScrollLayoutController.onExpansionStarted();
        this.mIsExpanding = true;
        boolean z = this.mQsFullyExpanded;
        this.mQsExpandedWhenExpandingStarted = z;
        this.mMediaHierarchyManager.setCollapsingShadeFromQS(z && !this.mAnimatingQS);
        if (this.mQsExpanded) {
            onQsExpansionStarted();
        }
        C1129QS qs = this.mQs;
        if (qs != null) {
            qs.setHeaderListening(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onExpandingFinished() {
        super.onExpandingFinished();
        this.mNotificationStackScrollLayoutController.onExpansionStopped();
        this.mHeadsUpManager.onExpandingFinished();
        this.mConversationNotificationManager.onNotificationPanelExpandStateChanged(isFullyCollapsed());
        this.mIsExpanding = false;
        this.mMediaHierarchyManager.setCollapsingShadeFromQS(false);
        this.mMediaHierarchyManager.setQsExpanded(this.mQsExpanded);
        if (isFullyCollapsed()) {
            DejankUtils.postAfterTraversal(new Runnable() {
                public void run() {
                    NotificationPanelViewController.this.setListening(false);
                }
            });
            this.mView.postOnAnimation(new Runnable() {
                public void run() {
                    NotificationPanelViewController.this.mView.getParent().invalidateChild(NotificationPanelViewController.this.mView, NotificationPanelViewController.M_DUMMY_DIRTY_RECT);
                }
            });
        } else {
            setListening(true);
        }
        this.mQsExpandImmediate = false;
        this.mNotificationStackScrollLayoutController.setShouldShowShelfOnly(false);
        this.mTwoFingerQsExpandPossible = false;
        notifyListenersTrackingHeadsUp((ExpandableNotificationRow) null);
        this.mExpandingFromHeadsUp = false;
        setPanelScrimMinFraction(0.0f);
    }

    private void notifyListenersTrackingHeadsUp(ExpandableNotificationRow expandableNotificationRow) {
        for (int i = 0; i < this.mTrackingHeadsUpListeners.size(); i++) {
            this.mTrackingHeadsUpListeners.get(i).accept(expandableNotificationRow);
        }
    }

    /* access modifiers changed from: private */
    public void setListening(boolean z) {
        this.mKeyguardStatusBar.setListening(z);
        C1129QS qs = this.mQs;
        if (qs != null) {
            qs.setListening(z);
        }
    }

    public void expand(boolean z) {
        super.expand(z);
        setListening(true);
    }

    public void setOverExpansion(float f) {
        if (f != this.mOverExpansion) {
            super.setOverExpansion(f);
            updateQsFrameTranslation();
            this.mNotificationStackScrollLayoutController.setOverExpansion(f);
        }
    }

    private void updateQsFrameTranslation() {
        this.mQsFrame.setTranslationY((this.mOverExpansion / 2.0f) + this.mQsTranslationForFullShadeTransition);
    }

    /* access modifiers changed from: protected */
    public void onTrackingStarted() {
        this.mFalsingCollector.onTrackingStarted(!this.mKeyguardStateController.canDismissLockScreen());
        super.onTrackingStarted();
        if (this.mQsFullyExpanded) {
            this.mQsExpandImmediate = true;
            if (!this.mShouldUseSplitNotificationShade) {
                this.mNotificationStackScrollLayoutController.setShouldShowShelfOnly(true);
            }
        }
        int i = this.mBarState;
        if (i == 1 || i == 2) {
            this.mAffordanceHelper.animateHideLeftRightIcon();
        }
        this.mNotificationStackScrollLayoutController.onPanelTrackingStarted();
    }

    /* access modifiers changed from: protected */
    public void onTrackingStopped(boolean z) {
        this.mFalsingCollector.onTrackingStopped();
        super.onTrackingStopped(z);
        if (z) {
            this.mNotificationStackScrollLayoutController.setOverScrollAmount(0.0f, true, true);
        }
        this.mNotificationStackScrollLayoutController.onPanelTrackingStopped();
        if (z) {
            int i = this.mBarState;
            if ((i == 1 || i == 2) && !this.mHintAnimationRunning) {
                this.mAffordanceHelper.reset(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateMaxHeadsUpTranslation() {
        this.mNotificationStackScrollLayoutController.setHeadsUpBoundaries(getHeight(), this.mNavigationBarBottomHeight);
    }

    /* access modifiers changed from: protected */
    public void startUnlockHintAnimation() {
        if (this.mPowerManager.isPowerSaveMode()) {
            onUnlockHintStarted();
            onUnlockHintFinished();
            return;
        }
        super.startUnlockHintAnimation();
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintFinished() {
        super.onUnlockHintFinished();
        this.mNotificationStackScrollLayoutController.setUnlockHintRunning(false);
    }

    /* access modifiers changed from: protected */
    public void onUnlockHintStarted() {
        super.onUnlockHintStarted();
        this.mNotificationStackScrollLayoutController.setUnlockHintRunning(true);
    }

    /* access modifiers changed from: protected */
    public boolean shouldUseDismissingAnimation() {
        return this.mBarState != 0 && (this.mKeyguardStateController.canDismissLockScreen() || !isTracking());
    }

    /* access modifiers changed from: protected */
    public boolean isTrackingBlocked() {
        return (this.mConflictingQsExpansionGesture && this.mQsExpanded) || this.mBlockingExpansionForCurrentTouch;
    }

    public boolean isQsExpanded() {
        return this.mQsExpanded;
    }

    public boolean isQsDetailShowing() {
        return this.mQs.isShowingDetail();
    }

    public void closeQsDetail() {
        this.mQs.closeDetail();
    }

    public boolean isLaunchTransitionFinished() {
        return this.mIsLaunchTransitionFinished;
    }

    public boolean isLaunchTransitionRunning() {
        return this.mIsLaunchTransitionRunning;
    }

    public void setLaunchTransitionEndRunnable(Runnable runnable) {
        this.mLaunchAnimationEndRunnable = runnable;
    }

    /* access modifiers changed from: private */
    public void updateDozingVisibilities(boolean z) {
        if (this.mIsBellowCarrierName) {
            this.mCarrierLabel.setVisibility(this.mDozing ? 4 : 0);
        }
        this.mKeyguardBottomArea.setDozing(this.mDozing, z);
        if (!this.mDozing && z) {
            animateKeyguardStatusBarIn(360);
        }
    }

    public boolean isDozing() {
        return this.mDozing;
    }

    public void setQsScrimEnabled(boolean z) {
        boolean z2 = this.mQsScrimEnabled != z;
        this.mQsScrimEnabled = z;
        if (z2) {
            updateQsState();
        }
    }

    public void onScreenTurningOn() {
        this.mKeyguardStatusViewController.dozeTimeTick();
    }

    /* access modifiers changed from: protected */
    public boolean onMiddleClicked() {
        int i = this.mBarState;
        if (i == 0) {
            this.mView.post(this.mPostCollapseRunnable);
            return false;
        } else if (i != 1) {
            if (i == 2 && !this.mQsExpanded) {
                this.mStatusBarStateController.setState(1);
            }
            return true;
        } else {
            if (!this.mDozingOnDown) {
                if (!this.mUpdateMonitor.isFaceEnrolled() || this.mUpdateMonitor.isFaceDetectionRunning() || this.mUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser())) {
                    this.mLockscreenGestureLogger.write(188, 0, 0);
                    this.mLockscreenGestureLogger.log(LockscreenGestureLogger.LockscreenUiEvent.LOCKSCREEN_LOCK_SHOW_HINT);
                    startUnlockHintAnimation();
                } else {
                    this.mUpdateMonitor.requestFaceAuth(true);
                }
            }
            return true;
        }
    }

    public void setPanelAlpha(int i, boolean z) {
        if (this.mPanelAlpha != i) {
            this.mPanelAlpha = i;
            PropertyAnimator.setProperty(this.mView, this.mPanelAlphaAnimator, (float) i, i == 255 ? this.mPanelAlphaInPropertiesAnimator : this.mPanelAlphaOutPropertiesAnimator, z);
        }
    }

    public void setPanelAlphaEndAction(Runnable runnable) {
        this.mPanelAlphaEndAction = runnable;
    }

    /* access modifiers changed from: private */
    public void updateKeyguardStatusBarForHeadsUp() {
        boolean z = this.mKeyguardShowing && this.mHeadsUpAppearanceController.shouldBeVisible();
        if (this.mShowingKeyguardHeadsUp != z) {
            this.mShowingKeyguardHeadsUp = z;
            float f = 0.0f;
            if (this.mKeyguardShowing) {
                NotificationPanelView notificationPanelView = this.mView;
                AnimatableProperty animatableProperty = this.KEYGUARD_HEADS_UP_SHOWING_AMOUNT;
                if (z) {
                    f = 1.0f;
                }
                PropertyAnimator.setProperty(notificationPanelView, animatableProperty, f, KEYGUARD_HUN_PROPERTIES, true);
                return;
            }
            PropertyAnimator.applyImmediately(this.mView, this.KEYGUARD_HEADS_UP_SHOWING_AMOUNT, 0.0f);
        }
    }

    private void setKeyguardHeadsUpShowingAmount(float f) {
        this.mKeyguardHeadsUpShowingAmount = f;
        updateHeaderKeyguardAlpha();
    }

    private float getKeyguardHeadsUpShowingAmount() {
        return this.mKeyguardHeadsUpShowingAmount;
    }

    public void setHeadsUpAnimatingAway(boolean z) {
        this.mHeadsUpAnimatingAway = z;
        this.mNotificationStackScrollLayoutController.setHeadsUpAnimatingAway(z);
        updateHeadsUpVisibility();
    }

    /* access modifiers changed from: private */
    public void updateHeadsUpVisibility() {
        ((PhoneStatusBarView) this.mBar).setHeadsUpVisible(this.mHeadsUpAnimatingAway || this.mHeadsUpPinnedMode);
    }

    public void setHeadsUpManager(HeadsUpManagerPhone headsUpManagerPhone) {
        super.setHeadsUpManager(headsUpManagerPhone);
        this.mHeadsUpTouchHelper = new HeadsUpTouchHelper(headsUpManagerPhone, this.mNotificationStackScrollLayoutController.getHeadsUpCallback(), this);
    }

    public void setTrackedHeadsUp(ExpandableNotificationRow expandableNotificationRow) {
        if (expandableNotificationRow != null) {
            notifyListenersTrackingHeadsUp(expandableNotificationRow);
            this.mExpandingFromHeadsUp = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onClosingFinished() {
        super.onClosingFinished();
        resetHorizontalPanelPosition();
        setClosingWithAlphaFadeout(false);
        this.mMediaHierarchyManager.closeGuts();
    }

    private void setClosingWithAlphaFadeout(boolean z) {
        this.mClosingWithAlphaFadeOut = z;
        this.mNotificationStackScrollLayoutController.forceNoOverlappingRendering(z);
    }

    /* access modifiers changed from: protected */
    public void updateHorizontalPanelPosition(float f) {
        if (this.mNotificationStackScrollLayoutController.getWidth() * 1.75f > ((float) this.mView.getWidth()) || this.mShouldUseSplitNotificationShade) {
            resetHorizontalPanelPosition();
            return;
        }
        float width = ((float) this.mPositionMinSideMargin) + (this.mNotificationStackScrollLayoutController.getWidth() / 2.0f);
        float width2 = ((float) (this.mView.getWidth() - this.mPositionMinSideMargin)) - (this.mNotificationStackScrollLayoutController.getWidth() / 2.0f);
        if (Math.abs(f - ((float) (this.mView.getWidth() / 2))) < this.mNotificationStackScrollLayoutController.getWidth() / 4.0f) {
            f = (float) (this.mView.getWidth() / 2);
        }
        setHorizontalPanelTranslation(Math.min(width2, Math.max(width, f)) - (((float) this.mNotificationStackScrollLayoutController.getLeft()) + (this.mNotificationStackScrollLayoutController.getWidth() / 2.0f)));
    }

    /* access modifiers changed from: private */
    public void resetHorizontalPanelPosition() {
        setHorizontalPanelTranslation(0.0f);
    }

    /* access modifiers changed from: protected */
    public void setHorizontalPanelTranslation(float f) {
        this.mNotificationStackScrollLayoutController.setTranslationX(f);
        this.mQsFrame.setTranslationX(f);
        Runnable runnable = this.mVerticalTranslationListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public void updateExpandedHeight(float f) {
        if (this.mTracking) {
            this.mNotificationStackScrollLayoutController.setExpandingVelocity(getCurrentExpandVelocity());
        }
        if (this.mKeyguardBypassController.getBypassEnabled() && isOnKeyguard()) {
            f = (float) getMaxPanelHeight();
        }
        this.mNotificationStackScrollLayoutController.setExpandedHeight(f);
        updateKeyguardBottomAreaAlpha();
        updateBigClockAlpha();
        updateStatusBarIcons();
    }

    public boolean isFullWidth() {
        return this.mIsFullWidth;
    }

    private void updateStatusBarIcons() {
        boolean z = (isPanelVisibleBecauseOfHeadsUp() || isFullWidth()) && getExpandedHeight() < getOpeningHeight();
        if (z && isOnKeyguard()) {
            z = false;
        }
        if (z != this.mShowIconsWhenExpanded) {
            this.mShowIconsWhenExpanded = z;
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
        }
    }

    /* access modifiers changed from: private */
    public boolean isOnKeyguard() {
        return this.mBarState == 1;
    }

    public void setPanelScrimMinFraction(float f) {
        this.mBar.panelScrimMinFractionChanged(f);
    }

    public void clearNotificationEffects() {
        this.mStatusBar.clearNotificationEffects();
    }

    /* access modifiers changed from: protected */
    public boolean isPanelVisibleBecauseOfHeadsUp() {
        return (this.mHeadsUpManager.hasPinnedHeadsUp() || this.mHeadsUpAnimatingAway) && this.mBarState == 0;
    }

    public void launchCamera(boolean z, int i, boolean z2, boolean z3) {
        this.mUseFrontCamera = z2;
        this.mAffordanceHelper.setForceShowAnimate(z3);
        launchCamera(z, i);
    }

    public void launchCameraOnFolioClosed(int i, boolean z) {
        if (i != 0) {
            String str = PanelViewController.TAG;
            Log.i(str, "Folio: Not launch camera. The source is " + i);
            return;
        }
        this.mUseFrontCamera = z;
        this.mLastCameraLaunchSource = "wiggle_gesture";
        this.mKeyguardBottomArea.launchCamera("wiggle_gesture", z);
    }

    public void launchCamera(boolean z, int i) {
        boolean z2 = false;
        if (MotoFeature.getInstance(this.mContext).isSupportCli() && MotoFeature.isLidClosed(this.mContext)) {
            z = false;
        }
        if (i == 1) {
            this.mLastCameraLaunchSource = "power_double_tap";
        } else if (i == 0) {
            this.mLastCameraLaunchSource = "wiggle_gesture";
        } else if (i == 2) {
            this.mLastCameraLaunchSource = "lift_to_launch_ml";
        } else {
            this.mLastCameraLaunchSource = "lockscreen_affordance";
        }
        if (!isFullyCollapsed()) {
            setLaunchingAffordance(true);
        } else {
            z = false;
        }
        this.mAffordanceHasPreview = this.mKeyguardBottomArea.getRightPreview() != null;
        KeyguardAffordanceHelper keyguardAffordanceHelper = this.mAffordanceHelper;
        if (this.mView.getLayoutDirection() == 1) {
            z2 = true;
        }
        keyguardAffordanceHelper.launchAffordance(z, z2);
    }

    public void onAffordanceLaunchEnded() {
        setLaunchingAffordance(false);
    }

    private void setLaunchingAffordance(boolean z) {
        this.mLaunchingAffordance = z;
        this.mKeyguardAffordanceHelperCallback.getLeftIcon().setLaunchingAffordance(z);
        this.mKeyguardAffordanceHelperCallback.getRightIcon().setLaunchingAffordance(z);
        this.mKeyguardBypassController.setLaunchingAffordance(z);
    }

    public boolean isLaunchingAffordanceWithPreview() {
        return this.mLaunchingAffordance && this.mAffordanceHasPreview;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0012, code lost:
        r0 = r0.activityInfo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean canCameraGestureBeLaunched() {
        /*
            r5 = this;
            com.android.systemui.statusbar.phone.StatusBar r0 = r5.mStatusBar
            boolean r0 = r0.isCameraAllowedByAdmin()
            r1 = 0
            if (r0 != 0) goto L_0x000a
            return r1
        L_0x000a:
            com.android.systemui.statusbar.phone.KeyguardBottomAreaView r0 = r5.mKeyguardBottomArea
            android.content.pm.ResolveInfo r0 = r0.resolveCameraIntent()
            if (r0 == 0) goto L_0x001a
            android.content.pm.ActivityInfo r0 = r0.activityInfo
            if (r0 != 0) goto L_0x0017
            goto L_0x001a
        L_0x0017:
            java.lang.String r0 = r0.packageName
            goto L_0x001b
        L_0x001a:
            r0 = 0
        L_0x001b:
            if (r0 != 0) goto L_0x001f
            java.lang.String r0 = ""
        L_0x001f:
            boolean r2 = r5.isForegroundApp(r0)
            if (r2 == 0) goto L_0x0038
            com.android.systemui.statusbar.phone.KeyguardBottomAreaView r3 = r5.mKeyguardBottomArea
            com.android.systemui.statusbar.phone.NotificationPanelView r4 = r5.mView
            android.content.Context r4 = r4.getContext()
            java.lang.String r3 = r3.getMotoCameraAppPackageName(r4)
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0038
            r2 = r1
        L_0x0038:
            int r0 = r5.mBarState
            if (r0 != 0) goto L_0x003e
            if (r2 != 0) goto L_0x0047
        L_0x003e:
            com.android.systemui.statusbar.phone.KeyguardAffordanceHelper r5 = r5.mAffordanceHelper
            boolean r5 = r5.isSwipingInProgress()
            if (r5 != 0) goto L_0x0047
            r1 = 1
        L_0x0047:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationPanelViewController.canCameraGestureBeLaunched():boolean");
    }

    public boolean isForegroundApp(String str) {
        List<ActivityManager.RunningTaskInfo> runningTasks = this.mActivityManager.getRunningTasks(1);
        if (runningTasks.isEmpty() || !str.equals(runningTasks.get(0).topActivity.getPackageName())) {
            return false;
        }
        return true;
    }

    public boolean hideStatusBarIconsWhenExpanded() {
        if (this.mIsLaunchAnimationRunning) {
            return this.mHideIconsDuringLaunchAnimation;
        }
        HeadsUpAppearanceController headsUpAppearanceController = this.mHeadsUpAppearanceController;
        if (headsUpAppearanceController != null && headsUpAppearanceController.shouldBeVisible()) {
            return false;
        }
        if (!isFullWidth() || !this.mShowIconsWhenExpanded) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11(int i) {
        if (i > 0 && !this.mQsFullyExpanded) {
            expandWithQs();
        }
    }

    /* access modifiers changed from: private */
    public void animateNextNotificationBounds(long j, long j2) {
        this.mAnimateNextNotificationBounds = true;
        this.mNotificationBoundsAnimationDuration = j;
        this.mNotificationBoundsAnimationDelay = j2;
    }

    public void setTouchAndAnimationDisabled(boolean z) {
        super.setTouchAndAnimationDisabled(z);
        if (z && this.mAffordanceHelper.isSwipingInProgress() && !this.mIsLaunchTransitionRunning) {
            this.mAffordanceHelper.reset(false);
        }
        this.mNotificationStackScrollLayoutController.setAnimationsEnabled(!z);
    }

    public void setDozing(boolean z, boolean z2, PointF pointF) {
        if (z != this.mDozing) {
            this.mView.setDozing(z);
            this.mDozing = z;
            this.mNotificationStackScrollLayoutController.setDozing(z, z2, pointF);
            this.mKeyguardBottomArea.setDozing(this.mDozing, z2);
            if (z) {
                this.mBottomAreaShadeAlphaAnimator.cancel();
            }
            int i = this.mBarState;
            if (i == 1 || i == 2) {
                updateDozingVisibilities(z2);
            }
            this.mStatusBarStateController.setAndInstrumentDozeAmount(this.mView, z ? 1.0f : 0.0f, z2);
        }
    }

    public void setPulsing(boolean z) {
        this.mPulsing = z;
        boolean z2 = !this.mDozeParameters.getDisplayNeedsBlanking() && this.mDozeParameters.getAlwaysOn();
        if (z2) {
            this.mAnimateNextPositionUpdate = true;
        }
        if (!this.mPulsing && !this.mDozing) {
            this.mAnimateNextPositionUpdate = false;
        }
        this.mNotificationStackScrollLayoutController.setPulsing(z, z2);
    }

    public void dozeTimeTick() {
        this.mKeyguardBottomArea.dozeTimeTick();
        this.mKeyguardStatusViewController.dozeTimeTick();
        if (this.mInterpolatedDarkAmount > 0.0f) {
            positionClockAndNotifications();
        }
    }

    public void setStatusAccessibilityImportance(int i) {
        this.mKeyguardStatusViewController.setStatusAccessibilityImportance(i);
    }

    public KeyguardBottomAreaView getKeyguardBottomAreaView() {
        return this.mKeyguardBottomArea;
    }

    public void setUserSetupComplete(boolean z) {
        this.mUserSetupComplete = z;
        this.mKeyguardBottomArea.setUserSetupComplete(z);
    }

    public void applyLaunchAnimationProgress(float f) {
        boolean z = ActivityLaunchAnimator.getProgress(f, 82, 100) == 0.0f;
        if (z != this.mHideIconsDuringLaunchAnimation) {
            this.mHideIconsDuringLaunchAnimation = z;
            if (!z) {
                this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
            }
        }
    }

    public void addTrackingHeadsUpListener(Consumer<ExpandableNotificationRow> consumer) {
        this.mTrackingHeadsUpListeners.add(consumer);
    }

    public void removeTrackingHeadsUpListener(Consumer<ExpandableNotificationRow> consumer) {
        this.mTrackingHeadsUpListeners.remove(consumer);
    }

    public void setVerticalTranslationListener(Runnable runnable) {
        this.mVerticalTranslationListener = runnable;
    }

    public void setHeadsUpAppearanceController(HeadsUpAppearanceController headsUpAppearanceController) {
        this.mHeadsUpAppearanceController = headsUpAppearanceController;
    }

    public void onBouncerPreHideAnimation() {
        KeyguardQsUserSwitchController keyguardQsUserSwitchController = this.mKeyguardQsUserSwitchController;
        if (keyguardQsUserSwitchController != null) {
            int i = this.mBarState;
            keyguardQsUserSwitchController.setKeyguardQsUserSwitchVisibility(i, true, false, i);
        }
        KeyguardUserSwitcherController keyguardUserSwitcherController = this.mKeyguardUserSwitcherController;
        if (keyguardUserSwitcherController != null) {
            int i2 = this.mBarState;
            keyguardUserSwitcherController.setKeyguardUserSwitcherVisibility(i2, true, false, i2);
        }
    }

    public void blockExpansionForCurrentTouch() {
        this.mBlockingExpansionForCurrentTouch = this.mTracking;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("    gestureExclusionRect: " + calculateGestureExclusionRect() + " applyQSClippingImmediately: top(" + this.mQsClipTop + ") bottom(" + this.mQsClipBottom + ") qsVisible(" + this.mQsVisible);
        KeyguardStatusBarView keyguardStatusBarView = this.mKeyguardStatusBar;
        if (keyguardStatusBarView != null) {
            keyguardStatusBarView.dump(fileDescriptor, printWriter, strArr);
        }
    }

    public RemoteInputController.Delegate createRemoteInputDelegate() {
        return this.mNotificationStackScrollLayoutController.createDelegate();
    }

    public void updateNotificationViews(String str) {
        this.mNotificationStackScrollLayoutController.updateSectionBoundaries(str);
        this.mNotificationStackScrollLayoutController.updateFooter();
        this.mNotificationIconAreaController.updateNotificationIcons(createVisibleEntriesList());
    }

    private List<ListEntry> createVisibleEntriesList() {
        ArrayList arrayList = new ArrayList(this.mNotificationStackScrollLayoutController.getChildCount());
        for (int i = 0; i < this.mNotificationStackScrollLayoutController.getChildCount(); i++) {
            ExpandableView childAt = this.mNotificationStackScrollLayoutController.getChildAt(i);
            if (childAt instanceof ExpandableNotificationRow) {
                arrayList.add(((ExpandableNotificationRow) childAt).getEntry());
            }
        }
        return arrayList;
    }

    public void onUpdateRowStates() {
        this.mNotificationStackScrollLayoutController.onUpdateRowStates();
    }

    public boolean hasPulsingNotifications() {
        return this.mNotificationStackScrollLayoutController.getNotificationListContainer().hasPulsingNotifications();
    }

    public ActivatableNotificationView getActivatedChild() {
        return this.mNotificationStackScrollLayoutController.getActivatedChild();
    }

    public void setActivatedChild(ActivatableNotificationView activatableNotificationView) {
        this.mNotificationStackScrollLayoutController.setActivatedChild(activatableNotificationView);
    }

    public void runAfterAnimationFinished(Runnable runnable) {
        this.mNotificationStackScrollLayoutController.runAfterAnimationFinished(runnable);
    }

    public void initDependencies(StatusBar statusBar, NotificationShelfController notificationShelfController) {
        setStatusBar(statusBar);
        this.mNotificationStackScrollLayoutController.setShelfController(notificationShelfController);
        this.mNotificationShelfController = notificationShelfController;
        this.mLockscreenShadeTransitionController.bindController(notificationShelfController);
        updateMaxDisplayedNotifications(true);
    }

    public void setAlpha(float f) {
        this.mView.setAlpha(f);
    }

    public ViewPropertyAnimator fadeOut(long j, long j2, Runnable runnable) {
        return this.mView.animate().alpha(0.0f).setStartDelay(j).setDuration(j2).setInterpolator(Interpolators.ALPHA_OUT).withLayer().withEndAction(runnable);
    }

    public void resetViewGroupFade() {
        ViewGroupFadeHelper.reset(this.mView);
    }

    public void addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        this.mView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        this.mView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public MyOnHeadsUpChangedListener getOnHeadsUpChangedListener() {
        return this.mOnHeadsUpChangedListener;
    }

    public int getHeight() {
        return this.mView.getHeight();
    }

    public void onThemeChanged() {
        this.mConfigurationListener.onThemeChanged();
    }

    public OnLayoutChangeListener createLayoutChangeListener() {
        return new OnLayoutChangeListener();
    }

    /* access modifiers changed from: protected */
    public PanelViewController.TouchHandler createTouchHandler() {
        return new PanelViewController.TouchHandler() {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (NotificationPanelViewController.this.mBlockTouches) {
                    return false;
                }
                if (NotificationPanelViewController.this.mQsFullyExpanded && NotificationPanelViewController.this.mQs.disallowPanelTouches()) {
                    return false;
                }
                NotificationPanelViewController.this.initDownStates(motionEvent);
                if (NotificationPanelViewController.this.mStatusBar.isBouncerShowing()) {
                    return true;
                }
                NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                if (notificationPanelViewController.mIsPrcCustom && (notificationPanelViewController.isTouchInSwitchRegion(motionEvent) || NotificationPanelViewController.this.mStartSwitchDetect || NotificationPanelViewController.this.mIsSwitching)) {
                    return true;
                }
                if (!NotificationPanelViewController.this.mBar.panelEnabled() || !NotificationPanelViewController.this.mHeadsUpTouchHelper.onInterceptTouchEvent(motionEvent)) {
                    NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                    if (!notificationPanelViewController2.shouldQuickSettingsIntercept(notificationPanelViewController2.mDownX, NotificationPanelViewController.this.mDownY, 0.0f) && NotificationPanelViewController.this.mPulseExpansionHandler.onInterceptTouchEvent(motionEvent)) {
                        return true;
                    }
                    if (NotificationPanelViewController.this.isFullyCollapsed() || !NotificationPanelViewController.this.onQsIntercept(motionEvent)) {
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                    return true;
                }
                NotificationPanelViewController.this.mMetricsLogger.count("panel_open", 1);
                NotificationPanelViewController.this.mMetricsLogger.count("panel_open_peek", 1);
                return true;
            }

            /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ad, code lost:
                if (com.android.systemui.statusbar.phone.NotificationPanelViewController.access$6800(r0, com.android.systemui.statusbar.phone.NotificationPanelViewController.access$6600(r0), com.android.systemui.statusbar.phone.NotificationPanelViewController.access$6700(r6.this$0), 0.0f) != false) goto L_0x00af;
             */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x00cc A[RETURN] */
            /* JADX WARNING: Removed duplicated region for block: B:48:0x00cd  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouch(android.view.View r7, android.view.MotionEvent r8) {
                /*
                    r6 = this;
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mBlockTouches
                    r1 = 0
                    if (r0 != 0) goto L_0x01c3
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mQsFullyExpanded
                    if (r0 == 0) goto L_0x001f
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.plugins.qs.QS r0 = r0.mQs
                    if (r0 == 0) goto L_0x001f
                    boolean r0 = r0.disallowPanelTouches()
                    if (r0 == 0) goto L_0x001f
                    goto L_0x01c3
                L_0x001f:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r2 = r0.mIsPrcCustom
                    r3 = 3
                    r4 = 1
                    if (r2 == 0) goto L_0x0060
                    boolean r0 = r0.mStartSwitchDetect
                    if (r0 != 0) goto L_0x0035
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mIsSwitching
                    if (r0 == 0) goto L_0x0060
                L_0x0035:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r7 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    r7.handleSwitchEvent(r8)
                    int r7 = r8.getAction()
                    if (r7 == r4) goto L_0x0046
                    int r7 = r8.getAction()
                    if (r7 != r3) goto L_0x005f
                L_0x0046:
                    boolean r7 = com.android.systemui.statusbar.phone.PanelViewController.DEBUG_PANEL
                    if (r7 == 0) goto L_0x0051
                    java.lang.String r7 = com.android.systemui.statusbar.phone.PanelViewController.TAG
                    java.lang.String r8 = "touch event up or cancle reset"
                    android.util.Log.i(r7, r8)
                L_0x0051:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r7 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean unused = r7.mStartSwitchDetect = r1
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r6 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.plugins.FalsingManager r6 = r6.mFalsingManager
                    r6.isFalseTouch(r1)
                L_0x005f:
                    return r4
                L_0x0060:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.StatusBar r0 = r0.mStatusBar
                    boolean r0 = r0.isBouncerShowingScrimmed()
                    if (r0 == 0) goto L_0x006b
                    return r1
                L_0x006b:
                    int r0 = r8.getAction()
                    if (r0 == r4) goto L_0x0077
                    int r0 = r8.getAction()
                    if (r0 != r3) goto L_0x007c
                L_0x0077:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean unused = r0.mBlockingExpansionForCurrentTouch = r1
                L_0x007c:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mLastEventSynthesizedDown
                    if (r0 == 0) goto L_0x008f
                    int r0 = r8.getAction()
                    if (r0 != r4) goto L_0x008f
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    r0.expand(r4)
                L_0x008f:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    r0.initDownStates(r8)
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mIsExpanding
                    if (r0 != 0) goto L_0x00af
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    float r2 = r0.mDownX
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r3 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    float r3 = r3.mDownY
                    r5 = 0
                    boolean r0 = r0.shouldQuickSettingsIntercept(r2, r3, r5)
                    if (r0 == 0) goto L_0x00bb
                L_0x00af:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.PulseExpansionHandler r0 = r0.mPulseExpansionHandler
                    boolean r0 = r0.isExpanding()
                    if (r0 == 0) goto L_0x00bd
                L_0x00bb:
                    r0 = r4
                    goto L_0x00be
                L_0x00bd:
                    r0 = r1
                L_0x00be:
                    if (r0 == 0) goto L_0x00cd
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.PulseExpansionHandler r0 = r0.mPulseExpansionHandler
                    boolean r0 = r0.onTouchEvent(r8)
                    if (r0 == 0) goto L_0x00cd
                    return r4
                L_0x00cd:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mListenForHeadsUp
                    if (r0 == 0) goto L_0x00f8
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.HeadsUpTouchHelper r0 = r0.mHeadsUpTouchHelper
                    boolean r0 = r0.isTrackingHeadsUp()
                    if (r0 != 0) goto L_0x00f8
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.HeadsUpTouchHelper r0 = r0.mHeadsUpTouchHelper
                    boolean r0 = r0.onInterceptTouchEvent(r8)
                    if (r0 == 0) goto L_0x00f8
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.internal.logging.MetricsLogger r0 = r0.mMetricsLogger
                    java.lang.String r2 = "panel_open_peek"
                    r0.count(r2, r4)
                L_0x00f8:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mIsExpanding
                    if (r0 == 0) goto L_0x0106
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mHintAnimationRunning
                    if (r0 == 0) goto L_0x012a
                L_0x0106:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mQsExpanded
                    if (r0 != 0) goto L_0x012a
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    int r0 = r0.mBarState
                    if (r0 == 0) goto L_0x012a
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r0 = r0.mDozing
                    if (r0 != 0) goto L_0x012a
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.KeyguardAffordanceHelper r0 = r0.mAffordanceHelper
                    boolean r0 = r0.onTouchEvent(r8)
                    r0 = r0 | r1
                    goto L_0x012b
                L_0x012a:
                    r0 = r1
                L_0x012b:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r2 = r2.mOnlyAffordanceInThisMotion
                    if (r2 == 0) goto L_0x0134
                    return r4
                L_0x0134:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.HeadsUpTouchHelper r2 = r2.mHeadsUpTouchHelper
                    boolean r2 = r2.onTouchEvent(r8)
                    r0 = r0 | r2
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.HeadsUpTouchHelper r2 = r2.mHeadsUpTouchHelper
                    boolean r2 = r2.isTrackingHeadsUp()
                    if (r2 != 0) goto L_0x0154
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r2 = r2.handleQsTouch(r8)
                    if (r2 == 0) goto L_0x0154
                    return r4
                L_0x0154:
                    int r2 = r8.getActionMasked()
                    if (r2 != 0) goto L_0x0177
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r2 = r2.isFullyCollapsed()
                    if (r2 == 0) goto L_0x0177
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.internal.logging.MetricsLogger r0 = r0.mMetricsLogger
                    java.lang.String r2 = "panel_open"
                    r0.count(r2, r4)
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    float r2 = r8.getX()
                    r0.updateHorizontalPanelPosition(r2)
                    r0 = r4
                L_0x0177:
                    int r2 = r8.getActionMasked()
                    if (r2 != 0) goto L_0x019e
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r2 = r2.isFullyExpanded()
                    if (r2 == 0) goto L_0x019e
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager r2 = r2.mStatusBarKeyguardViewManager
                    boolean r2 = r2.isShowing()
                    if (r2 == 0) goto L_0x019e
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager r2 = r2.mStatusBarKeyguardViewManager
                    float r3 = r8.getX()
                    r2.updateKeyguardPosition(r3)
                L_0x019e:
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r2 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    com.android.keyguard.LockIconViewController r2 = r2.mLockIconViewController
                    boolean r2 = r2.onTouchEvent(r8)
                    if (r2 == 0) goto L_0x01ab
                    return r4
                L_0x01ab:
                    boolean r7 = super.onTouch(r7, r8)
                    r7 = r7 | r0
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r8 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r8 = r8.mDozing
                    if (r8 == 0) goto L_0x01c2
                    com.android.systemui.statusbar.phone.NotificationPanelViewController r6 = com.android.systemui.statusbar.phone.NotificationPanelViewController.this
                    boolean r6 = r6.mPulsing
                    if (r6 != 0) goto L_0x01c2
                    if (r7 == 0) goto L_0x01c3
                L_0x01c2:
                    r1 = r4
                L_0x01c3:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.NotificationPanelViewController.C185317.onTouch(android.view.View, android.view.MotionEvent):boolean");
            }
        };
    }

    /* access modifiers changed from: private */
    public boolean isTouchInSwitchRegion(MotionEvent motionEvent) {
        if (this.mIsPrcCustom && isFullyExpanded() && !isNormalState()) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            this.mSwitchRegion.set(0, 0, this.mQsFrame.getWidth(), this.mSwitchRegionHeight);
            if (motionEvent.getAction() == 0) {
                this.mStartSwitchDetect = this.mSwitchRegion.contains((int) x, (int) y);
                if (PanelViewController.DEBUG_PANEL) {
                    String str = PanelViewController.TAG;
                    Log.i(str, "PrcPanel mStartSwitchDetect = " + this.mStartSwitchDetect);
                }
                return this.mStartSwitchDetect;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void handleSwitchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            int i = 1;
            if (actionMasked != 1) {
                int i2 = 3;
                if (actionMasked == 2) {
                    float f = y - this.mInitialSwitchY;
                    if (!this.mIsSwitching && f > ((float) this.mSwitchThreshold)) {
                        this.mIsSwitching = true;
                        if (isStatusBarShadeLockedOrKeyguard()) {
                            if (this.mInitialSwitchX >= ((float) (this.mQsFrame.getWidth() / 2))) {
                                i2 = 4;
                            }
                            this.mSwitchState = i2;
                        } else {
                            if (this.mInitialSwitchX >= ((float) (this.mQsFrame.getWidth() / 2))) {
                                i = 2;
                            }
                            this.mSwitchState = i;
                        }
                        switchPanelState(this.mSwitchState);
                        return;
                    }
                    return;
                } else if (actionMasked != 3) {
                    return;
                }
            }
            this.mInitialSwitchX = 0.0f;
            this.mInitialSwitchY = 0.0f;
            return;
        }
        this.mInitialSwitchX = x;
        this.mInitialSwitchY = y;
    }

    private void switchPanelState(int i) {
        int i2 = this.mPanelState;
        boolean z = false;
        if (i == i2 || i2 == 0) {
            this.mIsSwitching = false;
            return;
        }
        if (PanelViewController.DEBUG_PANEL) {
            String str = PanelViewController.TAG;
            Log.i(str, "PrcPanel switch from " + this.mPanelState + " to " + i);
        }
        updatePanelViewState(i);
        if (i == 4 || i == 2) {
            z = true;
        }
        switchAnimation(z);
    }

    public void resetViewPrc() {
        if (PanelViewController.DEBUG_PANEL) {
            Log.i(PanelViewController.TAG, "PrcPanel resetViewPrc()");
        }
        this.mPrcContainer.setAlpha(1.0f);
        this.mNotificationStackScrollLayoutController.setAlpha(1.0f);
        this.mNotificationStackScrollLayoutController.getView().setVisibility(0);
        this.mIsSwitching = false;
        if (isFullyCollapsed() && this.mQsExpanded) {
            resetViews(false);
        }
    }

    public void instantOpenQSPrc() {
        cancelQsAnimation();
        setQsExpansion((float) this.mQsMaxExpansionHeight);
    }

    /* access modifiers changed from: private */
    public void switchToNotificationIfNeed(boolean z) {
        if (z && this.mPanelState == 2) {
            if (PanelViewController.DEBUG_PANEL) {
                Log.i(PanelViewController.TAG, "PrcPanel Receive headsUp, switch to notification panel");
            }
            switchPanelState(1);
        }
    }

    public void switchAnimation(final boolean z) {
        this.mQs.getHeader();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(500);
        ofFloat.addUpdateListener(new NotificationPanelViewController$$ExternalSyntheticLambda3(this, z));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (z) {
                    NotificationPanelViewController.this.mNotificationStackScrollLayoutController.getView().setVisibility(8);
                    NotificationPanelViewController.this.instantOpenQSPrc();
                } else {
                    if (NotificationPanelViewController.this.mBarState == 1) {
                        NotificationPanelViewController.this.mStatusBarStateController.setState(2);
                    }
                    NotificationPanelViewController.this.closeQs();
                    NotificationPanelViewController.this.mNotificationStackScrollLayoutController.getView().setVisibility(0);
                }
                NotificationPanelViewController.this.mPrcContainer.setAlpha(0.0f);
                NotificationPanelViewController.this.mNotificationStackScrollLayoutController.setAlpha(0.0f);
            }

            public void onAnimationCancel(Animator animator) {
                boolean unused = NotificationPanelViewController.this.mIsSwitching = false;
            }

            public void onAnimationEnd(Animator animator) {
                if (PanelViewController.DEBUG_PANEL) {
                    Log.i(PanelViewController.TAG, "PrcPanel switchAnimation onAnimationEnd");
                }
                boolean unused = NotificationPanelViewController.this.mIsSwitching = false;
            }
        });
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$switchAnimation$12(boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (z) {
            this.mPrcContainer.setAlpha(floatValue);
        } else {
            this.mNotificationStackScrollLayoutController.setAlpha(floatValue);
        }
    }

    public void updatePanelStateAfterFling() {
        if (isKeyguardOpenQSState() && this.mBarState == 2 && this.mExpandedFraction == 1.0f) {
            if (PanelViewController.DEBUG_PANEL) {
                Log.i(PanelViewController.TAG, "PrcPanel, open qs while swip down from bouncer view");
            }
            instantOpenQSPrc();
        } else if (this.mBarState == 0 && this.mHeadsUpManager.hasPinnedHeadsUp() && this.mExpandedFraction == 0.0f) {
            if (PanelViewController.DEBUG_PANEL) {
                Log.i(PanelViewController.TAG, "PrcPanel, collapse notification panel while show headsup");
            }
            updatePanelViewState(0);
        }
    }

    /* access modifiers changed from: protected */
    public PanelViewController.OnConfigurationChangedListener createOnConfigurationChangedListener() {
        return new OnConfigurationChangedListener();
    }

    public NotificationStackScrollLayoutController getNotificationStackScrollLayoutController() {
        return this.mNotificationStackScrollLayoutController;
    }

    public boolean closeUserSwitcherIfOpen() {
        KeyguardUserSwitcherController keyguardUserSwitcherController = this.mKeyguardUserSwitcherController;
        if (keyguardUserSwitcherController != null) {
            return keyguardUserSwitcherController.closeSwitcherIfOpenAndNotSimple(true);
        }
        return false;
    }

    private void updateUserSwitcherFlags() {
        boolean z = this.mResources.getBoolean(17891640);
        this.mKeyguardUserSwitcherEnabled = z;
        this.mKeyguardQsUserSwitchEnabled = z && this.mResources.getBoolean(R$bool.config_keyguard_user_switch_opens_qs_details);
    }

    /* access modifiers changed from: private */
    public void registerSettingsChangeListener() {
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("user_switcher_enabled"), false, this.mSettingsChangeObserver);
    }

    /* access modifiers changed from: private */
    public void unregisterSettingsChangeListener() {
        this.mContentResolver.unregisterContentObserver(this.mSettingsChangeObserver);
    }

    private class OnHeightChangedListener implements ExpandableView.OnHeightChangedListener {
        public void onReset(ExpandableView expandableView) {
        }

        private OnHeightChangedListener() {
        }

        public void onHeightChanged(ExpandableView expandableView, boolean z) {
            if (expandableView != null || !NotificationPanelViewController.this.mQsExpanded) {
                if (z && NotificationPanelViewController.this.mInterpolatedDarkAmount == 0.0f) {
                    boolean unused = NotificationPanelViewController.this.mAnimateNextPositionUpdate = true;
                }
                ExpandableView firstChildNotGone = NotificationPanelViewController.this.mNotificationStackScrollLayoutController.getFirstChildNotGone();
                ExpandableNotificationRow expandableNotificationRow = firstChildNotGone instanceof ExpandableNotificationRow ? (ExpandableNotificationRow) firstChildNotGone : null;
                if (expandableNotificationRow != null && (expandableView == expandableNotificationRow || expandableNotificationRow.getNotificationParent() == expandableNotificationRow)) {
                    NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                }
                NotificationPanelViewController.this.requestPanelHeightUpdate();
            }
        }
    }

    private class OnClickListener implements View.OnClickListener {
        private OnClickListener() {
        }

        public void onClick(View view) {
            NotificationPanelViewController.this.onQsExpansionStarted();
            if (NotificationPanelViewController.this.mQsExpanded) {
                NotificationPanelViewController.this.flingSettings(0.0f, 1, (Runnable) null, true);
            } else if (NotificationPanelViewController.this.isQsExpansionEnabled()) {
                NotificationPanelViewController.this.mLockscreenGestureLogger.write(195, 0, 0);
                NotificationPanelViewController.this.flingSettings(0.0f, 0, (Runnable) null, true);
            }
        }
    }

    private class OnOverscrollTopChangedListener implements NotificationStackScrollLayout.OnOverscrollTopChangedListener {
        private OnOverscrollTopChangedListener() {
        }

        public void onOverscrollTopChanged(float f, boolean z) {
            if (!NotificationPanelViewController.this.mShouldUseSplitNotificationShade) {
                NotificationPanelViewController.this.cancelQsAnimation();
                if (!NotificationPanelViewController.this.isQsExpansionEnabled()) {
                    f = 0.0f;
                }
                if (f < 1.0f) {
                    f = 0.0f;
                }
                int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
                boolean z2 = true;
                NotificationPanelViewController.this.setOverScrolling(i != 0 && z);
                NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                if (i == 0) {
                    z2 = false;
                }
                boolean unused = notificationPanelViewController.mQsExpansionFromOverscroll = z2;
                float unused2 = NotificationPanelViewController.this.mLastOverscroll = f;
                NotificationPanelViewController.this.updateQsState();
                NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                notificationPanelViewController2.setQsExpansion(((float) notificationPanelViewController2.mQsMinExpansionHeight) + f);
            }
        }

        public void flingTopOverscroll(float f, boolean z) {
            if (!NotificationPanelViewController.this.mShouldUseSplitNotificationShade || (NotificationPanelViewController.this.mInitialTouchX >= NotificationPanelViewController.this.mQsFrame.getX() && NotificationPanelViewController.this.mInitialTouchX <= NotificationPanelViewController.this.mQsFrame.getX() + ((float) NotificationPanelViewController.this.mQsFrame.getWidth()))) {
                float unused = NotificationPanelViewController.this.mLastOverscroll = 0.0f;
                boolean unused2 = NotificationPanelViewController.this.mQsExpansionFromOverscroll = false;
                if (z) {
                    NotificationPanelViewController.this.setOverScrolling(false);
                }
                NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                notificationPanelViewController.setQsExpansion(notificationPanelViewController.mQsExpansionHeight);
                boolean access$4900 = NotificationPanelViewController.this.isQsExpansionEnabled();
                NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                if (!access$4900 && z) {
                    f = 0.0f;
                }
                notificationPanelViewController2.flingSettings(f, (!z || !access$4900) ? 1 : 0, new C1866x5c9cd5cc(this), false);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$flingTopOverscroll$0() {
            NotificationPanelViewController.this.setOverScrolling(false);
            NotificationPanelViewController.this.updateQsState();
        }
    }

    private class DynamicPrivacyControlListener implements DynamicPrivacyController.Listener {
        private DynamicPrivacyControlListener() {
        }

        public void onDynamicPrivacyChanged() {
            if (NotificationPanelViewController.this.mLinearDarkAmount == 0.0f) {
                boolean unused = NotificationPanelViewController.this.mAnimateNextPositionUpdate = true;
            }
        }
    }

    private class KeyguardAffordanceHelperCallback implements KeyguardAffordanceHelper.Callback {
        private KeyguardAffordanceHelperCallback() {
        }

        public void onAnimationToSideStarted(boolean z, float f, float f2) {
            if (NotificationPanelViewController.this.mView.getLayoutDirection() != 1) {
                z = !z;
            }
            boolean unused = NotificationPanelViewController.this.mIsLaunchTransitionRunning = true;
            Runnable unused2 = NotificationPanelViewController.this.mLaunchAnimationEndRunnable = null;
            float displayDensity = NotificationPanelViewController.this.mStatusBar.getDisplayDensity();
            int abs = Math.abs((int) (f / displayDensity));
            int abs2 = Math.abs((int) (f2 / displayDensity));
            if (z) {
                NotificationPanelViewController.this.mLockscreenGestureLogger.write(190, abs, abs2);
                NotificationPanelViewController.this.mLockscreenGestureLogger.log(LockscreenGestureLogger.LockscreenUiEvent.LOCKSCREEN_DIALER);
                NotificationPanelViewController.this.mFalsingCollector.onLeftAffordanceOn();
                if (NotificationPanelViewController.this.mFalsingCollector.shouldEnforceBouncer()) {
                    NotificationPanelViewController.this.mStatusBar.executeRunnableDismissingKeyguard(new C1864xe911bb02(this), (Runnable) null, true, false, true);
                } else {
                    NotificationPanelViewController.this.mKeyguardBottomArea.launchLeftAffordance();
                }
            } else {
                if ("lockscreen_affordance".equals(NotificationPanelViewController.this.mLastCameraLaunchSource)) {
                    NotificationPanelViewController.this.mLockscreenGestureLogger.write(189, abs, abs2);
                    NotificationPanelViewController.this.mLockscreenGestureLogger.log(LockscreenGestureLogger.LockscreenUiEvent.LOCKSCREEN_CAMERA);
                }
                NotificationPanelViewController.this.mFalsingCollector.onCameraOn();
                if (NotificationPanelViewController.this.mFalsingCollector.shouldEnforceBouncer()) {
                    NotificationPanelViewController.this.mStatusBar.executeRunnableDismissingKeyguard(new C1865xe911bb03(this), (Runnable) null, true, false, true);
                } else {
                    NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
                    notificationPanelViewController.mKeyguardBottomArea.launchCamera(notificationPanelViewController.mLastCameraLaunchSource, NotificationPanelViewController.this.mUseFrontCamera);
                }
            }
            NotificationPanelViewController.this.mStatusBar.startLaunchTransitionTimeout();
            boolean unused3 = NotificationPanelViewController.this.mBlockTouches = true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationToSideStarted$0() {
            NotificationPanelViewController.this.mKeyguardBottomArea.launchLeftAffordance();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationToSideStarted$1() {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mKeyguardBottomArea.launchCamera(notificationPanelViewController.mLastCameraLaunchSource, NotificationPanelViewController.this.mUseFrontCamera);
        }

        public void onAnimationToSideEnded() {
            boolean unused = NotificationPanelViewController.this.mIsLaunchTransitionRunning = false;
            boolean unused2 = NotificationPanelViewController.this.mIsLaunchTransitionFinished = true;
            if (NotificationPanelViewController.this.mLaunchAnimationEndRunnable != null) {
                NotificationPanelViewController.this.mLaunchAnimationEndRunnable.run();
                Runnable unused3 = NotificationPanelViewController.this.mLaunchAnimationEndRunnable = null;
            }
            NotificationPanelViewController.this.mStatusBar.readyForKeyguardDone();
        }

        public float getMaxTranslationDistance() {
            return (float) Math.hypot((double) NotificationPanelViewController.this.mView.getWidth(), (double) NotificationPanelViewController.this.getHeight());
        }

        public void onSwipingStarted(boolean z) {
            NotificationPanelViewController.this.mFalsingCollector.onAffordanceSwipingStarted(z);
            if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                z = !z;
            }
            if (z) {
                NotificationPanelViewController.this.mKeyguardBottomArea.bindCameraPrewarmService();
            }
            NotificationPanelViewController.this.mView.requestDisallowInterceptTouchEvent(true);
            boolean unused = NotificationPanelViewController.this.mOnlyAffordanceInThisMotion = true;
            boolean unused2 = NotificationPanelViewController.this.mQsTracking = false;
        }

        public void onSwipingAborted() {
            NotificationPanelViewController.this.mFalsingCollector.onAffordanceSwipingAborted();
            NotificationPanelViewController.this.mKeyguardBottomArea.unbindCameraPrewarmService(false);
        }

        public void onIconClicked(boolean z) {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            if (!notificationPanelViewController.mHintAnimationRunning) {
                notificationPanelViewController.mHintAnimationRunning = true;
                notificationPanelViewController.mAffordanceHelper.startHintAnimation(z, new C1863xe911bb01(this));
                if (NotificationPanelViewController.this.mView.getLayoutDirection() == 1) {
                    z = !z;
                }
                if (z) {
                    NotificationPanelViewController.this.mStatusBar.onCameraHintStarted();
                } else if (NotificationPanelViewController.this.mKeyguardBottomArea.isLeftVoiceAssist()) {
                    NotificationPanelViewController.this.mStatusBar.onVoiceAssistHintStarted();
                } else {
                    NotificationPanelViewController.this.mStatusBar.onPhoneHintStarted();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onIconClicked$2() {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mHintAnimationRunning = false;
            notificationPanelViewController.mStatusBar.onHintFinished();
        }

        public KeyguardAffordanceView getLeftIcon() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getRightView() : NotificationPanelViewController.this.mKeyguardBottomArea.getLeftView();
        }

        public KeyguardAffordanceView getRightIcon() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getLeftView() : NotificationPanelViewController.this.mKeyguardBottomArea.getRightView();
        }

        public View getLeftPreview() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getRightPreview() : NotificationPanelViewController.this.mKeyguardBottomArea.getLeftPreview();
        }

        public View getRightPreview() {
            return NotificationPanelViewController.this.mView.getLayoutDirection() == 1 ? NotificationPanelViewController.this.mKeyguardBottomArea.getLeftPreview() : NotificationPanelViewController.this.mKeyguardBottomArea.getRightPreview();
        }

        public float getAffordanceFalsingFactor() {
            return NotificationPanelViewController.this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f;
        }

        public boolean needsAntiFalsing() {
            return NotificationPanelViewController.this.mBarState == 1;
        }
    }

    private class OnEmptySpaceClickListener implements NotificationStackScrollLayout.OnEmptySpaceClickListener {
        private OnEmptySpaceClickListener() {
        }

        public void onEmptySpaceClicked(float f, float f2) {
            NotificationPanelViewController.this.onEmptySpaceClick(f);
        }
    }

    private class MyOnHeadsUpChangedListener implements OnHeadsUpChangedListener {
        private MyOnHeadsUpChangedListener() {
        }

        public void onHeadsUpPinnedModeChanged(boolean z) {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            if (notificationPanelViewController.mIsPrcCustom) {
                notificationPanelViewController.switchToNotificationIfNeed(z);
            }
            if (z) {
                NotificationPanelViewController.this.mHeadsUpExistenceChangedRunnable.run();
                NotificationPanelViewController.this.updateNotificationTranslucency();
            } else {
                NotificationPanelViewController.this.setHeadsUpAnimatingAway(true);
                NotificationPanelViewController.this.mNotificationStackScrollLayoutController.runAfterAnimationFinished(NotificationPanelViewController.this.mHeadsUpExistenceChangedRunnable);
            }
            NotificationPanelViewController.this.updateGestureExclusionRect();
            boolean unused = NotificationPanelViewController.this.mHeadsUpPinnedMode = z;
            NotificationPanelViewController.this.updateHeadsUpVisibility();
            NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
        }

        public void onHeadsUpPinned(NotificationEntry notificationEntry) {
            if (!NotificationPanelViewController.this.isOnKeyguard()) {
                NotificationPanelViewController.this.mNotificationStackScrollLayoutController.generateHeadsUpAnimation(notificationEntry.getHeadsUpAnimationView(), true);
            }
        }

        public void onHeadsUpUnPinned(NotificationEntry notificationEntry) {
            if (NotificationPanelViewController.this.isFullyCollapsed() && notificationEntry.isRowHeadsUp() && !NotificationPanelViewController.this.isOnKeyguard()) {
                NotificationPanelViewController.this.mNotificationStackScrollLayoutController.generateHeadsUpAnimation(notificationEntry.getHeadsUpAnimationView(), false);
                notificationEntry.setHeadsUpIsVisible();
            }
        }
    }

    private class HeightListener implements C1129QS.HeightListener {
        private HeightListener() {
        }

        public void onQsHeightChanged() {
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            C1129QS qs = notificationPanelViewController.mQs;
            int unused = notificationPanelViewController.mQsMaxExpansionHeight = qs != null ? qs.getDesiredHeight() : 0;
            if (NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mQsFullyExpanded) {
                NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                float unused2 = notificationPanelViewController2.mQsExpansionHeight = (float) notificationPanelViewController2.mQsMaxExpansionHeight;
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
            }
            if (NotificationPanelViewController.this.mAccessibilityManager.isEnabled()) {
                NotificationPanelViewController.this.mView.setAccessibilityPaneTitle(NotificationPanelViewController.this.determineAccessibilityPaneTitle());
            }
            NotificationPanelViewController.this.mNotificationStackScrollLayoutController.setMaxTopPadding(NotificationPanelViewController.this.mQsMaxExpansionHeight);
        }
    }

    private class ConfigurationListener implements ConfigurationController.ConfigurationListener {
        private ConfigurationListener() {
        }

        public void onThemeChanged() {
            int themeResId = NotificationPanelViewController.this.mView.getContext().getThemeResId();
            if (NotificationPanelViewController.this.mThemeResId != themeResId) {
                int unused = NotificationPanelViewController.this.mThemeResId = themeResId;
                NotificationPanelViewController.this.reInflateViews();
            }
        }

        public void onSmallestScreenWidthChanged() {
            NotificationPanelViewController.this.reInflateViews();
        }

        public void onOverlayChanged() {
            Log.i(PanelViewController.TAG, "RROs onOverlayChanged");
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            boolean unused = notificationPanelViewController.mIsBellowCarrierName = MotoFeature.getInstance(notificationPanelViewController.mContext).isBelowCarrierName();
            if (NotificationPanelViewController.this.mIsBellowCarrierName) {
                NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
                View unused2 = notificationPanelViewController2.mCarrierLabel = notificationPanelViewController2.mView.findViewById(R$id.keyguard_carrier_text);
                if (NotificationPanelViewController.this.mKeyguardShowing) {
                    NotificationPanelViewController.this.mCarrierLabel.setAlpha(1.0f);
                    NotificationPanelViewController.this.mCarrierLabel.setVisibility(0);
                } else {
                    NotificationPanelViewController.this.mCarrierLabel.setAlpha(0.0f);
                    NotificationPanelViewController.this.mCarrierLabel.setVisibility(4);
                }
            } else if (NotificationPanelViewController.this.mCarrierLabel != null) {
                NotificationPanelViewController.this.mCarrierLabel.setAlpha(0.0f);
                NotificationPanelViewController.this.mCarrierLabel.setVisibility(4);
            }
            NotificationPanelViewController.this.reInflateViews();
        }

        public void onDensityOrFontScaleChanged() {
            NotificationPanelViewController.this.reInflateViews();
        }
    }

    private class SettingsChangeObserver extends ContentObserver {
        SettingsChangeObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            NotificationPanelViewController.this.reInflateViews();
        }
    }

    private class StatusBarStateListener implements StatusBarStateController.StateListener {
        private StatusBarStateListener() {
        }

        public void onStateChanged(int i) {
            C1129QS qs;
            boolean goingToFullShade = NotificationPanelViewController.this.mStatusBarStateController.goingToFullShade();
            boolean isKeyguardFadingAway = NotificationPanelViewController.this.mKeyguardStateController.isKeyguardFadingAway();
            int access$1200 = NotificationPanelViewController.this.mBarState;
            boolean z = i == 1;
            if (NotificationPanelViewController.this.mDozeParameters.shouldControlUnlockedScreenOff() && access$1200 == 0 && i == 1) {
                NotificationPanelViewController.this.mKeyguardStatusViewController.updatePosition(NotificationPanelViewController.this.mClockPositionResult.clockX, NotificationPanelViewController.this.mClockPositionResult.clockYFullyDozing, NotificationPanelViewController.this.mClockPositionResult.clockScale, false);
            }
            NotificationPanelViewController.this.mKeyguardStatusViewController.setKeyguardStatusViewVisibility(i, isKeyguardFadingAway, goingToFullShade, NotificationPanelViewController.this.mBarState, NotificationPanelViewController.this.mStatusBar.isForceShowBouncer());
            NotificationPanelViewController.this.setKeyguardBottomAreaVisibility(i, goingToFullShade);
            int unused = NotificationPanelViewController.this.mBarState = i;
            boolean unused2 = NotificationPanelViewController.this.mKeyguardShowing = z;
            if (PanelViewController.DEBUG_PANEL) {
                Log.i(PanelViewController.TAG, "PrcPanel onStateChanged mBarState = " + NotificationPanelViewController.this.mBarState + " oldState = " + access$1200);
            }
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            if (!(!notificationPanelViewController.mIsPrcCustom || access$1200 == notificationPanelViewController.mBarState || NotificationPanelViewController.this.mBarState == 2)) {
                if (NotificationPanelViewController.this.mNotificationContainerParent.isCustomizerShown()) {
                    NotificationPanelViewController.this.updatePanelViewState(2);
                } else {
                    NotificationPanelViewController.this.updatePanelViewState(0);
                }
            }
            if (access$1200 == 1 && (goingToFullShade || i == 2)) {
                NotificationPanelViewController.this.animateKeyguardStatusBarOut();
                NotificationPanelViewController.this.updateQSMinHeight();
            } else if (access$1200 == 2 && i == 1) {
                NotificationPanelViewController.this.animateKeyguardStatusBarIn(360);
                NotificationPanelViewController.this.mNotificationStackScrollLayoutController.resetScrollPosition();
                if (!NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mShouldUseSplitNotificationShade) {
                    NotificationPanelViewController.this.mQs.animateHeaderSlidingOut();
                }
            } else {
                NotificationPanelViewController.this.mKeyguardStatusBar.setAlpha(1.0f);
                int i2 = 4;
                NotificationPanelViewController.this.mKeyguardStatusBar.setVisibility(z ? 0 : 4);
                if (NotificationPanelViewController.this.mIsBellowCarrierName) {
                    NotificationPanelViewController.this.mCarrierLabel.setAlpha(1.0f);
                    View access$2900 = NotificationPanelViewController.this.mCarrierLabel;
                    if (z) {
                        i2 = 0;
                    }
                    access$2900.setVisibility(i2);
                }
                if (!(!z || access$1200 == NotificationPanelViewController.this.mBarState || (qs = NotificationPanelViewController.this.mQs) == null)) {
                    qs.hideImmediately();
                }
            }
            NotificationPanelViewController.this.updateKeyguardStatusBarForHeadsUp();
            if (z) {
                NotificationPanelViewController.this.updateDozingVisibilities(false);
            }
            NotificationPanelViewController.this.updateMaxDisplayedNotifications(false);
            NotificationPanelViewController.this.maybeAnimateBottomAreaAlpha();
            NotificationPanelViewController.this.resetHorizontalPanelPosition();
            NotificationPanelViewController.this.updateQsState();
        }

        public void onDozeAmountChanged(float f, float f2) {
            float unused = NotificationPanelViewController.this.mInterpolatedDarkAmount = f2;
            float unused2 = NotificationPanelViewController.this.mLinearDarkAmount = f;
            NotificationPanelViewController.this.mKeyguardStatusViewController.setDarkAmount(NotificationPanelViewController.this.mInterpolatedDarkAmount);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mKeyguardBottomArea.setDarkAmount(notificationPanelViewController.mInterpolatedDarkAmount);
            NotificationPanelViewController.this.positionClockAndNotifications();
        }
    }

    public void showAodUi() {
        setDozing(true, false, (PointF) null);
        this.mStatusBarStateController.setUpcomingState(1);
        this.mEntryManager.updateNotifications("showAodUi");
        this.mStatusBarStateListener.onStateChanged(1);
        this.mStatusBarStateListener.onDozeAmountChanged(1.0f, 1.0f);
        setExpandedFraction(1.0f);
    }

    public void setOverStrechAmount(float f) {
        this.mOverStretchAmount = Interpolators.getOvershootInterpolation(f / ((float) this.mView.getHeight())) * ((float) this.mMaxOverscrollAmountForPulse);
        positionClockAndNotifications(true);
    }

    private class OnAttachStateChangeListener implements View.OnAttachStateChangeListener {
        private OnAttachStateChangeListener() {
        }

        public void onViewAttachedToWindow(View view) {
            NotificationPanelViewController.this.mFragmentService.getFragmentHostManager(NotificationPanelViewController.this.mView).addTagListener(C1129QS.TAG, NotificationPanelViewController.this.mFragmentListener);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mStatusBarStateController.addCallback(notificationPanelViewController.mStatusBarStateListener);
            NotificationPanelViewController.this.mConfigurationController.addCallback(NotificationPanelViewController.this.mConfigurationListener);
            NotificationPanelViewController.this.mUpdateMonitor.registerCallback(NotificationPanelViewController.this.mKeyguardUpdateCallback);
            NotificationPanelViewController.this.mConfigurationListener.onThemeChanged();
            NotificationPanelViewController.this.mFalsingManager.addTapListener(NotificationPanelViewController.this.mFalsingTapListener);
            NotificationPanelViewController.this.mKeyguardIndicationController.init();
            NotificationPanelViewController.this.registerSettingsChangeListener();
        }

        public void onViewDetachedFromWindow(View view) {
            NotificationPanelViewController.this.unregisterSettingsChangeListener();
            NotificationPanelViewController.this.mFragmentService.getFragmentHostManager(NotificationPanelViewController.this.mView).removeTagListener(C1129QS.TAG, NotificationPanelViewController.this.mFragmentListener);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            notificationPanelViewController.mStatusBarStateController.removeCallback(notificationPanelViewController.mStatusBarStateListener);
            NotificationPanelViewController.this.mConfigurationController.removeCallback(NotificationPanelViewController.this.mConfigurationListener);
            NotificationPanelViewController.this.mUpdateMonitor.removeCallback(NotificationPanelViewController.this.mKeyguardUpdateCallback);
            NotificationPanelViewController.this.mFalsingManager.removeTapListener(NotificationPanelViewController.this.mFalsingTapListener);
        }
    }

    private class OnLayoutChangeListener extends PanelViewController.OnLayoutChangeListener {
        private OnLayoutChangeListener() {
            super();
        }

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            C1129QS qs;
            DejankUtils.startDetectingBlockingIpcs("NVP#onLayout");
            super.onLayoutChange(view, i, i2, i3, i4, i5, i6, i7, i8);
            boolean z = true;
            NotificationPanelViewController.this.updateMaxDisplayedNotifications(true);
            NotificationPanelViewController notificationPanelViewController = NotificationPanelViewController.this;
            if (notificationPanelViewController.mNotificationStackScrollLayoutController.getWidth() != ((float) NotificationPanelViewController.this.mView.getWidth())) {
                z = false;
            }
            notificationPanelViewController.setIsFullWidth(z);
            NotificationPanelViewController.this.mKeyguardStatusViewController.setPivotX((float) (NotificationPanelViewController.this.mView.getWidth() / 2));
            NotificationPanelViewController.this.mKeyguardStatusViewController.setPivotY(NotificationPanelViewController.this.mKeyguardStatusViewController.getClockTextSize() * 0.34521484f);
            int access$11100 = NotificationPanelViewController.this.mQsMaxExpansionHeight;
            NotificationPanelViewController notificationPanelViewController2 = NotificationPanelViewController.this;
            if (notificationPanelViewController2.mQs != null) {
                notificationPanelViewController2.updateQSMinHeight();
                NotificationPanelViewController notificationPanelViewController3 = NotificationPanelViewController.this;
                int unused = notificationPanelViewController3.mQsMaxExpansionHeight = notificationPanelViewController3.mQs.getDesiredHeight();
                NotificationPanelViewController.this.mNotificationStackScrollLayoutController.setMaxTopPadding(NotificationPanelViewController.this.mQsMaxExpansionHeight);
            }
            NotificationPanelViewController.this.positionClockAndNotifications();
            if (NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mQsFullyExpanded) {
                NotificationPanelViewController notificationPanelViewController4 = NotificationPanelViewController.this;
                float unused2 = notificationPanelViewController4.mQsExpansionHeight = (float) notificationPanelViewController4.mQsMaxExpansionHeight;
                NotificationPanelViewController.this.requestScrollerTopPaddingUpdate(false);
                NotificationPanelViewController.this.requestPanelHeightUpdate();
                if (NotificationPanelViewController.this.mQsMaxExpansionHeight != access$11100) {
                    NotificationPanelViewController notificationPanelViewController5 = NotificationPanelViewController.this;
                    notificationPanelViewController5.startQsSizeChangeAnimation(access$11100, notificationPanelViewController5.mQsMaxExpansionHeight);
                }
            } else if (!NotificationPanelViewController.this.mQsExpanded && NotificationPanelViewController.this.mQsExpansionAnimator == null) {
                NotificationPanelViewController notificationPanelViewController6 = NotificationPanelViewController.this;
                notificationPanelViewController6.setQsExpansion(((float) notificationPanelViewController6.mQsMinExpansionHeight) + NotificationPanelViewController.this.mLastOverscroll);
            }
            NotificationPanelViewController notificationPanelViewController7 = NotificationPanelViewController.this;
            notificationPanelViewController7.updateExpandedHeight(notificationPanelViewController7.getExpandedHeight());
            NotificationPanelViewController.this.updateHeader();
            if (NotificationPanelViewController.this.mQsSizeChangeAnimator == null && (qs = NotificationPanelViewController.this.mQs) != null) {
                qs.setHeightOverride(qs.getDesiredHeight());
            }
            NotificationPanelViewController.this.updateMaxHeadsUpTranslation();
            NotificationPanelViewController.this.updateGestureExclusionRect();
            if (NotificationPanelViewController.this.mExpandAfterLayoutRunnable != null) {
                NotificationPanelViewController.this.mExpandAfterLayoutRunnable.run();
                Runnable unused3 = NotificationPanelViewController.this.mExpandAfterLayoutRunnable = null;
            }
            DejankUtils.stopDetectingBlockingIpcs("NVP#onLayout");
        }
    }

    /* access modifiers changed from: private */
    public void updateQSMinHeight() {
        float f = (float) this.mQsMinExpansionHeight;
        int qsMinExpansionHeight = this.mKeyguardShowing ? 0 : this.mQs.getQsMinExpansionHeight();
        this.mQsMinExpansionHeight = qsMinExpansionHeight;
        if (this.mQsExpansionHeight == f) {
            this.mQsExpansionHeight = (float) qsMinExpansionHeight;
        }
    }

    private class OnConfigurationChangedListener extends PanelViewController.OnConfigurationChangedListener {
        private OnConfigurationChangedListener() {
            super();
        }

        public void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            NotificationPanelViewController.this.mAffordanceHelper.onConfigurationChanged();
            if (configuration.orientation != NotificationPanelViewController.this.mLastOrientation) {
                NotificationPanelViewController.this.resetHorizontalPanelPosition();
            }
            int unused = NotificationPanelViewController.this.mLastOrientation = configuration.orientation;
        }
    }

    private class OnApplyWindowInsetsListener implements View.OnApplyWindowInsetsListener {
        private OnApplyWindowInsetsListener() {
        }

        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            Insets insetsIgnoringVisibility = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
            int unused = NotificationPanelViewController.this.mDisplayTopInset = insetsIgnoringVisibility.top;
            int unused2 = NotificationPanelViewController.this.mDisplayRightInset = insetsIgnoringVisibility.right;
            int unused3 = NotificationPanelViewController.this.mNavigationBarBottomHeight = windowInsets.getStableInsetBottom();
            NotificationPanelViewController.this.updateMaxHeadsUpTranslation();
            return windowInsets;
        }
    }

    /* access modifiers changed from: protected */
    public void setHeaderState(int i) {
        View header = this.mQs.getHeader();
        if (header instanceof QuickStatusBarHeader) {
            ((QuickStatusBarHeader) header).updateHeaderStatus(i);
        }
    }
}
