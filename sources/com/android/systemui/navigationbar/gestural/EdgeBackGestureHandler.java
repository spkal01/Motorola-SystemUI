package com.android.systemui.navigationbar.gestural;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.DeviceConfig;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Choreographer;
import android.view.ISystemGestureExclusionListener;
import android.view.IWindowManager;
import android.view.InputMonitor;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import com.android.internal.policy.GestureNavigationSettingsObserver;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$string;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.model.SysUiState;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.NavigationEdgeBackPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputChannelCompat;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBarWindowCallback;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.tracing.nano.EdgeBackGestureHandlerProto;
import com.android.systemui.tracing.nano.SystemUiTraceProto;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class EdgeBackGestureHandler extends CurrentUserTracker implements PluginListener<NavigationEdgeBackPlugin>, ProtoTraceable<SystemUiTraceProto> {
    private static final boolean ENABLE_PER_WINDOW_INPUT_ROTATION = SystemProperties.getBoolean("persist.debug.per_window_input_rotation", false);
    private static final int MAX_LONG_PRESS_TIMEOUT = SystemProperties.getInt("gestures.back_timeout", 250);
    private boolean mAllowGesture;
    private final NavigationEdgeBackPlugin.BackCallback mBackCallback;
    private BackGestureTfClassifierProvider mBackGestureTfClassifierProvider;
    private float mBottomGestureHeight;
    private CliStatusBarWindowController mCliStatusBarWindowController;
    /* access modifiers changed from: private */
    public final Context mContext;
    private boolean mDisabledForQuickstep;
    /* access modifiers changed from: private */
    public final int mDisplayId;
    private final Point mDisplaySize = new Point();
    /* access modifiers changed from: private */
    public final PointF mDownPoint = new PointF();
    private NavigationEdgeBackPlugin mEdgeBackPlugin;
    private int mEdgeWidthLeft;
    private int mEdgeWidthRight;
    private final PointF mEndPoint = new PointF();
    /* access modifiers changed from: private */
    public final Region mExcludeRegion = new Region();
    /* access modifiers changed from: private */
    public final FalsingManager mFalsingManager;
    private final List<ComponentName> mGestureBlockingActivities = new ArrayList();
    /* access modifiers changed from: private */
    public boolean mGestureBlockingActivityRunning;
    private ISystemGestureExclusionListener mGestureExclusionListener = new ISystemGestureExclusionListener.Stub() {
        public void onSystemGestureExclusionChanged(int i, Region region, Region region2) {
            if (i == EdgeBackGestureHandler.this.mDisplayId) {
                EdgeBackGestureHandler.this.mMainExecutor.execute(new EdgeBackGestureHandler$1$$ExternalSyntheticLambda0(this, region, region2));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSystemGestureExclusionChanged$0(Region region, Region region2) {
            EdgeBackGestureHandler.this.mExcludeRegion.set(region);
            Region access$300 = EdgeBackGestureHandler.this.mUnrestrictedExcludeRegion;
            if (region2 != null) {
                region = region2;
            }
            access$300.set(region);
        }
    };
    private LogArray mGestureLogInsideInsets;
    private LogArray mGestureLogOutsideInsets;
    private final GestureNavigationSettingsObserver mGestureNavigationSettingsObserver;
    /* access modifiers changed from: private */
    public boolean mInRejectedExclusion;
    private InputChannelCompat.InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private boolean mIsAttached;
    private boolean mIsBackGestureAllowed;
    private boolean mIsEnabled;
    private boolean mIsGesturalModeEnabled;
    /* access modifiers changed from: private */
    public boolean mIsInPipMode;
    private boolean mIsNavBarShownTransiently;
    /* access modifiers changed from: private */
    public boolean mIsOnLeftEdge;
    boolean mKeyguardShowing;
    private int mLeftInset;
    private boolean mLogGesture;
    private final int mLongPressTimeout;
    private int mMLEnableWidth;
    private float mMLModelThreshold;
    private float mMLResults;
    /* access modifiers changed from: private */
    public final Executor mMainExecutor;
    private final Rect mNavBarOverlayExcludedBounds = new Rect();
    private final NavigationModeController mNavigationModeController;
    private DeviceConfig.OnPropertiesChangedListener mOnPropertiesChangedListener = new DeviceConfig.OnPropertiesChangedListener() {
        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (!"systemui".equals(properties.getNamespace())) {
                return;
            }
            if (properties.getKeyset().contains("back_gesture_ml_model_threshold") || properties.getKeyset().contains("use_back_gesture_ml_model") || properties.getKeyset().contains("back_gesture_ml_model_name")) {
                EdgeBackGestureHandler.this.updateMLModelState();
            }
        }
    };
    /* access modifiers changed from: private */
    public final OverviewProxyService mOverviewProxyService;
    /* access modifiers changed from: private */
    public String mPackageName;
    boolean mPanelVisible;
    private final Rect mPipExcludedBounds = new Rect();
    private final PluginManager mPluginManager;
    private LogArray mPredictionLog;
    private final ProtoTracer mProtoTracer;
    private OverviewProxyService.OverviewProxyListener mQuickSwitchListener = new OverviewProxyService.OverviewProxyListener() {
        public void onPrioritizedRotation(int i) {
            int unused = EdgeBackGestureHandler.this.mStartingQuickstepRotation = i;
            EdgeBackGestureHandler edgeBackGestureHandler = EdgeBackGestureHandler.this;
            edgeBackGestureHandler.updateDisabledForQuickstep(edgeBackGestureHandler.mContext.getResources().getConfiguration());
        }
    };
    private int mRightInset;
    /* access modifiers changed from: private */
    public int mStartingQuickstepRotation = -1;
    private Runnable mStateChangeCallback;
    private StatusBarWindowCallback mStatusBarViewStateCallback;
    /* access modifiers changed from: private */
    public int mSysUiFlags;
    private final SysUiState mSysUiState;
    private final SysUiState.SysUiStateCallback mSysUiStateCallback;
    private TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() {
        public void onTaskStackChanged() {
            EdgeBackGestureHandler edgeBackGestureHandler = EdgeBackGestureHandler.this;
            boolean unused = edgeBackGestureHandler.mGestureBlockingActivityRunning = edgeBackGestureHandler.isGestureBlockingActivityRunning();
        }

        public void onTaskCreated(int i, ComponentName componentName) {
            if (componentName != null) {
                String unused = EdgeBackGestureHandler.this.mPackageName = componentName.getPackageName();
            } else {
                String unused2 = EdgeBackGestureHandler.this.mPackageName = "_UNKNOWN";
            }
        }

        public void onActivityPinned(String str, int i, int i2, int i3) {
            boolean unused = EdgeBackGestureHandler.this.mIsInPipMode = true;
        }

        public void onActivityUnpinned() {
            boolean unused = EdgeBackGestureHandler.this.mIsInPipMode = false;
        }
    };
    private boolean mThresholdCrossed;
    private float mTouchSlop;
    /* access modifiers changed from: private */
    public final Region mUnrestrictedExcludeRegion = new Region();
    private boolean mUseMLModel;
    private final ViewConfiguration mViewConfiguration;
    private Map<String, Integer> mVocab;
    private final WindowManager mWindowManager;
    private final IWindowManager mWindowManagerService;

    EdgeBackGestureHandler(Context context, OverviewProxyService overviewProxyService, SysUiState sysUiState, PluginManager pluginManager, Executor executor, BroadcastDispatcher broadcastDispatcher, ProtoTracer protoTracer, NavigationModeController navigationModeController, ViewConfiguration viewConfiguration, WindowManager windowManager, IWindowManager iWindowManager, FalsingManager falsingManager) {
        super(broadcastDispatcher);
        this.mThresholdCrossed = false;
        this.mAllowGesture = false;
        this.mLogGesture = false;
        this.mInRejectedExclusion = false;
        this.mPredictionLog = new LogArray(10);
        this.mGestureLogInsideInsets = new LogArray(10);
        this.mGestureLogOutsideInsets = new LogArray(10);
        this.mBackCallback = new NavigationEdgeBackPlugin.BackCallback() {
            public void triggerBack() {
                EdgeBackGestureHandler.this.mFalsingManager.isFalseTouch(16);
                boolean unused = EdgeBackGestureHandler.this.sendEvent(0, 4);
                int i = 1;
                boolean unused2 = EdgeBackGestureHandler.this.sendEvent(1, 4);
                EdgeBackGestureHandler.this.mOverviewProxyService.notifyBackAction(true, (int) EdgeBackGestureHandler.this.mDownPoint.x, (int) EdgeBackGestureHandler.this.mDownPoint.y, false, !EdgeBackGestureHandler.this.mIsOnLeftEdge);
                EdgeBackGestureHandler edgeBackGestureHandler = EdgeBackGestureHandler.this;
                if (edgeBackGestureHandler.mInRejectedExclusion) {
                    i = 2;
                }
                edgeBackGestureHandler.logGesture(i);
            }

            public void cancelBack() {
                EdgeBackGestureHandler.this.logGesture(4);
                EdgeBackGestureHandler.this.mOverviewProxyService.notifyBackAction(false, (int) EdgeBackGestureHandler.this.mDownPoint.x, (int) EdgeBackGestureHandler.this.mDownPoint.y, false, !EdgeBackGestureHandler.this.mIsOnLeftEdge);
            }
        };
        this.mSysUiStateCallback = new SysUiState.SysUiStateCallback() {
            public void onSystemUiStateChanged(int i) {
                int unused = EdgeBackGestureHandler.this.mSysUiFlags = i;
            }
        };
        this.mStatusBarViewStateCallback = new StatusBarWindowCallback() {
            public void onStateChanged(boolean z, boolean z2, boolean z3) {
            }

            public void onViewStateChanged(boolean z) {
                EdgeBackGestureHandler edgeBackGestureHandler = EdgeBackGestureHandler.this;
                if (edgeBackGestureHandler.mPanelVisible != z) {
                    edgeBackGestureHandler.mPanelVisible = z;
                    Log.d("EdgeBackGestureHandler", "onViewStateChanged mPanelVisible = " + EdgeBackGestureHandler.this.mPanelVisible);
                    EdgeBackGestureHandler.this.updateCliSysUiFlags();
                }
            }

            public void onStateChangedForCli(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
                EdgeBackGestureHandler edgeBackGestureHandler = EdgeBackGestureHandler.this;
                if (edgeBackGestureHandler.mKeyguardShowing != z) {
                    edgeBackGestureHandler.mKeyguardShowing = z;
                    Log.d("EdgeBackGestureHandler", "onStateChangedForCli mKeyguardShowing = " + EdgeBackGestureHandler.this.mKeyguardShowing);
                    EdgeBackGestureHandler.this.updateCliSysUiFlags();
                }
            }
        };
        this.mContext = context;
        this.mDisplayId = context.getDisplayId();
        this.mMainExecutor = executor;
        this.mOverviewProxyService = overviewProxyService;
        this.mSysUiState = sysUiState;
        this.mPluginManager = pluginManager;
        this.mProtoTracer = protoTracer;
        this.mNavigationModeController = navigationModeController;
        this.mViewConfiguration = viewConfiguration;
        this.mWindowManager = windowManager;
        this.mWindowManagerService = iWindowManager;
        this.mFalsingManager = falsingManager;
        ComponentName unflattenFromString = ComponentName.unflattenFromString(context.getString(17040011));
        if (unflattenFromString != null) {
            String packageName = unflattenFromString.getPackageName();
            try {
                Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(packageName);
                int identifier = resourcesForApplication.getIdentifier("gesture_blocking_activities", "array", packageName);
                if (identifier == 0) {
                    Log.e("EdgeBackGestureHandler", "No resource found for gesture-blocking activities");
                } else {
                    for (String unflattenFromString2 : resourcesForApplication.getStringArray(identifier)) {
                        this.mGestureBlockingActivities.add(ComponentName.unflattenFromString(unflattenFromString2));
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("EdgeBackGestureHandler", "Failed to add gesture blocking activities", e);
            }
        }
        this.mLongPressTimeout = Math.min(MAX_LONG_PRESS_TIMEOUT, ViewConfiguration.getLongPressTimeout());
        this.mGestureNavigationSettingsObserver = new GestureNavigationSettingsObserver(this.mContext.getMainThreadHandler(), this.mContext, new EdgeBackGestureHandler$$ExternalSyntheticLambda2(this));
        updateCurrentUserResources();
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || this.mDisplayId != 1) {
            sysUiState.addCallback(new EdgeBackGestureHandler$$ExternalSyntheticLambda0(this));
            return;
        }
        sysUiState.addCallback(new SysUiState.SysUiStateCallback() {
            public void onSystemUiStateChanged(int i) {
            }

            public void onSystemUiStateChanged(int i, int i2) {
                if (EdgeBackGestureHandler.this.mDisplayId == i2 && EdgeBackGestureHandler.this.mSysUiFlags != i) {
                    int unused = EdgeBackGestureHandler.this.mSysUiFlags = i;
                    EdgeBackGestureHandler.this.updateCliSysUiFlags();
                    Log.d("EdgeBackGestureHandler", "onSystemUiStateChanged mSysUiFlags = " + EdgeBackGestureHandler.this.mSysUiFlags);
                }
            }
        });
        CliStatusBarWindowController cliStatusBarWindowController = (CliStatusBarWindowController) Dependency.get(CliStatusBarWindowController.class);
        this.mCliStatusBarWindowController = cliStatusBarWindowController;
        cliStatusBarWindowController.registerCallback(this.mStatusBarViewStateCallback);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        this.mSysUiFlags = i;
    }

    /* access modifiers changed from: private */
    public void updateCliSysUiFlags() {
        if (this.mPanelVisible) {
            this.mSysUiFlags |= 4;
        } else {
            this.mSysUiFlags &= -5;
        }
        if (this.mKeyguardShowing) {
            this.mSysUiFlags |= 64;
        } else {
            this.mSysUiFlags &= -65;
        }
    }

    public void setStateChangeCallback(Runnable runnable) {
        this.mStateChangeCallback = runnable;
    }

    public void updateCurrentUserResources() {
        Resources resources = this.mNavigationModeController.getCurrentUserContext().getResources();
        this.mEdgeWidthLeft = this.mGestureNavigationSettingsObserver.getLeftSensitivity(resources);
        this.mEdgeWidthRight = this.mGestureNavigationSettingsObserver.getRightSensitivity(resources);
        this.mIsBackGestureAllowed = !this.mGestureNavigationSettingsObserver.areNavigationButtonForcedVisible();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        this.mBottomGestureHeight = TypedValue.applyDimension(1, DeviceConfig.getFloat("systemui", "back_gesture_bottom_height", resources.getDimension(17105360) / displayMetrics.density), displayMetrics);
        int applyDimension = (int) TypedValue.applyDimension(1, 12.0f, displayMetrics);
        this.mMLEnableWidth = applyDimension;
        int i = this.mEdgeWidthRight;
        if (applyDimension > i) {
            this.mMLEnableWidth = i;
        }
        int i2 = this.mMLEnableWidth;
        int i3 = this.mEdgeWidthLeft;
        if (i2 > i3) {
            this.mMLEnableWidth = i3;
        }
        this.mTouchSlop = ((float) this.mViewConfiguration.getScaledTouchSlop()) * DeviceConfig.getFloat("systemui", "back_gesture_slop_multiplier", 0.75f);
    }

    public void updateNavigationBarOverlayExcludeRegion(Rect rect) {
        this.mNavBarOverlayExcludedBounds.set(rect);
    }

    /* access modifiers changed from: private */
    public void onNavigationSettingsChanged() {
        boolean isHandlingGestures = isHandlingGestures();
        updateCurrentUserResources();
        if (isHandlingGestures != isHandlingGestures()) {
            this.mStateChangeCallback.run();
        }
    }

    public void onUserSwitched(int i) {
        updateIsEnabled();
        updateCurrentUserResources();
    }

    public void onNavBarAttached() {
        this.mIsAttached = true;
        this.mProtoTracer.add(this);
        this.mOverviewProxyService.addCallback(this.mQuickSwitchListener);
        this.mSysUiState.addCallback(this.mSysUiStateCallback);
        updateIsEnabled();
        startTracking();
    }

    public void onNavBarDetached() {
        this.mIsAttached = false;
        this.mProtoTracer.remove(this);
        this.mOverviewProxyService.removeCallback(this.mQuickSwitchListener);
        this.mSysUiState.removeCallback(this.mSysUiStateCallback);
        updateIsEnabled();
        stopTracking();
    }

    public void onNavigationModeChanged(int i) {
        this.mIsGesturalModeEnabled = QuickStepContract.isGesturalMode(i);
        updateIsEnabled();
        updateCurrentUserResources();
    }

    public void onNavBarTransientStateChanged(boolean z) {
        this.mIsNavBarShownTransiently = z;
    }

    private void disposeInputChannel() {
        InputChannelCompat.InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }

    private void updateIsEnabled() {
        boolean z = this.mIsAttached && this.mIsGesturalModeEnabled;
        if (z != this.mIsEnabled) {
            this.mIsEnabled = z;
            disposeInputChannel();
            NavigationEdgeBackPlugin navigationEdgeBackPlugin = this.mEdgeBackPlugin;
            if (navigationEdgeBackPlugin != null) {
                navigationEdgeBackPlugin.onDestroy();
                this.mEdgeBackPlugin = null;
            }
            if (!this.mIsEnabled) {
                this.mGestureNavigationSettingsObserver.unregister();
                this.mPluginManager.removePluginListener(this);
                TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
                DeviceConfig.removeOnPropertiesChangedListener(this.mOnPropertiesChangedListener);
                try {
                    this.mWindowManagerService.unregisterSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                } catch (RemoteException | IllegalArgumentException e) {
                    Log.e("EdgeBackGestureHandler", "Failed to unregister window manager callbacks", e);
                }
            } else {
                this.mGestureNavigationSettingsObserver.register();
                updateDisplaySize();
                TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mTaskStackListener);
                this.mTaskStackListener.onTaskStackChanged();
                Executor executor = this.mMainExecutor;
                Objects.requireNonNull(executor);
                DeviceConfig.addOnPropertiesChangedListener("systemui", new EdgeBackGestureHandler$$ExternalSyntheticLambda3(executor), this.mOnPropertiesChangedListener);
                try {
                    this.mWindowManagerService.registerSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                } catch (RemoteException | IllegalArgumentException e2) {
                    Log.e("EdgeBackGestureHandler", "Failed to register window manager callbacks", e2);
                }
                InputMonitor monitorGestureInput = InputManager.getInstance().monitorGestureInput("edge-swipe", this.mDisplayId);
                this.mInputMonitor = monitorGestureInput;
                this.mInputEventReceiver = new InputChannelCompat.InputEventReceiver(monitorGestureInput.getInputChannel(), Looper.getMainLooper(), Choreographer.getInstance(), new EdgeBackGestureHandler$$ExternalSyntheticLambda1(this));
                setEdgeBackPlugin(new NavigationBarEdgePanel(this.mContext));
                this.mPluginManager.addPluginListener(this, (Class<?>) NavigationEdgeBackPlugin.class, false);
            }
            updateMLModelState();
        }
    }

    public void onPluginConnected(NavigationEdgeBackPlugin navigationEdgeBackPlugin, Context context) {
        setEdgeBackPlugin(navigationEdgeBackPlugin);
    }

    public void onPluginDisconnected(NavigationEdgeBackPlugin navigationEdgeBackPlugin) {
        setEdgeBackPlugin(new NavigationBarEdgePanel(this.mContext));
    }

    private void setEdgeBackPlugin(NavigationEdgeBackPlugin navigationEdgeBackPlugin) {
        NavigationEdgeBackPlugin navigationEdgeBackPlugin2 = this.mEdgeBackPlugin;
        if (navigationEdgeBackPlugin2 != null) {
            navigationEdgeBackPlugin2.onDestroy();
        }
        this.mEdgeBackPlugin = navigationEdgeBackPlugin;
        navigationEdgeBackPlugin.setBackCallback(this.mBackCallback);
        this.mEdgeBackPlugin.setLayoutParams(createLayoutParams());
        updateDisplaySize();
    }

    public boolean isHandlingGestures() {
        return this.mIsEnabled && this.mIsBackGestureAllowed;
    }

    public void setPipStashExclusionBounds(Rect rect) {
        this.mPipExcludedBounds.set(rect);
    }

    private WindowManager.LayoutParams createLayoutParams() {
        Resources resources = this.mContext.getResources();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(resources.getDimensionPixelSize(R$dimen.navigation_edge_panel_width), resources.getDimensionPixelSize(R$dimen.navigation_edge_panel_height), 2024, 280, -3);
        layoutParams.accessibilityTitle = this.mContext.getString(R$string.nav_bar_edge_panel);
        layoutParams.windowAnimations = 0;
        layoutParams.privateFlags |= 16;
        layoutParams.setTitle("EdgeBackGestureHandler" + this.mContext.getDisplayId());
        layoutParams.setFitInsetsTypes(0);
        layoutParams.setTrustedOverlay();
        return layoutParams;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000b, code lost:
        r0 = r3.mContext.getDisplay();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onInputEvent(android.view.InputEvent r4) {
        /*
            r3 = this;
            boolean r0 = r4 instanceof android.view.MotionEvent
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            android.view.MotionEvent r4 = (android.view.MotionEvent) r4
            boolean r0 = ENABLE_PER_WINDOW_INPUT_ROTATION
            if (r0 == 0) goto L_0x002e
            android.content.Context r0 = r3.mContext
            android.view.Display r0 = r0.getDisplay()
            int r1 = r0.getRotation()
            if (r1 == 0) goto L_0x002e
            android.graphics.Point r2 = new android.graphics.Point
            r2.<init>()
            r0.getRealSize(r2)
            android.view.MotionEvent r4 = android.view.MotionEvent.obtain(r4)
            int r0 = r2.x
            int r2 = r2.y
            android.graphics.Matrix r0 = android.view.MotionEvent.createRotateMatrix(r1, r0, r2)
            r4.transform(r0)
        L_0x002e:
            r3.onMotionEvent(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler.onInputEvent(android.view.InputEvent):void");
    }

    /* access modifiers changed from: private */
    public void updateMLModelState() {
        boolean z = this.mIsEnabled && DeviceConfig.getBoolean("systemui", "use_back_gesture_ml_model", false);
        if (z != this.mUseMLModel) {
            if (z) {
                this.mBackGestureTfClassifierProvider = SystemUIFactory.getInstance().createBackGestureTfClassifierProvider(this.mContext.getAssets(), DeviceConfig.getString("systemui", "back_gesture_ml_model_name", "backgesture"));
                this.mMLModelThreshold = DeviceConfig.getFloat("systemui", "back_gesture_ml_model_threshold", 0.9f);
                if (this.mBackGestureTfClassifierProvider.isActive()) {
                    this.mVocab = this.mBackGestureTfClassifierProvider.loadVocab(this.mContext.getAssets());
                    this.mUseMLModel = true;
                    return;
                }
            }
            this.mUseMLModel = false;
            BackGestureTfClassifierProvider backGestureTfClassifierProvider = this.mBackGestureTfClassifierProvider;
            if (backGestureTfClassifierProvider != null) {
                backGestureTfClassifierProvider.release();
                this.mBackGestureTfClassifierProvider = null;
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getBackGesturePredictionsCategory(int r10, int r11, int r12) {
        /*
            r9 = this;
            r0 = -1
            if (r12 != r0) goto L_0x0004
            return r0
        L_0x0004:
            double r1 = (double) r10
            android.graphics.Point r3 = r9.mDisplaySize
            int r3 = r3.x
            double r4 = (double) r3
            r6 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r4 = r4 / r6
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            r2 = 2
            r4 = 1
            if (r1 > 0) goto L_0x0015
            r1 = r4
            goto L_0x0018
        L_0x0015:
            int r10 = r3 - r10
            r1 = r2
        L_0x0018:
            r5 = 5
            java.lang.Object[] r5 = new java.lang.Object[r5]
            long[] r6 = new long[r4]
            long r7 = (long) r3
            r3 = 0
            r6[r3] = r7
            r5[r3] = r6
            long[] r6 = new long[r4]
            long r7 = (long) r10
            r6[r3] = r7
            r5[r4] = r6
            long[] r10 = new long[r4]
            long r6 = (long) r1
            r10[r3] = r6
            r5[r2] = r10
            r10 = 3
            long[] r1 = new long[r4]
            long r6 = (long) r12
            r1[r3] = r6
            r5[r10] = r1
            r10 = 4
            long[] r12 = new long[r4]
            long r1 = (long) r11
            r12[r3] = r1
            r5[r10] = r12
            com.android.systemui.navigationbar.gestural.BackGestureTfClassifierProvider r10 = r9.mBackGestureTfClassifierProvider
            float r10 = r10.predict(r5)
            r9.mMLResults = r10
            r11 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r11 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r11 != 0) goto L_0x0050
            return r0
        L_0x0050:
            float r9 = r9.mMLModelThreshold
            int r9 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0057
            goto L_0x0058
        L_0x0057:
            r4 = r3
        L_0x0058:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler.getBackGesturePredictionsCategory(int, int, int):int");
    }

    private boolean isWithinInsets(int i, int i2) {
        Point point = this.mDisplaySize;
        if (((float) i2) >= ((float) point.y) - this.mBottomGestureHeight) {
            return false;
        }
        if (i <= (this.mEdgeWidthLeft + this.mLeftInset) * 2 || i >= point.x - ((this.mEdgeWidthRight + this.mRightInset) * 2)) {
            return true;
        }
        return false;
    }

    private boolean isWithinTouchRegion(int i, int i2) {
        int backGesturePredictionsCategory;
        if ((this.mIsInPipMode && this.mPipExcludedBounds.contains(i, i2)) || this.mNavBarOverlayExcludedBounds.contains(i, i2)) {
            return false;
        }
        Map<String, Integer> map = this.mVocab;
        int intValue = map != null ? map.getOrDefault(this.mPackageName, -1).intValue() : -1;
        int i3 = this.mEdgeWidthLeft;
        int i4 = this.mLeftInset;
        boolean z = i < i3 + i4 || i >= (this.mDisplaySize.x - this.mEdgeWidthRight) - this.mRightInset;
        if (z) {
            int i5 = this.mMLEnableWidth;
            if (!(i < i4 + i5 || i >= (this.mDisplaySize.x - i5) - this.mRightInset) && this.mUseMLModel && (backGesturePredictionsCategory = getBackGesturePredictionsCategory(i, i2, intValue)) != -1) {
                z = backGesturePredictionsCategory == 1;
            }
        }
        this.mPredictionLog.log(String.format("Prediction [%d,%d,%d,%d,%f,%d]", new Object[]{Long.valueOf(System.currentTimeMillis()), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(intValue), Float.valueOf(this.mMLResults), Integer.valueOf(z ? 1 : 0)}));
        if (this.mIsNavBarShownTransiently) {
            this.mLogGesture = true;
            return z;
        } else if (this.mExcludeRegion.contains(i, i2)) {
            if (z) {
                this.mOverviewProxyService.notifyBackAction(false, -1, -1, false, !this.mIsOnLeftEdge);
                PointF pointF = this.mEndPoint;
                pointF.x = -1.0f;
                pointF.y = -1.0f;
                this.mLogGesture = true;
                logGesture(3);
            }
            return false;
        } else {
            this.mInRejectedExclusion = this.mUnrestrictedExcludeRegion.contains(i, i2);
            this.mLogGesture = true;
            return z;
        }
    }

    private void cancelGesture(MotionEvent motionEvent) {
        this.mAllowGesture = false;
        this.mLogGesture = false;
        this.mInRejectedExclusion = false;
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        obtain.setAction(3);
        this.mEdgeBackPlugin.onMotionEvent(obtain);
        obtain.recycle();
    }

    /* access modifiers changed from: private */
    public void logGesture(int i) {
        if (this.mLogGesture) {
            this.mLogGesture = false;
            String str = (!this.mUseMLModel || !this.mVocab.containsKey(this.mPackageName) || this.mVocab.get(this.mPackageName).intValue() >= 100) ? "" : this.mPackageName;
            PointF pointF = this.mDownPoint;
            float f = pointF.y;
            int i2 = (int) f;
            int i3 = this.mIsOnLeftEdge ? 1 : 2;
            int i4 = (int) pointF.x;
            int i5 = (int) f;
            PointF pointF2 = this.mEndPoint;
            SysUiStatsLog.write(224, i, i2, i3, i4, i5, (int) pointF2.x, (int) pointF2.y, this.mEdgeWidthLeft + this.mLeftInset, this.mDisplaySize.x - (this.mEdgeWidthRight + this.mRightInset), this.mUseMLModel ? this.mMLResults : -2.0f, str);
        }
    }

    private void onMotionEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mInputEventReceiver.setBatchingEnabled(false);
            this.mIsOnLeftEdge = motionEvent.getX() <= ((float) (this.mEdgeWidthLeft + this.mLeftInset));
            this.mMLResults = 0.0f;
            this.mLogGesture = false;
            this.mInRejectedExclusion = false;
            boolean isWithinInsets = isWithinInsets((int) motionEvent.getX(), (int) motionEvent.getY());
            boolean z = !this.mDisabledForQuickstep && this.mIsBackGestureAllowed && isWithinInsets && !this.mGestureBlockingActivityRunning && !QuickStepContract.isBackGestureDisabled(this.mSysUiFlags) && isWithinTouchRegion((int) motionEvent.getX(), (int) motionEvent.getY());
            this.mAllowGesture = z;
            if (z) {
                this.mEdgeBackPlugin.setIsLeftPanel(this.mIsOnLeftEdge);
                this.mEdgeBackPlugin.onMotionEvent(motionEvent);
            }
            if (this.mLogGesture) {
                this.mDownPoint.set(motionEvent.getX(), motionEvent.getY());
                this.mEndPoint.set(-1.0f, -1.0f);
                this.mThresholdCrossed = false;
            }
            (isWithinInsets ? this.mGestureLogInsideInsets : this.mGestureLogOutsideInsets).log(String.format("Gesture [%d,alw=%B,%B,%B,%B,disp=%s,wl=%d,il=%d,wr=%d,ir=%d,excl=%s]", new Object[]{Long.valueOf(System.currentTimeMillis()), Boolean.valueOf(this.mAllowGesture), Boolean.valueOf(this.mIsOnLeftEdge), Boolean.valueOf(this.mIsBackGestureAllowed), Boolean.valueOf(QuickStepContract.isBackGestureDisabled(this.mSysUiFlags)), this.mDisplaySize, Integer.valueOf(this.mEdgeWidthLeft), Integer.valueOf(this.mLeftInset), Integer.valueOf(this.mEdgeWidthRight), Integer.valueOf(this.mRightInset), this.mExcludeRegion}));
        } else if (this.mAllowGesture || this.mLogGesture) {
            if (!this.mThresholdCrossed) {
                this.mEndPoint.x = (float) ((int) motionEvent.getX());
                this.mEndPoint.y = (float) ((int) motionEvent.getY());
                if (actionMasked == 5) {
                    if (this.mAllowGesture) {
                        logGesture(6);
                        cancelGesture(motionEvent);
                    }
                    this.mLogGesture = false;
                    return;
                } else if (actionMasked == 2) {
                    if (motionEvent.getEventTime() - motionEvent.getDownTime() > ((long) this.mLongPressTimeout)) {
                        if (this.mAllowGesture) {
                            logGesture(7);
                            cancelGesture(motionEvent);
                        }
                        this.mLogGesture = false;
                        return;
                    }
                    float abs = Math.abs(motionEvent.getX() - this.mDownPoint.x);
                    float abs2 = Math.abs(motionEvent.getY() - this.mDownPoint.y);
                    if (abs2 > abs && abs2 > this.mTouchSlop) {
                        if (this.mAllowGesture) {
                            logGesture(8);
                            cancelGesture(motionEvent);
                        }
                        this.mLogGesture = false;
                        return;
                    } else if (abs > abs2 && abs > this.mTouchSlop) {
                        if (this.mAllowGesture) {
                            this.mThresholdCrossed = true;
                            this.mInputMonitor.pilferPointers();
                            this.mInputEventReceiver.setBatchingEnabled(true);
                        } else {
                            logGesture(5);
                        }
                    }
                }
            }
            if (this.mAllowGesture) {
                this.mEdgeBackPlugin.onMotionEvent(motionEvent);
            }
        }
        this.mProtoTracer.scheduleFrameUpdate();
    }

    /* access modifiers changed from: private */
    public void updateDisabledForQuickstep(Configuration configuration) {
        int rotation = configuration.windowConfiguration.getRotation();
        int i = this.mStartingQuickstepRotation;
        this.mDisabledForQuickstep = i > -1 && i != rotation;
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.mStartingQuickstepRotation > -1) {
            updateDisabledForQuickstep(configuration);
        }
        updateDisplaySize();
    }

    private void updateDisplaySize() {
        Rect bounds = this.mWindowManager.getMaximumWindowMetrics().getBounds();
        this.mDisplaySize.set(bounds.width(), bounds.height());
        NavigationEdgeBackPlugin navigationEdgeBackPlugin = this.mEdgeBackPlugin;
        if (navigationEdgeBackPlugin != null) {
            navigationEdgeBackPlugin.setDisplaySize(this.mDisplaySize);
        }
    }

    /* access modifiers changed from: private */
    public boolean sendEvent(int i, int i2) {
        long uptimeMillis = SystemClock.uptimeMillis();
        KeyEvent keyEvent = new KeyEvent(uptimeMillis, uptimeMillis, i, i2, 0, 0, -1, 0, 72, 257);
        keyEvent.setDisplayId(this.mContext.getDisplay().getDisplayId());
        return InputManager.getInstance().injectInputEvent(keyEvent, 0);
    }

    public void setInsets(int i, int i2) {
        this.mLeftInset = i;
        this.mRightInset = i2;
        NavigationEdgeBackPlugin navigationEdgeBackPlugin = this.mEdgeBackPlugin;
        if (navigationEdgeBackPlugin != null) {
            navigationEdgeBackPlugin.setInsets(i, i2);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("EdgeBackGestureHandler:");
        printWriter.println("  mIsEnabled=" + this.mIsEnabled);
        printWriter.println("  mIsAttached=" + this.mIsAttached);
        printWriter.println("  mIsBackGestureAllowed=" + this.mIsBackGestureAllowed);
        printWriter.println("  mIsGesturalModeEnabled=" + this.mIsGesturalModeEnabled);
        printWriter.println("  mIsNavBarShownTransiently=" + this.mIsNavBarShownTransiently);
        printWriter.println("  mGestureBlockingActivityRunning=" + this.mGestureBlockingActivityRunning);
        printWriter.println("  mAllowGesture=" + this.mAllowGesture);
        printWriter.println("  mUseMLModel=" + this.mUseMLModel);
        printWriter.println("  mDisabledForQuickstep=" + this.mDisabledForQuickstep);
        printWriter.println("  mStartingQuickstepRotation=" + this.mStartingQuickstepRotation);
        printWriter.println("  mInRejectedExclusion=" + this.mInRejectedExclusion);
        printWriter.println("  mExcludeRegion=" + this.mExcludeRegion);
        printWriter.println("  mUnrestrictedExcludeRegion=" + this.mUnrestrictedExcludeRegion);
        printWriter.println("  mIsInPipMode=" + this.mIsInPipMode);
        printWriter.println("  mPipExcludedBounds=" + this.mPipExcludedBounds);
        printWriter.println("  mNavBarOverlayExcludedBounds=" + this.mNavBarOverlayExcludedBounds);
        printWriter.println("  mEdgeWidthLeft=" + this.mEdgeWidthLeft);
        printWriter.println("  mEdgeWidthRight=" + this.mEdgeWidthRight);
        printWriter.println("  mLeftInset=" + this.mLeftInset);
        printWriter.println("  mRightInset=" + this.mRightInset);
        printWriter.println("  mMLEnableWidth=" + this.mMLEnableWidth);
        printWriter.println("  mMLModelThreshold=" + this.mMLModelThreshold);
        printWriter.println("  mTouchSlop=" + this.mTouchSlop);
        printWriter.println("  mBottomGestureHeight=" + this.mBottomGestureHeight);
        printWriter.println("  mPredictionLog=" + String.join("\n", this.mPredictionLog));
        printWriter.println("  mGestureLogInsideInsets=" + String.join("\n", this.mGestureLogInsideInsets));
        printWriter.println("  mGestureLogOutsideInsets=" + String.join("\n", this.mGestureLogOutsideInsets));
        printWriter.println("  mEdgeBackPlugin=" + this.mEdgeBackPlugin);
        NavigationEdgeBackPlugin navigationEdgeBackPlugin = this.mEdgeBackPlugin;
        if (navigationEdgeBackPlugin != null) {
            navigationEdgeBackPlugin.dump(printWriter);
        }
    }

    /* access modifiers changed from: private */
    public boolean isGestureBlockingActivityRunning() {
        ComponentName componentName;
        ActivityManager.RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
        if (runningTask == null) {
            componentName = null;
        } else {
            componentName = runningTask.topActivity;
        }
        if (componentName != null) {
            this.mPackageName = componentName.getPackageName();
        } else {
            this.mPackageName = "_UNKNOWN";
        }
        return componentName != null && this.mGestureBlockingActivities.contains(componentName);
    }

    public void writeToProto(SystemUiTraceProto systemUiTraceProto) {
        if (systemUiTraceProto.edgeBackGestureHandler == null) {
            systemUiTraceProto.edgeBackGestureHandler = new EdgeBackGestureHandlerProto();
        }
        systemUiTraceProto.edgeBackGestureHandler.allowGesture = this.mAllowGesture;
    }

    public static class Factory {
        private final BroadcastDispatcher mBroadcastDispatcher;
        private final Executor mExecutor;
        private final FalsingManager mFalsingManager;
        private final NavigationModeController mNavigationModeController;
        private final OverviewProxyService mOverviewProxyService;
        private final PluginManager mPluginManager;
        private final ProtoTracer mProtoTracer;
        private final SysUiState mSysUiState;
        private final ViewConfiguration mViewConfiguration;
        private final WindowManager mWindowManager;
        private final IWindowManager mWindowManagerService;

        public Factory(OverviewProxyService overviewProxyService, SysUiState sysUiState, PluginManager pluginManager, Executor executor, BroadcastDispatcher broadcastDispatcher, ProtoTracer protoTracer, NavigationModeController navigationModeController, ViewConfiguration viewConfiguration, WindowManager windowManager, IWindowManager iWindowManager, FalsingManager falsingManager) {
            this.mOverviewProxyService = overviewProxyService;
            this.mSysUiState = sysUiState;
            this.mPluginManager = pluginManager;
            this.mExecutor = executor;
            this.mBroadcastDispatcher = broadcastDispatcher;
            this.mProtoTracer = protoTracer;
            this.mNavigationModeController = navigationModeController;
            this.mViewConfiguration = viewConfiguration;
            this.mWindowManager = windowManager;
            this.mWindowManagerService = iWindowManager;
            this.mFalsingManager = falsingManager;
        }

        public EdgeBackGestureHandler create(Context context) {
            return new EdgeBackGestureHandler(context, this.mOverviewProxyService, this.mSysUiState, this.mPluginManager, this.mExecutor, this.mBroadcastDispatcher, this.mProtoTracer, this.mNavigationModeController, this.mViewConfiguration, this.mWindowManager, this.mWindowManagerService, this.mFalsingManager);
        }
    }

    private static class LogArray extends ArrayDeque<String> {
        private final int mLength;

        LogArray(int i) {
            this.mLength = i;
        }

        /* access modifiers changed from: package-private */
        public void log(String str) {
            if (size() >= this.mLength) {
                removeFirst();
            }
            addLast(str);
        }
    }
}
