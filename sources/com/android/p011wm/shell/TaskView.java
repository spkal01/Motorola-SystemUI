package com.android.p011wm.shell;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.CloseGuard;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.p011wm.shell.ShellTaskOrganizer;
import java.io.PrintWriter;
import java.util.concurrent.Executor;

/* renamed from: com.android.wm.shell.TaskView */
public class TaskView extends SurfaceView implements SurfaceHolder.Callback, ShellTaskOrganizer.TaskListener, ViewTreeObserver.OnComputeInternalInsetsListener {
    private final CloseGuard mGuard;
    private boolean mIsInitialized;
    private Listener mListener;
    private Executor mListenerExecutor;
    private Rect mObscuredTouchRect;
    private final Executor mShellExecutor;
    private boolean mSurfaceCreated;
    private ActivityManager.RunningTaskInfo mTaskInfo;
    private SurfaceControl mTaskLeash;
    private final ShellTaskOrganizer mTaskOrganizer;
    private WindowContainerToken mTaskToken;
    private final int[] mTmpLocation = new int[2];
    private final Rect mTmpRect = new Rect();
    private final Rect mTmpRootRect = new Rect();
    private final SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();

    /* renamed from: com.android.wm.shell.TaskView$Listener */
    public interface Listener {
        void onBackPressedOnTaskRoot(int i) {
        }

        void onInitialized() {
        }

        void onReleased() {
        }

        void onTaskCreated(int i, ComponentName componentName) {
        }

        void onTaskRemovalStarted(int i) {
        }

        void onTaskVisibilityChanged(int i, boolean z) {
        }
    }

    public TaskView(Context context, ShellTaskOrganizer shellTaskOrganizer) {
        super(context, (AttributeSet) null, 0, 0, true);
        CloseGuard closeGuard = new CloseGuard();
        this.mGuard = closeGuard;
        this.mTaskOrganizer = shellTaskOrganizer;
        this.mShellExecutor = shellTaskOrganizer.getExecutor();
        setUseAlpha();
        getHolder().addCallback(this);
        closeGuard.open("release");
    }

    public void setListener(Executor executor, Listener listener) {
        if (this.mListener == null) {
            this.mListener = listener;
            this.mListenerExecutor = executor;
            return;
        }
        throw new IllegalStateException("Trying to set a listener when one has already been set");
    }

    public void startShortcutActivity(ShortcutInfo shortcutInfo, ActivityOptions activityOptions, Rect rect) {
        prepareActivityOptions(activityOptions, rect);
        try {
            ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).startShortcut(shortcutInfo, (Rect) null, activityOptions.toBundle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startActivity(PendingIntent pendingIntent, Intent intent, ActivityOptions activityOptions, Rect rect) {
        prepareActivityOptions(activityOptions, rect);
        try {
            pendingIntent.send(this.mContext, 0, intent, (PendingIntent.OnFinished) null, (Handler) null, (String) null, activityOptions.toBundle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareActivityOptions(ActivityOptions activityOptions, Rect rect) {
        Binder binder = new Binder();
        this.mShellExecutor.execute(new TaskView$$ExternalSyntheticLambda9(this, binder));
        activityOptions.setLaunchBounds(rect);
        activityOptions.setLaunchCookie(binder);
        activityOptions.setLaunchWindowingMode(6);
        activityOptions.setRemoveWithTaskOrganizer(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareActivityOptions$0(Binder binder) {
        this.mTaskOrganizer.setPendingLaunchCookieListener(binder, this);
    }

    public void setObscuredTouchRect(Rect rect) {
        this.mObscuredTouchRect = rect;
    }

    public void onLocationChanged() {
        if (this.mTaskToken != null) {
            getBoundsOnScreen(this.mTmpRect);
            getRootView().getBoundsOnScreen(this.mTmpRootRect);
            if (!this.mTmpRootRect.contains(this.mTmpRect)) {
                this.mTmpRect.offsetTo(0, 0);
            }
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            windowContainerTransaction.setBounds(this.mTaskToken, this.mTmpRect);
            this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        }
    }

    public void release() {
        performRelease();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
                performRelease();
            }
        } finally {
            super.finalize();
        }
    }

    private void performRelease() {
        getHolder().removeCallback(this);
        this.mShellExecutor.execute(new TaskView$$ExternalSyntheticLambda4(this));
        this.mGuard.close();
        if (this.mListener != null && this.mIsInitialized) {
            this.mListenerExecutor.execute(new TaskView$$ExternalSyntheticLambda0(this));
            this.mIsInitialized = false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performRelease$1() {
        this.mTaskOrganizer.removeListener(this);
        resetTaskInfo();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performRelease$2() {
        this.mListener.onReleased();
    }

    private void resetTaskInfo() {
        this.mTaskInfo = null;
        this.mTaskToken = null;
        this.mTaskLeash = null;
    }

    private void updateTaskVisibility() {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        windowContainerTransaction.setHidden(this.mTaskToken, !this.mSurfaceCreated);
        this.mTaskOrganizer.applyTransaction(windowContainerTransaction);
        if (this.mListener != null) {
            this.mListenerExecutor.execute(new TaskView$$ExternalSyntheticLambda6(this, this.mTaskInfo.taskId));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateTaskVisibility$3(int i) {
        this.mListener.onTaskVisibilityChanged(i, this.mSurfaceCreated);
    }

    public void onTaskAppeared(ActivityManager.RunningTaskInfo runningTaskInfo, SurfaceControl surfaceControl) {
        this.mTaskInfo = runningTaskInfo;
        this.mTaskToken = runningTaskInfo.token;
        this.mTaskLeash = surfaceControl;
        if (this.mSurfaceCreated) {
            this.mTransaction.reparent(surfaceControl, getSurfaceControl()).show(this.mTaskLeash).apply();
        } else {
            updateTaskVisibility();
        }
        this.mTaskOrganizer.setInterceptBackPressedOnTaskRoot(this.mTaskToken, true);
        onLocationChanged();
        ActivityManager.TaskDescription taskDescription = runningTaskInfo.taskDescription;
        if (taskDescription != null) {
            setResizeBackgroundColor(taskDescription.getBackgroundColor());
        }
        if (this.mListener != null) {
            this.mListenerExecutor.execute(new TaskView$$ExternalSyntheticLambda8(this, runningTaskInfo.taskId, runningTaskInfo.baseActivity));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskAppeared$4(int i, ComponentName componentName) {
        this.mListener.onTaskCreated(i, componentName);
    }

    public void onTaskVanished(ActivityManager.RunningTaskInfo runningTaskInfo) {
        WindowContainerToken windowContainerToken = this.mTaskToken;
        if (windowContainerToken != null && windowContainerToken.equals(runningTaskInfo.token)) {
            if (this.mListener != null) {
                this.mListenerExecutor.execute(new TaskView$$ExternalSyntheticLambda7(this, runningTaskInfo.taskId));
            }
            this.mTaskOrganizer.setInterceptBackPressedOnTaskRoot(this.mTaskToken, false);
            this.mTransaction.reparent(this.mTaskLeash, (SurfaceControl) null).apply();
            resetTaskInfo();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskVanished$5(int i) {
        this.mListener.onTaskRemovalStarted(i);
    }

    public void onTaskInfoChanged(ActivityManager.RunningTaskInfo runningTaskInfo) {
        ActivityManager.TaskDescription taskDescription = runningTaskInfo.taskDescription;
        if (taskDescription != null) {
            setResizeBackgroundColor(taskDescription.getBackgroundColor());
        }
    }

    public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo runningTaskInfo) {
        WindowContainerToken windowContainerToken = this.mTaskToken;
        if (windowContainerToken != null && windowContainerToken.equals(runningTaskInfo.token) && this.mListener != null) {
            this.mListenerExecutor.execute(new TaskView$$ExternalSyntheticLambda5(this, runningTaskInfo.taskId));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBackPressedOnTaskRoot$6(int i) {
        this.mListener.onBackPressedOnTaskRoot(i);
    }

    public void attachChildSurfaceToTask(int i, SurfaceControl.Builder builder) {
        if (this.mTaskInfo.taskId == i) {
            builder.setParent(this.mTaskLeash);
            return;
        }
        throw new IllegalArgumentException("There is no surface for taskId=" + i);
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("  ");
        printWriter.println(str + this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TaskView:");
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mTaskInfo;
        sb.append(runningTaskInfo != null ? Integer.valueOf(runningTaskInfo.taskId) : "null");
        return sb.toString();
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mSurfaceCreated = true;
        if (this.mListener != null && !this.mIsInitialized) {
            this.mIsInitialized = true;
            this.mListenerExecutor.execute(new TaskView$$ExternalSyntheticLambda1(this));
        }
        this.mShellExecutor.execute(new TaskView$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$surfaceCreated$7() {
        this.mListener.onInitialized();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$surfaceCreated$8() {
        if (this.mTaskToken != null) {
            this.mTransaction.reparent(this.mTaskLeash, getSurfaceControl()).show(this.mTaskLeash).apply();
            updateTaskVisibility();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (this.mTaskToken != null) {
            onLocationChanged();
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mSurfaceCreated = false;
        this.mShellExecutor.execute(new TaskView$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$surfaceDestroyed$9() {
        if (this.mTaskToken != null) {
            this.mTransaction.reparent(this.mTaskLeash, (SurfaceControl) null).apply();
            updateTaskVisibility();
        }
    }

    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
        if (internalInsetsInfo.touchableRegion.isEmpty()) {
            internalInsetsInfo.setTouchableInsets(3);
            View rootView = getRootView();
            rootView.getLocationInWindow(this.mTmpLocation);
            Rect rect = this.mTmpRootRect;
            int[] iArr = this.mTmpLocation;
            rect.set(iArr[0], iArr[1], rootView.getWidth(), rootView.getHeight());
            internalInsetsInfo.touchableRegion.set(this.mTmpRootRect);
        }
        getLocationInWindow(this.mTmpLocation);
        Rect rect2 = this.mTmpRect;
        int[] iArr2 = this.mTmpLocation;
        rect2.set(iArr2[0], iArr2[1], iArr2[0] + getWidth(), this.mTmpLocation[1] + getHeight());
        internalInsetsInfo.touchableRegion.op(this.mTmpRect, Region.Op.DIFFERENCE);
        Rect rect3 = this.mObscuredTouchRect;
        if (rect3 != null) {
            internalInsetsInfo.touchableRegion.union(rect3);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnComputeInternalInsetsListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
    }
}
