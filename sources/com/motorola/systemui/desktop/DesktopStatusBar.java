package com.motorola.systemui.desktop;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.Fragment;
import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.app.ProfilerInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.lifecycle.Lifecycle;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.startingsurface.StartingSurface;
import com.android.systemui.Dependency;
import com.android.systemui.InitController;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.accessibility.floatingmenu.AccessibilityFloatingMenuController;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.fragments.ExtensionFragmentListener;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.keyguard.KeyguardUnlockAnimationController;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.p006qs.QSContainerImpl;
import com.android.systemui.p006qs.QSFragment;
import com.android.systemui.p006qs.QSPanelController;
import com.android.systemui.p006qs.QSPrcPanelContainerController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.p005qs.C1129QS;
import com.android.systemui.recents.ScreenPinningRequest;
import com.android.systemui.settings.brightness.BrightnessSlider;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.NotificationShelfController;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.charging.WiredChargingRippleController;
import com.android.systemui.statusbar.events.SystemStatusAnimationScheduler;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LightsOutNotifController;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarLocationPublisher;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController;
import com.android.systemui.statusbar.phone.dagger.StatusBarComponent;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.volume.VolumeComponent;
import com.android.systemui.wmshell.BubblesManager;
import com.motorola.systemui.desktop.dagger.statusbar.phone.DesktopStatusBarComponent;
import com.motorola.systemui.desktop.overwrites.statusbar.notification.DesktopHeadsUpController;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopStatusBarNotificationPresenter;
import com.motorola.systemui.desktop.widget.DesktopNotificationPanelLayout;
import com.motorola.systemui.desktop.widget.DesktopQSPanelLayout;
import dagger.Lazy;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public class DesktopStatusBar extends StatusBar implements RemoteInputController.Callback {
    private ConfigurationController mConfigurationController;
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onUiModeChanged() {
            DesktopStatusBar.this.updateResources();
        }

        public void onConfigChanged(Configuration configuration) {
            DesktopStatusBar.this.mViewHierarchyManager.updateRowStates();
        }
    };
    /* access modifiers changed from: private */
    public DesktopNotificationPanelLayout mDesktopNotificationPanelLayout;
    /* access modifiers changed from: private */
    public DesktopQSPanelLayout mDesktopQSPanelLayout;
    private int mDisplayId;
    private WindowManager mDisplayWindowManager;
    private final ExtensionController mExtensionController;
    private DesktopHeadsUpController mHeadsUpController;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private final InjectionInflationController mInjectionInflationController;
    private boolean mIsQSNShowing = false;
    private boolean mIsRemoteInputActive = false;
    private long mLastQSNToggleTime = 0;
    private int mLayoutDirection = 0;
    private WindowManager.LayoutParams mLayoutParams;
    private final Handler mMainHandler;
    private final DesktopNotificationActivityStarter mNotificationActivityStarter;
    private NotificationEntryManager mNotificationEntryManager = null;
    protected final NotificationIconAreaController mNotificationIconAreaController;
    private DesktopStatusBarNotificationPresenter mNotificationPresenter;
    protected NotificationShelfController mNotificationShelfController;
    private DesktopNotificationStackScrollLayout mNotificationStackScrollLayout;
    private DesktopNotificationStackScrollLayoutController mNotificationStackScrollLayoutController;
    private final NotificationsController mNotificationsController;
    /* access modifiers changed from: private */
    public int mQSContainerImplExactlyHeight = 0;
    /* access modifiers changed from: private */
    public ViewGroup mQSNContentView;
    /* access modifiers changed from: private */
    public QSPanelController mQSPanelController;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final DesktopStatusBarComponent mStatusBarComponent;
    private final Provider<DesktopStatusBarComponent.Builder> mStatusBarComponentBuilder;
    private final SuperStatusBarViewFactory mSuperStatusBarViewFactory;
    /* access modifiers changed from: private */
    public NotificationViewHierarchyManager mViewHierarchyManager;
    private final VisualStabilityManager mVisualStabilityManager;

    public void dispatchDemoCommand(String str, Bundle bundle) {
    }

    public Lifecycle getLifecycle() {
        return null;
    }

    public int getQSPanelHeight() {
        return -1;
    }

    public void onColorsChanged(ColorExtractor colorExtractor, int i) {
    }

    public DesktopStatusBar(Context context, NotificationsController notificationsController, HeadsUpManagerPhone headsUpManagerPhone, NotificationRemoteInputManager notificationRemoteInputManager, VisualStabilityManager visualStabilityManager, InjectionInflationController injectionInflationController, Provider<DesktopStatusBarComponent.Builder> provider, DesktopNotificationActivityStarter desktopNotificationActivityStarter, SuperStatusBarViewFactory superStatusBarViewFactory, NotificationIconAreaController notificationIconAreaController, ExtensionController extensionController, ConfigurationController configurationController, NotificationViewHierarchyManager notificationViewHierarchyManager, StatusBarKeyguardViewManager statusBarKeyguardViewManager, Handler handler) {
        super(context, notificationsController, (LightBarController) null, (AutoHideController) null, (KeyguardUpdateMonitor) null, (StatusBarSignalPolicy) null, (PulseExpansionHandler) null, (NotificationWakeUpCoordinator) null, (KeyguardBypassController) null, (KeyguardStateController) null, headsUpManagerPhone, (DynamicPrivacyController) null, (BypassHeadsUpNotifier) null, (FalsingManager) null, (FalsingCollector) null, (BroadcastDispatcher) null, (RemoteInputQuickSettingsDisabler) null, (NotificationGutsManager) null, (NotificationLogger) null, (NotificationInterruptStateProvider) null, (NotificationViewHierarchyManager) null, (KeyguardViewMediator) null, (DisplayMetrics) null, (MetricsLogger) null, (Executor) null, (NotificationMediaManager) null, (NotificationLockscreenUserManager) null, notificationRemoteInputManager, (UserSwitcherController) null, (NetworkController) null, (BatteryController) null, (SysuiColorExtractor) null, (ScreenLifecycle) null, (WakefulnessLifecycle) null, (SysuiStatusBarStateController) null, (VibratorHelper) null, (Optional<BubblesManager>) null, (Optional<Bubbles>) null, visualStabilityManager, (DeviceProvisionedController) null, (NavigationBarController) null, (AccessibilityFloatingMenuController) null, (Lazy<AssistManager>) null, (ConfigurationController) null, (NotificationShadeWindowController) null, (DozeParameters) null, (ScrimController) null, (KeyguardLiftController) null, (Lazy<LockscreenWallpaper>) null, (Lazy<BiometricUnlockController>) null, (DozeServiceHost) null, (PowerManager) null, (ScreenPinningRequest) null, (DozeScrimController) null, (VolumeComponent) null, (CommandQueue) null, (Provider<StatusBarComponent.Builder>) null, (PluginManager) null, (Optional<LegacySplitScreen>) null, (LightsOutNotifController) null, (StatusBarNotificationActivityStarter.Builder) null, (ShadeController) null, superStatusBarViewFactory, statusBarKeyguardViewManager, (ViewMediatorCallback) null, (InitController) null, (Handler) null, (PluginDependencyProvider) null, (KeyguardDismissUtil) null, (ExtensionController) null, (UserInfoControllerImpl) null, (PhoneStatusBarPolicy) null, (KeyguardIndicationController) null, (DismissCallbackRegistry) null, (DemoModeController) null, (Lazy<NotificationShadeDepthController>) null, (StatusBarTouchableRegionManager) null, notificationIconAreaController, (BrightnessSlider.Factory) null, (WiredChargingRippleController) null, (OngoingCallController) null, (SystemStatusAnimationScheduler) null, (StatusBarLocationPublisher) null, (StatusBarIconController) null, (LockscreenShadeTransitionController) null, (FeatureFlags) null, (KeyguardUnlockAnimationController) null, (UnlockedScreenOffAnimationController) null, (Optional<StartingSurface>) null);
        this.mNotificationsController = notificationsController;
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mInjectionInflationController = injectionInflationController;
        this.mStatusBarComponentBuilder = provider;
        this.mSuperStatusBarViewFactory = superStatusBarViewFactory;
        this.mNotificationActivityStarter = desktopNotificationActivityStarter;
        this.mStatusBarComponent = provider.get().build();
        this.mNotificationIconAreaController = notificationIconAreaController;
        this.mExtensionController = extensionController;
        this.mMainHandler = handler;
        this.mConfigurationController = configurationController;
        this.mViewHierarchyManager = notificationViewHierarchyManager;
    }

    /* access modifiers changed from: private */
    public void updateResources() {
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            DesktopNotificationPanelLayout desktopNotificationPanelLayout = this.mDesktopNotificationPanelLayout;
            Context context = this.mContext;
            int i = R$color.prc_scrimview_background_color;
            desktopNotificationPanelLayout.setBackgroundColor(context.getColor(i));
            View findViewById = this.mQSNContentView.findViewById(R$id.qs_frame);
            if (findViewById != null) {
                findViewById.setBackgroundColor(this.mContext.getColor(i));
            }
        }
    }

    public void start() {
        Class<C1129QS> cls = C1129QS.class;
        this.mDisplayId = this.mContext.getDisplayId();
        this.mNotificationEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        this.mDisplayWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mLayoutDirection = this.mContext.getResources().getConfiguration().getLayoutDirection();
        this.mLayoutParams = genWindowManagerLayoutParams();
        this.mNotificationStackScrollLayoutController = this.mStatusBarComponent.getNotificationStackScrollLayoutController();
        this.mQSNContentView = (ViewGroup) this.mInjectionInflationController.injectable(LayoutInflater.from(this.mContext)).inflate(R$layout.desktop_notifications_content, (ViewGroup) null);
        if (!needQSPanelHorizontalPadding()) {
            this.mQSNContentView.setPaddingRelative(0, this.mQSNContentView.getPaddingTop(), 0, this.mQSNContentView.getPaddingBottom());
        }
        this.mDesktopNotificationPanelLayout = (DesktopNotificationPanelLayout) this.mQSNContentView.findViewById(R$id.notification_stack_panel);
        this.mNotificationStackScrollLayout = (DesktopNotificationStackScrollLayout) this.mQSNContentView.findViewById(R$id.notification_stack_scroller);
        ViewGroup viewGroup = this.mQSNContentView;
        int i = R$id.qs_frame;
        this.mDesktopQSPanelLayout = (DesktopQSPanelLayout) viewGroup.findViewById(i);
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            int paddingTop = this.mDesktopQSPanelLayout.getPaddingTop();
            this.mDesktopQSPanelLayout.setPadding(0, paddingTop, 0, paddingTop);
            this.mDesktopQSPanelLayout.setClipChildren(true);
            this.mDesktopQSPanelLayout.setClipToPadding(true);
        }
        this.mNotificationStackScrollLayoutController.attach(this.mNotificationStackScrollLayout);
        this.mNotificationStackScrollLayoutController.getNotificationListContainer().setNotificationActivityStarter(this.mNotificationActivityStarter);
        this.mNotificationStackScrollLayoutController.setIntrinsicPadding(this.mContext.getResources().getDimensionPixelSize(R$dimen.notification_side_paddings));
        NotificationShelfController notificationShelfController = this.mSuperStatusBarViewFactory.getNotificationShelfController(this.mNotificationStackScrollLayout);
        this.mNotificationShelfController = notificationShelfController;
        this.mNotificationIconAreaController.setupShelf(notificationShelfController);
        this.mHeadsUpManager.setup(this.mVisualStabilityManager);
        this.mNotificationPresenter = new DesktopStatusBarNotificationPresenter(this.mContext.getDisplayId(), this.mNotificationStackScrollLayoutController);
        this.mNotificationsController.initialize(this, Optional.ofNullable((Object) null), this.mNotificationPresenter, this.mNotificationStackScrollLayoutController.getNotificationListContainer(), this.mNotificationActivityStarter, this.mNotificationPresenter);
        this.mRemoteInputManager.getController().addCallback(this);
        this.mQSNContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                View findViewById = DesktopStatusBar.this.mQSNContentView.findViewById(R$id.quick_settings_panel);
                if (MotoFeature.getInstance(DesktopStatusBar.this.mContext).isCustomPanelView()) {
                    findViewById = DesktopStatusBar.this.mQSNContentView.findViewById(R$id.qs_prc_panel_container);
                }
                if (findViewById != null) {
                    int height = findViewById.getHeight();
                    int paddingTop = DesktopStatusBar.this.mDesktopQSPanelLayout.getPaddingTop();
                    int paddingBottom = DesktopStatusBar.this.mDesktopQSPanelLayout.getPaddingBottom();
                    int height2 = DesktopStatusBar.this.mQSNContentView.getHeight();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    DesktopStatusBar.this.mContext.getDisplay().getRealMetrics(displayMetrics);
                    if (MotoFeature.getInstance(DesktopStatusBar.this.mContext).isCustomPanelView() && DesktopStatusBar.this.mQSPanelController != null) {
                        height = ((QSPrcPanelContainerController) DesktopStatusBar.this.mQSPanelController).getDesktopQsPanelMaxHeight();
                        int i9 = height + paddingTop + paddingBottom;
                        int i10 = displayMetrics.heightPixels;
                        if (i9 > i10 / 2) {
                            i9 = i10 / 2;
                            height = (i9 - paddingTop) - paddingBottom;
                        }
                        int min = Math.min(i9, i10 / 2);
                        if (DesktopStatusBar.this.mDesktopQSPanelLayout.getHeight() > min) {
                            ViewGroup.LayoutParams layoutParams = DesktopStatusBar.this.mDesktopQSPanelLayout.getLayoutParams();
                            layoutParams.height = min;
                            DesktopStatusBar.this.mDesktopQSPanelLayout.setLayoutParams(layoutParams);
                            View childAt = DesktopStatusBar.this.mDesktopQSPanelLayout.getChildAt(0);
                            if (childAt != null && (childAt instanceof QSContainerImpl)) {
                                int unused = DesktopStatusBar.this.mQSContainerImplExactlyHeight = height;
                                ((QSContainerImpl) childAt).setExactlyHeight(height);
                            }
                        } else {
                            height = (DesktopStatusBar.this.mDesktopQSPanelLayout.getHeight() - paddingTop) - paddingBottom;
                        }
                    }
                    int dimensionPixelSize = DesktopStatusBar.this.mContext.getResources().getDimensionPixelSize(R$dimen.desktop_qs_notification_panel_gape);
                    int dimensionPixelSize2 = DesktopStatusBar.this.mContext.getResources().getDimensionPixelSize(R$dimen.desktop_qs_panel_margin);
                    int dimensionPixelSize3 = DesktopStatusBar.this.mContext.getResources().getDimensionPixelSize(17105631) + dimensionPixelSize2;
                    if (DesktopFeature.isInMobileUiMode(DesktopStatusBar.this.mContext) || height2 < displayMetrics.heightPixels) {
                        dimensionPixelSize3 = dimensionPixelSize2;
                    }
                    DesktopStatusBar.this.mDesktopNotificationPanelLayout.setTranslationY((float) (height + dimensionPixelSize + paddingTop + paddingBottom + dimensionPixelSize2));
                    ViewGroup.LayoutParams layoutParams2 = DesktopStatusBar.this.mDesktopNotificationPanelLayout.getLayoutParams();
                    int height3 = (((((DesktopStatusBar.this.mQSNContentView.getHeight() - height) - dimensionPixelSize) - paddingTop) - paddingBottom) - dimensionPixelSize2) - dimensionPixelSize3;
                    if (layoutParams2.height != height3) {
                        layoutParams2.height = height3;
                        DesktopStatusBar.this.mDesktopNotificationPanelLayout.setLayoutParams(layoutParams2);
                        DesktopStatusBar.this.mDesktopQSPanelLayout.setRealHeight(height + paddingTop + paddingBottom);
                    }
                }
            }
        });
        View findViewById = this.mQSNContentView.findViewById(i);
        if (findViewById != null) {
            FragmentHostManager.get(this.mQSNContentView).addTagListener(C1129QS.TAG, new DesktopStatusBar$$ExternalSyntheticLambda0(this));
            ExtensionFragmentListener.attachExtensonToFragment(findViewById, C1129QS.TAG, i, this.mExtensionController.newExtension(cls).withPlugin(cls).withDefault(new DesktopStatusBar$$ExternalSyntheticLambda7(this)).build());
        }
        DesktopHeadsUpController headsUpController = this.mStatusBarComponent.getHeadsUpController();
        this.mHeadsUpController = headsUpController;
        headsUpController.attach();
        updateResources();
        this.mConfigurationController.addCallback(this.mConfigurationListener);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$start$0(String str, Fragment fragment) {
        View childAt;
        C1129QS qs = (C1129QS) fragment;
        if (qs instanceof QSFragment) {
            QSFragment qSFragment = (QSFragment) qs;
            QSPanelController qSPanelController = qSFragment.getQSPanelController();
            this.mQSPanelController = qSPanelController;
            final View brightnessView = qSPanelController.getBrightnessView();
            if (brightnessView != null) {
                brightnessView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    public void onViewDetachedFromWindow(View view) {
                    }

                    public void onViewAttachedToWindow(View view) {
                        brightnessView.setVisibility(8);
                    }
                });
            }
            if (MotoFeature.getInstance(this.mContext).isCustomPanelView() && this.mQSContainerImplExactlyHeight > 0 && (childAt = this.mDesktopQSPanelLayout.getChildAt(0)) != null && (childAt instanceof QSContainerImpl)) {
                ((QSContainerImpl) childAt).setExactlyHeight(this.mQSContainerImplExactlyHeight);
            }
            qSFragment.setupAsDestopView();
        }
    }

    public void requestShowPanel() {
        this.mMainHandler.post(new DesktopStatusBar$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestShowPanel$1() {
        if (!this.mIsQSNShowing) {
            showPanelInternal(false, false);
        }
    }

    public void requestHidePanel() {
        this.mMainHandler.post(new DesktopStatusBar$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHidePanel$2() {
        if (this.mIsQSNShowing) {
            hidePanelInternal(false, false);
        }
    }

    public void requestTogglePanel() {
        this.mMainHandler.post(new DesktopStatusBar$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestTogglePanel$3() {
        if (this.mIsQSNShowing) {
            hidePanelInternal(false, false);
        } else {
            showPanelInternal(false, false);
        }
    }

    private void showPanelInternal(boolean z, boolean z2) {
        if (z || isValidRequest()) {
            this.mDisplayWindowManager.addView(this.mQSNContentView, this.mLayoutParams);
            this.mQSPanelController.setListening(true, true);
            this.mIsQSNShowing = true;
            this.mMainHandler.post(new DesktopStatusBar$$ExternalSyntheticLambda4(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPanelInternal$4() {
        if (this.mIsQSNShowing) {
            this.mQSPanelController.setQsExpansion(1.0f);
            this.mNotificationEntryManager.resetUnReadNotificationSize();
        }
    }

    private void hidePanelInternal(boolean z, boolean z2) {
        if (z || isValidRequest()) {
            this.mQSPanelController.setListening(false, false);
            if (z2) {
                this.mDisplayWindowManager.removeViewImmediate(this.mQSNContentView);
            } else {
                this.mDisplayWindowManager.removeView(this.mQSNContentView);
            }
            this.mIsQSNShowing = false;
        }
    }

    private boolean isValidRequest() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime - this.mLastQSNToggleTime <= 300) {
            return false;
        }
        this.mLastQSNToggleTime = elapsedRealtime;
        return true;
    }

    private WindowManager.LayoutParams genWindowManagerLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(getQSPanelWidth(), getQSPanelHeight(), 2041, 545521768, -3);
        if (this.mLayoutDirection == 1) {
            layoutParams.gravity = 51;
            layoutParams.windowAnimations = R$style.Animation_TaskbarNotificationPanelRTL;
        } else {
            layoutParams.gravity = 53;
            layoutParams.windowAnimations = R$style.Animation_TaskbarNotificationPanel;
        }
        layoutParams.setTitle("DesktopQS: " + this.mContext.getDisplayId());
        return layoutParams;
    }

    public int getQSPanelWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mContext.getDisplay().getRealMetrics(displayMetrics);
        if (this.mContext.getDisplay().getDisplayId() == 0) {
            return displayMetrics.widthPixels;
        }
        return Math.min(displayMetrics.widthPixels, this.mContext.getResources().getDimensionPixelSize(R$dimen.desktop_qsn_panel_width) + this.mContext.getResources().getDimensionPixelSize(R$dimen.desktop_qs_panel_margin));
    }

    private boolean needQSPanelHorizontalPadding() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mContext.getDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels >= this.mContext.getResources().getDimensionPixelSize(R$dimen.desktop_qsn_panel_width) + this.mContext.getResources().getDimensionPixelSize(R$dimen.desktop_qs_panel_margin);
    }

    /* access modifiers changed from: protected */
    public C1129QS createDefaultQSFragment() {
        return (C1129QS) FragmentHostManager.get(this.mQSNContentView).create(QSFragment.class);
    }

    public void postAnimateCollapsePanels() {
        requestHidePanel();
    }

    public void postAnimateOpenPanels() {
        requestShowPanel();
    }

    public void postAnimateForceCollapsePanels() {
        requestHidePanel();
    }

    private void startActivityInternal(Intent intent, ActivityStarter.Callback callback, int i) {
        int i2;
        Intent intent2 = intent;
        ActivityStarter.Callback callback2 = callback;
        intent2.setFlags(335544320);
        intent2.addFlags(i);
        try {
            i2 = ActivityTaskManager.getService().startActivityAsUser((IApplicationThread) null, this.mContext.getBasePackageName(), this.mContext.getAttributionTag(), intent, intent2.resolveTypeIfNeeded(this.mContext.getContentResolver()), (IBinder) null, (String) null, 0, 268435456, (ProfilerInfo) null, getActivityOptions(), UserHandle.CURRENT.getIdentifier());
        } catch (RemoteException e) {
            Log.w("StatusBar", "Unable to start activity", e);
            i2 = -96;
        }
        if (callback2 != null) {
            callback2.onActivityStarted(i2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: startPendingIntentInternal */
    public void lambda$postStartActivityDismissingKeyguard$6(PendingIntent pendingIntent) {
        try {
            pendingIntent.sendAndReturnResult((Context) null, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, getActivityOptions());
        } catch (PendingIntent.CanceledException e) {
            Log.w("StatusBar", "Sending intent failed: " + e);
        }
    }

    private Bundle getActivityOptions() {
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setLaunchDisplayId(this.mDisplayId);
        return makeBasic.toBundle();
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent) {
        startPendingIntentDismissingKeyguard(pendingIntent, (Runnable) null);
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable) {
        startPendingIntentDismissingKeyguard(pendingIntent, runnable, (ActivityLaunchAnimator.Controller) null);
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable, View view) {
        startPendingIntentDismissingKeyguard(pendingIntent, runnable, (ActivityLaunchAnimator.Controller) null);
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable, ActivityLaunchAnimator.Controller controller) {
        lambda$postStartActivityDismissingKeyguard$6(pendingIntent);
    }

    public void startActivity(Intent intent, boolean z, boolean z2, int i) {
        startActivityInternal(intent, (ActivityStarter.Callback) null, i);
    }

    public void startActivity(Intent intent, boolean z) {
        startActivityInternal(intent, (ActivityStarter.Callback) null, 0);
    }

    public void startActivity(Intent intent, boolean z, ActivityLaunchAnimator.Controller controller) {
        startActivityInternal(intent, (ActivityStarter.Callback) null, 0);
    }

    public void startActivity(Intent intent, boolean z, boolean z2) {
        startActivityInternal(intent, (ActivityStarter.Callback) null, 0);
    }

    public void startActivity(Intent intent, boolean z, ActivityStarter.Callback callback) {
        startActivityInternal(intent, callback, 0);
    }

    public void postStartActivityDismissingKeyguard(Intent intent, int i) {
        postStartActivityDismissingKeyguard(intent, i, (ActivityLaunchAnimator.Controller) null);
    }

    public void postStartActivityDismissingKeyguard(Intent intent, int i, ActivityLaunchAnimator.Controller controller) {
        this.mHandler.postDelayed(new DesktopStatusBar$$ExternalSyntheticLambda6(this, intent), (long) i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$postStartActivityDismissingKeyguard$5(Intent intent) {
        requestHidePanel();
        startActivityInternal(intent, (ActivityStarter.Callback) null, 0);
    }

    public void postStartActivityDismissingKeyguard(PendingIntent pendingIntent) {
        postStartActivityDismissingKeyguard(pendingIntent, (ActivityLaunchAnimator.Controller) null);
    }

    public void postStartActivityDismissingKeyguard(PendingIntent pendingIntent, ActivityLaunchAnimator.Controller controller) {
        this.mHandler.post(new DesktopStatusBar$$ExternalSyntheticLambda5(this, pendingIntent));
    }

    public void postQSRunnableDismissingKeyguard(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    public void dismissKeyguardThenExecute(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable, boolean z) {
        onDismissAction.onDismiss();
    }

    public void onRemoteInputActive(boolean z) {
        ViewGroup viewGroup = this.mQSNContentView;
        if (viewGroup != null) {
            if (z) {
                this.mLayoutParams.flags &= -9;
            } else {
                this.mLayoutParams.flags |= 8;
            }
            if (viewGroup.isAttachedToWindow()) {
                this.mDisplayWindowManager.updateViewLayout(this.mQSNContentView, this.mLayoutParams);
            }
            this.mIsRemoteInputActive = z;
        }
    }

    public boolean isRemoteInputActive() {
        return this.mIsRemoteInputActive;
    }
}
