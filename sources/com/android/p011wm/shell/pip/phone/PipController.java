package com.android.p011wm.shell.pip.phone;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import android.util.Size;
import android.util.Slog;
import android.view.SurfaceControl;
import android.view.WindowManagerGlobal;
import android.window.WindowContainerTransaction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.WindowManagerShellWrapper;
import com.android.p011wm.shell.common.DisplayChangeController;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.common.ExecutorUtils;
import com.android.p011wm.shell.common.RemoteCallable;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.TaskStackListenerCallback;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.onehanded.OneHandedController;
import com.android.p011wm.shell.onehanded.OneHandedTransitionCallback;
import com.android.p011wm.shell.pip.IPip;
import com.android.p011wm.shell.pip.IPipAnimationListener;
import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.pip.PipAnimationController;
import com.android.p011wm.shell.pip.PipBoundsAlgorithm;
import com.android.p011wm.shell.pip.PipBoundsState;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipSnapAlgorithm;
import com.android.p011wm.shell.pip.PipTaskOrganizer;
import com.android.p011wm.shell.pip.PipTransitionController;
import com.android.p011wm.shell.pip.PipUtils;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.phone.PipController */
public class PipController implements PipTransitionController.PipTransitionCallback, RemoteCallable<PipController> {
    /* access modifiers changed from: private */
    public PipAppOpsListener mAppOpsListener;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public DisplayController mDisplayController;
    @VisibleForTesting
    final DisplayController.OnDisplaysChangedListener mDisplaysChangedListener = new DisplayController.OnDisplaysChangedListener() {
        public void onFixedRotationStarted(int i, int i2) {
            boolean unused = PipController.this.mIsInFixedRotation = true;
        }

        public void onFixedRotationFinished(int i) {
            boolean unused = PipController.this.mIsInFixedRotation = false;
        }

        public void onDisplayAdded(int i) {
            if (i == PipController.this.mPipBoundsState.getDisplayId()) {
                PipController pipController = PipController.this;
                pipController.onDisplayChanged(pipController.mDisplayController.getDisplayLayout(i), false);
            }
        }

        public void onDisplayConfigurationChanged(int i, Configuration configuration) {
            if (i == PipController.this.mPipBoundsState.getDisplayId()) {
                PipController pipController = PipController.this;
                pipController.onDisplayChanged(pipController.mDisplayController.getDisplayLayout(i), true);
            }
        }
    };
    protected final PipImpl mImpl;
    /* access modifiers changed from: private */
    public boolean mIsInFixedRotation;
    protected ShellExecutor mMainExecutor;
    /* access modifiers changed from: private */
    public PipMediaController mMediaController;
    protected PhonePipMenuController mMenuController;
    private Optional<OneHandedController> mOneHandedController;
    private IPipAnimationListener mPinnedStackAnimationRecentsCallback;
    protected PinnedStackListenerForwarder.PinnedTaskListener mPinnedTaskListener = new PipControllerPinnedTaskListener();
    private PipBoundsAlgorithm mPipBoundsAlgorithm;
    /* access modifiers changed from: private */
    public PipBoundsState mPipBoundsState;
    /* access modifiers changed from: private */
    public PipInputConsumer mPipInputConsumer;
    protected PipTaskOrganizer mPipTaskOrganizer;
    private PipTransitionController mPipTransitionController;
    private final DisplayChangeController.OnDisplayChangingListener mRotationController = new PipController$$ExternalSyntheticLambda1(this);
    private TaskStackListenerImpl mTaskStackListener;
    private final Rect mTmpInsetBounds = new Rect();
    /* access modifiers changed from: private */
    public PipTouchHandler mTouchHandler;
    private WindowManagerShellWrapper mWindowManagerShellWrapper;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
        if (this.mPipBoundsState.getDisplayLayout().rotation() == i3) {
            updateMovementBounds((Rect) null, false, false, false, windowContainerTransaction);
        } else if (!this.mPipTaskOrganizer.isInPip() || this.mPipTaskOrganizer.isEntryScheduled()) {
            onDisplayRotationChangedNotInPip(this.mContext, i3);
            updateMovementBounds(this.mPipBoundsState.getNormalBounds(), true, false, false, windowContainerTransaction);
            this.mPipTaskOrganizer.onDisplayRotationSkipped();
        } else {
            Rect currentOrAnimatingBounds = this.mPipTaskOrganizer.getCurrentOrAnimatingBounds();
            Rect rect = new Rect();
            if (onDisplayRotationChanged(this.mContext, rect, currentOrAnimatingBounds, this.mTmpInsetBounds, i, i2, i3, windowContainerTransaction)) {
                this.mTouchHandler.adjustBoundsForRotation(rect, this.mPipBoundsState.getBounds(), this.mTmpInsetBounds);
                if (!this.mIsInFixedRotation) {
                    this.mPipBoundsState.setShelfVisibility(false, 0, false);
                    this.mPipBoundsState.setImeVisibility(false, 0);
                    this.mTouchHandler.onShelfVisibilityChanged(false, 0);
                    this.mTouchHandler.onImeVisibilityChanged(false, 0);
                }
                updateMovementBounds(rect, true, false, false, windowContainerTransaction);
            }
        }
    }

    /* renamed from: com.android.wm.shell.pip.phone.PipController$PipControllerPinnedTaskListener */
    private class PipControllerPinnedTaskListener extends PinnedStackListenerForwarder.PinnedTaskListener {
        private PipControllerPinnedTaskListener() {
        }

        public void onImeVisibilityChanged(boolean z, int i) {
            PipController.this.mPipBoundsState.setImeVisibility(z, i);
            PipController.this.mTouchHandler.onImeVisibilityChanged(z, i);
        }

        public void onMovementBoundsChanged(boolean z) {
            PipController.this.updateMovementBounds((Rect) null, false, z, false, (WindowContainerTransaction) null);
        }

        public void onActionsChanged(ParceledListSlice<RemoteAction> parceledListSlice) {
            PipController.this.mMenuController.setAppActions(parceledListSlice);
        }

        public void onActivityHidden(ComponentName componentName) {
            if (componentName.equals(PipController.this.mPipBoundsState.getLastPipComponentName())) {
                PipController.this.mPipBoundsState.setLastPipComponentName((ComponentName) null);
            }
        }

        public void onAspectRatioChanged(float f) {
            PipController.this.mPipBoundsState.setAspectRatio(f);
            PipController.this.mTouchHandler.onAspectRatioChanged();
        }
    }

    public static Pip create(Context context, DisplayController displayController, PipAppOpsListener pipAppOpsListener, PipBoundsAlgorithm pipBoundsAlgorithm, PipBoundsState pipBoundsState, PipMediaController pipMediaController, PhonePipMenuController phonePipMenuController, PipTaskOrganizer pipTaskOrganizer, PipTouchHandler pipTouchHandler, PipTransitionController pipTransitionController, WindowManagerShellWrapper windowManagerShellWrapper, TaskStackListenerImpl taskStackListenerImpl, Optional<OneHandedController> optional, ShellExecutor shellExecutor) {
        if (context.getPackageManager().hasSystemFeature("android.software.picture_in_picture")) {
            return new PipController(context, displayController, pipAppOpsListener, pipBoundsAlgorithm, pipBoundsState, pipMediaController, phonePipMenuController, pipTaskOrganizer, pipTouchHandler, pipTransitionController, windowManagerShellWrapper, taskStackListenerImpl, optional, shellExecutor).mImpl;
        }
        Slog.w("PipController", "Device doesn't support Pip feature");
        return null;
    }

    protected PipController(Context context, DisplayController displayController, PipAppOpsListener pipAppOpsListener, PipBoundsAlgorithm pipBoundsAlgorithm, PipBoundsState pipBoundsState, PipMediaController pipMediaController, PhonePipMenuController phonePipMenuController, PipTaskOrganizer pipTaskOrganizer, PipTouchHandler pipTouchHandler, PipTransitionController pipTransitionController, WindowManagerShellWrapper windowManagerShellWrapper, TaskStackListenerImpl taskStackListenerImpl, Optional<OneHandedController> optional, ShellExecutor shellExecutor) {
        ShellExecutor shellExecutor2 = shellExecutor;
        if (UserManager.get(context).getUserHandle() == 0) {
            this.mContext = context;
            this.mImpl = new PipImpl();
            this.mWindowManagerShellWrapper = windowManagerShellWrapper;
            this.mDisplayController = displayController;
            this.mPipBoundsAlgorithm = pipBoundsAlgorithm;
            this.mPipBoundsState = pipBoundsState;
            this.mPipTaskOrganizer = pipTaskOrganizer;
            this.mMainExecutor = shellExecutor2;
            this.mMediaController = pipMediaController;
            this.mMenuController = phonePipMenuController;
            this.mTouchHandler = pipTouchHandler;
            this.mAppOpsListener = pipAppOpsListener;
            this.mOneHandedController = optional;
            this.mPipTransitionController = pipTransitionController;
            this.mTaskStackListener = taskStackListenerImpl;
            PipInputConsumer pipInputConsumer = new PipInputConsumer(WindowManagerGlobal.getWindowManagerService(), "pip_input_consumer", shellExecutor2);
            this.mPipInputConsumer = pipInputConsumer;
            pipInputConsumer.SetXrvdFeatureEnabled(this.mContext);
            this.mMainExecutor.execute(new PipController$$ExternalSyntheticLambda4(this));
            return;
        }
        throw new IllegalStateException("Non-primary Pip component not currently supported.");
    }

    public void init() {
        this.mPipTransitionController.registerPipTransitionCallback(this);
        this.mPipTaskOrganizer.registerOnDisplayIdChangeCallback(new PipController$$ExternalSyntheticLambda8(this));
        this.mPipBoundsState.setOnMinimalSizeChangeCallback(new PipController$$ExternalSyntheticLambda5(this));
        this.mPipBoundsState.setOnShelfVisibilityChangeCallback(new PipController$$ExternalSyntheticLambda0(this));
        PipTouchHandler pipTouchHandler = this.mTouchHandler;
        if (pipTouchHandler != null) {
            PipInputConsumer pipInputConsumer = this.mPipInputConsumer;
            Objects.requireNonNull(pipTouchHandler);
            pipInputConsumer.setInputListener(new PipController$$ExternalSyntheticLambda2(pipTouchHandler));
            PipInputConsumer pipInputConsumer2 = this.mPipInputConsumer;
            PipTouchHandler pipTouchHandler2 = this.mTouchHandler;
            Objects.requireNonNull(pipTouchHandler2);
            pipInputConsumer2.setRegistrationListener(new PipController$$ExternalSyntheticLambda3(pipTouchHandler2));
        }
        this.mDisplayController.addDisplayChangingController(this.mRotationController);
        this.mDisplayController.addDisplayWindowListener(this.mDisplaysChangedListener);
        this.mPipBoundsState.setDisplayId(this.mContext.getDisplayId());
        PipBoundsState pipBoundsState = this.mPipBoundsState;
        Context context = this.mContext;
        pipBoundsState.setDisplayLayout(new DisplayLayout(context, context.getDisplay()));
        try {
            this.mWindowManagerShellWrapper.addPinnedStackListener(this.mPinnedTaskListener);
        } catch (RemoteException e) {
            Slog.e("PipController", "Failed to register pinned stack listener", e);
        }
        try {
            if (ActivityTaskManager.getService().getRootTaskInfo(2, 0) != null) {
                this.mPipInputConsumer.registerInputConsumer();
            }
        } catch (RemoteException | UnsupportedOperationException e2) {
            Log.e("PipController", "Failed to register pinned stack listener", e2);
            e2.printStackTrace();
        }
        this.mTaskStackListener.addListener(new TaskStackListenerCallback() {
            public void onActivityPinned(String str, int i, int i2, int i3) {
                PipController.this.mTouchHandler.onActivityPinned();
                PipController.this.mMediaController.onActivityPinned();
                PipController.this.mAppOpsListener.onActivityPinned(str);
                PipController.this.mPipInputConsumer.registerInputConsumer();
            }

            public void onActivityUnpinned() {
                PipController.this.mTouchHandler.onActivityUnpinned((ComponentName) PipUtils.getTopPipActivity(PipController.this.mContext).first);
                PipController.this.mAppOpsListener.onActivityUnpinned();
                PipController.this.mPipInputConsumer.unregisterInputConsumer();
            }

            public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo runningTaskInfo, boolean z, boolean z2, boolean z3) {
                if (runningTaskInfo.getWindowingMode() == 2) {
                    PipController.this.mTouchHandler.getMotionHelper().expandLeavePip(z2);
                }
            }
        });
        this.mOneHandedController.ifPresent(new PipController$$ExternalSyntheticLambda7(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$1(int i) {
        this.mPipBoundsState.setDisplayId(i);
        onDisplayChanged(this.mDisplayController.getDisplayLayout(i), false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$2() {
        updateMovementBounds((Rect) null, false, false, false, (WindowContainerTransaction) null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$3(Boolean bool, Integer num, Boolean bool2) {
        this.mTouchHandler.onShelfVisibilityChanged(bool.booleanValue(), num.intValue());
        if (bool2.booleanValue()) {
            updateMovementBounds(this.mPipBoundsState.getBounds(), false, false, true, (WindowContainerTransaction) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$4(OneHandedController oneHandedController) {
        oneHandedController.asOneHanded().registerTransitionCallback(new OneHandedTransitionCallback() {
            public void onStartFinished(Rect rect) {
                PipController.this.mTouchHandler.setOhmOffset(rect.top);
            }

            public void onStopFinished(Rect rect) {
                PipController.this.mTouchHandler.setOhmOffset(rect.top);
            }
        });
    }

    public Context getContext() {
        return this.mContext;
    }

    public ShellExecutor getRemoteCallExecutor() {
        return this.mMainExecutor;
    }

    /* access modifiers changed from: private */
    public void onConfigurationChanged(Configuration configuration) {
        this.mPipBoundsAlgorithm.onConfigurationChanged(this.mContext);
        this.mTouchHandler.onConfigurationChanged();
        this.mPipBoundsState.onConfigurationChanged();
    }

    /* access modifiers changed from: private */
    public void onDensityOrFontScaleChanged() {
        this.mPipTaskOrganizer.onDensityOrFontScaleChanged(this.mContext);
        onPipCornerRadiusChanged();
    }

    /* access modifiers changed from: private */
    public void onOverlayChanged() {
        Context context = this.mContext;
        onDisplayChanged(new DisplayLayout(context, context.getDisplay()), false);
    }

    /* access modifiers changed from: private */
    public void onDisplayChanged(DisplayLayout displayLayout, boolean z) {
        if (!Objects.equals(displayLayout, this.mPipBoundsState.getDisplayLayout())) {
            PipController$$ExternalSyntheticLambda6 pipController$$ExternalSyntheticLambda6 = new PipController$$ExternalSyntheticLambda6(this, displayLayout);
            if (!this.mPipTaskOrganizer.isInPip() || !z) {
                pipController$$ExternalSyntheticLambda6.run();
                return;
            }
            PipSnapAlgorithm snapAlgorithm = this.mPipBoundsAlgorithm.getSnapAlgorithm();
            Rect rect = new Rect(this.mPipBoundsState.getBounds());
            float snapFraction = snapAlgorithm.getSnapFraction(rect, this.mPipBoundsAlgorithm.getMovementBounds(rect), this.mPipBoundsState.getStashedState());
            pipController$$ExternalSyntheticLambda6.run();
            snapAlgorithm.applySnapFraction(rect, this.mPipBoundsAlgorithm.getMovementBounds(rect, false), snapFraction, this.mPipBoundsState.getStashedState(), this.mPipBoundsState.getStashOffset(), this.mPipBoundsState.getDisplayBounds(), this.mPipBoundsState.getDisplayLayout().stableInsets());
            this.mTouchHandler.getMotionHelper().movePip(rect);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDisplayChanged$5(DisplayLayout displayLayout) {
        this.mPipBoundsState.setDisplayLayout(displayLayout);
        updateMovementBounds((Rect) null, false, false, false, (WindowContainerTransaction) null);
    }

    /* access modifiers changed from: private */
    public void registerSessionListenerForCurrentUser() {
        this.mMediaController.registerSessionListenerForCurrentUser();
    }

    /* access modifiers changed from: private */
    public void onSystemUiStateChanged(boolean z, int i) {
        this.mTouchHandler.onSystemUiStateChanged(z);
    }

    public void hidePipMenu(Runnable runnable, Runnable runnable2) {
        this.mMenuController.hideMenu(runnable, runnable2);
    }

    public void showPictureInPictureMenu() {
        this.mTouchHandler.showPictureInPictureMenu();
    }

    /* access modifiers changed from: private */
    public void setShelfHeight(boolean z, int i) {
        setShelfHeightLocked(z, i);
    }

    private void setShelfHeightLocked(boolean z, int i) {
        if (!z) {
            i = 0;
        }
        this.mPipBoundsState.setShelfVisibility(z, i);
    }

    /* access modifiers changed from: private */
    public void setPinnedStackAnimationType(int i) {
        this.mPipTaskOrganizer.setOneShotAnimationType(i);
    }

    /* access modifiers changed from: private */
    public void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) {
        this.mPinnedStackAnimationRecentsCallback = iPipAnimationListener;
        onPipCornerRadiusChanged();
    }

    private void onPipCornerRadiusChanged() {
        if (this.mPinnedStackAnimationRecentsCallback != null) {
            try {
                this.mPinnedStackAnimationRecentsCallback.onPipCornerRadiusChanged(this.mContext.getResources().getDimensionPixelSize(C2219R.dimen.pip_corner_radius));
            } catch (RemoteException e) {
                Log.e("PipController", "Failed to call onPipCornerRadiusChanged", e);
            }
        }
    }

    /* access modifiers changed from: private */
    public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) {
        setShelfHeightLocked(i2 > 0, i2);
        onDisplayRotationChangedNotInPip(this.mContext, i);
        Rect startSwipePipToHome = this.mPipTaskOrganizer.startSwipePipToHome(componentName, activityInfo, pictureInPictureParams);
        this.mPipBoundsState.setNormalBounds(startSwipePipToHome);
        return startSwipePipToHome;
    }

    /* access modifiers changed from: private */
    public void stopSwipePipToHome(ComponentName componentName, Rect rect, SurfaceControl surfaceControl) {
        this.mPipTaskOrganizer.stopSwipePipToHome(componentName, rect, surfaceControl);
    }

    public void onPipTransitionStarted(int i, Rect rect) {
        if (PipAnimationController.isOutPipDirection(i)) {
            saveReentryState(rect);
        }
        this.mTouchHandler.setTouchEnabled(false);
        IPipAnimationListener iPipAnimationListener = this.mPinnedStackAnimationRecentsCallback;
        if (iPipAnimationListener != null) {
            try {
                iPipAnimationListener.onPipAnimationStarted();
            } catch (RemoteException e) {
                Log.e("PipController", "Failed to call onPinnedStackAnimationStarted()", e);
            }
        }
    }

    public void saveReentryState(Rect rect) {
        float snapFraction = this.mPipBoundsAlgorithm.getSnapFraction(rect);
        if (this.mPipBoundsState.hasUserResizedPip()) {
            Rect userResizeBounds = this.mTouchHandler.getUserResizeBounds();
            this.mPipBoundsState.saveReentryState(new Size(userResizeBounds.width(), userResizeBounds.height()), snapFraction);
            return;
        }
        this.mPipBoundsState.saveReentryState((Size) null, snapFraction);
    }

    public void onPipTransitionFinished(int i) {
        onPipTransitionFinishedOrCanceled(i);
    }

    public void onPipTransitionCanceled(int i) {
        onPipTransitionFinishedOrCanceled(i);
    }

    private void onPipTransitionFinishedOrCanceled(int i) {
        this.mTouchHandler.setTouchEnabled(true);
        this.mTouchHandler.onPinnedStackAnimationEnded(i);
    }

    /* access modifiers changed from: private */
    public void updateMovementBounds(Rect rect, boolean z, boolean z2, boolean z3, WindowContainerTransaction windowContainerTransaction) {
        Rect rect2 = new Rect(rect);
        int rotation = this.mPipBoundsState.getDisplayLayout().rotation();
        this.mPipBoundsAlgorithm.getInsetBounds(this.mTmpInsetBounds);
        this.mPipBoundsState.setNormalBounds(this.mPipBoundsAlgorithm.getNormalBounds());
        if (rect2.isEmpty()) {
            rect2.set(this.mPipBoundsAlgorithm.getDefaultBounds());
        }
        this.mPipTaskOrganizer.onMovementBoundsChanged(rect2, z, z2, z3, windowContainerTransaction);
        this.mPipTaskOrganizer.finishResizeForMenu(rect2);
        this.mTouchHandler.onMovementBoundsChanged(this.mTmpInsetBounds, this.mPipBoundsState.getNormalBounds(), rect2, z2, z3, rotation);
    }

    private void onDisplayRotationChangedNotInPip(Context context, int i) {
        this.mPipBoundsState.getDisplayLayout().rotateTo(context.getResources(), i);
    }

    private boolean onDisplayRotationChanged(Context context, Rect rect, Rect rect2, Rect rect3, int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
        Rect rect4 = rect;
        int i4 = i3;
        if (i == this.mPipBoundsState.getDisplayId() && i2 != i4) {
            try {
                ActivityTaskManager.RootTaskInfo rootTaskInfo = ActivityTaskManager.getService().getRootTaskInfo(2, 0);
                if (rootTaskInfo == null) {
                    return false;
                }
                PipSnapAlgorithm snapAlgorithm = this.mPipBoundsAlgorithm.getSnapAlgorithm();
                Rect rect5 = new Rect(rect2);
                float snapFraction = snapAlgorithm.getSnapFraction(rect5, this.mPipBoundsAlgorithm.getMovementBounds(rect5), this.mPipBoundsState.getStashedState());
                this.mPipBoundsState.getDisplayLayout().rotateTo(context.getResources(), i4);
                snapAlgorithm.applySnapFraction(rect5, this.mPipBoundsAlgorithm.getMovementBounds(rect5, false), snapFraction, this.mPipBoundsState.getStashedState(), this.mPipBoundsState.getStashOffset(), this.mPipBoundsState.getDisplayBounds(), this.mPipBoundsState.getDisplayLayout().stableInsets());
                this.mPipBoundsAlgorithm.getInsetBounds(rect3);
                rect4.set(rect5);
                windowContainerTransaction.setBounds(rootTaskInfo.token, rect4);
                return true;
            } catch (RemoteException e) {
                Log.e("PipController", "Failed to get RootTaskInfo for pinned task", e);
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void dump(PrintWriter printWriter) {
        printWriter.println("PipController");
        this.mMenuController.dump(printWriter, "  ");
        this.mTouchHandler.dump(printWriter, "  ");
        this.mPipBoundsAlgorithm.dump(printWriter, "  ");
        this.mPipTaskOrganizer.dump(printWriter, "  ");
        this.mPipBoundsState.dump(printWriter, "  ");
        this.mPipInputConsumer.dump(printWriter, "  ");
    }

    /* renamed from: com.android.wm.shell.pip.phone.PipController$PipImpl */
    private class PipImpl implements Pip {
        private IPipImpl mIPip;

        private PipImpl() {
        }

        public IPip createExternalInterface() {
            IPipImpl iPipImpl = this.mIPip;
            if (iPipImpl != null) {
                iPipImpl.invalidate();
            }
            IPipImpl iPipImpl2 = new IPipImpl(PipController.this);
            this.mIPip = iPipImpl2;
            return iPipImpl2;
        }

        public void hidePipMenu(Runnable runnable, Runnable runnable2) {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda7(this, runnable, runnable2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$hidePipMenu$0(Runnable runnable, Runnable runnable2) {
            PipController.this.hidePipMenu(runnable, runnable2);
        }

        public void onConfigurationChanged(Configuration configuration) {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda5(this, configuration));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onConfigurationChanged$2(Configuration configuration) {
            PipController.this.onConfigurationChanged(configuration);
        }

        public void onDensityOrFontScaleChanged() {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onDensityOrFontScaleChanged$3() {
            PipController.this.onDensityOrFontScaleChanged();
        }

        public void onOverlayChanged() {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda3(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onOverlayChanged$4() {
            PipController.this.onOverlayChanged();
        }

        public void onSystemUiStateChanged(boolean z, int i) {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda9(this, z, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSystemUiStateChanged$5(boolean z, int i) {
            PipController.this.onSystemUiStateChanged(z, i);
        }

        public void registerSessionListenerForCurrentUser() {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda2(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$registerSessionListenerForCurrentUser$6() {
            PipController.this.registerSessionListenerForCurrentUser();
        }

        public void setPinnedStackAnimationType(int i) {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda4(this, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setPinnedStackAnimationType$8(int i) {
            PipController.this.setPinnedStackAnimationType(i);
        }

        public void setPipExclusionBoundsChangeListener(Consumer<Rect> consumer) {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda8(this, consumer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setPipExclusionBoundsChangeListener$9(Consumer consumer) {
            PipController.this.mPipBoundsState.setPipExclusionBoundsChangeCallback(consumer);
        }

        public void showPictureInPictureMenu() {
            PipController.this.mMainExecutor.execute(new PipController$PipImpl$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showPictureInPictureMenu$10() {
            PipController.this.showPictureInPictureMenu();
        }

        public void dump(PrintWriter printWriter) {
            try {
                PipController.this.mMainExecutor.executeBlocking(new PipController$PipImpl$$ExternalSyntheticLambda6(this, printWriter));
            } catch (InterruptedException unused) {
                Slog.e("PipController", "Failed to dump PipController in 2s");
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$dump$11(PrintWriter printWriter) {
            PipController.this.dump(printWriter);
        }
    }

    /* renamed from: com.android.wm.shell.pip.phone.PipController$IPipImpl */
    private static class IPipImpl extends IPip.Stub {
        /* access modifiers changed from: private */
        public PipController mController;
        /* access modifiers changed from: private */
        public IPipAnimationListener mListener;
        private final IBinder.DeathRecipient mListenerDeathRecipient = new IBinder.DeathRecipient() {
            public void binderDied() {
                PipController access$2000 = IPipImpl.this.mController;
                access$2000.getRemoteCallExecutor().execute(new PipController$IPipImpl$1$$ExternalSyntheticLambda0(this, access$2000));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$binderDied$0(PipController pipController) {
                IPipAnimationListener unused = IPipImpl.this.mListener = null;
                pipController.setPinnedStackAnimationListener((IPipAnimationListener) null);
            }
        };

        IPipImpl(PipController pipController) {
            this.mController = pipController;
        }

        /* access modifiers changed from: package-private */
        public void invalidate() {
            this.mController = null;
        }

        public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) {
            Rect[] rectArr = new Rect[1];
            ExecutorUtils.executeRemoteCallWithTaskPermission(this.mController, "startSwipePipToHome", new PipController$IPipImpl$$ExternalSyntheticLambda3(rectArr, componentName, activityInfo, pictureInPictureParams, i, i2), true);
            return rectArr[0];
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$startSwipePipToHome$0(Rect[] rectArr, ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2, PipController pipController) {
            rectArr[0] = pipController.startSwipePipToHome(componentName, activityInfo, pictureInPictureParams, i, i2);
        }

        public void stopSwipePipToHome(ComponentName componentName, Rect rect, SurfaceControl surfaceControl) {
            ExecutorUtils.executeRemoteCallWithTaskPermission(this.mController, "stopSwipePipToHome", new PipController$IPipImpl$$ExternalSyntheticLambda0(componentName, rect, surfaceControl));
        }

        public void setShelfHeight(boolean z, int i) {
            ExecutorUtils.executeRemoteCallWithTaskPermission(this.mController, "setShelfHeight", new PipController$IPipImpl$$ExternalSyntheticLambda2(z, i));
        }

        public void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) {
            ExecutorUtils.executeRemoteCallWithTaskPermission(this.mController, "setPinnedStackAnimationListener", new PipController$IPipImpl$$ExternalSyntheticLambda1(this, iPipAnimationListener));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setPinnedStackAnimationListener$3(IPipAnimationListener iPipAnimationListener, PipController pipController) {
            IPipAnimationListener iPipAnimationListener2 = this.mListener;
            if (iPipAnimationListener2 != null) {
                iPipAnimationListener2.asBinder().unlinkToDeath(this.mListenerDeathRecipient, 0);
            }
            if (iPipAnimationListener != null) {
                try {
                    iPipAnimationListener.asBinder().linkToDeath(this.mListenerDeathRecipient, 0);
                } catch (RemoteException unused) {
                    Slog.e("PipController", "Failed to link to death");
                    return;
                }
            }
            this.mListener = iPipAnimationListener;
            pipController.setPinnedStackAnimationListener(iPipAnimationListener);
        }
    }
}
