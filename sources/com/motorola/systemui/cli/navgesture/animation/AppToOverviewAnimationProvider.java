package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.Log;
import android.view.ViewGroup;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.android.systemui.shared.system.TransactionCompat;
import com.motorola.systemui.cli.navgesture.ActivityControlHelper;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.IRecentsView;
import com.motorola.systemui.cli.navgesture.Interpolators;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationTargetSet;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;

final class AppToOverviewAnimationProvider<T extends BaseGestureActivity> implements RemoteAnimationProvider {
    private T mActivity;
    private final ActivityControlHelper<T> mHelper;
    /* access modifiers changed from: private */
    public IRecentsView mRecentsView;
    private final int mTargetTaskId;

    /* access modifiers changed from: package-private */
    public long getRecentsLaunchDuration() {
        return 250;
    }

    AppToOverviewAnimationProvider(ActivityControlHelper<T> activityControlHelper, int i) {
        this.mHelper = activityControlHelper;
        this.mTargetTaskId = i;
    }

    /* access modifiers changed from: package-private */
    public boolean onActivityReady(T t, Boolean bool) {
        t.getOverviewPanel().showRunningTask(this.mTargetTaskId);
        ActivityControlHelper.AnimationFactory prepareRecentsUI = this.mHelper.prepareRecentsUI(bool.booleanValue(), false, AppToOverviewAnimationProvider$$ExternalSyntheticLambda2.INSTANCE);
        prepareRecentsUI.onRemoteAnimationReceived((RemoteAnimationTargetSet) null);
        prepareRecentsUI.createActivityController(250);
        prepareRecentsUI.setRecentsAttachedToAppWindow(true, false);
        this.mActivity = t;
        this.mRecentsView = t.getOverviewPanel();
        return false;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onActivityReady$0(AnimatorPlaybackController animatorPlaybackController) {
        animatorPlaybackController.dispatchOnStart();
        ValueAnimator duration = animatorPlaybackController.getAnimationPlayer().setDuration(250);
        duration.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        duration.start();
    }

    public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3 = remoteAnimationTargetCompatArr;
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr4 = remoteAnimationTargetCompatArr2;
        IRecentsView iRecentsView = this.mRecentsView;
        if (iRecentsView != null) {
            iRecentsView.prepareWindowAnimation(true);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                if (AppToOverviewAnimationProvider.this.mRecentsView != null) {
                    AppToOverviewAnimationProvider.this.mRecentsView.onWindowAnimationEnd();
                }
            }
        });
        if (this.mActivity == null) {
            Log.e("AppToOverviewAnimationProvider", "Animation created, before activity");
            animatorSet.play(ValueAnimator.ofInt(new int[]{0, 1}).setDuration(250));
            return animatorSet;
        }
        RemoteAnimationTargetSet remoteAnimationTargetSet = new RemoteAnimationTargetSet(remoteAnimationTargetCompatArr3, remoteAnimationTargetCompatArr4, 1);
        RemoteAnimationTargetCompat findTask = remoteAnimationTargetSet.findTask(this.mTargetTaskId);
        if (findTask == null) {
            Log.e("AppToOverviewAnimationProvider", "No closing app");
            animatorSet.play(ValueAnimator.ofInt(new int[]{0, 1}).setDuration(250));
            return animatorSet;
        }
        ClipAnimationHelper clipAnimationHelper = new ClipAnimationHelper(this.mActivity);
        int[] iArr = new int[2];
        ViewGroup rootView = this.mActivity.getRootView();
        rootView.getLocationOnScreen(iArr);
        clipAnimationHelper.updateSource(new Rect(iArr[0], iArr[1], iArr[0] + rootView.getWidth(), iArr[1] + rootView.getHeight()), findTask);
        Rect rect = new Rect();
        this.mHelper.getSwipeUpDestinationAndLength(this.mActivity.getDeviceProfile(), this.mActivity, rect);
        clipAnimationHelper.updateTargetRect(rect);
        clipAnimationHelper.prepareAnimation(false);
        ClipAnimationHelper.TransformParams syncTransactionApplier = new ClipAnimationHelper.TransformParams().setSyncTransactionApplier(new SyncRtSurfaceTransactionApplierCompat(rootView));
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(250);
        ofFloat.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
        ofFloat.addUpdateListener(new AppToOverviewAnimationProvider$$ExternalSyntheticLambda1(syncTransactionApplier, clipAnimationHelper, remoteAnimationTargetSet));
        if (remoteAnimationTargetSet.isAnimatingHome()) {
            ofFloat.addUpdateListener(new AppToOverviewAnimationProvider$$ExternalSyntheticLambda0(new RemoteAnimationTargetSet(remoteAnimationTargetCompatArr3, remoteAnimationTargetCompatArr4, 0), new TransactionCompat()));
        }
        animatorSet.play(ofFloat);
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createWindowAnimation$1(ClipAnimationHelper.TransformParams transformParams, ClipAnimationHelper clipAnimationHelper, RemoteAnimationTargetSet remoteAnimationTargetSet, ValueAnimator valueAnimator) {
        transformParams.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        clipAnimationHelper.applyTransform(remoteAnimationTargetSet, transformParams);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createWindowAnimation$2(RemoteAnimationTargetSet remoteAnimationTargetSet, TransactionCompat transactionCompat, ValueAnimator valueAnimator) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetSet.apps) {
            transactionCompat.setAlpha(remoteAnimationTargetCompat.leash, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
        transactionCompat.apply();
    }
}
