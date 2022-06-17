package com.motorola.systemui.cli.navgesture;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationTargetSetController;
import com.motorola.systemui.cli.navgesture.recents.ITaskViewAware;
import com.motorola.systemui.cli.navgesture.states.LauncherState;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;
import com.motorola.systemui.cli.navgesture.util.ScaleTranslation;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface IRecentsView {
    public static final FloatProperty<IRecentsView> CONTENT_ALPHA = new FloatProperty<IRecentsView>("contentAlpha") {
        public void setValue(IRecentsView iRecentsView, float f) {
            iRecentsView.setContentAlpha(f);
        }

        public Float get(IRecentsView iRecentsView) {
            return Float.valueOf(iRecentsView.getContentAlpha());
        }
    };
    public static final FloatProperty<IRecentsView> FULLSCREEN_PROGRESS = new FloatProperty<IRecentsView>("fullscreenProgress") {
        public void setValue(IRecentsView iRecentsView, float f) {
            iRecentsView.setFullscreenProgress(f);
        }

        public Float get(IRecentsView iRecentsView) {
            return Float.valueOf(iRecentsView.getFullscreenProgress());
        }
    };

    View asView();

    ITaskViewAware checkingBeforeStartNewTask();

    void createRecentsShowHideTransitionAnimation(LauncherState launcherState, LauncherState launcherState2, AnimatorSet animatorSet);

    void fillRemoteWindowTransformParams(ClipAnimationHelper clipAnimationHelper, ClipAnimationHelper.TransformParams transformParams);

    float getContentAlpha();

    Consumer<MotionEvent> getEventDispatcher(float f);

    float getFullscreenProgress();

    int getRunningTaskIndex();

    ScaleTranslation getScaleTranslation(LauncherState launcherState);

    long getSnapDuration();

    int getSysUiStatusNavFlags(float f);

    int getTaskIndex(View view);

    boolean goingToNewTask(PointF pointF);

    ITaskViewAware isTaskViewVisible(int i);

    boolean isTaskViewVisible(View view);

    boolean onBackPressed();

    void onGestureAnimationEnd();

    void onGestureAnimationStart(int i);

    void onSwipeUpAnimationSuccess();

    void onWindowAnimationEnd() {
    }

    void prepareWindowAnimation(boolean z) {
    }

    void resetTaskVisuals();

    void setContentAlpha(float f);

    void setFullscreenProgress(float f);

    void setRecentsAnimationTargetSetController(RecentsAnimationTargetSetController recentsAnimationTargetSetController);

    Animator setRecentsAttachedToAppWindow(boolean z, boolean z2);

    void setRemoteWindowAnimationDependentSyncRTListener(DoubleConsumer doubleConsumer);

    void setRunningTaskHidden(boolean z);

    void showNextTask() {
    }

    void showRunningTask(int i);

    void snapToNearestCenterOfScreenPosition();

    void startHome();

    void startNewTask(boolean z, Consumer<Boolean> consumer);

    void switchToScreenshot(ThumbnailData thumbnailData, Runnable runnable) {
    }

    void updateEmptyMessage();

    View updateTaskToLatestScreenshot(int i, ThumbnailData thumbnailData);

    void updateWindowAnimationProgress(float f);
}
