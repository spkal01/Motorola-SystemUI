package com.android.p011wm.shell.legacysplitscreen;

import android.animation.AnimationHandler;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.ViewGroup;
import android.window.TaskOrganizer;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.DisplayChangeController;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.common.TaskStackListenerCallback;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.transition.Transitions;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController */
public class LegacySplitScreenController implements DisplayController.OnDisplaysChangedListener {
    private volatile boolean mAdjustedForIme = false;
    private final ArrayList<WeakReference<BiConsumer<Rect, Rect>>> mBoundsChangedListeners = new ArrayList<>();
    private final Context mContext;
    private final DisplayController mDisplayController;
    private final DividerState mDividerState = new DividerState();
    private final CopyOnWriteArrayList<WeakReference<Consumer<Boolean>>> mDockedStackExistsListeners = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    public final ForcedResizableInfoActivityController mForcedResizableController;
    /* access modifiers changed from: private */
    public boolean mHomeStackResizable = false;
    private final DisplayImeController mImeController;
    private final DividerImeController mImePositionProcessor;
    private final SplitScreenImpl mImpl = new SplitScreenImpl();
    private boolean mIsKeyguardShowing;
    /* access modifiers changed from: private */
    public final ShellExecutor mMainExecutor;
    /* access modifiers changed from: private */
    public volatile boolean mMinimized = false;
    private LegacySplitDisplayLayout mRotateSplitLayout;
    private final DisplayChangeController.OnDisplayChangingListener mRotationController;
    private final AnimationHandler mSfVsyncAnimationHandler;
    private LegacySplitDisplayLayout mSplitLayout;
    /* access modifiers changed from: private */
    public final LegacySplitScreenTaskListener mSplits;
    private final SystemWindows mSystemWindows;
    private final TaskOrganizer mTaskOrganizer;
    final TransactionPool mTransactionPool;
    private DividerView mView;
    private boolean mVisible = false;
    private DividerWindowManager mWindowManager;
    private final WindowManagerProxy mWindowManagerProxy;

    public LegacySplitScreenController(Context context, DisplayController displayController, SystemWindows systemWindows, DisplayImeController displayImeController, TransactionPool transactionPool, ShellTaskOrganizer shellTaskOrganizer, SyncTransactionQueue syncTransactionQueue, TaskStackListenerImpl taskStackListenerImpl, Transitions transitions, ShellExecutor shellExecutor, AnimationHandler animationHandler) {
        this.mContext = context;
        this.mDisplayController = displayController;
        this.mSystemWindows = systemWindows;
        this.mImeController = displayImeController;
        this.mMainExecutor = shellExecutor;
        this.mSfVsyncAnimationHandler = animationHandler;
        this.mForcedResizableController = new ForcedResizableInfoActivityController(context, this, shellExecutor);
        this.mTransactionPool = transactionPool;
        this.mWindowManagerProxy = new WindowManagerProxy(syncTransactionQueue, shellTaskOrganizer);
        this.mTaskOrganizer = shellTaskOrganizer;
        LegacySplitScreenTaskListener legacySplitScreenTaskListener = new LegacySplitScreenTaskListener(this, shellTaskOrganizer, transitions, syncTransactionQueue);
        this.mSplits = legacySplitScreenTaskListener;
        this.mImePositionProcessor = new DividerImeController(legacySplitScreenTaskListener, transactionPool, shellExecutor, shellTaskOrganizer);
        this.mRotationController = new LegacySplitScreenController$$ExternalSyntheticLambda0(this);
        this.mWindowManager = new DividerWindowManager(systemWindows);
        displayController.addDisplayWindowListener(this);
        taskStackListenerImpl.addListener(new TaskStackListenerCallback() {
            public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo runningTaskInfo, boolean z, boolean z2, boolean z3) {
                if (z3 && runningTaskInfo.getWindowingMode() == 3 && LegacySplitScreenController.this.mSplits.isSplitScreenSupported() && LegacySplitScreenController.this.isMinimized()) {
                    LegacySplitScreenController.this.onUndockingTask();
                }
            }

            public void onActivityForcedResizable(String str, int i, int i2) {
                LegacySplitScreenController.this.mForcedResizableController.activityForcedResizable(str, i, i2);
            }

            public void onActivityDismissingDockedStack() {
                LegacySplitScreenController.this.mForcedResizableController.activityDismissingSplitScreen();
            }

            public void onActivityLaunchOnSecondaryDisplayFailed() {
                LegacySplitScreenController.this.mForcedResizableController.activityLaunchOnSecondaryDisplayFailed();
            }
        });
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, WindowContainerTransaction windowContainerTransaction) {
        int i4;
        if (this.mSplits.isSplitScreenSupported() && this.mWindowManagerProxy != null) {
            WindowContainerTransaction windowContainerTransaction2 = new WindowContainerTransaction();
            LegacySplitDisplayLayout legacySplitDisplayLayout = new LegacySplitDisplayLayout(this.mContext, new DisplayLayout(this.mDisplayController.getDisplayLayout(i)), this.mSplits);
            legacySplitDisplayLayout.rotateTo(i3);
            this.mRotateSplitLayout = legacySplitDisplayLayout;
            if (this.mMinimized) {
                i4 = this.mView.mSnapTargetBeforeMinimized.position;
            } else {
                i4 = legacySplitDisplayLayout.getSnapAlgorithm().getMiddleTarget().position;
            }
            legacySplitDisplayLayout.resizeSplits(legacySplitDisplayLayout.getSnapAlgorithm().calculateNonDismissingSnapTarget(i4).position, windowContainerTransaction2);
            if (isSplitActive() && this.mHomeStackResizable) {
                this.mWindowManagerProxy.applyHomeTasksMinimized(legacySplitDisplayLayout, this.mSplits.mSecondary.token, windowContainerTransaction2);
            }
            if (this.mWindowManagerProxy.queueSyncTransactionIfWaiting(windowContainerTransaction2)) {
                Slog.w("SplitScreenCtrl", "Screen rotated while other operations were pending, this may result in some graphical artifacts.");
            } else {
                windowContainerTransaction.merge(windowContainerTransaction2, true);
            }
        }
    }

    public LegacySplitScreen asLegacySplitScreen() {
        return this.mImpl;
    }

    public void onSplitScreenSupported() {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        this.mSplitLayout.resizeSplits(this.mSplitLayout.getSnapAlgorithm().getMiddleTarget().position, windowContainerTransaction);
        this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
    }

    public void onKeyguardVisibilityChanged(boolean z) {
        DividerView dividerView;
        if (isSplitActive() && (dividerView = this.mView) != null) {
            dividerView.setHidden(z);
            this.mIsKeyguardShowing = z;
        }
    }

    public void onDisplayAdded(int i) {
        if (i == 0) {
            this.mSplitLayout = new LegacySplitDisplayLayout(this.mDisplayController.getDisplayContext(i), this.mDisplayController.getDisplayLayout(i), this.mSplits);
            this.mImeController.addPositionProcessor(this.mImePositionProcessor);
            this.mDisplayController.addDisplayChangingController(this.mRotationController);
            if (!ActivityTaskManager.supportsSplitScreenMultiWindow(this.mContext)) {
                removeDivider();
                return;
            }
            try {
                this.mSplits.init();
            } catch (Exception e) {
                Slog.e("SplitScreenCtrl", "Failed to register docked stack listener", e);
                removeDivider();
            }
        }
    }

    public void onDisplayConfigurationChanged(int i, Configuration configuration) {
        if (i == 0 && this.mSplits.isSplitScreenSupported()) {
            LegacySplitDisplayLayout legacySplitDisplayLayout = new LegacySplitDisplayLayout(this.mDisplayController.getDisplayContext(i), this.mDisplayController.getDisplayLayout(i), this.mSplits);
            this.mSplitLayout = legacySplitDisplayLayout;
            if (this.mRotateSplitLayout == null) {
                int i2 = legacySplitDisplayLayout.getSnapAlgorithm().getMiddleTarget().position;
                WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
                this.mSplitLayout.resizeSplits(i2, windowContainerTransaction);
                this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
            } else if (legacySplitDisplayLayout.mDisplayLayout.rotation() == this.mRotateSplitLayout.mDisplayLayout.rotation()) {
                this.mSplitLayout.mPrimary = new Rect(this.mRotateSplitLayout.mPrimary);
                this.mSplitLayout.mSecondary = new Rect(this.mRotateSplitLayout.mSecondary);
                this.mRotateSplitLayout = null;
            }
            if (isSplitActive()) {
                update(configuration);
            }
        }
    }

    public boolean isMinimized() {
        return this.mMinimized;
    }

    public boolean isHomeStackResizable() {
        return this.mHomeStackResizable;
    }

    public DividerView getDividerView() {
        return this.mView;
    }

    public boolean isDividerVisible() {
        DividerView dividerView = this.mView;
        return dividerView != null && dividerView.getVisibility() == 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r1 = r1.mSecondary;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isSplitActive() {
        /*
            r1 = this;
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenTaskListener r1 = r1.mSplits
            android.app.ActivityManager$RunningTaskInfo r0 = r1.mPrimary
            if (r0 == 0) goto L_0x0014
            android.app.ActivityManager$RunningTaskInfo r1 = r1.mSecondary
            if (r1 == 0) goto L_0x0014
            int r0 = r0.topActivityType
            if (r0 != 0) goto L_0x0012
            int r1 = r1.topActivityType
            if (r1 == 0) goto L_0x0014
        L_0x0012:
            r1 = 1
            goto L_0x0015
        L_0x0014:
            r1 = 0
        L_0x0015:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController.isSplitActive():boolean");
    }

    public void addDivider(Configuration configuration) {
        int i;
        Context displayContext = this.mDisplayController.getDisplayContext(this.mContext.getDisplayId());
        DividerView dividerView = (DividerView) LayoutInflater.from(displayContext).inflate(C2219R.layout.docked_stack_divider, (ViewGroup) null);
        this.mView = dividerView;
        dividerView.setAnimationHandler(this.mSfVsyncAnimationHandler);
        DisplayLayout displayLayout = this.mDisplayController.getDisplayLayout(this.mContext.getDisplayId());
        this.mView.injectDependencies(this, this.mWindowManager, this.mDividerState, this.mForcedResizableController, this.mSplits, this.mSplitLayout, this.mImePositionProcessor, this.mWindowManagerProxy);
        boolean z = false;
        this.mView.setVisibility(this.mVisible ? 0 : 4);
        this.mView.setMinimizedDockStack(this.mMinimized, this.mHomeStackResizable, (SurfaceControl.Transaction) null);
        int dimensionPixelSize = displayContext.getResources().getDimensionPixelSize(17105206);
        if (configuration.orientation == 2) {
            z = true;
        }
        if (z) {
            i = dimensionPixelSize;
        } else {
            i = displayLayout.width();
        }
        if (z) {
            dimensionPixelSize = displayLayout.height();
        }
        this.mWindowManager.add(this.mView, i, dimensionPixelSize, this.mContext.getDisplayId());
    }

    public void removeDivider() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onDividerRemoved();
        }
        this.mWindowManager.remove();
    }

    public void update(Configuration configuration) {
        boolean z = this.mView != null && this.mIsKeyguardShowing;
        removeDivider();
        addDivider(configuration);
        if (this.mMinimized) {
            this.mView.setMinimizedDockStack(true, this.mHomeStackResizable, (SurfaceControl.Transaction) null);
            updateTouchable();
        }
        this.mView.setHidden(z);
    }

    public void onTaskVanished() {
        removeDivider();
    }

    public void updateVisibility(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            this.mView.setVisibility(z ? 0 : 4);
            if (z) {
                this.mView.enterSplitMode(this.mHomeStackResizable);
                this.mWindowManagerProxy.runInSync(new LegacySplitScreenController$$ExternalSyntheticLambda1(this));
            } else {
                this.mView.exitSplitMode();
                this.mWindowManagerProxy.runInSync(new LegacySplitScreenController$$ExternalSyntheticLambda2(this));
            }
            synchronized (this.mDockedStackExistsListeners) {
                this.mDockedStackExistsListeners.removeIf(new LegacySplitScreenController$$ExternalSyntheticLambda5(z));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateVisibility$1(SurfaceControl.Transaction transaction) {
        this.mView.setMinimizedDockStack(this.mMinimized, this.mHomeStackResizable, transaction);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateVisibility$2(SurfaceControl.Transaction transaction) {
        this.mView.setMinimizedDockStack(false, this.mHomeStackResizable, transaction);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateVisibility$3(boolean z, WeakReference weakReference) {
        Consumer consumer = (Consumer) weakReference.get();
        if (consumer != null) {
            consumer.accept(Boolean.valueOf(z));
        }
        return consumer == null;
    }

    public void setMinimized(boolean z) {
        this.mMainExecutor.execute(new LegacySplitScreenController$$ExternalSyntheticLambda3(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setMinimized$4(boolean z) {
        if (this.mVisible) {
            setHomeMinimized(z);
        }
    }

    public void setHomeMinimized(boolean z) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        int i = 0;
        boolean z2 = this.mMinimized != z;
        if (z2) {
            this.mMinimized = z;
        }
        windowContainerTransaction.setFocusable(this.mSplits.mPrimary.token, true ^ this.mMinimized);
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            if (dividerView.getDisplay() != null) {
                i = this.mView.getDisplay().getDisplayId();
            }
            if (this.mMinimized) {
                this.mImePositionProcessor.pause(i);
            }
            if (z2) {
                this.mView.setMinimizedDockStack(z, getAnimDuration(), this.mHomeStackResizable);
            }
            if (!this.mMinimized) {
                this.mImePositionProcessor.resume(i);
            }
        }
        updateTouchable();
        if (!this.mWindowManagerProxy.queueSyncTransactionIfWaiting(windowContainerTransaction)) {
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
    }

    public void setAdjustedForIme(boolean z) {
        if (this.mAdjustedForIme != z) {
            this.mAdjustedForIme = z;
            updateTouchable();
        }
    }

    public void updateTouchable() {
        this.mWindowManager.setTouchable(!this.mAdjustedForIme);
    }

    public void onUndockingTask() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onUndockingTask();
        }
    }

    public void onAppTransitionFinished() {
        if (this.mView != null) {
            this.mForcedResizableController.onAppTransitionFinished();
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print("  mVisible=");
        printWriter.println(this.mVisible);
        printWriter.print("  mMinimized=");
        printWriter.println(this.mMinimized);
        printWriter.print("  mAdjustedForIme=");
        printWriter.println(this.mAdjustedForIme);
    }

    public long getAnimDuration() {
        return (long) (Settings.Global.getFloat(this.mContext.getContentResolver(), "transition_animation_scale", this.mContext.getResources().getFloat(17105066)) * 336.0f);
    }

    public void registerInSplitScreenListener(Consumer<Boolean> consumer) {
        consumer.accept(Boolean.valueOf(isDividerVisible()));
        synchronized (this.mDockedStackExistsListeners) {
            this.mDockedStackExistsListeners.add(new WeakReference(consumer));
        }
    }

    public void registerBoundsChangeListener(BiConsumer<Rect, Rect> biConsumer) {
        synchronized (this.mBoundsChangedListeners) {
            this.mBoundsChangedListeners.add(new WeakReference(biConsumer));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
        r1 = (android.app.ActivityManager.RunningTaskInfo) r1.get(0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean splitPrimaryTask() {
        /*
            r5 = this;
            r0 = 0
            android.app.IActivityTaskManager r1 = android.app.ActivityTaskManager.getService()     // Catch:{ RemoteException -> 0x0073 }
            int r1 = r1.getLockTaskModeState()     // Catch:{ RemoteException -> 0x0073 }
            r2 = 2
            if (r1 != r2) goto L_0x000d
            return r0
        L_0x000d:
            boolean r1 = r5.isSplitActive()
            if (r1 != 0) goto L_0x0073
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenTaskListener r1 = r5.mSplits
            android.app.ActivityManager$RunningTaskInfo r1 = r1.mPrimary
            if (r1 != 0) goto L_0x001a
            goto L_0x0073
        L_0x001a:
            android.app.ActivityTaskManager r1 = android.app.ActivityTaskManager.getInstance()
            r3 = 1
            java.util.List r1 = r1.getTasks(r3)
            if (r1 == 0) goto L_0x0073
            boolean r4 = r1.isEmpty()
            if (r4 == 0) goto L_0x002c
            goto L_0x0073
        L_0x002c:
            java.lang.Object r1 = r1.get(r0)
            android.app.ActivityManager$RunningTaskInfo r1 = (android.app.ActivityManager.RunningTaskInfo) r1
            int r4 = r1.getActivityType()
            if (r4 == r2) goto L_0x0073
            r2 = 3
            if (r4 != r2) goto L_0x003c
            goto L_0x0073
        L_0x003c:
            android.content.res.Configuration r2 = r1.getConfiguration()
            android.app.WindowConfiguration r2 = r2.windowConfiguration
            boolean r2 = r2.isAlwaysOnTop()
            boolean r4 = r1.supportsSplitScreenMultiWindow
            if (r4 == 0) goto L_0x0068
            if (r2 == 0) goto L_0x004d
            goto L_0x0068
        L_0x004d:
            android.window.WindowContainerTransaction r2 = new android.window.WindowContainerTransaction
            r2.<init>()
            android.window.WindowContainerToken r4 = r1.token
            r2.setWindowingMode(r4, r0)
            android.window.WindowContainerToken r0 = r1.token
            com.android.wm.shell.legacysplitscreen.LegacySplitScreenTaskListener r1 = r5.mSplits
            android.app.ActivityManager$RunningTaskInfo r1 = r1.mPrimary
            android.window.WindowContainerToken r1 = r1.token
            r2.reparent(r0, r1, r3)
            com.android.wm.shell.legacysplitscreen.WindowManagerProxy r5 = r5.mWindowManagerProxy
            r5.applySyncTransaction(r2)
            return r3
        L_0x0068:
            android.content.Context r5 = r5.mContext
            int r1 = com.android.p011wm.shell.C2219R.string.dock_non_resizeble_failed_to_dock_text
            android.widget.Toast r5 = android.widget.Toast.makeText(r5, r1, r0)
            r5.show()
        L_0x0073:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController.splitPrimaryTask():boolean");
    }

    public void notifyBoundsChanged(Rect rect, Rect rect2) {
        synchronized (this.mBoundsChangedListeners) {
            this.mBoundsChangedListeners.removeIf(new LegacySplitScreenController$$ExternalSyntheticLambda4(rect, rect2));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$notifyBoundsChanged$5(Rect rect, Rect rect2, WeakReference weakReference) {
        BiConsumer biConsumer = (BiConsumer) weakReference.get();
        if (biConsumer != null) {
            biConsumer.accept(rect, rect2);
        }
        return biConsumer == null;
    }

    public void startEnterSplit() {
        update(this.mDisplayController.getDisplayContext(this.mContext.getDisplayId()).getResources().getConfiguration());
        WindowManagerProxy windowManagerProxy = this.mWindowManagerProxy;
        LegacySplitScreenTaskListener legacySplitScreenTaskListener = this.mSplits;
        LegacySplitDisplayLayout legacySplitDisplayLayout = this.mRotateSplitLayout;
        if (legacySplitDisplayLayout == null) {
            legacySplitDisplayLayout = this.mSplitLayout;
        }
        this.mHomeStackResizable = windowManagerProxy.applyEnterSplit(legacySplitScreenTaskListener, legacySplitDisplayLayout);
    }

    public void prepareEnterSplitTransition(WindowContainerTransaction windowContainerTransaction) {
        WindowManagerProxy windowManagerProxy = this.mWindowManagerProxy;
        LegacySplitScreenTaskListener legacySplitScreenTaskListener = this.mSplits;
        LegacySplitDisplayLayout legacySplitDisplayLayout = this.mRotateSplitLayout;
        if (legacySplitDisplayLayout == null) {
            legacySplitDisplayLayout = this.mSplitLayout;
        }
        this.mHomeStackResizable = windowManagerProxy.buildEnterSplit(windowContainerTransaction, legacySplitScreenTaskListener, legacySplitDisplayLayout);
    }

    public void finishEnterSplitTransition(boolean z) {
        update(this.mDisplayController.getDisplayContext(this.mContext.getDisplayId()).getResources().getConfiguration());
        if (z) {
            ensureMinimizedSplit();
        } else {
            ensureNormalSplit();
        }
    }

    public void startDismissSplit(boolean z) {
        startDismissSplit(z, false);
    }

    public void startDismissSplit(boolean z, boolean z2) {
        if (Transitions.ENABLE_SHELL_TRANSITIONS) {
            this.mSplits.getSplitTransitions().dismissSplit(this.mSplits, this.mSplitLayout, !z, z2);
            return;
        }
        this.mWindowManagerProxy.applyDismissSplit(this.mSplits, this.mSplitLayout, !z);
        onDismissSplit();
    }

    public void onDismissSplit() {
        updateVisibility(false);
        this.mMinimized = false;
        this.mDividerState.mRatioPositionBeforeMinimized = 0.0f;
        removeDivider();
        this.mImePositionProcessor.reset();
    }

    public void ensureMinimizedSplit() {
        setHomeMinimized(true);
        if (this.mView != null && !isDividerVisible()) {
            updateVisibility(true);
        }
    }

    public void ensureNormalSplit() {
        setHomeMinimized(false);
        if (this.mView != null && !isDividerVisible()) {
            updateVisibility(true);
        }
    }

    public LegacySplitDisplayLayout getSplitLayout() {
        return this.mSplitLayout;
    }

    public WindowManagerProxy getWmProxy() {
        return this.mWindowManagerProxy;
    }

    public WindowContainerToken getSecondaryRoot() {
        ActivityManager.RunningTaskInfo runningTaskInfo;
        LegacySplitScreenTaskListener legacySplitScreenTaskListener = this.mSplits;
        if (legacySplitScreenTaskListener == null || (runningTaskInfo = legacySplitScreenTaskListener.mSecondary) == null) {
            return null;
        }
        return runningTaskInfo.token;
    }

    /* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$SplitScreenImpl */
    private class SplitScreenImpl implements LegacySplitScreen {
        private SplitScreenImpl() {
        }

        public boolean isMinimized() {
            return LegacySplitScreenController.this.mMinimized;
        }

        public boolean isHomeStackResizable() {
            return LegacySplitScreenController.this.mHomeStackResizable;
        }

        public DividerView getDividerView() {
            return LegacySplitScreenController.this.getDividerView();
        }

        public boolean isDividerVisible() {
            boolean[] zArr = new boolean[1];
            try {
                LegacySplitScreenController.this.mMainExecutor.executeBlocking(new C2328x586845b1(this, zArr));
            } catch (InterruptedException unused) {
                Slog.e("SplitScreenCtrl", "Failed to get divider visible");
            }
            return zArr[0];
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$isDividerVisible$0(boolean[] zArr) {
            zArr[0] = LegacySplitScreenController.this.isDividerVisible();
        }

        public void onKeyguardVisibilityChanged(boolean z) {
            LegacySplitScreenController.this.mMainExecutor.execute(new C2325x586845ae(this, z));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onKeyguardVisibilityChanged$1(boolean z) {
            LegacySplitScreenController.this.onKeyguardVisibilityChanged(z);
        }

        public void setMinimized(boolean z) {
            LegacySplitScreenController.this.mMainExecutor.execute(new C2326x586845af(this, z));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setMinimized$2(boolean z) {
            LegacySplitScreenController.this.setMinimized(z);
        }

        public void onUndockingTask() {
            LegacySplitScreenController.this.mMainExecutor.execute(new C2321x586845aa(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onUndockingTask$3() {
            LegacySplitScreenController.this.onUndockingTask();
        }

        public void onAppTransitionFinished() {
            LegacySplitScreenController.this.mMainExecutor.execute(new C2322x586845ab(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAppTransitionFinished$4() {
            LegacySplitScreenController.this.onAppTransitionFinished();
        }

        public void registerInSplitScreenListener(Consumer<Boolean> consumer) {
            LegacySplitScreenController.this.mMainExecutor.execute(new C2324x586845ad(this, consumer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$registerInSplitScreenListener$5(Consumer consumer) {
            LegacySplitScreenController.this.registerInSplitScreenListener(consumer);
        }

        public void registerBoundsChangeListener(BiConsumer<Rect, Rect> biConsumer) {
            LegacySplitScreenController.this.mMainExecutor.execute(new C2323x586845ac(this, biConsumer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$registerBoundsChangeListener$7(BiConsumer biConsumer) {
            LegacySplitScreenController.this.registerBoundsChangeListener(biConsumer);
        }

        public boolean splitPrimaryTask() {
            boolean[] zArr = new boolean[1];
            try {
                LegacySplitScreenController.this.mMainExecutor.executeBlocking(new C2327x586845b0(this, zArr));
            } catch (InterruptedException unused) {
                Slog.e("SplitScreenCtrl", "Failed to split primary task");
            }
            return zArr[0];
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$splitPrimaryTask$9(boolean[] zArr) {
            zArr[0] = LegacySplitScreenController.this.splitPrimaryTask();
        }
    }
}
