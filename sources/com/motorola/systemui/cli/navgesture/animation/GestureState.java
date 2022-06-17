package com.motorola.systemui.cli.navgesture.animation;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Intent;
import android.util.Log;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.ActivityControlHelper;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.OverviewComponentObserver;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationCallbacks;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationTargetSet;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationTargetSetController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@TargetApi(30)
public class GestureState implements RecentsAnimationCallbacks.RecentsAnimationListener {
    public static final GestureState DEFAULT_STATE = new GestureState();
    private static int FLAG_COUNT = 0;
    public static final int STATE_END_TARGET_ANIMATION_FINISHED = getFlagForIndex("STATE_END_TARGET_ANIMATION_FINISHED");
    public static final int STATE_END_TARGET_SET = getFlagForIndex("STATE_END_TARGET_SET");
    private static final ArrayList<String> STATE_NAMES = new ArrayList<>();
    public static final int STATE_OVERSCROLL_WINDOW_CREATED = getFlagForIndex("STATE_OVERSCROLL_WINDOW_CREATED");
    public static final int STATE_RECENTS_ANIMATION_CANCELED = getFlagForIndex("STATE_RECENTS_ANIMATION_CANCELED");
    public static final int STATE_RECENTS_ANIMATION_ENDED = getFlagForIndex("STATE_RECENTS_ANIMATION_ENDED");
    public static final int STATE_RECENTS_ANIMATION_FINISHED = getFlagForIndex("STATE_RECENTS_ANIMATION_FINISHED");
    public static final int STATE_RECENTS_ANIMATION_INITIALIZED = getFlagForIndex("STATE_RECENTS_ANIMATION_INITIALIZED");
    public static final int STATE_RECENTS_ANIMATION_STARTED = getFlagForIndex("STATE_RECENTS_ANIMATION_STARTED");
    public static final int STATE_RECENTS_SCROLLING_FINISHED = getFlagForIndex("STATE_RECENTS_SCROLLING_FINISHED");
    private final ActivityControlHelper mActivityInterface;
    private GestureEndTarget mEndTarget;
    private final int mGestureId;
    private Intent mHomeIntent;
    private RemoteAnimationTargetCompat mLastAppearedTaskTarget;
    private int mLastStartedTaskId;
    private final Intent mOverviewIntent;
    private Set<Integer> mPreviouslyAppearedTaskIds;
    private ActivityManager.RunningTaskInfo mRunningTask;
    private final MultiStateCallback mStateCallback;

    public enum GestureEndTarget {
        HOME(true, false),
        RECENTS(true, true),
        NEW_TASK(false, true),
        LAST_TASK(false, true);
        
        public final boolean isLauncher;
        public final boolean recentsAttachedToAppWindow;

        private GestureEndTarget(boolean z, boolean z2) {
            this.isLauncher = z;
            this.recentsAttachedToAppWindow = z2;
        }
    }

    private static int getFlagForIndex(String str) {
        STATE_NAMES.add(str);
        int i = FLAG_COUNT;
        int i2 = 1 << i;
        FLAG_COUNT = i + 1;
        return i2;
    }

    public GestureState(OverviewComponentObserver overviewComponentObserver, int i) {
        this.mHomeIntent = null;
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mOverviewIntent = overviewComponentObserver.getOverviewIntent();
        this.mActivityInterface = overviewComponentObserver.getActivityControlHelper();
        this.mStateCallback = new MultiStateCallback((String[]) STATE_NAMES.toArray(new String[0]));
        this.mGestureId = i;
    }

    public GestureState(GestureState gestureState) {
        this.mHomeIntent = null;
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mHomeIntent = gestureState.mHomeIntent;
        this.mOverviewIntent = gestureState.mOverviewIntent;
        this.mActivityInterface = gestureState.mActivityInterface;
        this.mStateCallback = gestureState.mStateCallback;
        this.mGestureId = gestureState.mGestureId;
        this.mRunningTask = gestureState.mRunningTask;
        this.mEndTarget = gestureState.mEndTarget;
        this.mLastAppearedTaskTarget = gestureState.mLastAppearedTaskTarget;
        this.mPreviouslyAppearedTaskIds = gestureState.mPreviouslyAppearedTaskIds;
        this.mLastStartedTaskId = gestureState.mLastStartedTaskId;
    }

    public GestureState() {
        this.mHomeIntent = null;
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mHomeIntent = new Intent();
        this.mOverviewIntent = new Intent();
        this.mActivityInterface = null;
        this.mStateCallback = new MultiStateCallback((String[]) STATE_NAMES.toArray(new String[0]));
        this.mGestureId = -1;
    }

    public void setState(int i) {
        this.mStateCallback.lambda$setStateOnUiThread$0(i);
    }

    public void runOnceAtState(int i, Runnable runnable) {
        this.mStateCallback.runOnceAtState(i, runnable);
    }

    public Intent getOverviewIntent() {
        return this.mOverviewIntent;
    }

    public <T extends BaseGestureActivity> ActivityControlHelper<T> getActivityInterface() {
        return this.mActivityInterface;
    }

    public ActivityManager.RunningTaskInfo getRunningTask() {
        return this.mRunningTask;
    }

    public int getRunningTaskId() {
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mRunningTask;
        if (runningTaskInfo != null) {
            return runningTaskInfo.taskId;
        }
        return -1;
    }

    public void updateRunningTask(ActivityManager.RunningTaskInfo runningTaskInfo) {
        this.mRunningTask = runningTaskInfo;
    }

    public void updateLastAppearedTaskTarget(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        this.mLastAppearedTaskTarget = remoteAnimationTargetCompat;
        if (remoteAnimationTargetCompat != null) {
            this.mPreviouslyAppearedTaskIds.add(Integer.valueOf(remoteAnimationTargetCompat.taskId));
        }
    }

    public void updatePreviouslyAppearedTaskIds(Set<Integer> set) {
        this.mPreviouslyAppearedTaskIds = set;
    }

    public Set<Integer> getPreviouslyAppearedTaskIds() {
        return this.mPreviouslyAppearedTaskIds;
    }

    public void updateLastStartedTaskId(int i) {
        this.mLastStartedTaskId = i;
    }

    public int getLastStartedTaskId() {
        return this.mLastStartedTaskId;
    }

    public GestureEndTarget getEndTarget() {
        return this.mEndTarget;
    }

    public void setEndTarget(GestureEndTarget gestureEndTarget, boolean z) {
        this.mEndTarget = gestureEndTarget;
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_END_TARGET_SET);
        Log.i("GestureState", "setEndTarget = " + this.mEndTarget);
        if (z) {
            this.mStateCallback.lambda$setStateOnUiThread$0(STATE_END_TARGET_ANIMATION_FINISHED);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r1 = r1.mEndTarget;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isRunningAnimationToLauncher() {
        /*
            r1 = this;
            boolean r0 = r1.isRecentsAnimationRunning()
            if (r0 == 0) goto L_0x0010
            com.motorola.systemui.cli.navgesture.animation.GestureState$GestureEndTarget r1 = r1.mEndTarget
            if (r1 == 0) goto L_0x0010
            boolean r1 = r1.isLauncher
            if (r1 == 0) goto L_0x0010
            r1 = 1
            goto L_0x0011
        L_0x0010:
            r1 = 0
        L_0x0011:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.navgesture.animation.GestureState.isRunningAnimationToLauncher():boolean");
    }

    public boolean isRecentsAnimationRunning() {
        return this.mStateCallback.hasStates(STATE_RECENTS_ANIMATION_INITIALIZED) && !this.mStateCallback.hasStates(STATE_RECENTS_ANIMATION_ENDED);
    }

    public void onRecentsAnimationStart(RecentsAnimationTargetSetController recentsAnimationTargetSetController, RecentsAnimationTargetSet recentsAnimationTargetSet) {
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_RECENTS_ANIMATION_STARTED);
    }

    public void onRecentsAnimationCanceled(ThumbnailData thumbnailData) {
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_RECENTS_ANIMATION_CANCELED);
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_RECENTS_ANIMATION_ENDED);
    }

    public void onRecentsAnimationFinished(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_RECENTS_ANIMATION_FINISHED);
        this.mStateCallback.lambda$setStateOnUiThread$0(STATE_RECENTS_ANIMATION_ENDED);
    }
}
