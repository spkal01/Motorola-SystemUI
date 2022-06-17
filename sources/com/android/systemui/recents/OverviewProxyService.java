package com.android.systemui.recents;

import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.accessibility.dialog.AccessibilityButtonChooserActivity;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.internal.util.ScreenshotHelper;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.onehanded.OneHanded;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.splitscreen.SplitScreen;
import com.android.p011wm.shell.startingsurface.StartingSurface;
import com.android.p011wm.shell.transition.ShellTransitions;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.model.SysUiState;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.navigationbar.NavigationBar;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.navigationbar.NavigationBarView;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.smartspace.SmartspaceTransitionController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarWindowCallback;
import com.android.systemui.statusbar.policy.CallbackController;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OverviewProxyService extends CurrentUserTracker implements CallbackController<OverviewProxyListener>, NavigationModeController.ModeChangedListener, Dumpable {
    private Region mActiveNavBarRegion;
    private boolean mBound;
    /* access modifiers changed from: private */
    public final CommandQueue mCommandQueue;
    /* access modifiers changed from: private */
    public int mConnectionBackoffAttempts;
    /* access modifiers changed from: private */
    public final List<OverviewProxyListener> mConnectionCallbacks = new ArrayList();
    private final Runnable mConnectionRunnable = new OverviewProxyService$$ExternalSyntheticLambda3(this);
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentBoundedUserId = -1;
    /* access modifiers changed from: private */
    public final Runnable mDeferredConnectionCallback = new OverviewProxyService$$ExternalSyntheticLambda4(this);
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public long mInputFocusTransferStartMillis;
    /* access modifiers changed from: private */
    public float mInputFocusTransferStartY;
    /* access modifiers changed from: private */
    public boolean mInputFocusTransferStarted;
    private boolean mIsEnabled;
    private final BroadcastReceiver mLauncherStateChangedReceiver;
    /* access modifiers changed from: private */
    public final Optional<LegacySplitScreen> mLegacySplitScreenOptional;
    /* access modifiers changed from: private */
    public float mNavBarButtonAlpha;
    private final Lazy<NavigationBarController> mNavBarControllerLazy;
    private int mNavBarMode = 0;
    /* access modifiers changed from: private */
    public final Optional<OneHanded> mOneHandedOptional;
    /* access modifiers changed from: private */
    public IOverviewProxy mOverviewProxy;
    private final ServiceConnection mOverviewServiceConnection;
    /* access modifiers changed from: private */
    public final IBinder.DeathRecipient mOverviewServiceDeathRcpt;
    /* access modifiers changed from: private */
    public final Optional<Pip> mPipOptional;
    private final Intent mQuickStepIntent;
    private final ComponentName mRecentsComponentName;
    /* access modifiers changed from: private */
    public final ScreenshotHelper mScreenshotHelper;
    /* access modifiers changed from: private */
    public final ShellTransitions mShellTransitions;
    /* access modifiers changed from: private */
    public final SmartspaceTransitionController mSmartspaceTransitionController;
    private final BiConsumer<Rect, Rect> mSplitScreenBoundsChangeListener;
    /* access modifiers changed from: private */
    public final Optional<SplitScreen> mSplitScreenOptional;
    /* access modifiers changed from: private */
    public final Optional<StartingSurface> mStartingSurface;
    /* access modifiers changed from: private */
    public final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy;
    private final NotificationShadeWindowController mStatusBarWinController;
    private final StatusBarWindowCallback mStatusBarWindowCallback;
    /* access modifiers changed from: private */
    public boolean mSupportsRoundedCornersOnWindows;
    @VisibleForTesting
    public ISystemUiProxy mSysUiProxy = new ISystemUiProxy.Stub() {
        public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) {
        }

        public void startScreenPinning(int i) {
            if (verifyCaller("startScreenPinning")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda5(this, i));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startScreenPinning$1(int i) {
            OverviewProxyService.this.mStatusBarOptionalLazy.ifPresent(new OverviewProxyService$1$$ExternalSyntheticLambda13(i));
        }

        public void stopScreenPinning() {
            if (verifyCaller("stopScreenPinning")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(OverviewProxyService$1$$ExternalSyntheticLambda12.INSTANCE);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$stopScreenPinning$2() {
            try {
                ActivityTaskManager.getService().stopSystemLockTaskMode();
            } catch (RemoteException unused) {
                Log.e("OverviewProxyService", "Failed to stop screen pinning");
            }
        }

        public void onStatusBarMotionEvent(MotionEvent motionEvent) {
            if (verifyCaller("onStatusBarMotionEvent")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mStatusBarOptionalLazy.ifPresent(new OverviewProxyService$1$$ExternalSyntheticLambda14(this, motionEvent));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onStatusBarMotionEvent$4(MotionEvent motionEvent, Lazy lazy) {
            StatusBar statusBar = (StatusBar) lazy.get();
            if (motionEvent.getActionMasked() == 0) {
                statusBar.getPanelController().startExpandLatencyTracking();
            }
            OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda8(this, motionEvent, statusBar));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onStatusBarMotionEvent$3(MotionEvent motionEvent, StatusBar statusBar) {
            int actionMasked = motionEvent.getActionMasked();
            boolean z = false;
            if (actionMasked == 0) {
                boolean unused = OverviewProxyService.this.mInputFocusTransferStarted = true;
                float unused2 = OverviewProxyService.this.mInputFocusTransferStartY = motionEvent.getY();
                long unused3 = OverviewProxyService.this.mInputFocusTransferStartMillis = motionEvent.getEventTime();
                statusBar.onInputFocusTransfer(OverviewProxyService.this.mInputFocusTransferStarted, false, 0.0f);
            }
            if (actionMasked == 1 || actionMasked == 3) {
                boolean unused4 = OverviewProxyService.this.mInputFocusTransferStarted = false;
                boolean access$1700 = OverviewProxyService.this.mInputFocusTransferStarted;
                if (actionMasked == 3) {
                    z = true;
                }
                statusBar.onInputFocusTransfer(access$1700, z, (motionEvent.getY() - OverviewProxyService.this.mInputFocusTransferStartY) / ((float) (motionEvent.getEventTime() - OverviewProxyService.this.mInputFocusTransferStartMillis)));
            }
            motionEvent.recycle();
        }

        public void onBackPressed() throws RemoteException {
            if (verifyCaller("onBackPressed")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda0(this));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackPressed$5() {
            sendEvent(0, 4);
            sendEvent(1, 4);
            OverviewProxyService.this.notifyBackAction(true, -1, -1, true, false);
        }

        public void setHomeRotationEnabled(boolean z) {
            if (verifyCaller("setHomeRotationEnabled")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda10(this, z));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setHomeRotationEnabled$6(boolean z) {
            OverviewProxyService.this.notifyHomeRotationEnabled(z);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setHomeRotationEnabled$7(boolean z) {
            OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda11(this, z));
        }

        private boolean sendEvent(int i, int i2) {
            long uptimeMillis = SystemClock.uptimeMillis();
            KeyEvent keyEvent = new KeyEvent(uptimeMillis, uptimeMillis, i, i2, 0, 0, -1, 0, 72, 257);
            keyEvent.setDisplayId(OverviewProxyService.this.mContext.getDisplay().getDisplayId());
            return InputManager.getInstance().injectInputEvent(keyEvent, 0);
        }

        public void onOverviewShown(boolean z) {
            if (verifyCaller("onOverviewShown")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda9(this, z));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onOverviewShown$8(boolean z) {
            for (int size = OverviewProxyService.this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
                ((OverviewProxyListener) OverviewProxyService.this.mConnectionCallbacks.get(size)).onOverviewShown(z);
            }
        }

        public Rect getNonMinimizedSplitScreenSecondaryBounds() {
            if (!verifyCaller("getNonMinimizedSplitScreenSecondaryBounds")) {
                return null;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return (Rect) OverviewProxyService.this.mLegacySplitScreenOptional.map(OverviewProxyService$1$$ExternalSyntheticLambda17.INSTANCE).orElse((Object) null);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setNavBarButtonAlpha(float f, boolean z) {
            if (verifyCaller("setNavBarButtonAlpha")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    float unused = OverviewProxyService.this.mNavBarButtonAlpha = f;
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda4(this, f, z));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setNavBarButtonAlpha$10(float f, boolean z) {
            OverviewProxyService.this.notifyNavBarButtonAlphaChanged(f, z);
        }

        public void onAssistantProgress(float f) {
            if (verifyCaller("onAssistantProgress")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda2(this, f));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAssistantProgress$11(float f) {
            OverviewProxyService.this.notifyAssistantProgress(f);
        }

        public void onAssistantGestureCompletion(float f) {
            if (verifyCaller("onAssistantGestureCompletion")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda3(this, f));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAssistantGestureCompletion$12(float f) {
            OverviewProxyService.this.notifyAssistantGestureCompletion(f);
        }

        public void startAssistant(Bundle bundle) {
            if (verifyCaller("startAssistant")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda7(this, bundle));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startAssistant$13(Bundle bundle) {
            OverviewProxyService.this.notifyStartAssistant(bundle);
        }

        public void notifyAccessibilityButtonClicked(int i) {
            if (verifyCaller("notifyAccessibilityButtonClicked")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    AccessibilityManager.getInstance(OverviewProxyService.this.mContext).notifyAccessibilityButtonClicked(i);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void notifyAccessibilityButtonLongClicked() {
            if (verifyCaller("notifyAccessibilityButtonLongClicked")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    Intent intent = new Intent("com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON");
                    intent.setClassName("android", AccessibilityButtonChooserActivity.class.getName());
                    intent.addFlags(268468224);
                    OverviewProxyService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void setSplitScreenMinimized(boolean z) {
            OverviewProxyService.this.mLegacySplitScreenOptional.ifPresent(new OverviewProxyService$1$$ExternalSyntheticLambda15(z));
        }

        public void notifySwipeToHomeFinished() {
            if (verifyCaller("notifySwipeToHomeFinished")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mPipOptional.ifPresent(OverviewProxyService$1$$ExternalSyntheticLambda16.INSTANCE);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void notifySwipeUpGestureStarted() {
            if (verifyCaller("notifySwipeUpGestureStarted")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda1(this));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$notifySwipeUpGestureStarted$16() {
            OverviewProxyService.this.notifySwipeUpGestureStartedInternal();
        }

        public void notifyPrioritizedRotation(int i) {
            if (verifyCaller("notifyPrioritizedRotation")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mHandler.post(new OverviewProxyService$1$$ExternalSyntheticLambda6(this, i));
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyPrioritizedRotation$17(int i) {
            OverviewProxyService.this.notifyPrioritizedRotationInternal(i);
        }

        public void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) {
            OverviewProxyService.this.mScreenshotHelper.provideScreenshot(bundle, rect, insets, taskKey.f124id, taskKey.userId, taskKey.sourceComponent, 3, OverviewProxyService.this.mHandler, (Consumer) null);
        }

        public void expandNotificationPanel() {
            if (verifyCaller("expandNotificationPanel")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    OverviewProxyService.this.mCommandQueue.handleSystemKey(281);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        private boolean verifyCaller(String str) {
            int identifier = Binder.getCallingUserHandle().getIdentifier();
            if (identifier == OverviewProxyService.this.mCurrentBoundedUserId) {
                return true;
            }
            Log.w("OverviewProxyService", "Launcher called sysui with invalid user: " + identifier + ", reason: " + str);
            return false;
        }
    };
    /* access modifiers changed from: private */
    public SysUiState mSysUiState;
    /* access modifiers changed from: private */
    public float mWindowCornerRadius;

    public interface OverviewProxyListener {
        void onAssistantGestureCompletion(float f) {
        }

        void onAssistantProgress(float f) {
        }

        void onConnectionChanged(boolean z) {
        }

        void onHomeRotationEnabled(boolean z) {
        }

        void onNavBarButtonAlphaChanged(float f, boolean z) {
        }

        void onOverviewShown(boolean z) {
        }

        void onPrioritizedRotation(int i) {
        }

        void onSwipeUpGestureStarted() {
        }

        void onToggleRecentApps() {
        }

        void startAssistant(Bundle bundle) {
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        Log.w("OverviewProxyService", "Binder supposed established connection but actual connection to service timed out, trying again");
        retryConnectionWithBackoff();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public OverviewProxyService(Context context, CommandQueue commandQueue, Lazy<NavigationBarController> lazy, NavigationModeController navigationModeController, NotificationShadeWindowController notificationShadeWindowController, SysUiState sysUiState, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<Lazy<StatusBar>> optional4, Optional<OneHanded> optional5, BroadcastDispatcher broadcastDispatcher, ShellTransitions shellTransitions, Optional<StartingSurface> optional6, SmartspaceTransitionController smartspaceTransitionController) {
        super(broadcastDispatcher);
        CommandQueue commandQueue2 = commandQueue;
        NotificationShadeWindowController notificationShadeWindowController2 = notificationShadeWindowController;
        SysUiState sysUiState2 = sysUiState;
        Optional<LegacySplitScreen> optional7 = optional2;
        C12662 r7 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                OverviewProxyService.this.updateEnabledState();
                OverviewProxyService.this.startConnectionToCurrentUser();
            }
        };
        this.mLauncherStateChangedReceiver = r7;
        this.mOverviewServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                int unused = OverviewProxyService.this.mConnectionBackoffAttempts = 0;
                OverviewProxyService.this.mHandler.removeCallbacks(OverviewProxyService.this.mDeferredConnectionCallback);
                try {
                    iBinder.linkToDeath(OverviewProxyService.this.mOverviewServiceDeathRcpt, 0);
                    OverviewProxyService overviewProxyService = OverviewProxyService.this;
                    int unused2 = overviewProxyService.mCurrentBoundedUserId = overviewProxyService.getCurrentUserId();
                    IOverviewProxy unused3 = OverviewProxyService.this.mOverviewProxy = IOverviewProxy.Stub.asInterface(iBinder);
                    Bundle bundle = new Bundle();
                    bundle.putBinder("extra_sysui_proxy", OverviewProxyService.this.mSysUiProxy.asBinder());
                    bundle.putFloat("extra_window_corner_radius", OverviewProxyService.this.mWindowCornerRadius);
                    bundle.putBoolean("extra_supports_window_corners", OverviewProxyService.this.mSupportsRoundedCornersOnWindows);
                    OverviewProxyService.this.mPipOptional.ifPresent(new OverviewProxyService$3$$ExternalSyntheticLambda1(bundle));
                    OverviewProxyService.this.mSplitScreenOptional.ifPresent(new OverviewProxyService$3$$ExternalSyntheticLambda2(bundle));
                    OverviewProxyService.this.mOneHandedOptional.ifPresent(new OverviewProxyService$3$$ExternalSyntheticLambda0(bundle));
                    bundle.putBinder("extra_shell_shell_transitions", OverviewProxyService.this.mShellTransitions.createExternalInterface().asBinder());
                    OverviewProxyService.this.mStartingSurface.ifPresent(new OverviewProxyService$3$$ExternalSyntheticLambda3(bundle));
                    bundle.putBinder("smartspace_transition", OverviewProxyService.this.mSmartspaceTransitionController.createExternalInterface().asBinder());
                    try {
                        OverviewProxyService.this.mOverviewProxy.onInitialize(bundle);
                    } catch (RemoteException e) {
                        int unused4 = OverviewProxyService.this.mCurrentBoundedUserId = -1;
                        Log.e("OverviewProxyService", "Failed to call onInitialize()", e);
                    }
                    OverviewProxyService.this.dispatchNavButtonBounds();
                    OverviewProxyService.this.updateSystemUiStateFlags();
                    OverviewProxyService overviewProxyService2 = OverviewProxyService.this;
                    overviewProxyService2.notifySystemUiStateFlags(overviewProxyService2.mSysUiState.getFlags());
                    OverviewProxyService.this.notifyConnectionChanged();
                } catch (RemoteException e2) {
                    Log.e("OverviewProxyService", "Lost connection to launcher service", e2);
                    OverviewProxyService.this.disconnectFromLauncherService();
                    OverviewProxyService.this.retryConnectionWithBackoff();
                }
            }

            public void onNullBinding(ComponentName componentName) {
                Log.w("OverviewProxyService", "Null binding of '" + componentName + "', try reconnecting");
                int unused = OverviewProxyService.this.mCurrentBoundedUserId = -1;
                OverviewProxyService.this.retryConnectionWithBackoff();
            }

            public void onBindingDied(ComponentName componentName) {
                Log.w("OverviewProxyService", "Binding died of '" + componentName + "', try reconnecting");
                int unused = OverviewProxyService.this.mCurrentBoundedUserId = -1;
                OverviewProxyService.this.retryConnectionWithBackoff();
            }

            public void onServiceDisconnected(ComponentName componentName) {
                Log.w("OverviewProxyService", "Service disconnected");
                int unused = OverviewProxyService.this.mCurrentBoundedUserId = -1;
            }
        };
        OverviewProxyService$$ExternalSyntheticLambda2 overviewProxyService$$ExternalSyntheticLambda2 = new OverviewProxyService$$ExternalSyntheticLambda2(this);
        this.mStatusBarWindowCallback = overviewProxyService$$ExternalSyntheticLambda2;
        this.mSplitScreenBoundsChangeListener = new OverviewProxyService$$ExternalSyntheticLambda6(this);
        this.mOverviewServiceDeathRcpt = new OverviewProxyService$$ExternalSyntheticLambda0(this);
        this.mContext = context;
        this.mPipOptional = optional;
        this.mStatusBarOptionalLazy = optional4;
        this.mHandler = new Handler();
        this.mNavBarControllerLazy = lazy;
        this.mStatusBarWinController = notificationShadeWindowController2;
        this.mConnectionBackoffAttempts = 0;
        ComponentName unflattenFromString = ComponentName.unflattenFromString(context.getString(17040011));
        this.mRecentsComponentName = unflattenFromString;
        this.mQuickStepIntent = new Intent("android.intent.action.QUICKSTEP_SERVICE").setPackage(unflattenFromString.getPackageName());
        this.mWindowCornerRadius = ScreenDecorationsUtils.getWindowCornerRadius(context.getResources());
        this.mSupportsRoundedCornersOnWindows = ScreenDecorationsUtils.supportsRoundedCornersOnWindows(context.getResources());
        this.mSysUiState = sysUiState2;
        sysUiState2.addCallback(new OverviewProxyService$$ExternalSyntheticLambda1(this));
        this.mOneHandedOptional = optional5;
        this.mShellTransitions = shellTransitions;
        this.mNavBarButtonAlpha = 1.0f;
        this.mNavBarMode = navigationModeController.addListener(this);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart(unflattenFromString.getPackageName(), 0);
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        if (!DesktopFeature.isDesktopDisplayContext(context)) {
            context.registerReceiver(r7, intentFilter);
        }
        notificationShadeWindowController2.registerCallback(overviewProxyService$$ExternalSyntheticLambda2);
        this.mScreenshotHelper = new ScreenshotHelper(context);
        commandQueue.addCallback((CommandQueue.Callbacks) new CommandQueue.Callbacks() {
            public void onTracingStateChanged(boolean z) {
                OverviewProxyService.this.mSysUiState.setFlag(4096, z).commitUpdate(OverviewProxyService.this.mContext.getDisplayId());
            }
        });
        this.mCommandQueue = commandQueue2;
        this.mSplitScreenOptional = optional3;
        optional7.ifPresent(new OverviewProxyService$$ExternalSyntheticLambda7(this));
        this.mLegacySplitScreenOptional = optional7;
        startTracking();
        updateEnabledState();
        if (!DesktopFeature.isDesktopDisplayContext(context)) {
            startConnectionToCurrentUser();
        }
        this.mStartingSurface = optional6;
        this.mSmartspaceTransitionController = smartspaceTransitionController;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(LegacySplitScreen legacySplitScreen) {
        legacySplitScreen.registerBoundsChangeListener(this.mSplitScreenBoundsChangeListener);
    }

    public void onUserSwitched(int i) {
        this.mConnectionBackoffAttempts = 0;
        internalConnectToCurrentUser();
    }

    public void notifyBackAction(boolean z, int i, int i2, boolean z2, boolean z3) {
        try {
            IOverviewProxy iOverviewProxy = this.mOverviewProxy;
            if (iOverviewProxy != null) {
                iOverviewProxy.onBackAction(z, i, i2, z2, z3);
            }
        } catch (RemoteException e) {
            Log.e("OverviewProxyService", "Failed to notify back action", e);
        }
    }

    /* access modifiers changed from: private */
    public void updateSystemUiStateFlags() {
        NavigationBar defaultNavigationBar = this.mNavBarControllerLazy.get().getDefaultNavigationBar();
        NavigationBarView navigationBarView = this.mNavBarControllerLazy.get().getNavigationBarView(this.mContext.getDisplayId());
        if (defaultNavigationBar != null) {
            defaultNavigationBar.updateSystemUiStateFlags(-1);
        }
        if (navigationBarView != null) {
            navigationBarView.updatePanelSystemUiStateFlags();
            navigationBarView.updateDisabledSystemUiStateFlags();
        }
        NotificationShadeWindowController notificationShadeWindowController = this.mStatusBarWinController;
        if (notificationShadeWindowController != null) {
            notificationShadeWindowController.notifyStateChangedCallbacks();
        }
    }

    /* access modifiers changed from: private */
    public void notifySystemUiStateFlags(int i) {
        try {
            IOverviewProxy iOverviewProxy = this.mOverviewProxy;
            if (iOverviewProxy != null) {
                iOverviewProxy.onSystemUiStateChanged(i);
            }
        } catch (RemoteException e) {
            Log.e("OverviewProxyService", "Failed to notify sysui state change", e);
        }
    }

    /* access modifiers changed from: private */
    public void onStatusBarStateChanged(boolean z, boolean z2, boolean z3) {
        boolean z4 = true;
        SysUiState flag = this.mSysUiState.setFlag(64, z && !z2);
        if (!z || !z2) {
            z4 = false;
        }
        flag.setFlag(512, z4).setFlag(8, z3).commitUpdate(this.mContext.getDisplayId());
    }

    public void onActiveNavBarRegionChanges(Region region) {
        this.mActiveNavBarRegion = region;
        dispatchNavButtonBounds();
    }

    /* access modifiers changed from: private */
    public void dispatchNavButtonBounds() {
        Region region;
        IOverviewProxy iOverviewProxy = this.mOverviewProxy;
        if (iOverviewProxy != null && (region = this.mActiveNavBarRegion) != null) {
            try {
                iOverviewProxy.onActiveNavBarRegionChanges(region);
            } catch (RemoteException e) {
                Log.e("OverviewProxyService", "Failed to call onActiveNavBarRegionChanges()", e);
            }
        }
    }

    public void cleanupAfterDeath() {
        if (this.mInputFocusTransferStarted) {
            this.mHandler.post(new OverviewProxyService$$ExternalSyntheticLambda5(this));
        }
        startConnectionToCurrentUser();
        this.mLegacySplitScreenOptional.ifPresent(OverviewProxyService$$ExternalSyntheticLambda9.INSTANCE);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanupAfterDeath$3() {
        this.mStatusBarOptionalLazy.ifPresent(new OverviewProxyService$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanupAfterDeath$2(Lazy lazy) {
        this.mInputFocusTransferStarted = false;
        ((StatusBar) lazy.get()).onInputFocusTransfer(false, true, 0.0f);
    }

    public void startConnectionToCurrentUser() {
        if (this.mHandler.getLooper() != Looper.myLooper()) {
            this.mHandler.post(this.mConnectionRunnable);
        } else {
            internalConnectToCurrentUser();
        }
    }

    /* access modifiers changed from: private */
    public void internalConnectToCurrentUser() {
        disconnectFromLauncherService();
        if (!isEnabled()) {
            Log.v("OverviewProxyService", "Cannot attempt connection, is enabled " + isEnabled());
            return;
        }
        this.mHandler.removeCallbacks(this.mConnectionRunnable);
        try {
            this.mBound = this.mContext.bindServiceAsUser(new Intent("android.intent.action.QUICKSTEP_SERVICE").setPackage(this.mRecentsComponentName.getPackageName()), this.mOverviewServiceConnection, 33554433, UserHandle.of(getCurrentUserId()));
        } catch (SecurityException e) {
            Log.e("OverviewProxyService", "Unable to bind because of security error", e);
        }
        if (this.mBound) {
            this.mHandler.postDelayed(this.mDeferredConnectionCallback, 5000);
        } else {
            retryConnectionWithBackoff();
        }
    }

    /* access modifiers changed from: private */
    public void retryConnectionWithBackoff() {
        if (!this.mHandler.hasCallbacks(this.mConnectionRunnable)) {
            long min = (long) Math.min(Math.scalb(1000.0f, this.mConnectionBackoffAttempts), 600000.0f);
            this.mHandler.postDelayed(this.mConnectionRunnable, min);
            this.mConnectionBackoffAttempts++;
            Log.w("OverviewProxyService", "Failed to connect on attempt " + this.mConnectionBackoffAttempts + " will try again in " + min + "ms");
        }
    }

    public void addCallback(OverviewProxyListener overviewProxyListener) {
        if (!this.mConnectionCallbacks.contains(overviewProxyListener)) {
            this.mConnectionCallbacks.add(overviewProxyListener);
        }
        overviewProxyListener.onConnectionChanged(this.mOverviewProxy != null);
        overviewProxyListener.onNavBarButtonAlphaChanged(this.mNavBarButtonAlpha, false);
    }

    public void removeCallback(OverviewProxyListener overviewProxyListener) {
        this.mConnectionCallbacks.remove(overviewProxyListener);
    }

    public boolean shouldShowSwipeUpUI() {
        return isEnabled() && !QuickStepContract.isLegacyMode(this.mNavBarMode);
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public IOverviewProxy getProxy() {
        return this.mOverviewProxy;
    }

    /* access modifiers changed from: private */
    public void disconnectFromLauncherService() {
        if (this.mBound) {
            this.mContext.unbindService(this.mOverviewServiceConnection);
            this.mBound = false;
        }
        IOverviewProxy iOverviewProxy = this.mOverviewProxy;
        if (iOverviewProxy != null) {
            try {
                iOverviewProxy.asBinder().unlinkToDeath(this.mOverviewServiceDeathRcpt, 0);
            } catch (NoSuchElementException unused) {
                Log.w("OverviewProxyService", "Unable to unlink mOverviewServiceDeathRcpt");
            }
            this.mOverviewProxy = null;
            notifyNavBarButtonAlphaChanged(1.0f, false);
            notifyConnectionChanged();
        }
    }

    /* access modifiers changed from: private */
    public void notifyNavBarButtonAlphaChanged(float f, boolean z) {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onNavBarButtonAlphaChanged(f, z);
        }
    }

    /* access modifiers changed from: private */
    public void notifyHomeRotationEnabled(boolean z) {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onHomeRotationEnabled(z);
        }
    }

    /* access modifiers changed from: private */
    public void notifyConnectionChanged() {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onConnectionChanged(this.mOverviewProxy != null);
        }
    }

    /* access modifiers changed from: private */
    public void notifyPrioritizedRotationInternal(int i) {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onPrioritizedRotation(i);
        }
    }

    /* access modifiers changed from: private */
    public void notifyAssistantProgress(float f) {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onAssistantProgress(f);
        }
    }

    /* access modifiers changed from: private */
    public void notifyAssistantGestureCompletion(float f) {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onAssistantGestureCompletion(f);
        }
    }

    /* access modifiers changed from: private */
    public void notifyStartAssistant(Bundle bundle) {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).startAssistant(bundle);
        }
    }

    /* access modifiers changed from: private */
    public void notifySwipeUpGestureStartedInternal() {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onSwipeUpGestureStarted();
        }
    }

    public void notifySplitScreenBoundsChanged(Rect rect, Rect rect2) {
        try {
            IOverviewProxy iOverviewProxy = this.mOverviewProxy;
            if (iOverviewProxy != null) {
                iOverviewProxy.onSplitScreenSecondaryBoundsChanged(rect, rect2);
            } else {
                Log.e("OverviewProxyService", "Failed to get overview proxy for split screen bounds.");
            }
        } catch (RemoteException e) {
            Log.e("OverviewProxyService", "Failed to call onSplitScreenSecondaryBoundsChanged()", e);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyToggleRecentApps() {
        for (int size = this.mConnectionCallbacks.size() - 1; size >= 0; size--) {
            this.mConnectionCallbacks.get(size).onToggleRecentApps();
        }
    }

    public void notifyImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
        try {
            IOverviewProxy iOverviewProxy = this.mOverviewProxy;
            if (iOverviewProxy != null) {
                iOverviewProxy.onImeWindowStatusChanged(i, iBinder, i2, i3, z);
            } else {
                Log.e("OverviewProxyService", "Failed to get overview proxy for setting IME status.");
            }
        } catch (RemoteException e) {
            Log.e("OverviewProxyService", "Failed to call notifyImeWindowStatus()", e);
        }
    }

    /* access modifiers changed from: private */
    public void updateEnabledState() {
        this.mIsEnabled = this.mContext.getPackageManager().resolveServiceAsUser(this.mQuickStepIntent, 1048576, ActivityManagerWrapper.getInstance().getCurrentUserId()) != null;
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("OverviewProxyService state:");
        printWriter.print("  isConnected=");
        printWriter.println(this.mOverviewProxy != null);
        printWriter.print("  mIsEnabled=");
        printWriter.println(isEnabled());
        printWriter.print("  mRecentsComponentName=");
        printWriter.println(this.mRecentsComponentName);
        printWriter.print("  mQuickStepIntent=");
        printWriter.println(this.mQuickStepIntent);
        printWriter.print("  mBound=");
        printWriter.println(this.mBound);
        printWriter.print("  mCurrentBoundedUserId=");
        printWriter.println(this.mCurrentBoundedUserId);
        printWriter.print("  mConnectionBackoffAttempts=");
        printWriter.println(this.mConnectionBackoffAttempts);
        printWriter.print("  mInputFocusTransferStarted=");
        printWriter.println(this.mInputFocusTransferStarted);
        printWriter.print("  mInputFocusTransferStartY=");
        printWriter.println(this.mInputFocusTransferStartY);
        printWriter.print("  mInputFocusTransferStartMillis=");
        printWriter.println(this.mInputFocusTransferStartMillis);
        printWriter.print("  mWindowCornerRadius=");
        printWriter.println(this.mWindowCornerRadius);
        printWriter.print("  mSupportsRoundedCornersOnWindows=");
        printWriter.println(this.mSupportsRoundedCornersOnWindows);
        printWriter.print("  mNavBarButtonAlpha=");
        printWriter.println(this.mNavBarButtonAlpha);
        printWriter.print("  mActiveNavBarRegion=");
        printWriter.println(this.mActiveNavBarRegion);
        printWriter.print("  mNavBarMode=");
        printWriter.println(this.mNavBarMode);
        this.mSysUiState.dump(fileDescriptor, printWriter, strArr);
    }
}
