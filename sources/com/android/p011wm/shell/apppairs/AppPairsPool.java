package com.android.p011wm.shell.apppairs;

import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import java.io.PrintWriter;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.apppairs.AppPairsPool */
class AppPairsPool {
    private static final String TAG = "AppPairsPool";
    @VisibleForTesting
    final AppPairsController mController;
    private final ArrayList<AppPair> mPool = new ArrayList<>();

    AppPairsPool(AppPairsController appPairsController) {
        this.mController = appPairsController;
        incrementPool();
    }

    /* access modifiers changed from: package-private */
    public AppPair acquire() {
        ArrayList<AppPair> arrayList = this.mPool;
        AppPair remove = arrayList.remove(arrayList.size() - 1);
        if (ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled) {
            String valueOf = String.valueOf(remove.getRootTaskId());
            String valueOf2 = String.valueOf(remove);
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TASK_ORG, 2006473416, 16, (String) null, valueOf, valueOf2, Long.valueOf((long) this.mPool.size()));
        }
        if (this.mPool.size() == 0) {
            incrementPool();
        }
        return remove;
    }

    /* access modifiers changed from: package-private */
    public void release(AppPair appPair) {
        this.mPool.add(appPair);
        if (ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled) {
            String valueOf = String.valueOf(appPair.getRootTaskId());
            String valueOf2 = String.valueOf(appPair);
            long size = (long) this.mPool.size();
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TASK_ORG, 1891981945, 16, (String) null, valueOf, valueOf2, Long.valueOf(size));
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void incrementPool() {
        if (ShellProtoLogCache.WM_SHELL_TASK_ORG_enabled) {
            long size = (long) this.mPool.size();
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TASK_ORG, 1079041527, 1, (String) null, Long.valueOf(size));
        }
        AppPair appPair = new AppPair(this.mController);
        this.mController.getTaskOrganizer().createRootTask(0, 1, appPair);
        this.mPool.add(appPair);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int poolSize() {
        return this.mPool.size();
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = (str + "  ") + "  ";
        printWriter.println(str + this);
        for (int size = this.mPool.size() + -1; size >= 0; size--) {
            this.mPool.get(size).dump(printWriter, str2);
        }
    }

    public String toString() {
        return TAG + "#" + this.mPool.size();
    }
}
