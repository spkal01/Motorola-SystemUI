package com.android.p011wm.shell.legacysplitscreen;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.ArraySet;
import android.widget.Toast;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.legacysplitscreen.DividerView;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.legacysplitscreen.ForcedResizableInfoActivityController */
final class ForcedResizableInfoActivityController implements DividerView.DividerCallbacks {
    private final Context mContext;
    private boolean mDividerDragging;
    private final Consumer<Boolean> mDockedStackExistsListener;
    private final ShellExecutor mMainExecutor;
    private final ArraySet<String> mPackagesShownInSession = new ArraySet<>();
    private final ArraySet<PendingTaskRecord> mPendingTasks = new ArraySet<>();
    private final Runnable mTimeoutRunnable = new ForcedResizableInfoActivityController$$ExternalSyntheticLambda0(this);

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Boolean bool) {
        if (!bool.booleanValue()) {
            this.mPackagesShownInSession.clear();
        }
    }

    /* renamed from: com.android.wm.shell.legacysplitscreen.ForcedResizableInfoActivityController$PendingTaskRecord */
    private class PendingTaskRecord {
        int mReason;
        int mTaskId;

        PendingTaskRecord(int i, int i2) {
            this.mTaskId = i;
            this.mReason = i2;
        }
    }

    ForcedResizableInfoActivityController(Context context, LegacySplitScreenController legacySplitScreenController, ShellExecutor shellExecutor) {
        ForcedResizableInfoActivityController$$ExternalSyntheticLambda1 forcedResizableInfoActivityController$$ExternalSyntheticLambda1 = new ForcedResizableInfoActivityController$$ExternalSyntheticLambda1(this);
        this.mDockedStackExistsListener = forcedResizableInfoActivityController$$ExternalSyntheticLambda1;
        this.mContext = context;
        this.mMainExecutor = shellExecutor;
        legacySplitScreenController.registerInSplitScreenListener(forcedResizableInfoActivityController$$ExternalSyntheticLambda1);
    }

    public void onDraggingStart() {
        this.mDividerDragging = true;
        this.mMainExecutor.removeCallbacks(this.mTimeoutRunnable);
    }

    public void onDraggingEnd() {
        this.mDividerDragging = false;
        showPending();
    }

    /* access modifiers changed from: package-private */
    public void onAppTransitionFinished() {
        if (!this.mDividerDragging) {
            showPending();
        }
    }

    /* access modifiers changed from: package-private */
    public void activityForcedResizable(String str, int i, int i2) {
        if (!debounce(str)) {
            this.mPendingTasks.add(new PendingTaskRecord(i, i2));
            postTimeout();
        }
    }

    /* access modifiers changed from: package-private */
    public void activityDismissingSplitScreen() {
        Toast.makeText(this.mContext, C2219R.string.dock_non_resizeble_failed_to_dock_text, 0).show();
    }

    /* access modifiers changed from: package-private */
    public void activityLaunchOnSecondaryDisplayFailed() {
        Toast.makeText(this.mContext, C2219R.string.activity_launch_on_secondary_display_failed_text, 0).show();
    }

    /* access modifiers changed from: private */
    public void showPending() {
        this.mMainExecutor.removeCallbacks(this.mTimeoutRunnable);
        for (int size = this.mPendingTasks.size() - 1; size >= 0; size--) {
            PendingTaskRecord valueAt = this.mPendingTasks.valueAt(size);
            Intent intent = new Intent(this.mContext, ForcedResizableInfoActivity.class);
            ActivityOptions makeBasic = ActivityOptions.makeBasic();
            makeBasic.setLaunchTaskId(valueAt.mTaskId);
            makeBasic.setTaskOverlay(true, true);
            intent.putExtra("extra_forced_resizeable_reason", valueAt.mReason);
            this.mContext.startActivityAsUser(intent, makeBasic.toBundle(), UserHandle.CURRENT);
        }
        this.mPendingTasks.clear();
    }

    private void postTimeout() {
        this.mMainExecutor.removeCallbacks(this.mTimeoutRunnable);
        this.mMainExecutor.executeDelayed(this.mTimeoutRunnable, 1000);
    }

    private boolean debounce(String str) {
        if (str == null) {
            return false;
        }
        if ("com.android.systemui".equals(str)) {
            return true;
        }
        boolean contains = this.mPackagesShownInSession.contains(str);
        this.mPackagesShownInSession.add(str);
        return contains;
    }
}
