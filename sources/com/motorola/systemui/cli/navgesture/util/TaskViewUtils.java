package com.motorola.systemui.cli.navgesture.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;
import com.motorola.systemui.cli.navgesture.IRecentsView;
import com.motorola.systemui.cli.navgesture.Interpolators;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationTargetSet;
import com.motorola.systemui.cli.navgesture.recents.ITaskViewAware;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;
import com.motorola.systemui.cli.navgesture.util.MultiValueUpdateListener;

public final class TaskViewUtils {
    public static ITaskViewAware findTaskViewToLaunch(AbstractRecentGestureLauncher abstractRecentGestureLauncher, View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        int i;
        if (remoteAnimationTargetCompatArr == null) {
            return null;
        }
        IRecentsView overviewPanel = abstractRecentGestureLauncher.getOverviewPanel();
        if (view instanceof ITaskViewAware) {
            ITaskViewAware iTaskViewAware = (ITaskViewAware) view;
            if (overviewPanel.isTaskViewVisible(iTaskViewAware.asView())) {
                return iTaskViewAware;
            }
            return null;
        }
        int length = remoteAnimationTargetCompatArr.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                i = -1;
                break;
            }
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[i2];
            if (remoteAnimationTargetCompat.mode == 0) {
                i = remoteAnimationTargetCompat.taskId;
                break;
            }
            i2++;
        }
        if (i == -1) {
            return null;
        }
        return overviewPanel.isTaskViewVisible(i);
    }

    public static Animator getRecentsWindowAnimator(ITaskViewAware iTaskViewAware, IRecentsView iRecentsView, boolean z, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, ClipAnimationHelper clipAnimationHelper) {
        View asView = iTaskViewAware.asView();
        SyncRtSurfaceTransactionApplierCompat syncRtSurfaceTransactionApplierCompat = new SyncRtSurfaceTransactionApplierCompat(asView);
        ClipAnimationHelper.TransformParams syncTransactionApplier = new ClipAnimationHelper.TransformParams().setSyncTransactionApplier(syncRtSurfaceTransactionApplierCompat);
        final RemoteAnimationTargetSet remoteAnimationTargetSet = new RemoteAnimationTargetSet(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, 0);
        remoteAnimationTargetSet.addDependentTransactionApplier(syncRtSurfaceTransactionApplierCompat);
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
        ofFloat.addUpdateListener(new MultiValueUpdateListener(iTaskViewAware, remoteAnimationTargetSet, asView, syncTransactionApplier, iRecentsView, z) {
            final MultiValueUpdateListener.FloatProp mTaskAlpha;
            final RectF mThumbnailRect;
            final MultiValueUpdateListener.FloatProp mViewAlpha;
            final /* synthetic */ ClipAnimationHelper.TransformParams val$params;
            final /* synthetic */ IRecentsView val$recentsView;
            final /* synthetic */ boolean val$skipViewChanges;
            final /* synthetic */ RemoteAnimationTargetSet val$targetSet;
            final /* synthetic */ ITaskViewAware val$taskViewAware;
            final /* synthetic */ View val$v;

            {
                this.val$taskViewAware = r9;
                this.val$targetSet = r10;
                this.val$v = r11;
                this.val$params = r12;
                this.val$recentsView = r13;
                this.val$skipViewChanges = r14;
                Interpolator interpolator = Interpolators.LINEAR;
                this.mViewAlpha = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 75.0f, 75.0f, interpolator);
                this.mTaskAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, 0.0f, 75.0f, interpolator);
                ClipAnimationHelper.this.setTaskAlphaCallback(new TaskViewUtils$1$$ExternalSyntheticLambda0(this));
                ClipAnimationHelper.this.prepareAnimation(true);
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = r10.apps;
                ClipAnimationHelper.this.fromTaskThumbnailView(r9, remoteAnimationTargetCompatArr.length == 0 ? null : remoteAnimationTargetCompatArr[0]);
                RectF rectF = new RectF(ClipAnimationHelper.this.getTargetRect());
                this.mThumbnailRect = rectF;
                rectF.offset(-r11.getTranslationX(), -r11.getTranslationY());
                Utilities.scaleRectFAboutCenter(rectF, 1.0f / r11.getScaleX());
            }

            /* access modifiers changed from: private */
            public /* synthetic */ Float lambda$new$0(RemoteAnimationTargetCompat remoteAnimationTargetCompat, Float f) {
                return Float.valueOf(this.mTaskAlpha.value);
            }

            public void onUpdate(float f) {
                this.val$params.setProgress(1.0f - f);
                RectF applyTransform = ClipAnimationHelper.this.applyTransform(this.val$targetSet, this.val$params);
                boolean z = this.val$recentsView.getTaskIndex(this.val$v) != this.val$recentsView.getRunningTaskIndex();
                if (!this.val$skipViewChanges && z) {
                    float width = applyTransform.width() / this.mThumbnailRect.width();
                    if (!Float.isNaN(width) && !Float.isInfinite(width)) {
                        this.val$v.setScaleX(width);
                        this.val$v.setScaleY(width);
                        this.val$v.setTranslationX(applyTransform.centerX() - this.mThumbnailRect.centerX());
                        this.val$v.setTranslationY(applyTransform.centerY() - this.mThumbnailRect.centerY());
                        this.val$v.setAlpha(this.mViewAlpha.value);
                    }
                }
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                RemoteAnimationTargetSet.this.release();
            }
        });
        animatorSet.play(ofFloat);
        return animatorSet;
    }
}
