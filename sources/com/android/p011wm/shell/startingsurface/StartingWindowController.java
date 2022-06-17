package com.android.p011wm.shell.startingsurface;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Trace;
import android.util.Slog;
import android.util.SparseIntArray;
import android.view.SurfaceControl;
import android.window.StartingWindowInfo;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.function.TriConsumer;
import com.android.p011wm.shell.common.ExecutorUtils;
import com.android.p011wm.shell.common.RemoteCallable;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.startingsurface.IStartingWindow;

/* renamed from: com.android.wm.shell.startingsurface.StartingWindowController */
public class StartingWindowController implements RemoteCallable<StartingWindowController> {
    public static final boolean DEBUG_SPLASH_SCREEN = Build.isDebuggable();
    /* access modifiers changed from: private */
    public static final String TAG = "StartingWindowController";
    private final Context mContext;
    private final StartingSurfaceImpl mImpl = new StartingSurfaceImpl();
    private final ShellExecutor mSplashScreenExecutor;
    /* access modifiers changed from: private */
    public final StartingSurfaceDrawer mStartingSurfaceDrawer;
    private final StartingWindowTypeAlgorithm mStartingWindowTypeAlgorithm;
    /* access modifiers changed from: private */
    @GuardedBy({"mTaskBackgroundColors"})
    public final SparseIntArray mTaskBackgroundColors = new SparseIntArray();
    private TriConsumer<Integer, Integer, Integer> mTaskLaunchingCallback;

    private static boolean isSplashScreenType(@StartingWindowInfo.StartingWindowType int i) {
        return i == 1 || i == 3 || i == 4;
    }

    public StartingWindowController(Context context, ShellExecutor shellExecutor, StartingWindowTypeAlgorithm startingWindowTypeAlgorithm, TransactionPool transactionPool) {
        this.mContext = context;
        this.mStartingSurfaceDrawer = new StartingSurfaceDrawer(context, shellExecutor, transactionPool);
        this.mStartingWindowTypeAlgorithm = startingWindowTypeAlgorithm;
        this.mSplashScreenExecutor = shellExecutor;
    }

    public StartingSurface asStartingSurface() {
        return this.mImpl;
    }

    public Context getContext() {
        return this.mContext;
    }

    public ShellExecutor getRemoteCallExecutor() {
        return this.mSplashScreenExecutor;
    }

    /* access modifiers changed from: package-private */
    public void setStartingWindowListener(TriConsumer<Integer, Integer, Integer> triConsumer) {
        this.mTaskLaunchingCallback = triConsumer;
    }

    public void addStartingWindow(StartingWindowInfo startingWindowInfo, IBinder iBinder) {
        this.mSplashScreenExecutor.execute(new StartingWindowController$$ExternalSyntheticLambda4(this, startingWindowInfo, iBinder));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addStartingWindow$0(StartingWindowInfo startingWindowInfo, IBinder iBinder) {
        Trace.traceBegin(32, "addStartingWindow");
        int suggestedWindowType = this.mStartingWindowTypeAlgorithm.getSuggestedWindowType(startingWindowInfo);
        ActivityManager.RunningTaskInfo runningTaskInfo = startingWindowInfo.taskInfo;
        if (isSplashScreenType(suggestedWindowType)) {
            this.mStartingSurfaceDrawer.addSplashScreenStartingWindow(startingWindowInfo, iBinder, suggestedWindowType);
        } else if (suggestedWindowType == 2) {
            this.mStartingSurfaceDrawer.makeTaskSnapshotWindow(startingWindowInfo, iBinder, startingWindowInfo.mTaskSnapshot);
        }
        if (suggestedWindowType != 0) {
            int i = runningTaskInfo.taskId;
            int startingWindowBackgroundColorForTask = this.mStartingSurfaceDrawer.getStartingWindowBackgroundColorForTask(i);
            if (startingWindowBackgroundColorForTask != 0) {
                synchronized (this.mTaskBackgroundColors) {
                    this.mTaskBackgroundColors.append(i, startingWindowBackgroundColorForTask);
                }
            }
            if (this.mTaskLaunchingCallback != null && isSplashScreenType(suggestedWindowType)) {
                this.mTaskLaunchingCallback.accept(Integer.valueOf(i), Integer.valueOf(suggestedWindowType), Integer.valueOf(startingWindowBackgroundColorForTask));
            }
        }
        Trace.traceEnd(32);
    }

    public void copySplashScreenView(int i) {
        this.mSplashScreenExecutor.execute(new StartingWindowController$$ExternalSyntheticLambda1(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$copySplashScreenView$1(int i) {
        this.mStartingSurfaceDrawer.copySplashScreenView(i);
    }

    public void onAppSplashScreenViewRemoved(int i) {
        this.mSplashScreenExecutor.execute(new StartingWindowController$$ExternalSyntheticLambda0(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAppSplashScreenViewRemoved$2(int i) {
        this.mStartingSurfaceDrawer.onAppSplashScreenViewRemoved(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeStartingWindow$3(int i, SurfaceControl surfaceControl, Rect rect, boolean z) {
        this.mStartingSurfaceDrawer.removeStartingWindow(i, surfaceControl, rect, z);
    }

    public void removeStartingWindow(int i, SurfaceControl surfaceControl, Rect rect, boolean z) {
        this.mSplashScreenExecutor.execute(new StartingWindowController$$ExternalSyntheticLambda3(this, i, surfaceControl, rect, z));
        this.mSplashScreenExecutor.executeDelayed(new StartingWindowController$$ExternalSyntheticLambda2(this, i), 5000);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeStartingWindow$4(int i) {
        synchronized (this.mTaskBackgroundColors) {
            this.mTaskBackgroundColors.delete(i);
        }
    }

    /* renamed from: com.android.wm.shell.startingsurface.StartingWindowController$StartingSurfaceImpl */
    private class StartingSurfaceImpl implements StartingSurface {
        private IStartingWindowImpl mIStartingWindow;

        private StartingSurfaceImpl() {
        }

        public IStartingWindowImpl createExternalInterface() {
            IStartingWindowImpl iStartingWindowImpl = this.mIStartingWindow;
            if (iStartingWindowImpl != null) {
                iStartingWindowImpl.invalidate();
            }
            IStartingWindowImpl iStartingWindowImpl2 = new IStartingWindowImpl(StartingWindowController.this);
            this.mIStartingWindow = iStartingWindowImpl2;
            return iStartingWindowImpl2;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x002c, code lost:
            if (r3 == 0) goto L_0x002f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            return com.android.p011wm.shell.startingsurface.SplashscreenContentDrawer.getSystemBGColor();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
            return r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0022, code lost:
            r3 = com.android.p011wm.shell.startingsurface.StartingWindowController.access$200(r3.this$0).estimateTaskBackgroundColor(r4);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getBackgroundColor(android.app.TaskInfo r4) {
            /*
                r3 = this;
                com.android.wm.shell.startingsurface.StartingWindowController r0 = com.android.p011wm.shell.startingsurface.StartingWindowController.this
                android.util.SparseIntArray r0 = r0.mTaskBackgroundColors
                monitor-enter(r0)
                com.android.wm.shell.startingsurface.StartingWindowController r1 = com.android.p011wm.shell.startingsurface.StartingWindowController.this     // Catch:{ all -> 0x0034 }
                android.util.SparseIntArray r1 = r1.mTaskBackgroundColors     // Catch:{ all -> 0x0034 }
                int r2 = r4.taskId     // Catch:{ all -> 0x0034 }
                int r1 = r1.indexOfKey(r2)     // Catch:{ all -> 0x0034 }
                if (r1 < 0) goto L_0x0021
                com.android.wm.shell.startingsurface.StartingWindowController r3 = com.android.p011wm.shell.startingsurface.StartingWindowController.this     // Catch:{ all -> 0x0034 }
                android.util.SparseIntArray r3 = r3.mTaskBackgroundColors     // Catch:{ all -> 0x0034 }
                int r3 = r3.valueAt(r1)     // Catch:{ all -> 0x0034 }
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                return r3
            L_0x0021:
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                com.android.wm.shell.startingsurface.StartingWindowController r3 = com.android.p011wm.shell.startingsurface.StartingWindowController.this
                com.android.wm.shell.startingsurface.StartingSurfaceDrawer r3 = r3.mStartingSurfaceDrawer
                int r3 = r3.estimateTaskBackgroundColor(r4)
                if (r3 == 0) goto L_0x002f
                goto L_0x0033
            L_0x002f:
                int r3 = com.android.p011wm.shell.startingsurface.SplashscreenContentDrawer.getSystemBGColor()
            L_0x0033:
                return r3
            L_0x0034:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0034 }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.startingsurface.StartingWindowController.StartingSurfaceImpl.getBackgroundColor(android.app.TaskInfo):int");
        }
    }

    /* renamed from: com.android.wm.shell.startingsurface.StartingWindowController$IStartingWindowImpl */
    private static class IStartingWindowImpl extends IStartingWindow.Stub {
        /* access modifiers changed from: private */
        public StartingWindowController mController;
        /* access modifiers changed from: private */
        public IStartingWindowListener mListener;
        private final IBinder.DeathRecipient mListenerDeathRecipient = new IBinder.DeathRecipient() {
            public void binderDied() {
                StartingWindowController access$300 = IStartingWindowImpl.this.mController;
                access$300.getRemoteCallExecutor().execute(new C2408xf68ffddd(this, access$300));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$binderDied$0(StartingWindowController startingWindowController) {
                IStartingWindowListener unused = IStartingWindowImpl.this.mListener = null;
                startingWindowController.setStartingWindowListener((TriConsumer<Integer, Integer, Integer>) null);
            }
        };
        private final TriConsumer<Integer, Integer, Integer> mStartingWindowListener = new C2406x795f7bd0(this);

        public IStartingWindowImpl(StartingWindowController startingWindowController) {
            this.mController = startingWindowController;
        }

        /* access modifiers changed from: package-private */
        public void invalidate() {
            this.mController = null;
        }

        public void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) {
            ExecutorUtils.executeRemoteCallWithTaskPermission(this.mController, "setStartingWindowListener", new C2407x795f7bd1(this, iStartingWindowListener));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setStartingWindowListener$0(IStartingWindowListener iStartingWindowListener, StartingWindowController startingWindowController) {
            IStartingWindowListener iStartingWindowListener2 = this.mListener;
            if (iStartingWindowListener2 != null) {
                iStartingWindowListener2.asBinder().unlinkToDeath(this.mListenerDeathRecipient, 0);
            }
            if (iStartingWindowListener != null) {
                try {
                    iStartingWindowListener.asBinder().linkToDeath(this.mListenerDeathRecipient, 0);
                } catch (RemoteException unused) {
                    Slog.e(StartingWindowController.TAG, "Failed to link to death");
                    return;
                }
            }
            this.mListener = iStartingWindowListener;
            startingWindowController.setStartingWindowListener(this.mStartingWindowListener);
        }

        /* access modifiers changed from: private */
        public void notifyIStartingWindowListener(int i, int i2, int i3) {
            IStartingWindowListener iStartingWindowListener = this.mListener;
            if (iStartingWindowListener != null) {
                try {
                    iStartingWindowListener.onTaskLaunching(i, i2, i3);
                } catch (RemoteException e) {
                    Slog.e(StartingWindowController.TAG, "Failed to notify task launching", e);
                }
            }
        }
    }
}
