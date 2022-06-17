package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.SystemClock;
import android.view.ViewConfiguration;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.ActivityControlHelper;
import com.motorola.systemui.cli.navgesture.ActivityInitListener;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.IRecentsView;
import com.motorola.systemui.cli.navgesture.OverviewComponentObserver;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.recents.RecentsModel;
import java.util.ArrayList;
import java.util.function.Consumer;

public class OverviewCommandHelper {
    private final ActivityManagerWrapper mAM = ActivityManagerWrapper.getInstance();
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public long mLastToggleTime;
    /* access modifiers changed from: private */
    public final OverviewComponentObserver mOverviewComponentObserver;
    /* access modifiers changed from: private */
    public final RecentsModel mRecentsModel;

    public OverviewCommandHelper(Context context, OverviewComponentObserver overviewComponentObserver) {
        this.mContext = context;
        this.mRecentsModel = RecentsModel.INSTANCE.lambda$get$0(context);
        this.mOverviewComponentObserver = overviewComponentObserver;
    }

    public void onOverviewToggle() {
        if (!this.mAM.isScreenPinningActive()) {
            this.mAM.closeSystemWindows("recentapps");
            AppExecutors.m97ui().execute(new RecentsActivityCommand());
        }
    }

    private class RecentsActivityCommand<T extends BaseGestureActivity> implements Runnable {
        private final AppToOverviewAnimationProvider<T> mAnimationProvider;
        private final long mCreateTime = SystemClock.elapsedRealtime();
        protected final ActivityControlHelper<T> mHelper;
        private ActivityInitListener mListener;

        /* access modifiers changed from: protected */
        public void onTransitionComplete() {
        }

        public RecentsActivityCommand() {
            ActivityControlHelper<T> activityControlHelper = OverviewCommandHelper.this.mOverviewComponentObserver.getActivityControlHelper();
            this.mHelper = activityControlHelper;
            this.mAnimationProvider = new AppToOverviewAnimationProvider<>(activityControlHelper, RecentsModel.getRunningTaskId());
            OverviewCommandHelper.this.mRecentsModel.getTasks((Consumer<ArrayList<Task>>) null);
        }

        public void run() {
            long access$300 = this.mCreateTime - OverviewCommandHelper.this.mLastToggleTime;
            long unused = OverviewCommandHelper.this.mLastToggleTime = this.mCreateTime;
            if (!handleCommand(access$300) && !this.mHelper.switchToRecentsIfVisible(new C2704x67c32af8(this))) {
                ActivityInitListener<T> createActivityInitListener = this.mHelper.createActivityInitListener(new C2705x67c32af9(this));
                this.mListener = createActivityInitListener;
                createActivityInitListener.registerAndStartActivity(OverviewCommandHelper.this.mOverviewComponentObserver.getOverviewIntent(), new C2703x67c32af7(this), OverviewCommandHelper.this.mContext, AppExecutors.m97ui().getHandler(), this.mAnimationProvider.getRecentsLaunchDuration());
            }
        }

        /* access modifiers changed from: protected */
        public boolean handleCommand(long j) {
            IRecentsView visibleRecentsView = this.mHelper.getVisibleRecentsView();
            if (visibleRecentsView != null) {
                visibleRecentsView.showNextTask();
                return true;
            } else if (j < ((long) ViewConfiguration.getDoubleTapTimeout())) {
                return true;
            } else {
                return false;
            }
        }

        /* access modifiers changed from: private */
        public boolean onActivityReady(Boolean bool) {
            T createdActivity = this.mHelper.getCreatedActivity();
            if (createdActivity == null) {
                return false;
            }
            return this.mAnimationProvider.onActivityReady(createdActivity, bool);
        }

        /* access modifiers changed from: private */
        public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
            this.mListener.unregister();
            AnimatorSet createWindowAnimation = this.mAnimationProvider.createWindowAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2);
            createWindowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    RecentsActivityCommand.this.onTransitionComplete();
                }
            });
            return createWindowAnimation;
        }
    }
}
