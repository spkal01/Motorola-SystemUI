package com.android.p011wm.shell.pip.p012tv;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.RemoteAction;
import android.app.TaskInfo;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.WindowManagerShellWrapper;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.TaskStackListenerCallback;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.pip.PinnedStackListenerForwarder;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.pip.PipBoundsAlgorithm;
import com.android.p011wm.shell.pip.PipBoundsState;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipTaskOrganizer;
import com.android.p011wm.shell.pip.PipTransitionController;
import com.android.p011wm.shell.pip.p012tv.TvPipMenuController;
import com.android.p011wm.shell.pip.p012tv.TvPipNotificationController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.tv.TvPipController */
public class TvPipController implements PipTransitionController.PipTransitionCallback, TvPipMenuController.Delegate, TvPipNotificationController.Delegate {
    private final Context mContext;
    private final TvPipImpl mImpl = new TvPipImpl();
    /* access modifiers changed from: private */
    public final ShellExecutor mMainExecutor;
    private int mPinnedTaskId = -1;
    private final PipBoundsAlgorithm mPipBoundsAlgorithm;
    /* access modifiers changed from: private */
    public final PipBoundsState mPipBoundsState;
    private final PipMediaController mPipMediaController;
    private final TvPipNotificationController mPipNotificationController;
    private final PipTaskOrganizer mPipTaskOrganizer;
    private int mResizeAnimationDuration;
    /* access modifiers changed from: private */
    public int mState = 0;
    /* access modifiers changed from: private */
    public final TvPipMenuController mTvPipMenuController;

    public static Pip create(Context context, PipBoundsState pipBoundsState, PipBoundsAlgorithm pipBoundsAlgorithm, PipTaskOrganizer pipTaskOrganizer, PipTransitionController pipTransitionController, TvPipMenuController tvPipMenuController, PipMediaController pipMediaController, TvPipNotificationController tvPipNotificationController, TaskStackListenerImpl taskStackListenerImpl, WindowManagerShellWrapper windowManagerShellWrapper, ShellExecutor shellExecutor) {
        return new TvPipController(context, pipBoundsState, pipBoundsAlgorithm, pipTaskOrganizer, pipTransitionController, tvPipMenuController, pipMediaController, tvPipNotificationController, taskStackListenerImpl, windowManagerShellWrapper, shellExecutor).mImpl;
    }

    private TvPipController(Context context, PipBoundsState pipBoundsState, PipBoundsAlgorithm pipBoundsAlgorithm, PipTaskOrganizer pipTaskOrganizer, PipTransitionController pipTransitionController, TvPipMenuController tvPipMenuController, PipMediaController pipMediaController, TvPipNotificationController tvPipNotificationController, TaskStackListenerImpl taskStackListenerImpl, WindowManagerShellWrapper windowManagerShellWrapper, ShellExecutor shellExecutor) {
        this.mContext = context;
        this.mMainExecutor = shellExecutor;
        this.mPipBoundsState = pipBoundsState;
        pipBoundsState.setDisplayId(context.getDisplayId());
        pipBoundsState.setDisplayLayout(new DisplayLayout(context, context.getDisplay()));
        this.mPipBoundsAlgorithm = pipBoundsAlgorithm;
        this.mPipMediaController = pipMediaController;
        this.mPipNotificationController = tvPipNotificationController;
        tvPipNotificationController.setDelegate(this);
        this.mTvPipMenuController = tvPipMenuController;
        tvPipMenuController.setDelegate(this);
        this.mPipTaskOrganizer = pipTaskOrganizer;
        pipTransitionController.registerPipTransitionCallback(this);
        loadConfigurations();
        registerTaskStackListenerCallback(taskStackListenerImpl);
        registerWmShellPinnedStackListener(windowManagerShellWrapper);
    }

    /* access modifiers changed from: private */
    public void onConfigurationChanged(Configuration configuration) {
        Log.d("TvPipController", "onConfigurationChanged(), state=" + stateToName(this.mState));
        if (isPipShown()) {
            Log.d("TvPipController", "  > closing Pip.");
            closePip();
        }
        loadConfigurations();
        this.mPipNotificationController.onConfigurationChanged(this.mContext);
    }

    private boolean isPipShown() {
        return this.mState != 0;
    }

    public void showPictureInPictureMenu() {
        Log.d("TvPipController", "showPictureInPictureMenu(), state=" + stateToName(this.mState));
        if (this.mState != 1) {
            Log.d("TvPipController", "  > cannot open Menu from the current state.");
            return;
        }
        setState(2);
        resizePinnedStack(2);
    }

    public void movePipToNormalPosition() {
        Log.d("TvPipController", "movePipToNormalPosition(), state=" + stateToName(this.mState));
        setState(1);
        resizePinnedStack(1);
    }

    public void movePipToFullscreen() {
        Log.d("TvPipController", "movePipToFullscreen(), state=" + stateToName(this.mState));
        this.mPipTaskOrganizer.exitPip(this.mResizeAnimationDuration);
        onPipDisappeared();
    }

    public void closePip() {
        Log.d("TvPipController", "closePip(), state=" + stateToName(this.mState));
        removeTask(this.mPinnedTaskId);
        onPipDisappeared();
    }

    /* access modifiers changed from: private */
    public void resizePinnedStack(int i) {
        Rect rect;
        if (i == this.mState) {
            Log.d("TvPipController", "resizePinnedStack() state=" + stateToName(this.mState));
            int i2 = this.mState;
            if (i2 == 1) {
                rect = this.mPipBoundsAlgorithm.getNormalBounds();
            } else if (i2 == 2) {
                rect = this.mPipBoundsState.getExpandedBounds();
            } else {
                return;
            }
            this.mPipTaskOrganizer.scheduleAnimateResizePip(rect, this.mResizeAnimationDuration, (Consumer<Rect>) null);
            return;
        }
        throw new IllegalArgumentException("The passed state should match the current state!");
    }

    /* access modifiers changed from: private */
    public void registerSessionListenerForCurrentUser() {
        this.mPipMediaController.registerSessionListenerForCurrentUser();
    }

    /* access modifiers changed from: private */
    public void checkIfPinnedTaskAppeared() {
        TaskInfo pinnedTaskInfo = getPinnedTaskInfo();
        Log.d("TvPipController", "checkIfPinnedTaskAppeared(), task=" + pinnedTaskInfo);
        if (pinnedTaskInfo != null && pinnedTaskInfo.topActivity != null) {
            this.mPinnedTaskId = pinnedTaskInfo.taskId;
            setState(1);
            this.mPipMediaController.onActivityPinned();
            this.mPipNotificationController.show(pinnedTaskInfo.topActivity.getPackageName());
        }
    }

    /* access modifiers changed from: private */
    public void checkIfPinnedTaskIsGone() {
        Log.d("TvPipController", "onTaskStackChanged()");
        if (isPipShown() && getPinnedTaskInfo() == null) {
            Log.w("TvPipController", "Pinned task is gone.");
            onPipDisappeared();
        }
    }

    private void onPipDisappeared() {
        Log.d("TvPipController", "onPipDisappeared() state=" + stateToName(this.mState));
        this.mPipNotificationController.dismiss();
        this.mTvPipMenuController.hideMenu();
        setState(0);
        this.mPinnedTaskId = -1;
    }

    public void onPipTransitionStarted(int i, Rect rect) {
        Log.d("TvPipController", "onPipTransition_Started(), state=" + stateToName(this.mState));
    }

    public void onPipTransitionCanceled(int i) {
        Log.d("TvPipController", "onPipTransition_Canceled(), state=" + stateToName(this.mState));
    }

    public void onPipTransitionFinished(int i) {
        Log.d("TvPipController", "onPipTransition_Finished(), state=" + stateToName(this.mState));
        if (this.mState == 2) {
            Log.d("TvPipController", "  > show menu");
            this.mTvPipMenuController.showMenu();
        }
    }

    private void setState(int i) {
        Log.d("TvPipController", "setState(), state=" + stateToName(i) + ", prev=" + stateToName(this.mState));
        this.mState = i;
    }

    private void loadConfigurations() {
        Resources resources = this.mContext.getResources();
        this.mResizeAnimationDuration = resources.getInteger(C2219R.integer.config_pipResizeAnimationDuration);
        this.mPipBoundsState.setExpandedBounds(Rect.unflattenFromString(resources.getString(C2219R.string.pip_menu_bounds)));
    }

    private void registerTaskStackListenerCallback(TaskStackListenerImpl taskStackListenerImpl) {
        taskStackListenerImpl.addListener(new TaskStackListenerCallback() {
            public void onActivityPinned(String str, int i, int i2, int i3) {
                TvPipController.this.checkIfPinnedTaskAppeared();
            }

            public void onTaskStackChanged() {
                TvPipController.this.checkIfPinnedTaskIsGone();
            }

            public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo runningTaskInfo, boolean z, boolean z2, boolean z3) {
                if (runningTaskInfo.getWindowingMode() == 2) {
                    Log.d("TvPipController", "onPinnedActivityRestartAttempt()");
                    TvPipController.this.movePipToFullscreen();
                }
            }
        });
    }

    private void registerWmShellPinnedStackListener(WindowManagerShellWrapper windowManagerShellWrapper) {
        try {
            windowManagerShellWrapper.addPinnedStackListener(new PinnedStackListenerForwarder.PinnedTaskListener() {
                public void onMovementBoundsChanged(boolean z) {
                }

                public void onImeVisibilityChanged(boolean z, int i) {
                    Log.d("TvPipController", "onImeVisibilityChanged(), visible=" + z + ", height=" + i);
                    if (z != TvPipController.this.mPipBoundsState.isImeShowing() || (z && i != TvPipController.this.mPipBoundsState.getImeHeight())) {
                        TvPipController.this.mPipBoundsState.setImeVisibility(z, i);
                        if (TvPipController.this.mState == 1) {
                            TvPipController.this.resizePinnedStack(1);
                        }
                    }
                }

                public void onActionsChanged(ParceledListSlice<RemoteAction> parceledListSlice) {
                    Log.d("TvPipController", "onActionsChanged()");
                    TvPipController.this.mTvPipMenuController.setAppActions(parceledListSlice);
                }
            });
        } catch (RemoteException e) {
            Log.e("TvPipController", "Failed to register pinned stack listener", e);
        }
    }

    private static TaskInfo getPinnedTaskInfo() {
        Log.d("TvPipController", "getPinnedTaskInfo()");
        try {
            ActivityTaskManager.RootTaskInfo rootTaskInfo = ActivityTaskManager.getService().getRootTaskInfo(2, 0);
            Log.d("TvPipController", "  > taskInfo=" + rootTaskInfo);
            return rootTaskInfo;
        } catch (RemoteException e) {
            Log.e("TvPipController", "getRootTaskInfo() failed", e);
            return null;
        }
    }

    private static void removeTask(int i) {
        Log.d("TvPipController", "removeTask(), taskId=" + i);
        try {
            ActivityTaskManager.getService().removeTask(i);
        } catch (Exception e) {
            Log.e("TvPipController", "Atm.removeTask() failed", e);
        }
    }

    private static String stateToName(int i) {
        if (i == 0) {
            return "NO_PIP";
        }
        if (i == 1) {
            return "PIP";
        }
        if (i == 2) {
            return "PIP_MENU";
        }
        throw new IllegalArgumentException("Unknown state " + i);
    }

    /* renamed from: com.android.wm.shell.pip.tv.TvPipController$TvPipImpl */
    private class TvPipImpl implements Pip {
        private TvPipImpl() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            TvPipController.this.mMainExecutor.execute(new TvPipController$TvPipImpl$$ExternalSyntheticLambda1(this, configuration));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onConfigurationChanged$0(Configuration configuration) {
            TvPipController.this.onConfigurationChanged(configuration);
        }

        public void registerSessionListenerForCurrentUser() {
            TvPipController.this.mMainExecutor.execute(new TvPipController$TvPipImpl$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$registerSessionListenerForCurrentUser$1() {
            TvPipController.this.registerSessionListenerForCurrentUser();
        }
    }
}
