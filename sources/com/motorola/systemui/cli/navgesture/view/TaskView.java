package com.motorola.systemui.cli.navgesture.view;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Toast;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.QuickStepContract;
import com.motorola.systemui.cli.navgesture.ActivityContext;
import com.motorola.systemui.cli.navgesture.Interpolators;
import com.motorola.systemui.cli.navgesture.recents.ITaskViewAware;
import com.motorola.systemui.cli.navgesture.recents.RecentsModel;
import com.motorola.systemui.cli.navgesture.recents.TaskThumbnailCache;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import com.motorola.systemui.cli.navgesture.util.TaskCornerRadius;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TaskView extends SwipeDismissFrameLayout implements ITaskViewAware {
    private static final TimeInterpolator CURVE_INTERPOLATOR = TaskView$$ExternalSyntheticLambda0.INSTANCE;
    private static final FloatProperty<TaskView> FOCUS_TRANSITION = new FloatProperty<TaskView>("focusTransition") {
        public void setValue(TaskView taskView, float f) {
            taskView.setIconAndDimTransitionProgress(f, false);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mFocusTransitionProgress);
        }
    };
    public static final FloatProperty<TaskView> FULLSCREEN_PROGRESS = new FloatProperty<TaskView>("fullscreenProgress") {
        public void setValue(TaskView taskView, float f) {
            taskView.setFullscreenProgress(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mFullscreenProgress);
        }
    };
    private static final List<Rect> SYSTEM_GESTURE_EXCLUSION_RECT = Collections.singletonList(new Rect());
    private static final String TAG = TaskView.class.getSimpleName();
    protected final ActivityContext mActivity;
    private final float mCornerRadius;
    private final FullscreenDrawParams mCurrentFullscreenParams;
    private float mCurveScale;
    private boolean mDisallowBackGesture;
    /* access modifiers changed from: private */
    public float mFocusTransitionProgress;
    /* access modifiers changed from: private */
    public float mFullscreenProgress;
    private ObjectAnimator mIconAndDimAnimator;
    private float mIconScaleAnimStartProgress;
    private final TaskOutlineProvider mOutlineProvider;
    private TaskThumbnailView mSnapshotView;
    private float mStableAlpha;
    private Task mTask;
    private TaskThumbnailCache.ThumbnailLoadRequest mThumbnailLoadRequest;
    private final float mWindowCornerRadius;

    private static float getCurveScaleForCurveInterpolation(float f) {
        return 1.0f - (f * 0.03f);
    }

    private void setIcon(Drawable drawable) {
    }

    public View asView() {
        return this;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean needLoadTaskIcon() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onThumbnailLoad(Task task, ThumbnailData thumbnailData) {
    }

    /* access modifiers changed from: protected */
    public void onThumbnailUnLoad(Task task) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$0(float f) {
        return (((float) (-Math.cos(((double) f) * 3.141592653589793d))) / 2.0f) + 0.5f;
    }

    public TaskView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TaskView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIconScaleAnimStartProgress = 0.0f;
        this.mFocusTransitionProgress = 1.0f;
        this.mStableAlpha = 1.0f;
        this.mActivity = ActivityContext.lookupContext(context);
        setOnClickListener(new TaskView$$ExternalSyntheticLambda1(this));
        float f = TaskCornerRadius.get(context);
        this.mCornerRadius = f;
        this.mWindowCornerRadius = QuickStepContract.getWindowCornerRadius(context.getResources());
        FullscreenDrawParams fullscreenDrawParams = new FullscreenDrawParams(f);
        this.mCurrentFullscreenParams = fullscreenDrawParams;
        TaskOutlineProvider taskOutlineProvider = new TaskOutlineProvider(getResources(), fullscreenDrawParams);
        this.mOutlineProvider = taskOutlineProvider;
        setOutlineProvider(taskOutlineProvider);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        String str = TAG;
        Log.d(str, "TaskView onclick = " + this.mTask.getClass());
        if (getTask() == null) {
            DebugLog.m99e(str, "TaskView: null task");
        } else {
            launchTask(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mSnapshotView = (TaskThumbnailView) findViewById(R$id.snapshot);
    }

    public Task getTask() {
        return this.mTask;
    }

    public TaskThumbnailView getThumbnail() {
        return this.mSnapshotView;
    }

    public void bind(Task task) {
        this.mTask = task;
        this.mSnapshotView.bind(task);
        onTaskListVisibilityChanged(true);
    }

    public void onTaskListVisibilityChanged(boolean z) {
        cancelPendingLoadTasks();
        if (z) {
            this.mThumbnailLoadRequest = RecentsModel.INSTANCE.lambda$get$0(getContext()).getThumbnailCache().updateThumbnailInBackground(this.mTask, new TaskView$$ExternalSyntheticLambda5(this));
            if (!needLoadTaskIcon()) {
                setIcon((Drawable) null);
                return;
            }
            return;
        }
        this.mSnapshotView.setThumbnail((Task) null, (ThumbnailData) null);
        setIcon((Drawable) null);
        Task task = this.mTask;
        task.thumbnail = null;
        onThumbnailUnLoad(task);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTaskListVisibilityChanged$2(ThumbnailData thumbnailData) {
        this.mSnapshotView.setThumbnail(this.mTask, thumbnailData);
        onThumbnailLoad(this.mTask, thumbnailData);
    }

    private void cancelPendingLoadTasks() {
        TaskThumbnailCache.ThumbnailLoadRequest thumbnailLoadRequest = this.mThumbnailLoadRequest;
        if (thumbnailLoadRequest != null) {
            thumbnailLoadRequest.cancel();
            this.mThumbnailLoadRequest = null;
        }
    }

    /* access modifiers changed from: private */
    public void setIconAndDimTransitionProgress(float f, boolean z) {
        float f2 = 1.0f;
        if (z) {
            f = 1.0f - f;
        }
        this.mFocusTransitionProgress = f;
        this.mSnapshotView.setDimAlphaMultipler(f);
        float f3 = z ? 0.82857144f : 0.0f;
        if (!z) {
            f2 = 0.17142858f;
        }
        Interpolators.clampToProgress(Interpolators.FAST_OUT_SLOW_IN, f3, f2).getInterpolation(f);
    }

    /* access modifiers changed from: protected */
    public void setIconScaleAndDim(float f) {
        setIconScaleAndDim(f, false);
    }

    private void setIconScaleAndDim(float f, boolean z) {
        ObjectAnimator objectAnimator = this.mIconAndDimAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        setIconAndDimTransitionProgress(f, z);
    }

    public void resetVisualProperties() {
        resetViewStatus();
        setFullscreenProgress(0.0f);
    }

    public void setStableAlpha(float f) {
        this.mStableAlpha = f;
        boolean z = f == 1.0f;
        this.mDisallowBackGesture = z;
        setDisallowBackGesture(z);
        setAlpha(this.mStableAlpha);
    }

    private void resetViewStatus() {
        setCurveScale(1.0f);
        setTranslationX(0.0f);
        setTranslationY(0.0f);
        setTranslationZ(0.0f);
        setAlpha(this.mStableAlpha);
        setIconScaleAndDim(1.0f);
        this.mSnapshotView.setDimAlpha(0.0f);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setPivotX(((float) (i3 - i)) * 0.5f);
        setPivotY(((float) this.mSnapshotView.getTop()) + (((float) this.mSnapshotView.getHeight()) * 0.5f));
        setDisallowBackGesture(this.mDisallowBackGesture);
    }

    @TargetApi(29)
    public void setDisallowBackGesture(boolean z) {
        this.mDisallowBackGesture = z;
        if (z) {
            SYSTEM_GESTURE_EXCLUSION_RECT.get(0).set(0, 0, getWidth(), getHeight());
        } else {
            SYSTEM_GESTURE_EXCLUSION_RECT.get(0).set(0, 0, 0, 0);
        }
        setSystemGestureExclusionRects(SYSTEM_GESTURE_EXCLUSION_RECT);
    }

    public static float getCurveScaleForInterpolation(float f) {
        return getCurveScaleForCurveInterpolation(CURVE_INTERPOLATOR.getInterpolation(f));
    }

    public void setCurveScale(float f) {
        this.mCurveScale = f;
        onScaleChanged();
    }

    private void onScaleChanged() {
        float f = this.mCurveScale;
        setScaleX(f);
        setScaleY(f);
    }

    public void launchTask(boolean z) {
        launchTask(z, false);
    }

    public void launchTask(boolean z, boolean z2) {
        launchTask(z, z2, new TaskView$$ExternalSyntheticLambda6(this), getHandler());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$launchTask$3(Boolean bool) {
        if (!bool.booleanValue()) {
            notifyTaskLaunchFailed(TAG);
        }
    }

    public void launchTask(boolean z, boolean z2, Consumer<Boolean> consumer, Handler handler) {
        if (this.mTask == null) {
            return;
        }
        if (z) {
            ActivityOptions activityLaunchOptions = this.mActivity.getActivityLaunchOptions(this);
            if (z2) {
                ActivityOptionsCompat.setFreezeRecentTasksList(activityLaunchOptions);
            }
            activityLaunchOptions.setLaunchDisplayId(this.mTask.key.displayId);
            ActivityManagerWrapper.getInstance().startActivityFromRecentsAsync(this.mTask.key, activityLaunchOptions, consumer, handler);
            return;
        }
        ActivityOptions makeCustomAnimation = ActivityOptionsCompat.makeCustomAnimation(getContext(), 0, 0, new TaskView$$ExternalSyntheticLambda4(consumer, handler), handler);
        if (z2) {
            ActivityOptionsCompat.setFreezeRecentTasksList(makeCustomAnimation);
        }
        makeCustomAnimation.setLaunchDisplayId(this.mTask.key.displayId);
        ActivityManagerWrapper.getInstance().startActivityFromRecentsAsync(this.mTask.key, makeCustomAnimation, new TaskView$$ExternalSyntheticLambda7(consumer, handler), handler);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$launchTask$5(Consumer consumer, Handler handler) {
        if (consumer != null) {
            handler.post(new TaskView$$ExternalSyntheticLambda2(consumer));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$launchTask$7(Consumer consumer, Handler handler, Boolean bool) {
        if (consumer != null && !bool.booleanValue()) {
            handler.post(new TaskView$$ExternalSyntheticLambda3(consumer));
        }
    }

    public void notifyTaskLaunchFailed(String str) {
        String str2 = "Failed to launch task";
        if (this.mTask != null) {
            str2 = str2 + " (task=" + this.mTask.key.baseIntent + " userId=" + this.mTask.key.userId + ")";
        }
        Log.w(str, str2);
        Toast.makeText(getContext(), R$string.activity_not_available, 0).show();
    }

    public void setFullscreenProgress(float f) {
        float boundToRange = Utilities.boundToRange(f, 0.0f, 1.0f);
        this.mFullscreenProgress = boundToRange;
        boolean z = boundToRange > 0.0f;
        needLoadTaskIcon();
        setClipChildren(!z);
        setClipToPadding(!z);
        TaskThumbnailView thumbnail = getThumbnail();
        RectF insetsToDrawInFullscreen = thumbnail.getInsetsToDrawInFullscreen();
        float f2 = insetsToDrawInFullscreen.left;
        float f3 = this.mFullscreenProgress;
        float f4 = f2 * f3;
        float f5 = insetsToDrawInFullscreen.right * f3;
        this.mCurrentFullscreenParams.setInsets(f4, insetsToDrawInFullscreen.top * f3, f5, insetsToDrawInFullscreen.bottom * f3);
        this.mCurrentFullscreenParams.setCornerRadius(Utilities.mapRange(this.mFullscreenProgress, this.mCornerRadius, this.mWindowCornerRadius) / 1.0f);
        if (getWidth() > 0) {
            this.mCurrentFullscreenParams.setScale(((float) getWidth()) / ((((float) getWidth()) + f4) + f5));
        }
        setIconScaleAndDim(boundToRange, true);
        thumbnail.setFullscreenParams(this.mCurrentFullscreenParams);
        this.mOutlineProvider.setFullscreenParams(this.mCurrentFullscreenParams);
        invalidateOutline();
    }

    public ThumbnailData getThumbnailData() {
        return getThumbnail().getThumbnailData();
    }

    public int getTaskId() {
        Task task = this.mTask;
        if (task == null) {
            return -1;
        }
        return task.key.f124id;
    }

    private static final class TaskOutlineProvider extends ViewOutlineProvider {
        private FullscreenDrawParams mFullscreenParams;
        private final int mMarginTop = 0;

        TaskOutlineProvider(Resources resources, FullscreenDrawParams fullscreenDrawParams) {
            this.mFullscreenParams = fullscreenDrawParams;
        }

        public void setFullscreenParams(FullscreenDrawParams fullscreenDrawParams) {
            this.mFullscreenParams = fullscreenDrawParams;
        }

        public void getOutline(View view, Outline outline) {
            FullscreenDrawParams fullscreenDrawParams = this.mFullscreenParams;
            RectF rectF = fullscreenDrawParams.mCurrentDrawnInsets;
            float f = fullscreenDrawParams.mScale;
            outline.setRoundRect(0, (int) (((float) this.mMarginTop) * f), (int) ((rectF.left + ((float) view.getWidth()) + rectF.right) * f), (int) ((rectF.top + ((float) view.getHeight()) + rectF.bottom) * f), this.mFullscreenParams.mCurrentDrawnCornerRadius);
        }
    }

    static class FullscreenDrawParams {
        float mCurrentDrawnCornerRadius;
        RectF mCurrentDrawnInsets = new RectF();
        float mScale = 1.0f;

        public FullscreenDrawParams(float f) {
            setCornerRadius(f);
        }

        public void setInsets(float f, float f2, float f3, float f4) {
            this.mCurrentDrawnInsets.set(f, f2, f3, f4);
        }

        public void setCornerRadius(float f) {
            this.mCurrentDrawnCornerRadius = f;
        }

        public void setScale(float f) {
            this.mScale = f;
        }

        public String toString() {
            return "FullscreenDrawParams{mCurrentDrawnInsets=" + this.mCurrentDrawnInsets + ", mCurrentDrawnCornerRadius=" + this.mCurrentDrawnCornerRadius + ", mScale=" + this.mScale + '}';
        }
    }
}
