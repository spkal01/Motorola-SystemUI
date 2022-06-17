package com.android.systemui.navigationbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.pip.Pip;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.accessibility.AccessibilityButtonModeObserver;
import com.android.systemui.accessibility.SystemActions;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.model.SysUiState;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.Recents;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.motorola.taskbar.MotoTaskBarController;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Optional;

public class NavigationBarController implements CommandQueue.Callbacks, ConfigurationController.ConfigurationListener, NavigationModeController.ModeChangedListener, Dumpable {
    private static final String TAG = "NavigationBarController";
    private final AccessibilityButtonModeObserver mAccessibilityButtonModeObserver;
    private final AccessibilityManager mAccessibilityManager;
    private final AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    private final Lazy<AssistManager> mAssistManagerLazy;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final CommandQueue mCommandQueue;
    private final InterestingConfigChanges mConfigChanges;
    private final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private final DisplayManager mDisplayManager;
    private final Handler mHandler;
    private boolean mIsTablet;
    private final MetricsLogger mMetricsLogger;
    private MotoTaskBarController mMotoTaskBarController;
    private final NavigationBarOverlayController mNavBarOverlayController;
    private int mNavMode;
    @VisibleForTesting
    SparseArray<NavigationBar> mNavigationBars = new SparseArray<>();
    private final NavigationModeController mNavigationModeController;
    private final NotificationRemoteInputManager mNotificationRemoteInputManager;
    private final NotificationShadeDepthController mNotificationShadeDepthController;
    private final OverviewProxyService mOverviewProxyService;
    private final Optional<Pip> mPipOptional;
    private final Optional<Recents> mRecentsOptional;
    private final ShadeController mShadeController;
    private final Optional<LegacySplitScreen> mSplitScreenOptional;
    private final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    private final SysUiState mSysUiFlagsContainer;
    private final SystemActions mSystemActions;
    private final TaskbarDelegate mTaskbarDelegate;
    private final UiEventLogger mUiEventLogger;
    private final UserTracker mUserTracker;
    private final WindowManager mWindowManager;

    public NavigationBarController(Context context, WindowManager windowManager, Lazy<AssistManager> lazy, AccessibilityManager accessibilityManager, AccessibilityManagerWrapper accessibilityManagerWrapper, DeviceProvisionedController deviceProvisionedController, MetricsLogger metricsLogger, OverviewProxyService overviewProxyService, NavigationModeController navigationModeController, AccessibilityButtonModeObserver accessibilityButtonModeObserver, StatusBarStateController statusBarStateController, SysUiState sysUiState, BroadcastDispatcher broadcastDispatcher, CommandQueue commandQueue, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<Recents> optional3, Lazy<StatusBar> lazy2, ShadeController shadeController, NotificationRemoteInputManager notificationRemoteInputManager, NotificationShadeDepthController notificationShadeDepthController, SystemActions systemActions, Handler handler, UiEventLogger uiEventLogger, NavigationBarOverlayController navigationBarOverlayController, ConfigurationController configurationController, UserTracker userTracker) {
        NavigationModeController navigationModeController2 = navigationModeController;
        CommandQueue commandQueue2 = commandQueue;
        InterestingConfigChanges interestingConfigChanges = new InterestingConfigChanges(1073742592);
        this.mConfigChanges = interestingConfigChanges;
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mAssistManagerLazy = lazy;
        this.mAccessibilityManager = accessibilityManager;
        this.mAccessibilityManagerWrapper = accessibilityManagerWrapper;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mMetricsLogger = metricsLogger;
        this.mOverviewProxyService = overviewProxyService;
        this.mNavigationModeController = navigationModeController2;
        this.mAccessibilityButtonModeObserver = accessibilityButtonModeObserver;
        this.mStatusBarStateController = statusBarStateController;
        this.mSysUiFlagsContainer = sysUiState;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mCommandQueue = commandQueue2;
        this.mPipOptional = optional;
        this.mSplitScreenOptional = optional2;
        this.mRecentsOptional = optional3;
        this.mStatusBarLazy = lazy2;
        this.mShadeController = shadeController;
        this.mNotificationRemoteInputManager = notificationRemoteInputManager;
        this.mNotificationShadeDepthController = notificationShadeDepthController;
        this.mSystemActions = systemActions;
        this.mUiEventLogger = uiEventLogger;
        this.mHandler = handler;
        this.mDisplayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        commandQueue2.addCallback((CommandQueue.Callbacks) this);
        configurationController.addCallback(this);
        interestingConfigChanges.applyNewConfig(context.getResources());
        this.mNavBarOverlayController = navigationBarOverlayController;
        this.mNavMode = navigationModeController2.addListener(this);
        navigationModeController2.addListener(this);
        this.mTaskbarDelegate = new TaskbarDelegate(overviewProxyService);
        this.mIsTablet = isTablet(context.getResources().getConfiguration());
        this.mUserTracker = userTracker;
        this.mMotoTaskBarController = (MotoTaskBarController) Dependency.get(MotoTaskBarController.class);
    }

    public void onConfigChanged(Configuration configuration) {
        boolean z = this.mIsTablet;
        boolean isTablet = isTablet(configuration);
        this.mIsTablet = isTablet;
        int i = 0;
        if ((isTablet != z) && updateNavbarForTaskbar()) {
            return;
        }
        if (this.mConfigChanges.applyNewConfig(this.mContext.getResources())) {
            boolean isNavGuideShow = this.mMotoTaskBarController.isNavGuideShow();
            if (isNavGuideShow) {
                this.mMotoTaskBarController.handleTrackpadGuideShow(false);
            }
            while (i < this.mNavigationBars.size()) {
                recreateNavigationBar(this.mNavigationBars.keyAt(i));
                i++;
            }
            if (isNavGuideShow) {
                this.mMotoTaskBarController.handleTrackpadGuideShow(true);
                this.mHandler.post(new NavigationBarController$$ExternalSyntheticLambda0(this));
                return;
            }
            return;
        }
        while (i < this.mNavigationBars.size()) {
            this.mNavigationBars.valueAt(i).onConfigurationChanged(configuration);
            i++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigChanged$0() {
        NavigationBarView defaultNavigationBarView = getDefaultNavigationBarView();
        if (defaultNavigationBarView != null) {
            defaultNavigationBarView.lambda$updateCurrentView$4();
        }
    }

    public void onNavigationModeChanged(int i) {
        int i2 = this.mNavMode;
        if (i2 != i) {
            this.mNavMode = i;
            this.mHandler.post(new NavigationBarController$$ExternalSyntheticLambda1(this, i2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNavigationModeChanged$1(int i) {
        if (i != this.mNavMode) {
            updateNavbarForTaskbar();
        }
        for (int i2 = 0; i2 < this.mNavigationBars.size(); i2++) {
            NavigationBar valueAt = this.mNavigationBars.valueAt(i2);
            if (valueAt != null) {
                valueAt.getView().updateStates();
            }
        }
    }

    public boolean updateNavbarForTaskbar() {
        if (!isThreeButtonTaskbarFlagEnabled()) {
            return false;
        }
        if (this.mIsTablet && this.mNavMode == 0) {
            removeNavigationBar(this.mContext.getDisplayId());
            this.mCommandQueue.addCallback((CommandQueue.Callbacks) this.mTaskbarDelegate);
            return true;
        } else if (this.mNavigationBars.get(this.mContext.getDisplayId()) != null) {
            return true;
        } else {
            createNavigationBar(this.mContext.getDisplay(), (Bundle) null, (RegisterStatusBarResult) null);
            this.mCommandQueue.removeCallback((CommandQueue.Callbacks) this.mTaskbarDelegate);
            return true;
        }
    }

    public void onDisplayRemoved(int i) {
        removeNavigationBar(i);
    }

    public void onDisplayReady(int i) {
        Display display = this.mDisplayManager.getDisplay(i);
        this.mIsTablet = isTablet(this.mContext.getResources().getConfiguration());
        createNavigationBar(display, (Bundle) null, (RegisterStatusBarResult) null);
    }

    public void setNavigationBarLumaSamplingEnabled(int i, boolean z) {
        NavigationBarView navigationBarView = getNavigationBarView(i);
        if (navigationBarView != null) {
            navigationBarView.setNavigationBarLumaSamplingEnabled(z);
        }
    }

    private void recreateNavigationBar(int i) {
        Bundle bundle = new Bundle();
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar != null) {
            navigationBar.onSaveInstanceState(bundle);
        }
        removeNavigationBar(i);
        createNavigationBar(this.mDisplayManager.getDisplay(i), bundle, (RegisterStatusBarResult) null);
    }

    public void createNavigationBars(boolean z, RegisterStatusBarResult registerStatusBarResult) {
        if (!updateNavbarForTaskbar()) {
            for (Display display : this.mDisplayManager.getDisplays()) {
                if (z || display.getDisplayId() != 0) {
                    createNavigationBar(display, (Bundle) null, registerStatusBarResult);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void createNavigationBar(Display display, Bundle bundle, RegisterStatusBarResult registerStatusBarResult) {
        Context context;
        Display display2 = display;
        if (display2 != null && !isThreeButtonTaskbarEnabled()) {
            int displayId = display.getDisplayId();
            boolean z = displayId == 0;
            IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
            try {
                if (windowManagerService.hasNavigationBar(displayId)) {
                    if (z || !this.mMotoTaskBarController.isMotoTaskBarAvailable() || !windowManagerService.shouldShowSystemDecors(displayId) || !this.mMotoTaskBarController.isDesktopModeDisplay(displayId)) {
                        if (z) {
                            context = this.mContext;
                        } else {
                            context = this.mContext.createDisplayContext(display2);
                        }
                        final NavigationBar navigationBar = new NavigationBar(context, this.mWindowManager, this.mAssistManagerLazy, this.mAccessibilityManager, this.mAccessibilityManagerWrapper, this.mDeviceProvisionedController, this.mMetricsLogger, this.mOverviewProxyService, this.mNavigationModeController, this.mAccessibilityButtonModeObserver, this.mStatusBarStateController, this.mSysUiFlagsContainer, this.mBroadcastDispatcher, this.mCommandQueue, this.mPipOptional, this.mSplitScreenOptional, this.mRecentsOptional, this.mStatusBarLazy, this.mShadeController, this.mNotificationRemoteInputManager, this.mNotificationShadeDepthController, this.mSystemActions, this.mHandler, this.mNavBarOverlayController, this.mUiEventLogger, this.mUserTracker);
                        this.mNavigationBars.put(displayId, navigationBar);
                        View createView = navigationBar.createView(bundle);
                        final Display display3 = display;
                        final RegisterStatusBarResult registerStatusBarResult2 = registerStatusBarResult;
                        createView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                            public void onViewAttachedToWindow(View view) {
                                if (registerStatusBarResult2 != null) {
                                    NavigationBar navigationBar = navigationBar;
                                    int displayId = display3.getDisplayId();
                                    RegisterStatusBarResult registerStatusBarResult = registerStatusBarResult2;
                                    navigationBar.setImeWindowStatus(displayId, registerStatusBarResult.mImeToken, registerStatusBarResult.mImeWindowVis, registerStatusBarResult.mImeBackDisposition, registerStatusBarResult.mShowImeSwitcher);
                                }
                            }

                            public void onViewDetachedFromWindow(View view) {
                                view.removeOnAttachStateChangeListener(this);
                            }
                        });
                        updateVisibility();
                    }
                }
            } catch (RemoteException unused) {
                Log.w(TAG, "Cannot get WindowManager.");
            }
        }
    }

    private void updateVisibility() {
        if (MotoFeature.getInstance(this.mContext).isSupportCli() && this.mContext.getDisplayId() == 1) {
            this.mStatusBarLazy.get().getCliStatusbar().updateNavigationBar();
        }
    }

    /* access modifiers changed from: package-private */
    public void removeNavigationBar(int i) {
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar != null) {
            navigationBar.setAutoHideController((AutoHideController) null);
            navigationBar.destroyView();
            this.mNavigationBars.remove(i);
        }
    }

    public void checkNavBarModes(int i) {
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar != null) {
            navigationBar.checkNavBarModes();
        }
    }

    public void finishBarAnimations(int i) {
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar != null) {
            navigationBar.finishBarAnimations();
        }
    }

    public void touchAutoDim(int i) {
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar != null) {
            navigationBar.touchAutoDim();
        }
    }

    public void transitionTo(int i, int i2, boolean z) {
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar != null) {
            navigationBar.transitionTo(i2, z);
        }
    }

    public void disableAnimationsDuringHide(int i, long j) {
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar != null) {
            navigationBar.disableAnimationsDuringHide(j);
        }
    }

    public NavigationBarView getDefaultNavigationBarView() {
        return getNavigationBarView(0);
    }

    public NavigationBarView getNavigationBarView(int i) {
        NavigationBar navigationBar = this.mNavigationBars.get(i);
        if (navigationBar == null) {
            return null;
        }
        return navigationBar.getView();
    }

    public NavigationBar getDefaultNavigationBar() {
        return this.mNavigationBars.get(0);
    }

    private boolean isThreeButtonTaskbarEnabled() {
        return this.mIsTablet && this.mNavMode == 0 && isThreeButtonTaskbarFlagEnabled();
    }

    private boolean isThreeButtonTaskbarFlagEnabled() {
        return SystemProperties.getBoolean("persist.debug.taskbar_three_button", false);
    }

    private boolean isTablet(Configuration configuration) {
        float f = Resources.getSystem().getDisplayMetrics().density;
        return ((float) Math.min((int) (((float) configuration.screenWidthDp) * f), (int) (f * ((float) configuration.screenHeightDp)))) / (((float) this.mContext.getResources().getDisplayMetrics().densityDpi) / 160.0f) >= 600.0f;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < this.mNavigationBars.size(); i++) {
            if (i > 0) {
                printWriter.println();
            }
            this.mNavigationBars.valueAt(i).dump(printWriter);
        }
    }
}
