package com.android.p011wm.shell.startingsurface;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.ActivityThread;
import android.app.TaskInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Choreographer;
import android.view.Display;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.window.SplashScreenView;
import android.window.StartingWindowInfo;
import android.window.TaskSnapshot;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.TransactionPool;
import java.util.function.Supplier;

/* renamed from: com.android.wm.shell.startingsurface.StartingSurfaceDrawer */
public class StartingSurfaceDrawer {
    static final boolean DEBUG_SPLASH_SCREEN = StartingWindowController.DEBUG_SPLASH_SCREEN;
    static final String TAG = "StartingSurfaceDrawer";
    private final SparseArray<SurfaceControlViewHost> mAnimatedSplashScreenSurfaceHosts = new SparseArray<>(1);
    private Choreographer mChoreographer;
    private final Context mContext;
    private final DisplayManager mDisplayManager;
    private final ShellExecutor mSplashScreenExecutor;
    @VisibleForTesting
    final SplashscreenContentDrawer mSplashscreenContentDrawer;
    private final SparseArray<StartingWindowRecord> mStartingWindowRecords = new SparseArray<>();

    public StartingSurfaceDrawer(Context context, ShellExecutor shellExecutor, TransactionPool transactionPool) {
        this.mContext = context;
        this.mDisplayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mSplashScreenExecutor = shellExecutor;
        this.mSplashscreenContentDrawer = new SplashscreenContentDrawer(context, transactionPool);
        shellExecutor.execute(new StartingSurfaceDrawer$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mChoreographer = Choreographer.getInstance();
    }

    private Context getDisplayContext(Context context, int i) {
        if (i == 0) {
            return context;
        }
        Display display = this.mDisplayManager.getDisplay(i);
        if (display == null) {
            return null;
        }
        return context.createDisplayContext(display);
    }

    private int getSplashScreenTheme(int i, ActivityInfo activityInfo) {
        if (i != 0) {
            return i;
        }
        if (activityInfo.getThemeResource() != 0) {
            return activityInfo.getThemeResource();
        }
        return 16974563;
    }

    /* access modifiers changed from: package-private */
    public void addSplashScreenStartingWindow(StartingWindowInfo startingWindowInfo, IBinder iBinder, @StartingWindowInfo.StartingWindowType int i) {
        StartingWindowInfo startingWindowInfo2 = startingWindowInfo;
        int i2 = i;
        ActivityManager.RunningTaskInfo runningTaskInfo = startingWindowInfo2.taskInfo;
        ActivityInfo activityInfo = startingWindowInfo2.targetActivityInfo;
        if (activityInfo == null) {
            activityInfo = runningTaskInfo.topActivityInfo;
        }
        ActivityInfo activityInfo2 = activityInfo;
        if (activityInfo2 != null && activityInfo2.packageName != null) {
            int i3 = runningTaskInfo.displayId;
            int i4 = runningTaskInfo.taskId;
            Context context = this.mContext;
            int splashScreenTheme = getSplashScreenTheme(startingWindowInfo2.splashScreenThemeResId, activityInfo2);
            boolean z = DEBUG_SPLASH_SCREEN;
            if (z) {
                Slog.d(TAG, "addSplashScreen " + activityInfo2.packageName + " theme=" + Integer.toHexString(splashScreenTheme) + " task=" + runningTaskInfo.taskId + " suggestType=" + i2);
            }
            Context displayContext = getDisplayContext(context, i3);
            if (displayContext != null) {
                if (splashScreenTheme != displayContext.getThemeResId()) {
                    try {
                        displayContext = displayContext.createPackageContextAsUser(activityInfo2.packageName, 4, UserHandle.of(runningTaskInfo.userId));
                        displayContext.setTheme(splashScreenTheme);
                    } catch (PackageManager.NameNotFoundException e) {
                        Slog.w(TAG, "Failed creating package context with package name " + activityInfo2.packageName + " for user " + runningTaskInfo.userId, e);
                        return;
                    }
                }
                Configuration configuration = runningTaskInfo.getConfiguration();
                if (configuration.diffPublicOnly(displayContext.getResources().getConfiguration()) != 0) {
                    if (z) {
                        Slog.d(TAG, "addSplashScreen: creating context based on task Configuration " + configuration + " for splash screen");
                    }
                    Context createConfigurationContext = displayContext.createConfigurationContext(configuration);
                    createConfigurationContext.setTheme(splashScreenTheme);
                    TypedArray obtainStyledAttributes = createConfigurationContext.obtainStyledAttributes(R.styleable.Window);
                    int resourceId = obtainStyledAttributes.getResourceId(1, 0);
                    if (resourceId != 0) {
                        try {
                            if (createConfigurationContext.getDrawable(resourceId) != null) {
                                if (z) {
                                    Slog.d(TAG, "addSplashScreen: apply overrideConfig" + configuration + " to starting window resId=" + resourceId);
                                }
                                displayContext = createConfigurationContext;
                            }
                        } catch (Resources.NotFoundException e2) {
                            Slog.w(TAG, "failed creating starting window for overrideConfig at taskId: " + i4, e2);
                            return;
                        }
                    }
                    obtainStyledAttributes.recycle();
                }
                Context context2 = displayContext;
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(3);
                layoutParams.setFitInsetsSides(0);
                layoutParams.setFitInsetsTypes(0);
                layoutParams.format = -3;
                int i5 = 16843008;
                TypedArray obtainStyledAttributes2 = context2.obtainStyledAttributes(R.styleable.Window);
                if (obtainStyledAttributes2.getBoolean(14, false)) {
                    i5 = 17891584;
                }
                if (i2 != 4 || obtainStyledAttributes2.getBoolean(33, false)) {
                    i5 |= Integer.MIN_VALUE;
                }
                layoutParams.layoutInDisplayCutoutMode = obtainStyledAttributes2.getInt(50, layoutParams.layoutInDisplayCutoutMode);
                layoutParams.windowAnimations = obtainStyledAttributes2.getResourceId(8, 0);
                obtainStyledAttributes2.recycle();
                if (i3 == 0 && startingWindowInfo2.isKeyguardOccluded) {
                    i5 |= 524288;
                }
                layoutParams.flags = 131096 | i5;
                layoutParams.token = iBinder;
                layoutParams.packageName = activityInfo2.packageName;
                int i6 = layoutParams.privateFlags | 16;
                layoutParams.privateFlags = i6;
                layoutParams.privateFlags = i6 | 536870912;
                if (!context2.getResources().getCompatibilityInfo().supportsScreen()) {
                    layoutParams.privateFlags |= 128;
                }
                layoutParams.setTitle("Splash Screen " + activityInfo2.packageName);
                SplashScreenViewSupplier splashScreenViewSupplier = new SplashScreenViewSupplier();
                FrameLayout frameLayout = new FrameLayout(context2);
                frameLayout.setPadding(0, 0, 0, 0);
                frameLayout.setFitsSystemWindows(false);
                FrameLayout frameLayout2 = frameLayout;
                StartingSurfaceDrawer$$ExternalSyntheticLambda5 startingSurfaceDrawer$$ExternalSyntheticLambda5 = new StartingSurfaceDrawer$$ExternalSyntheticLambda5(this, splashScreenViewSupplier, i4, iBinder, frameLayout);
                this.mSplashscreenContentDrawer.createContentView(context2, i, activityInfo2, i4, new StartingSurfaceDrawer$$ExternalSyntheticLambda7(splashScreenViewSupplier));
                try {
                    if (addWindow(i4, iBinder, frameLayout2, (WindowManager) context2.getSystemService(WindowManager.class), layoutParams, i)) {
                        this.mChoreographer.postCallback(2, startingSurfaceDrawer$$ExternalSyntheticLambda5, (Object) null);
                        int unused = this.mStartingWindowRecords.get(i4).mBGColor = splashScreenViewSupplier.get().getInitBackgroundColor();
                    }
                } catch (RuntimeException e3) {
                    Slog.w(TAG, "failed creating starting window at taskId: " + i4, e3);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addSplashScreenStartingWindow$1(SplashScreenViewSupplier splashScreenViewSupplier, int i, IBinder iBinder, FrameLayout frameLayout) {
        Trace.traceBegin(32, "addSplashScreenView");
        SplashScreenView splashScreenView = splashScreenViewSupplier.get();
        StartingWindowRecord startingWindowRecord = this.mStartingWindowRecords.get(i);
        if (startingWindowRecord != null && iBinder == startingWindowRecord.mAppToken) {
            if (splashScreenView != null) {
                try {
                    frameLayout.addView(splashScreenView);
                } catch (RuntimeException e) {
                    String str = TAG;
                    Slog.w(str, "failed set content view to starting window at taskId: " + i, e);
                    splashScreenView = null;
                }
            }
            startingWindowRecord.setSplashScreenView(splashScreenView);
        }
        Trace.traceEnd(32);
    }

    /* access modifiers changed from: package-private */
    public int getStartingWindowBackgroundColorForTask(int i) {
        StartingWindowRecord startingWindowRecord = this.mStartingWindowRecords.get(i);
        if (startingWindowRecord == null) {
            return 0;
        }
        return startingWindowRecord.mBGColor;
    }

    /* renamed from: com.android.wm.shell.startingsurface.StartingSurfaceDrawer$SplashScreenViewSupplier */
    private static class SplashScreenViewSupplier implements Supplier<SplashScreenView> {
        private boolean mIsViewSet;
        private SplashScreenView mView;

        private SplashScreenViewSupplier() {
        }

        /* access modifiers changed from: package-private */
        public void setView(SplashScreenView splashScreenView) {
            synchronized (this) {
                this.mView = splashScreenView;
                this.mIsViewSet = true;
                notify();
            }
        }

        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* JADX WARNING: Missing exception handler attribute for start block: B:1:0x0001 */
        /* JADX WARNING: Removed duplicated region for block: B:1:0x0001 A[LOOP:0: B:1:0x0001->B:13:0x0001, LOOP_START, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.window.SplashScreenView get() {
            /*
                r1 = this;
                monitor-enter(r1)
            L_0x0001:
                boolean r0 = r1.mIsViewSet     // Catch:{ all -> 0x000d }
                if (r0 != 0) goto L_0x0009
                r1.wait()     // Catch:{ InterruptedException -> 0x0001 }
                goto L_0x0001
            L_0x0009:
                android.window.SplashScreenView r0 = r1.mView     // Catch:{ all -> 0x000d }
                monitor-exit(r1)     // Catch:{ all -> 0x000d }
                return r0
            L_0x000d:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x000d }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.startingsurface.StartingSurfaceDrawer.SplashScreenViewSupplier.get():android.window.SplashScreenView");
        }
    }

    /* access modifiers changed from: package-private */
    public int estimateTaskBackgroundColor(TaskInfo taskInfo) {
        ActivityInfo activityInfo = taskInfo.topActivityInfo;
        if (activityInfo == null) {
            return 0;
        }
        String str = activityInfo.packageName;
        int i = taskInfo.userId;
        try {
            Context createPackageContextAsUser = this.mContext.createPackageContextAsUser(str, 4, UserHandle.of(i));
            try {
                String splashScreenTheme = ActivityThread.getPackageManager().getSplashScreenTheme(str, i);
                int splashScreenTheme2 = getSplashScreenTheme(splashScreenTheme != null ? createPackageContextAsUser.getResources().getIdentifier(splashScreenTheme, (String) null, (String) null) : 0, activityInfo);
                if (splashScreenTheme2 != createPackageContextAsUser.getThemeResId()) {
                    createPackageContextAsUser.setTheme(splashScreenTheme2);
                }
                return this.mSplashscreenContentDrawer.estimateTaskBackgroundColor(createPackageContextAsUser);
            } catch (RemoteException | RuntimeException e) {
                String str2 = TAG;
                Slog.w(str2, "failed get starting window background color at taskId: " + taskInfo.taskId, e);
                return 0;
            }
        } catch (PackageManager.NameNotFoundException e2) {
            String str3 = TAG;
            Slog.w(str3, "Failed creating package context with package name " + str + " for user " + taskInfo.userId, e2);
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void makeTaskSnapshotWindow(StartingWindowInfo startingWindowInfo, IBinder iBinder, TaskSnapshot taskSnapshot) {
        int i = startingWindowInfo.taskInfo.taskId;
        lambda$makeTaskSnapshotWindow$2(i);
        TaskSnapshotWindow create = TaskSnapshotWindow.create(startingWindowInfo, iBinder, taskSnapshot, this.mSplashScreenExecutor, new StartingSurfaceDrawer$$ExternalSyntheticLambda4(this, i));
        if (create != null) {
            this.mStartingWindowRecords.put(i, new StartingWindowRecord(iBinder, (View) null, create, 2));
        }
    }

    public void removeStartingWindow(int i, SurfaceControl surfaceControl, Rect rect, boolean z) {
        if (DEBUG_SPLASH_SCREEN) {
            String str = TAG;
            Slog.d(str, "Task start finish, remove starting surface for task " + i);
        }
        removeWindowSynced(i, surfaceControl, rect, z);
    }

    public void copySplashScreenView(int i) {
        StartingWindowRecord startingWindowRecord = this.mStartingWindowRecords.get(i);
        SplashScreenView.SplashScreenViewParcelable splashScreenViewParcelable = null;
        SplashScreenView access$200 = startingWindowRecord != null ? startingWindowRecord.mContentView : null;
        if (access$200 != null && access$200.isCopyable()) {
            splashScreenViewParcelable = new SplashScreenView.SplashScreenViewParcelable(access$200);
            splashScreenViewParcelable.setClientCallback(new RemoteCallback(new StartingSurfaceDrawer$$ExternalSyntheticLambda0(this, i)));
            access$200.onCopied();
            this.mAnimatedSplashScreenSurfaceHosts.append(i, access$200.getSurfaceHost());
        }
        if (DEBUG_SPLASH_SCREEN) {
            String str = TAG;
            Slog.v(str, "Copying splash screen window view for task: " + i + " parcelable: " + splashScreenViewParcelable);
        }
        ActivityTaskManager.getInstance().onSplashScreenViewCopyFinished(i, splashScreenViewParcelable);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$copySplashScreenView$4(int i, Bundle bundle) {
        this.mSplashScreenExecutor.execute(new StartingSurfaceDrawer$$ExternalSyntheticLambda3(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$copySplashScreenView$3(int i) {
        onAppSplashScreenViewRemoved(i, false);
    }

    public void onAppSplashScreenViewRemoved(int i) {
        onAppSplashScreenViewRemoved(i, true);
    }

    private void onAppSplashScreenViewRemoved(int i, boolean z) {
        SurfaceControlViewHost surfaceControlViewHost = this.mAnimatedSplashScreenSurfaceHosts.get(i);
        if (surfaceControlViewHost != null) {
            this.mAnimatedSplashScreenSurfaceHosts.remove(i);
            if (DEBUG_SPLASH_SCREEN) {
                String str = z ? "Server cleaned up" : "App removed";
                String str2 = TAG;
                Slog.v(str2, str + "the splash screen. Releasing SurfaceControlViewHost for task:" + i);
            }
            surfaceControlViewHost.getView().post(new StartingSurfaceDrawer$$ExternalSyntheticLambda1(surfaceControlViewHost));
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0054  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean addWindow(int r8, android.os.IBinder r9, android.view.View r10, android.view.WindowManager r11, android.view.WindowManager.LayoutParams r12, @android.window.StartingWindowInfo.StartingWindowType int r13) {
        /*
            r7 = this;
            java.lang.String r0 = "view not successfully added to wm, removing view"
            r1 = 0
            r2 = 32
            java.lang.String r4 = "addRootView"
            android.os.Trace.traceBegin(r2, r4)     // Catch:{ BadTokenException -> 0x0025 }
            r11.addView(r10, r12)     // Catch:{ BadTokenException -> 0x0025 }
            android.os.Trace.traceEnd(r2)
            if (r10 == 0) goto L_0x0021
            android.view.ViewParent r12 = r10.getParent()
            if (r12 != 0) goto L_0x0021
            java.lang.String r12 = TAG
            android.util.Slog.w(r12, r0)
        L_0x001d:
            r11.removeViewImmediate(r10)
            goto L_0x0052
        L_0x0021:
            r1 = 1
            goto L_0x0052
        L_0x0023:
            r7 = move-exception
            goto L_0x005b
        L_0x0025:
            r12 = move-exception
            java.lang.String r4 = TAG     // Catch:{ all -> 0x0023 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0023 }
            r5.<init>()     // Catch:{ all -> 0x0023 }
            r5.append(r9)     // Catch:{ all -> 0x0023 }
            java.lang.String r6 = " already running, starting window not displayed. "
            r5.append(r6)     // Catch:{ all -> 0x0023 }
            java.lang.String r12 = r12.getMessage()     // Catch:{ all -> 0x0023 }
            r5.append(r12)     // Catch:{ all -> 0x0023 }
            java.lang.String r12 = r5.toString()     // Catch:{ all -> 0x0023 }
            android.util.Slog.w(r4, r12)     // Catch:{ all -> 0x0023 }
            android.os.Trace.traceEnd(r2)
            if (r10 == 0) goto L_0x0052
            android.view.ViewParent r12 = r10.getParent()
            if (r12 != 0) goto L_0x0052
            android.util.Slog.w(r4, r0)
            goto L_0x001d
        L_0x0052:
            if (r1 == 0) goto L_0x005a
            r7.lambda$makeTaskSnapshotWindow$2(r8)
            r7.saveSplashScreenRecord(r9, r8, r10, r13)
        L_0x005a:
            return r1
        L_0x005b:
            android.os.Trace.traceEnd(r2)
            if (r10 == 0) goto L_0x006e
            android.view.ViewParent r8 = r10.getParent()
            if (r8 != 0) goto L_0x006e
            java.lang.String r8 = TAG
            android.util.Slog.w(r8, r0)
            r11.removeViewImmediate(r10)
        L_0x006e:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.startingsurface.StartingSurfaceDrawer.addWindow(int, android.os.IBinder, android.view.View, android.view.WindowManager, android.view.WindowManager$LayoutParams, int):boolean");
    }

    private void saveSplashScreenRecord(IBinder iBinder, int i, View view, @StartingWindowInfo.StartingWindowType int i2) {
        this.mStartingWindowRecords.put(i, new StartingWindowRecord(iBinder, view, (TaskSnapshotWindow) null, i2));
    }

    /* access modifiers changed from: private */
    /* renamed from: removeWindowNoAnimate */
    public void lambda$makeTaskSnapshotWindow$2(int i) {
        removeWindowSynced(i, (SurfaceControl) null, (Rect) null, false);
    }

    /* access modifiers changed from: protected */
    public void removeWindowSynced(int i, SurfaceControl surfaceControl, Rect rect, boolean z) {
        StartingWindowRecord startingWindowRecord = this.mStartingWindowRecords.get(i);
        if (startingWindowRecord != null) {
            if (startingWindowRecord.mDecorView != null) {
                if (DEBUG_SPLASH_SCREEN) {
                    String str = TAG;
                    Slog.v(str, "Removing splash screen window for task: " + i);
                }
                if (startingWindowRecord.mContentView == null) {
                    Slog.e(TAG, "Found empty splash screen, remove!");
                    removeWindowInner(startingWindowRecord.mDecorView, false);
                } else if (startingWindowRecord.mSuggestType == 4) {
                    removeWindowInner(startingWindowRecord.mDecorView, false);
                } else if (z) {
                    this.mSplashscreenContentDrawer.applyExitAnimation(startingWindowRecord.mContentView, surfaceControl, rect, new StartingSurfaceDrawer$$ExternalSyntheticLambda6(this, startingWindowRecord));
                } else {
                    removeWindowInner(startingWindowRecord.mDecorView, true);
                }
            }
            if (startingWindowRecord.mTaskSnapshotWindow != null) {
                startingWindowRecord.mTaskSnapshotWindow.lambda$remove$0();
            }
            this.mStartingWindowRecords.remove(i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeWindowSynced$5(StartingWindowRecord startingWindowRecord) {
        removeWindowInner(startingWindowRecord.mDecorView, true);
    }

    private void removeWindowInner(View view, boolean z) {
        if (z) {
            view.setVisibility(8);
        }
        WindowManager windowManager = (WindowManager) view.getContext().getSystemService(WindowManager.class);
        if (windowManager != null) {
            windowManager.removeView(view);
        }
    }

    /* renamed from: com.android.wm.shell.startingsurface.StartingSurfaceDrawer$StartingWindowRecord */
    private static class StartingWindowRecord {
        /* access modifiers changed from: private */
        public final IBinder mAppToken;
        /* access modifiers changed from: private */
        public int mBGColor;
        /* access modifiers changed from: private */
        public SplashScreenView mContentView;
        /* access modifiers changed from: private */
        public final View mDecorView;
        private boolean mSetSplashScreen;
        /* access modifiers changed from: private */
        @StartingWindowInfo.StartingWindowType
        public int mSuggestType;
        /* access modifiers changed from: private */
        public final TaskSnapshotWindow mTaskSnapshotWindow;

        StartingWindowRecord(IBinder iBinder, View view, TaskSnapshotWindow taskSnapshotWindow, @StartingWindowInfo.StartingWindowType int i) {
            this.mAppToken = iBinder;
            this.mDecorView = view;
            this.mTaskSnapshotWindow = taskSnapshotWindow;
            if (taskSnapshotWindow != null) {
                this.mBGColor = taskSnapshotWindow.getBackgroundColor();
            }
            this.mSuggestType = i;
        }

        /* access modifiers changed from: private */
        public void setSplashScreenView(SplashScreenView splashScreenView) {
            if (!this.mSetSplashScreen) {
                this.mContentView = splashScreenView;
                this.mSetSplashScreen = true;
            }
        }
    }
}
