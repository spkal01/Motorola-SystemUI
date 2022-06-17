package com.android.p011wm.shell.draganddrop;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.PendingIntent;
import android.app.WindowConfiguration;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.wm.shell.draganddrop.DragAndDropPolicy */
public class DragAndDropPolicy {
    /* access modifiers changed from: private */
    public static final String TAG = "DragAndDropPolicy";
    private final ActivityTaskManager mActivityTaskManager;
    private final Context mContext;
    private DragSession mSession;
    private final SplitScreenController mSplitScreen;
    private final Starter mStarter;
    private final ArrayList<Target> mTargets;

    /* renamed from: com.android.wm.shell.draganddrop.DragAndDropPolicy$Starter */
    public interface Starter {
        void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle);

        void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle);

        void startTask(int i, int i2, int i3, Bundle bundle);
    }

    public DragAndDropPolicy(Context context, SplitScreenController splitScreenController) {
        this(context, ActivityTaskManager.getInstance(), splitScreenController, new DefaultStarter(context));
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [com.android.wm.shell.draganddrop.DragAndDropPolicy$Starter] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    DragAndDropPolicy(android.content.Context r2, android.app.ActivityTaskManager r3, com.android.p011wm.shell.splitscreen.SplitScreenController r4, com.android.p011wm.shell.draganddrop.DragAndDropPolicy.Starter r5) {
        /*
            r1 = this;
            r1.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.mTargets = r0
            r1.mContext = r2
            r1.mActivityTaskManager = r3
            r1.mSplitScreen = r4
            if (r4 == 0) goto L_0x0013
            goto L_0x0014
        L_0x0013:
            r4 = r5
        L_0x0014:
            r1.mStarter = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.draganddrop.DragAndDropPolicy.<init>(android.content.Context, android.app.ActivityTaskManager, com.android.wm.shell.splitscreen.SplitScreenController, com.android.wm.shell.draganddrop.DragAndDropPolicy$Starter):void");
    }

    /* access modifiers changed from: package-private */
    public void start(DisplayLayout displayLayout, ClipData clipData) {
        DragSession dragSession = new DragSession(this.mContext, this.mActivityTaskManager, displayLayout, clipData);
        this.mSession = dragSession;
        dragSession.update();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00e2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.android.p011wm.shell.draganddrop.DragAndDropPolicy.Target> getTargets(android.graphics.Insets r12) {
        /*
            r11 = this;
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r0 = r11.mTargets
            r0.clear()
            com.android.wm.shell.draganddrop.DragAndDropPolicy$DragSession r0 = r11.mSession
            if (r0 != 0) goto L_0x000c
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r11 = r11.mTargets
            return r11
        L_0x000c:
            com.android.wm.shell.common.DisplayLayout r0 = r0.displayLayout
            int r0 = r0.width()
            com.android.wm.shell.draganddrop.DragAndDropPolicy$DragSession r1 = r11.mSession
            com.android.wm.shell.common.DisplayLayout r1 = r1.displayLayout
            int r1 = r1.height()
            int r2 = r12.left
            int r0 = r0 - r2
            int r3 = r12.right
            int r0 = r0 - r3
            int r3 = r12.top
            int r1 = r1 - r3
            int r12 = r12.bottom
            int r1 = r1 - r12
            android.graphics.Rect r12 = new android.graphics.Rect
            int r0 = r0 + r2
            int r1 = r1 + r3
            r12.<init>(r2, r3, r0, r1)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>(r12)
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>(r12)
            com.android.wm.shell.draganddrop.DragAndDropPolicy$DragSession r2 = r11.mSession
            com.android.wm.shell.common.DisplayLayout r2 = r2.displayLayout
            boolean r2 = r2.isLandscape()
            com.android.wm.shell.splitscreen.SplitScreenController r3 = r11.mSplitScreen
            r4 = 1
            r5 = 0
            if (r3 == 0) goto L_0x004d
            boolean r3 = r3.isSplitScreenVisible()
            if (r3 == 0) goto L_0x004d
            r3 = r4
            goto L_0x004e
        L_0x004d:
            r3 = r5
        L_0x004e:
            if (r3 != 0) goto L_0x005d
            com.android.wm.shell.draganddrop.DragAndDropPolicy$DragSession r3 = r11.mSession
            int r6 = r3.runningTaskActType
            if (r6 != r4) goto L_0x005b
            int r3 = r3.runningTaskWinMode
            if (r3 != r4) goto L_0x005b
            goto L_0x005d
        L_0x005b:
            r3 = r5
            goto L_0x005e
        L_0x005d:
            r3 = r4
        L_0x005e:
            if (r3 == 0) goto L_0x00e2
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            com.android.wm.shell.splitscreen.SplitScreenController r7 = r11.mSplitScreen
            r7.getStageBounds(r3, r6)
            r3.intersect(r12)
            r6.intersect(r12)
            r7 = 2
            r8 = 3
            if (r2 == 0) goto L_0x00ad
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            android.graphics.Rect[] r10 = new android.graphics.Rect[r8]
            r10[r5] = r2
            r10[r4] = r1
            r10[r7] = r9
            r12.splitVertically(r10)
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r12 = r11.mTargets
            com.android.wm.shell.draganddrop.DragAndDropPolicy$Target r7 = new com.android.wm.shell.draganddrop.DragAndDropPolicy$Target
            r7.<init>(r5, r1, r0)
            r12.add(r7)
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r12 = r11.mTargets
            com.android.wm.shell.draganddrop.DragAndDropPolicy$Target r0 = new com.android.wm.shell.draganddrop.DragAndDropPolicy$Target
            r0.<init>(r4, r2, r3)
            r12.add(r0)
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r12 = r11.mTargets
            com.android.wm.shell.draganddrop.DragAndDropPolicy$Target r0 = new com.android.wm.shell.draganddrop.DragAndDropPolicy$Target
            r0.<init>(r8, r9, r6)
            r12.add(r0)
            goto L_0x00ec
        L_0x00ad:
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            android.graphics.Rect[] r8 = new android.graphics.Rect[r8]
            r8[r5] = r2
            r8[r4] = r1
            r8[r7] = r9
            r12.splitHorizontally(r8)
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r12 = r11.mTargets
            com.android.wm.shell.draganddrop.DragAndDropPolicy$Target r4 = new com.android.wm.shell.draganddrop.DragAndDropPolicy$Target
            r4.<init>(r5, r1, r0)
            r12.add(r4)
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r12 = r11.mTargets
            com.android.wm.shell.draganddrop.DragAndDropPolicy$Target r0 = new com.android.wm.shell.draganddrop.DragAndDropPolicy$Target
            r0.<init>(r7, r2, r3)
            r12.add(r0)
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r12 = r11.mTargets
            com.android.wm.shell.draganddrop.DragAndDropPolicy$Target r0 = new com.android.wm.shell.draganddrop.DragAndDropPolicy$Target
            r1 = 4
            r0.<init>(r1, r9, r6)
            r12.add(r0)
            goto L_0x00ec
        L_0x00e2:
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r12 = r11.mTargets
            com.android.wm.shell.draganddrop.DragAndDropPolicy$Target r2 = new com.android.wm.shell.draganddrop.DragAndDropPolicy$Target
            r2.<init>(r5, r1, r0)
            r12.add(r2)
        L_0x00ec:
            java.util.ArrayList<com.android.wm.shell.draganddrop.DragAndDropPolicy$Target> r11 = r11.mTargets
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.draganddrop.DragAndDropPolicy.getTargets(android.graphics.Insets):java.util.ArrayList");
    }

    /* access modifiers changed from: package-private */
    public Target getTargetAtLocation(int i, int i2) {
        for (int size = this.mTargets.size() - 1; size >= 0; size--) {
            Target target = this.mTargets.get(size);
            if (target.hitRegion.contains(i, i2)) {
                return target;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void handleDrop(Target target, ClipData clipData) {
        if (target != null && this.mTargets.contains(target)) {
            SplitScreenController splitScreenController = this.mSplitScreen;
            int i = 0;
            int i2 = 1;
            boolean z = splitScreenController != null && splitScreenController.isSplitScreenVisible();
            int i3 = target.type;
            if (i3 == 2 || i3 == 1) {
                i = 1;
            }
            int i4 = -1;
            if (i3 == 0 || this.mSplitScreen == null) {
                i2 = -1;
            } else {
                int i5 = i ^ 1;
                if (z) {
                    i2 = -1;
                }
                i4 = i5;
            }
            startClipDescription(clipData.getDescription(), this.mSession.dragData, i2, i4);
        }
    }

    private void startClipDescription(ClipDescription clipDescription, Intent intent, int i, int i2) {
        boolean hasMimeType = clipDescription.hasMimeType("application/vnd.android.task");
        boolean hasMimeType2 = clipDescription.hasMimeType("application/vnd.android.shortcut");
        Bundle bundleExtra = intent.hasExtra("android.intent.extra.ACTIVITY_OPTIONS") ? intent.getBundleExtra("android.intent.extra.ACTIVITY_OPTIONS") : new Bundle();
        if (hasMimeType) {
            this.mStarter.startTask(intent.getIntExtra("android.intent.extra.TASK_ID", -1), i, i2, bundleExtra);
        } else if (hasMimeType2) {
            this.mStarter.startShortcut(intent.getStringExtra("android.intent.extra.PACKAGE_NAME"), intent.getStringExtra("android.intent.extra.shortcut.ID"), i, i2, bundleExtra, (UserHandle) intent.getParcelableExtra("android.intent.extra.USER"));
        } else {
            this.mStarter.startIntent((PendingIntent) intent.getParcelableExtra("android.intent.extra.PENDING_INTENT"), (Intent) null, i, i2, bundleExtra);
        }
    }

    /* renamed from: com.android.wm.shell.draganddrop.DragAndDropPolicy$DragSession */
    private static class DragSession {
        final DisplayLayout displayLayout;
        Intent dragData;
        boolean dragItemSupportsSplitscreen;
        private final ActivityTaskManager mActivityTaskManager;
        private final Context mContext;
        private final ClipData mInitialDragData;
        @WindowConfiguration.ActivityType
        int runningTaskActType = 1;
        int runningTaskId;
        boolean runningTaskIsResizeable;
        @WindowConfiguration.WindowingMode
        int runningTaskWinMode = 0;

        DragSession(Context context, ActivityTaskManager activityTaskManager, DisplayLayout displayLayout2, ClipData clipData) {
            this.mContext = context;
            this.mActivityTaskManager = activityTaskManager;
            this.mInitialDragData = clipData;
            this.displayLayout = displayLayout2;
        }

        /* access modifiers changed from: package-private */
        public void update() {
            boolean z = true;
            List tasks = this.mActivityTaskManager.getTasks(1, false);
            if (!tasks.isEmpty()) {
                ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) tasks.get(0);
                this.runningTaskWinMode = runningTaskInfo.getWindowingMode();
                this.runningTaskActType = runningTaskInfo.getActivityType();
                this.runningTaskId = runningTaskInfo.taskId;
                this.runningTaskIsResizeable = runningTaskInfo.isResizeable;
            }
            ActivityInfo activityInfo = this.mInitialDragData.getItemAt(0).getActivityInfo();
            if (activityInfo != null && !ActivityInfo.isResizeableMode(activityInfo.resizeMode)) {
                z = false;
            }
            this.dragItemSupportsSplitscreen = z;
            this.dragData = this.mInitialDragData.getItemAt(0).getIntent();
        }
    }

    /* renamed from: com.android.wm.shell.draganddrop.DragAndDropPolicy$DefaultStarter */
    private static class DefaultStarter implements Starter {
        private final Context mContext;

        public DefaultStarter(Context context) {
            this.mContext = context;
        }

        public void startTask(int i, int i2, int i3, Bundle bundle) {
            try {
                ActivityTaskManager.getService().startActivityFromRecents(i, bundle);
            } catch (RemoteException e) {
                Slog.e(DragAndDropPolicy.TAG, "Failed to launch task", e);
            }
        }

        public void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) {
            try {
                ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).startShortcut(str, str2, (Rect) null, bundle, userHandle);
            } catch (ActivityNotFoundException e) {
                Slog.e(DragAndDropPolicy.TAG, "Failed to launch shortcut", e);
            }
        }

        public void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) {
            try {
                pendingIntent.send(this.mContext, 0, intent, (PendingIntent.OnFinished) null, (Handler) null, (String) null, bundle);
            } catch (PendingIntent.CanceledException e) {
                Slog.e(DragAndDropPolicy.TAG, "Failed to launch activity", e);
            }
        }
    }

    /* renamed from: com.android.wm.shell.draganddrop.DragAndDropPolicy$Target */
    static class Target {
        final Rect drawRegion;
        final Rect hitRegion;
        final int type;

        public Target(int i, Rect rect, Rect rect2) {
            this.type = i;
            this.hitRegion = rect;
            this.drawRegion = rect2;
        }

        public String toString() {
            return "Target {hit=" + this.hitRegion + " draw=" + this.drawRegion + "}";
        }
    }
}
