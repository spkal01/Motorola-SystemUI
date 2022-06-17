package com.motorola.systemui.cli.navgesture.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.motorola.systemui.cli.navgesture.AbstractRecentGestureLauncher;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.IRecentsView;
import com.motorola.systemui.cli.navgesture.Interpolators;
import com.motorola.systemui.cli.navgesture.RecentsViewStateHandler;
import com.motorola.systemui.cli.navgesture.SysUINavigationMode;
import com.motorola.systemui.cli.navgesture.Themes;
import com.motorola.systemui.cli.navgesture.adapter.BaseBindingViewHolder;
import com.motorola.systemui.cli.navgesture.adapter.BaseViewAdapter;
import com.motorola.systemui.cli.navgesture.animation.remote.RecentsAnimationTargetSetController;
import com.motorola.systemui.cli.navgesture.display.SecondaryDisplay;
import com.motorola.systemui.cli.navgesture.recents.ITaskViewAware;
import com.motorola.systemui.cli.navgesture.recents.RecentsModel;
import com.motorola.systemui.cli.navgesture.recents.TaskThumbnailCache;
import com.motorola.systemui.cli.navgesture.states.LauncherState;
import com.motorola.systemui.cli.navgesture.states.StateManager;
import com.motorola.systemui.cli.navgesture.util.ClipAnimationHelper;
import com.motorola.systemui.cli.navgesture.util.DebugLog;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;
import com.motorola.systemui.cli.navgesture.util.DeviceProfileProvider;
import com.motorola.systemui.cli.navgesture.util.LayoutCalculator;
import com.motorola.systemui.cli.navgesture.util.ScaleTranslation;
import com.motorola.systemui.cli.navgesture.util.SwipeDismissHelper;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import com.motorola.systemui.cli.navgesture.view.ISwipeDismissView;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecentsViewContainer extends FrameLayout implements IRecentsView, StateManager.StateListener, DeviceProfileProvider.DeviceProfileChangeListener, TaskThumbnailCache.HighResLoadingState.HighResLoadingStateChangedCallback, RecentsModel.TaskVisualsChangeListener {
    /* access modifiers changed from: private */
    public AbstractRecentGestureLauncher mActivity;
    protected float mContentAlpha;
    private DeviceProfile mDeviceProfile;
    private Point mDownPoint;
    private final Drawable mEmptyIcon;
    private final CharSequence mEmptyMessage;
    private final int mEmptyMessagePadding;
    private final TextPaint mEmptyMessagePaint;
    private Layout mEmptyTextLayout;
    private boolean mFreezeViewVisibility;
    protected float mFullscreenProgress;
    private boolean mHandleTaskStackChanges;
    private final Rect mInsets;
    private final Point mLastMeasureSize;
    /* access modifiers changed from: private */
    public IOnTaskItemOperationCallback mOnTaskItemOperationCallback;
    private boolean mOverviewStateEnabled;
    private RecentsModel mRecentsModel;
    /* access modifiers changed from: private */
    public RecentsTaskViewHelper mRecentsTaskViewHelper;
    /* access modifiers changed from: private */
    public int mRunningTaskId;
    private boolean mRunningTaskTileHidden;
    private SecondaryDisplay mSecondaryDisplay;
    private boolean mShowEmptyMessage;
    private float mSquaredTouchSlop;
    private int mTaskListChangeId;
    private ClipAnimationHelper mTempClipAnimationHelper;
    private final Rect mTempRect;
    private final Rect mTouchDeadZoneRect;
    private boolean mTouchDownToStartHome;

    private interface IOnTaskItemOperationCallback {
        boolean performDismiss(Task task);
    }

    interface RecentsTaskViewHelper {
        boolean dispatchTouchEvent(MotionEvent motionEvent);

        void fillRemoteWindowTransformParams(ClipAnimationHelper clipAnimationHelper, ClipAnimationHelper.TransformParams transformParams);

        int getCurrentItem();

        int getRunningTaskIndex();

        ITaskViewAware getRunningTaskView();

        long getSnapDuration();

        int getSysUiStatusNavFlags(float f);

        int getTaskCount();

        int getTaskIndex(ITaskViewAware iTaskViewAware);

        ITaskViewAware getTaskViewAt(int i);

        ITaskViewAware getTaskViewByTaskId(int i);

        int getTaskViewCount();

        boolean goingToNewTask(PointF pointF);

        boolean isHandlingTouchEvent();

        boolean isLayoutRtl();

        boolean isTaskViewVisible(ITaskViewAware iTaskViewAware);

        boolean onBackPressed();

        void onFinishInflate();

        void onGestureAnimationEnd();

        void onGestureAnimationStart();

        void onHighResLoadingStateChanged(boolean z);

        void onTaskContentAlphaChanged(Predicate<TaskParamsOverride> predicate, float f);

        void onTaskThumbnailChanged(int i, ThumbnailData thumbnailData);

        boolean onTouchEvent(MotionEvent motionEvent);

        void removeTask(Task task);

        void resetTaskVisuals();

        void setCurrentItem(int i);

        void setFullscreenProgress(float f);

        Animator setRecentsAttachedToAppWindow(boolean z, boolean z2);

        void setRemoteWindowAnimationDependentSyncRTListener(DoubleConsumer doubleConsumer);

        void setup();

        void snapToNearestCenterOfScreenPosition();

        void startNewTask(boolean z, Consumer<Boolean> consumer);

        void updateRecentsList(List<TaskParamsOverride> list);

        void updateTap2GoHomeDeadZoneRect(Rect rect);

        void updateWindowAnimationProgress(float f);
    }

    public View asView() {
        return this;
    }

    public void setRecentsAnimationTargetSetController(RecentsAnimationTargetSetController recentsAnimationTargetSetController) {
    }

    public RecentsViewContainer(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecentsViewContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mInsets = new Rect();
        this.mTempRect = new Rect();
        this.mTouchDeadZoneRect = new Rect();
        this.mDownPoint = new Point();
        this.mContentAlpha = 1.0f;
        this.mFullscreenProgress = 0.0f;
        this.mTaskListChangeId = -1;
        this.mLastMeasureSize = new Point();
        this.mOnTaskItemOperationCallback = new IOnTaskItemOperationCallback() {
            public boolean performDismiss(Task task) {
                DebugLog.m98d("StateManager", "perform remove task: task id =" + task.key.f124id);
                ActivityManagerWrapper.getInstance().removeTask(task.key.f124id);
                RecentsViewContainer.this.mRecentsTaskViewHelper.removeTask(task);
                return true;
            }
        };
        this.mActivity = (AbstractRecentGestureLauncher) BaseGestureActivity.fromContext(context);
        Drawable drawable = context.getDrawable(R$drawable.zz_moto_cli_empty_recents);
        this.mEmptyIcon = drawable;
        drawable.setCallback(this);
        this.mEmptyMessage = context.getText(R$string.zz_moto_cli_recents_empty_message);
        TextPaint textPaint = new TextPaint(1);
        this.mEmptyMessagePaint = textPaint;
        textPaint.setColor(Themes.getAttrColor(context, 16842806));
        textPaint.setTextSize(getResources().getDimension(R$dimen.recents_empty_message_text_size));
        textPaint.setTypeface(Typeface.create(Themes.getDefaultBodyFont(context), 0));
        this.mEmptyMessagePadding = getResources().getDimensionPixelSize(R$dimen.recents_empty_message_text_padding);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        C27332 r0 = new RecentsTaskViewHelper() {
            RecentsViewAdapter adapter;
            boolean isLayoutRtl;
            LinearLayoutManager layoutManager;
            OnScrollCallback onScrollCallback;
            OrientationHelper orientationHelper;
            int pageSpacing;
            PagerSnapHelper pagerSnapHelper;
            SparseIntArray positionOffsetInPx;
            RecyclerView recyclerView;
            int scaledTouchSlop;
            ViewPager2 viewPager;
            boolean willHandleNextEvent;

            public long getSnapDuration() {
                return 350;
            }

            public void onGestureAnimationEnd() {
            }

            public void updateWindowAnimationProgress(float f) {
            }

            /* renamed from: com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$2$OnScrollCallback */
            class OnScrollCallback extends RecyclerView.OnScrollListener {
                DoubleConsumer callback;

                OnScrollCallback() {
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (this.callback != null) {
                        RecentsViewContainer.this.post(new C2737xe7dd8712(this, i));
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onScrolled$0(int i) {
                    DoubleConsumer doubleConsumer = this.callback;
                    if (doubleConsumer != null) {
                        doubleConsumer.accept((double) (((float) i) * 1.0f));
                    }
                }
            }

            public void onFinishInflate() {
                ViewPager2 viewPager2 = (ViewPager2) RecentsViewContainer.this.requireViewById(R$id.recents_view);
                this.viewPager = viewPager2;
                RecyclerView recyclerView2 = (RecyclerView) viewPager2.getChildAt(0);
                this.recyclerView = recyclerView2;
                this.layoutManager = (LinearLayoutManager) recyclerView2.getLayoutManager();
                this.recyclerView.setClipToPadding(false);
                this.recyclerView.setClipChildren(false);
            }

            public void setup() {
                this.scaledTouchSlop = ViewConfiguration.get(RecentsViewContainer.this.getContext()).getScaledTouchSlop();
                this.pageSpacing = RecentsViewContainer.this.getResources().getDimensionPixelSize(R$dimen.recents_page_spacing);
                this.onScrollCallback = new OnScrollCallback();
                this.adapter = new RecentsViewAdapter(RecentsViewContainer.this.mOnTaskItemOperationCallback);
                this.positionOffsetInPx = new SparseIntArray();
                this.viewPager.setOffscreenPageLimit(5);
                boolean z = false;
                this.viewPager.setOrientation(0);
                if (RecentsViewContainer.this.getResources().getConfiguration().getLayoutDirection() != 1) {
                    z = true;
                }
                this.isLayoutRtl = z;
                this.viewPager.setPageTransformer(new MarginPageTransformer(this.pageSpacing));
                this.viewPager.setAdapter(this.adapter);
                this.recyclerView.setOnFlingListener((RecyclerView.OnFlingListener) null);
                PagerSnapHelper pagerSnapHelper2 = new PagerSnapHelper();
                this.pagerSnapHelper = pagerSnapHelper2;
                pagerSnapHelper2.attachToRecyclerView(this.recyclerView);
                this.orientationHelper = OrientationHelper.createHorizontalHelper(this.layoutManager);
                this.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    public void onPageSelected(int i) {
                        DebugLog.m98d("RecentsView", "RecentsViewContainer#onPageSelected: position = " + i);
                    }

                    public void onPageScrolled(int i, float f, int i2) {
                        C27332.this.positionOffsetInPx.put(i, i2);
                    }

                    public void onPageScrollStateChanged(int i) {
                        DebugLog.m98d("RecentsView", "RecentsViewContainer#onPageScrollStateChanged: state = " + i);
                    }
                });
                this.recyclerView.addOnScrollListener(this.onScrollCallback);
            }

            public boolean isLayoutRtl() {
                return this.isLayoutRtl;
            }

            public void setCurrentItem(int i) {
                DebugLog.m98d("RecentsView", "RecentsViewContainer#setCurrentItem: pos = " + i);
                this.viewPager.setCurrentItem(i, true);
            }

            public int getCurrentItem() {
                return this.viewPager.getCurrentItem();
            }

            public TaskView getTaskViewByTaskId(int i) {
                for (int i2 = 0; i2 < getTaskViewCount(); i2++) {
                    View childAt = this.layoutManager.getChildAt(i2);
                    if (childAt instanceof TaskView) {
                        TaskView taskView = (TaskView) childAt;
                        if (this.adapter.sameTask(i, taskView.getTask())) {
                            return taskView;
                        }
                    }
                }
                return null;
            }

            public int getTaskCount() {
                return this.adapter.getItemCount();
            }

            public int getTaskViewCount() {
                return this.layoutManager.getChildCount();
            }

            public TaskView getTaskViewAt(int i) {
                View childAt = this.layoutManager.getChildAt(i);
                if (childAt instanceof TaskView) {
                    return (TaskView) childAt;
                }
                return null;
            }

            public TaskView getRunningTaskView() {
                return getTaskViewByTaskId(RecentsViewContainer.this.mRunningTaskId);
            }

            public int getTaskIndex(ITaskViewAware iTaskViewAware) {
                return this.recyclerView.indexOfChild(iTaskViewAware.asView());
            }

            public void onTaskContentAlphaChanged(Predicate<TaskParamsOverride> predicate, float f) {
                this.adapter.onTaskContentAlphaChanged(predicate, f);
            }

            public void setRemoteWindowAnimationDependentSyncRTListener(DoubleConsumer doubleConsumer) {
                this.onScrollCallback.callback = doubleConsumer;
            }

            public int getSysUiStatusNavFlags(float f) {
                View findSnapView = this.pagerSnapHelper.findSnapView(this.layoutManager);
                if (findSnapView instanceof TaskView) {
                    return ((TaskView) findSnapView).getThumbnail().getSysUiStatusNavFlags();
                }
                return 0;
            }

            public void setFullscreenProgress(float f) {
                this.adapter.onTaskFullScreenChanged(f);
            }

            public float getScrollForRunningTaskView() {
                int runningTaskIndex;
                if (this.recyclerView.computeHorizontalScrollExtent() == 0 || (runningTaskIndex = getRunningTaskIndex()) == -1) {
                    return 0.0f;
                }
                if (this.positionOffsetInPx.get(runningTaskIndex, 0) == 0) {
                    return 0.0f;
                }
                int i = 0;
                for (int i2 = 0; i2 < this.positionOffsetInPx.size(); i2++) {
                    if (i2 != 0) {
                        i += this.pageSpacing;
                    }
                    i += this.positionOffsetInPx.valueAt(i2);
                }
                return (float) i;
            }

            public void fillRemoteWindowTransformParams(ClipAnimationHelper clipAnimationHelper, ClipAnimationHelper.TransformParams transformParams) {
                float scrollForRunningTaskView = getScrollForRunningTaskView() * RecentsViewContainer.this.getScaleX();
                transformParams.setOffsetX(scrollForRunningTaskView).setOffsetScale(getScaleForOffsetX(scrollForRunningTaskView, clipAnimationHelper.getTargetRect().width()));
            }

            public Animator setRecentsAttachedToAppWindow(boolean z, boolean z2) {
                int runningTaskIndex = getRunningTaskIndex();
                TaskView taskViewAt = getTaskViewAt(0);
                if (runningTaskIndex == 0 && taskViewAt != null) {
                    float scaleX = RecentsViewContainer.this.getScaleX();
                    float scrollForRunningTaskView = getScrollForRunningTaskView();
                    float f = StateManager.NORMAL.getOverviewScaleTranslation(RecentsViewContainer.this.mActivity).translationX;
                    int width = taskViewAt.getWidth();
                    float f2 = 0.0f;
                    float max = Math.max(0.0f, (f - (((float) (this.pageSpacing + width)) * scaleX)) + ((((float) width) * (scaleX - 1.0f)) / 2.0f));
                    if (isLayoutRtl()) {
                        max = -max;
                    }
                    float f3 = max - scrollForRunningTaskView;
                    float f4 = z ? f3 : 0.0f;
                    if (!z) {
                        f2 = f3;
                    }
                    if (RecentsViewContainer.this.isShown() || !z2) {
                        f4 = RecentsViewContainer.this.getTranslationX();
                    } else {
                        RecentsViewContainer.this.setTranslationX(f4);
                    }
                    if (!z2) {
                        RecentsViewContainer.this.setTranslationX(f2);
                    } else {
                        return ObjectAnimator.ofFloat(RecentsViewContainer.this, View.TRANSLATION_X, new float[]{f4, f2});
                    }
                }
                return null;
            }

            private float getScaleForOffsetX(float f, float f2) {
                return TaskView.getCurveScaleForInterpolation(Math.min(1.0f, f / ((((((float) RecentsViewContainer.this.mActivity.getDeviceProfile().widthPx) * 1.0f) / 2.0f) + (f2 / 2.0f)) + ((float) this.pageSpacing))));
            }

            public int getRunningTaskIndex() {
                return this.adapter.getRunningTaskIndex(RecentsViewContainer.this.mRunningTaskId);
            }

            public boolean isTaskViewVisible(ITaskViewAware iTaskViewAware) {
                return this.recyclerView.indexOfChild(iTaskViewAware.asView()) != -1;
            }

            public boolean goingToNewTask(PointF pointF) {
                int runningTaskIndex;
                int findTargetSnapPosition = this.pagerSnapHelper.findTargetSnapPosition(this.layoutManager, (int) pointF.x, (int) pointF.y);
                DebugLog.m98d("RecentsView", "RecentsViewContainer#goingToNewTask: findTargetSnapPosition = " + findTargetSnapPosition);
                View findSnapView = this.pagerSnapHelper.findSnapView(this.layoutManager);
                if (findSnapView == null) {
                    return false;
                }
                int indexOfChild = this.recyclerView.indexOfChild(findSnapView);
                DebugLog.m98d("RecentsView", "RecentsViewContainer#goingToNewTask: targetPosition = " + indexOfChild + " cur = " + this.viewPager.getCurrentItem() + " running = " + getRunningTaskIndex());
                if (indexOfChild == -1 || (runningTaskIndex = getRunningTaskIndex()) < 0 || indexOfChild == runningTaskIndex) {
                    return false;
                }
                return true;
            }

            public void onTaskThumbnailChanged(int i, ThumbnailData thumbnailData) {
                this.adapter.onTaskThumbnailChanged(i, thumbnailData);
            }

            public void onHighResLoadingStateChanged(boolean z) {
                this.adapter.onHighResLoadingStateChanged(z);
            }

            public void updateRecentsList(List<TaskParamsOverride> list) {
                this.adapter.swap(list);
                DebugLog.m98d("RecentsView", "RecentsViewContainer#updateRecentsList: getRunningTaskIndex = " + getRunningTaskIndex() + " cur = " + getCurrentItem() + " scrollPos = " + this.positionOffsetInPx);
                setCurrentItem(getRunningTaskIndex());
                this.viewPager.post(new RecentsViewContainer$2$$ExternalSyntheticLambda0(this));
                this.viewPager.setUserInputEnabled(list.isEmpty() ^ true);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$updateRecentsList$0() {
                this.viewPager.requestTransform();
            }

            public void snapToNearestCenterOfScreenPosition() {
                View findSnapView = this.pagerSnapHelper.findSnapView(this.layoutManager);
                if (findSnapView != null) {
                    int[] calculateDistanceToFinalSnap = this.pagerSnapHelper.calculateDistanceToFinalSnap(this.layoutManager, findSnapView);
                    Objects.requireNonNull(calculateDistanceToFinalSnap);
                    if (calculateDistanceToFinalSnap[0] != 0) {
                        this.recyclerView.smoothScrollBy(calculateDistanceToFinalSnap[0], 0, Interpolators.SCROLL);
                    }
                }
            }

            public void startNewTask(boolean z, Consumer<Boolean> consumer) {
                TaskView taskViewAt = getTaskViewAt(getCurrentItem());
                if (taskViewAt == null) {
                    DebugLog.m101w("RecentsView", "RecentsViewContainer:startNewTask: null view");
                    return;
                }
                if (consumer != null) {
                    consumer = consumer.andThen(new RecentsViewContainer$2$$ExternalSyntheticLambda2(taskViewAt));
                }
                taskViewAt.launchTask(false, true, consumer, RecentsViewContainer.this.getHandler());
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$startNewTask$1(TaskView taskView, Boolean bool) {
                if (!bool.booleanValue()) {
                    taskView.notifyTaskLaunchFailed("RecentsView");
                } else {
                    DebugLog.m98d("RecentsView", "RecentsViewContainer:startNewTask: success");
                }
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                boolean z;
                int scrollX = RecentsViewContainer.this.getScrollX() - this.viewPager.getLeft();
                int scrollY = RecentsViewContainer.this.getScrollY() - this.viewPager.getTop();
                motionEvent.offsetLocation((float) scrollX, (float) scrollY);
                boolean z2 = false;
                if (motionEvent.getAction() == 0) {
                    this.willHandleNextEvent = false;
                }
                if (!this.viewPager.isUserInputEnabled()) {
                    this.willHandleNextEvent = false;
                }
                if (this.willHandleNextEvent) {
                    this.recyclerView.dispatchTouchEvent(motionEvent);
                    motionEvent.offsetLocation((float) (-scrollX), (float) (-scrollY));
                    return true;
                } else if (Utilities.pointInView(this.recyclerView, motionEvent.getX(), motionEvent.getY(), (float) this.scaledTouchSlop)) {
                    this.willHandleNextEvent = true;
                    motionEvent.offsetLocation((float) (-scrollX), (float) (-scrollY));
                    return false;
                } else {
                    int taskViewCount = getTaskViewCount();
                    int i = 0;
                    while (true) {
                        if (i >= taskViewCount) {
                            z = false;
                            break;
                        } else if (Utilities.pointInView(getTaskViewAt(i), motionEvent.getX(), motionEvent.getY(), (float) this.scaledTouchSlop)) {
                            z = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (z) {
                        this.willHandleNextEvent = true;
                        z2 = this.recyclerView.dispatchTouchEvent(motionEvent);
                    } else {
                        this.recyclerView.dispatchTouchEvent(motionEvent);
                    }
                    motionEvent.offsetLocation((float) (-scrollX), (float) (-scrollY));
                    return z2;
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (this.layoutManager.getChildCount() <= 0 || Utilities.shouldDisableGestures(motionEvent)) {
                    return false;
                }
                return this.recyclerView.onTouchEvent(motionEvent);
            }

            public boolean isHandlingTouchEvent() {
                return this.recyclerView.getScrollState() == 1;
            }

            public void updateTap2GoHomeDeadZoneRect(Rect rect) {
                int taskViewCount = getTaskViewCount();
                if (taskViewCount > 0) {
                    Rect rect2 = new Rect();
                    TaskView taskViewAt = getTaskViewAt(taskViewCount - 1);
                    Objects.requireNonNull(taskViewAt);
                    Utilities.getDescendantCoordRelativeToAncestor(taskViewAt, RecentsViewContainer.this, rect2);
                    Rect rect3 = new Rect();
                    TaskView taskViewAt2 = getTaskViewAt(0);
                    Objects.requireNonNull(taskViewAt2);
                    Utilities.getDescendantCoordRelativeToAncestor(taskViewAt2, RecentsViewContainer.this, rect3);
                    rect.union(rect2);
                    rect.union(rect3);
                }
            }

            public void onGestureAnimationStart() {
                DebugLog.m98d("RecentsView", "RecentsViewContainer:onGestureAnimationStart: clear offset cache " + this.positionOffsetInPx);
                this.positionOffsetInPx.clear();
            }

            public void resetTaskVisuals() {
                this.adapter.resetTaskVisuals(new RecentsViewContainer$2$$ExternalSyntheticLambda1(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$resetTaskVisuals$2(TaskParamsOverride taskParamsOverride) {
                taskParamsOverride.fullscreenProgress = 0.0f;
                taskParamsOverride.stableAlpha = RecentsViewContainer.this.mContentAlpha;
            }

            public boolean onBackPressed() {
                TaskView runningTaskView = getRunningTaskView();
                if (runningTaskView == null) {
                    return false;
                }
                runningTaskView.launchTask(true);
                return true;
            }

            public void removeTask(Task task) {
                this.adapter.onTaskRemoved(task.key.f124id);
                if (getTaskCount() == 0) {
                    RecentsViewContainer.this.startHome();
                }
            }
        };
        this.mRecentsTaskViewHelper = r0;
        r0.onFinishInflate();
    }

    public void setup() {
        Context context = getContext();
        this.mSquaredTouchSlop = Utilities.squaredTouchSlop(context);
        this.mRecentsModel = RecentsModel.INSTANCE.lambda$get$0(context);
        this.mDeviceProfile = this.mActivity.getDeviceProfile();
        this.mActivity.getStateManager().registerStateHandler(new RecentsViewStateHandler(this.mActivity));
        this.mActivity.getStateManager().addStateListener(this);
        this.mSecondaryDisplay = SecondaryDisplay.INSTANCE.lambda$get$0(context);
        this.mTempClipAnimationHelper = new ClipAnimationHelper(context);
        this.mRecentsTaskViewHelper.setup();
        setLayoutDirection(isLayoutRtl() ? 1 : 0);
        setContentAlpha(0.0f);
        updateEmptyMessage();
    }

    public boolean isLayoutRtl() {
        return this.mRecentsTaskViewHelper.isLayoutRtl();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        this.mTempRect.set(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        this.mActivity.getDeviceProfile().updateInsets(this.mTempRect);
        setInsets(this.mTempRect);
        return windowInsets.inset(0, 0, 0, 0);
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        getTaskSize(deviceProfile, this.mTempRect);
        DebugLog.m98d("RecentsView", "setInsets mTempRect = " + this.mTempRect + "; mInsets = " + this.mInsets);
        Rect rect2 = this.mTempRect;
        int i = rect2.left;
        Rect rect3 = this.mInsets;
        setPadding(i - rect3.left, rect2.top - rect3.top, (deviceProfile.widthPx - rect3.right) - rect2.right, (deviceProfile.heightPx - rect3.bottom) - rect2.bottom);
    }

    public void onHighResLoadingStateChanged(boolean z) {
        DebugLog.m98d("RecentsView", "RecentsViewContainer#onHighResLoadingStateChanged: enabled = " + z);
        this.mRecentsTaskViewHelper.onHighResLoadingStateChanged(true);
    }

    public void onTaskThumbnailChanged(int i, ThumbnailData thumbnailData) {
        DebugLog.m98d("RecentsView", "onTaskThumbnailChanged: taskId = " + i + " thumbnailData: " + thumbnailData.thumbnail);
        if (this.mHandleTaskStackChanges) {
            this.mRecentsTaskViewHelper.onTaskThumbnailChanged(i, thumbnailData);
        }
    }

    static class TaskParamsOverride {
        float fullscreenProgress = 1.0f;
        float stableAlpha = 1.0f;
        Task task;

        TaskParamsOverride(Task task2) {
            this.task = task2;
        }
    }

    private static class RecentsViewAdapter extends BaseViewAdapter<RecentsViewHolder> {
        private IOnTaskItemOperationCallback mOnTaskItemOperationCallback;
        private List<TaskParamsOverride> mTask;

        public void onBindViewHolder(RecentsViewHolder recentsViewHolder, int i) {
        }

        /* renamed from: com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$RecentsViewAdapter$OP */
        private enum C2735OP {
            THUMBNAIL_CHANGED("THUMBNAIL"),
            ICON_CHANGED("ICON"),
            CONTENT_ALPHA_CHANGED("CONTENT_ALPHA"),
            FULLSCREEN_PROGRESS_CHANGED("FULLSCREEN"),
            LOAD_HIGH_RES_THUMBNAIL_CHANGED("LOAD_HIGH_RES"),
            RESET("RESET");
            
            String desc;

            private C2735OP(String str) {
                this.desc = str;
            }

            public String toString() {
                return this.desc;
            }
        }

        RecentsViewAdapter(IOnTaskItemOperationCallback iOnTaskItemOperationCallback) {
            this.mOnTaskItemOperationCallback = iOnTaskItemOperationCallback;
        }

        /* access modifiers changed from: package-private */
        public void swap(List<TaskParamsOverride> list) {
            if (this.mTask == null) {
                this.mTask = list;
                notifyItemRangeInserted(0, list.size());
                return;
            }
            this.mTask = list;
            notifyDataSetChanged();
        }

        /* access modifiers changed from: package-private */
        public int getRunningTaskIndex(int i) {
            int itemCount = getItemCount();
            for (int i2 = 0; i2 < itemCount; i2++) {
                if (sameTask(i, getItem(i2).task)) {
                    return i2;
                }
            }
            return -1;
        }

        /* access modifiers changed from: package-private */
        public void onTaskThumbnailChanged(int i, ThumbnailData thumbnailData) {
            int itemCount = getItemCount();
            int i2 = 0;
            while (i2 < itemCount) {
                Task task = getItem(i2).task;
                Task.TaskKey taskKey = task.key;
                if (taskKey == null || taskKey.f124id != i) {
                    i2++;
                } else {
                    task.thumbnail = thumbnailData;
                    notifyItemChanged(i2, C2735OP.THUMBNAIL_CHANGED);
                    return;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void onTaskFullScreenChanged(float f) {
            int itemCount = getItemCount();
            for (int i = 0; i < itemCount; i++) {
                getItem(i).fullscreenProgress = f;
            }
            notifyItemRangeChanged(0, itemCount, C2735OP.FULLSCREEN_PROGRESS_CHANGED);
        }

        /* access modifiers changed from: package-private */
        public void onTaskContentAlphaChanged(Predicate<TaskParamsOverride> predicate, float f) {
            int itemCount = getItemCount();
            for (int i = 0; i < itemCount; i++) {
                TaskParamsOverride item = getItem(i);
                if (predicate.test(item)) {
                    item.stableAlpha = f;
                }
            }
            notifyItemRangeChanged(0, itemCount, C2735OP.CONTENT_ALPHA_CHANGED);
        }

        /* access modifiers changed from: package-private */
        public void onHighResLoadingStateChanged(boolean z) {
            notifyItemRangeChanged(0, getItemCount(), C2735OP.LOAD_HIGH_RES_THUMBNAIL_CHANGED);
        }

        /* access modifiers changed from: package-private */
        public void resetTaskVisuals(Consumer<TaskParamsOverride> consumer) {
            int itemCount = getItemCount();
            for (int i = 0; i < itemCount; i++) {
                consumer.accept(getItem(i));
            }
            notifyItemRangeChanged(0, itemCount, C2735OP.RESET);
        }

        /* access modifiers changed from: package-private */
        public void onTaskRemoved(int i) {
            int itemCount = getItemCount();
            int i2 = 0;
            while (i2 < itemCount) {
                Task.TaskKey taskKey = getItem(i2).task.key;
                if (taskKey == null || taskKey.f124id != i) {
                    i2++;
                } else {
                    this.mTask.remove(i2);
                    notifyItemRemoved(i2);
                    DebugLog.m100v("StateManager", "onTaskRemoved: task id= " + i);
                    return;
                }
            }
        }

        public RecentsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecentsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.zz_moto_cli_recents_view_item, viewGroup, false));
        }

        public void onBindViewHolder(RecentsViewHolder recentsViewHolder, int i, List<Object> list) {
            TaskParamsOverride item = getItem(i);
            if (list.isEmpty()) {
                recentsViewHolder.bind(item, i, this.mOnTaskItemOperationCallback);
            } else {
                recentsViewHolder.bindWithPayload(item, i, (List) list.stream().distinct().collect(Collectors.toList()));
            }
        }

        public int getItemCount() {
            List<TaskParamsOverride> list = this.mTask;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        /* access modifiers changed from: package-private */
        public TaskParamsOverride getItem(int i) {
            return this.mTask.get(i);
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
            r0 = r2.key;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean sameTask(int r1, com.android.systemui.shared.recents.model.Task r2) {
            /*
                r0 = this;
                if (r2 == 0) goto L_0x000c
                com.android.systemui.shared.recents.model.Task$TaskKey r0 = r2.key
                if (r0 == 0) goto L_0x000c
                int r0 = r0.f124id
                if (r0 != r1) goto L_0x000c
                r0 = 1
                goto L_0x000d
            L_0x000c:
                r0 = 0
            L_0x000d:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.navgesture.view.RecentsViewContainer.RecentsViewAdapter.sameTask(int, com.android.systemui.shared.recents.model.Task):boolean");
        }

        static class RecentsViewHolder extends BaseBindingViewHolder {
            TaskView taskView;

            RecentsViewHolder(View view) {
                super(view);
                TaskView taskView2 = (TaskView) view.findViewById(R$id.task_view);
                this.taskView = taskView2;
                taskView2.setSwipeDirection(SwipeDismissHelper.VERTICAL);
                this.taskView.setSwipeDirection(4);
                this.taskView.setDismissible(true);
                this.taskView.setOnSwipeProgressChangedListener(new ISwipeDismissView.OnSwipeProgressChangedListener() {
                    public void onSwipeProgressChanged(ISwipeDismissView iSwipeDismissView, float f, float f2) {
                        iSwipeDismissView.getView().setTranslationY(f2);
                        iSwipeDismissView.getView().setAlpha(f);
                    }

                    public void onSwipeCancelled(ISwipeDismissView iSwipeDismissView) {
                        iSwipeDismissView.getView().setTranslationY(0.0f);
                        iSwipeDismissView.getView().setAlpha(1.0f);
                    }
                });
                this.taskView.setMinDismissThreshold(0.55f);
            }

            /* access modifiers changed from: package-private */
            public void bind(TaskParamsOverride taskParamsOverride, int i, IOnTaskItemOperationCallback iOnTaskItemOperationCallback) {
                this.taskView.resetVisualProperties();
                this.taskView.bind(taskParamsOverride.task);
                this.taskView.setStableAlpha(taskParamsOverride.stableAlpha);
                this.taskView.setFullscreenProgress(taskParamsOverride.fullscreenProgress);
                this.taskView.setOnDismissedListener(new C2738xf9f2f646(iOnTaskItemOperationCallback, taskParamsOverride));
            }

            /* access modifiers changed from: package-private */
            public void bindWithPayload(TaskParamsOverride taskParamsOverride, int i, List<Object> list) {
                for (Object next : list) {
                    if (next == C2735OP.RESET) {
                        this.taskView.setStableAlpha(taskParamsOverride.stableAlpha);
                        this.taskView.setFullscreenProgress(taskParamsOverride.fullscreenProgress);
                    } else if (next == C2735OP.THUMBNAIL_CHANGED) {
                        TaskThumbnailView thumbnail = this.taskView.getThumbnail();
                        Task task = taskParamsOverride.task;
                        thumbnail.setThumbnail(task, task.thumbnail);
                    } else if (next == C2735OP.CONTENT_ALPHA_CHANGED) {
                        this.taskView.setStableAlpha(taskParamsOverride.stableAlpha);
                    } else if (next == C2735OP.FULLSCREEN_PROGRESS_CHANGED) {
                        this.taskView.setFullscreenProgress(taskParamsOverride.fullscreenProgress);
                    } else if (next == C2735OP.LOAD_HIGH_RES_THUMBNAIL_CHANGED) {
                        this.taskView.onTaskListVisibilityChanged(true);
                    }
                }
            }
        }
    }

    public float getContentAlpha() {
        return this.mContentAlpha;
    }

    public void setContentAlpha(float f) {
        if (f != this.mContentAlpha) {
            float boundToRange = Utilities.boundToRange(f, 0.0f, 1.0f);
            this.mContentAlpha = boundToRange;
            this.mRecentsTaskViewHelper.onTaskContentAlphaChanged(new RecentsViewContainer$$ExternalSyntheticLambda10(this), boundToRange);
            if (boundToRange > 0.0f) {
                setVisibility(0);
            } else if (!this.mFreezeViewVisibility) {
                setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setContentAlpha$0(TaskParamsOverride taskParamsOverride) {
        return !this.mRunningTaskTileHidden || taskParamsOverride.task.key.f124id != this.mRunningTaskId;
    }

    public float getFullscreenProgress() {
        return this.mFullscreenProgress;
    }

    public void setFullscreenProgress(float f) {
        this.mFullscreenProgress = f;
        this.mRecentsTaskViewHelper.setFullscreenProgress(f);
    }

    public void setRemoteWindowAnimationDependentSyncRTListener(DoubleConsumer doubleConsumer) {
        DebugLog.m98d("RecentsView", "setRemoteWindowAnimationDependentSyncRTListener: listener = " + doubleConsumer);
        this.mRecentsTaskViewHelper.setRemoteWindowAnimationDependentSyncRTListener(doubleConsumer);
    }

    public void updateWindowAnimationProgress(float f) {
        DebugLog.m98d("RecentsView", "updateWindowAnimationProgress: currentFraction = " + f);
        this.mRecentsTaskViewHelper.updateWindowAnimationProgress(f);
    }

    public int getSysUiStatusNavFlags(float f) {
        return this.mRecentsTaskViewHelper.getSysUiStatusNavFlags(f);
    }

    public boolean isTaskViewVisible(View view) {
        if (view instanceof ITaskViewAware) {
            return this.mRecentsTaskViewHelper.isTaskViewVisible((ITaskViewAware) view);
        }
        return false;
    }

    public ITaskViewAware isTaskViewVisible(int i) {
        if (i == -1) {
            return null;
        }
        return this.mRecentsTaskViewHelper.getTaskViewByTaskId(i);
    }

    public void onGestureAnimationStart(int i) {
        DebugLog.m98d("RecentsView", "onGestureAnimationStart: runningTaskId = " + i);
        showRunningTask(i);
        setRunningTaskHidden(true);
        this.mRecentsTaskViewHelper.onGestureAnimationStart();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            updateTaskStackListenerState();
            this.mRecentsModel.getThumbnailCache().getHighResLoadingState().addCallback(this);
            this.mRecentsModel.addThumbnailChangeListener(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            updateTaskStackListenerState();
            this.mRecentsModel.getThumbnailCache().getHighResLoadingState().removeCallback(this);
            this.mRecentsModel.removeThumbnailChangeListener(this);
        }
    }

    public void showRunningTask(int i) {
        setCurrentTask(i);
        setRunningTaskHidden(this.mRunningTaskTileHidden);
        this.mTaskListChangeId = this.mRecentsModel.getTasks(new RecentsViewContainer$$ExternalSyntheticLambda3(this));
        DebugLog.m98d("RecentsView", "showRunningTask: runningTaskId = " + i);
    }

    private void setCurrentTask(int i) {
        int i2 = this.mRunningTaskId;
        if (i2 != i) {
            if (i2 != -1) {
                setRunningTaskHidden(false);
            }
            this.mRunningTaskId = i;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: java.util.List} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyLoadPlan(java.util.List<com.android.systemui.shared.recents.model.Task> r6) {
        /*
            r5 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "---------- apply recent task load result ChangeId: [ "
            r0.append(r1)
            int r1 = r5.mTaskListChangeId
            r0.append(r1)
            java.lang.String r1 = " ] ----------"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "RecentsView"
            android.util.Log.d(r2, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            boolean r3 = r6.isEmpty()
            if (r3 != 0) goto L_0x0061
            java.util.stream.Stream r6 = r6.stream()
            com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$$ExternalSyntheticLambda8 r0 = new com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$$ExternalSyntheticLambda8
            r0.<init>(r5)
            java.util.stream.Stream r6 = r6.filter(r0)
            com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$$ExternalSyntheticLambda6 r0 = new com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$$ExternalSyntheticLambda6
            r0.<init>(r5)
            java.util.stream.Stream r6 = r6.map(r0)
            java.util.stream.Collector r0 = java.util.stream.Collectors.toList()
            java.lang.Object r6 = r6.collect(r0)
            r0 = r6
            java.util.List r0 = (java.util.List) r0
            int r6 = r0.size()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "apply recent task: bind task: size: "
            r3.append(r4)
            r3.append(r6)
            java.lang.String r6 = r3.toString()
            android.util.Log.i(r2, r6)
        L_0x0061:
            r5.dumpRecentTaskInfo(r0)
            com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$RecentsTaskViewHelper r6 = r5.mRecentsTaskViewHelper
            r6.updateRecentsList(r0)
            r5.resetTaskVisuals()
            r5.updateEmptyMessage()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r0 = "---------- end ChangeId: [ "
            r6.append(r0)
            int r5 = r5.mTaskListChangeId
            r6.append(r5)
            r6.append(r1)
            java.lang.String r5 = r6.toString()
            android.util.Log.d(r2, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.navgesture.view.RecentsViewContainer.applyLoadPlan(java.util.List):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$applyLoadPlan$1(Task task) {
        return task.key.displayId == this.mSecondaryDisplay.getDisplayId();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ TaskParamsOverride lambda$applyLoadPlan$2(Task task) {
        TaskParamsOverride taskParamsOverride = new TaskParamsOverride(task);
        taskParamsOverride.fullscreenProgress = this.mFullscreenProgress;
        taskParamsOverride.stableAlpha = this.mContentAlpha;
        return taskParamsOverride;
    }

    private void dumpRecentTaskInfo(List<TaskParamsOverride> list) {
        if (list.isEmpty()) {
            Log.d("RecentsView", "apply recent task: empty recent task list");
        } else {
            list.stream().limit(3).map(RecentsViewContainer$$ExternalSyntheticLambda7.INSTANCE).reduce(RecentsViewContainer$$ExternalSyntheticLambda1.INSTANCE).ifPresent(RecentsViewContainer$$ExternalSyntheticLambda5.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$dumpRecentTaskInfo$3(TaskParamsOverride taskParamsOverride) {
        return "[" + taskParamsOverride.task.key.f124id + "]" + taskParamsOverride.task.key.getComponent().getPackageName();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$dumpRecentTaskInfo$4(String str, String str2) {
        return str2 + " , " + str;
    }

    public void onGestureAnimationEnd() {
        DebugLog.m98d("RecentsView", "onGestureAnimationEnd: ");
        setRunningTaskHidden(false);
        this.mRecentsTaskViewHelper.onGestureAnimationEnd();
    }

    public int getRunningTaskIndex() {
        return this.mRecentsTaskViewHelper.getRunningTaskIndex();
    }

    public int getTaskIndex(View view) {
        if (view instanceof ITaskViewAware) {
            return this.mRecentsTaskViewHelper.getTaskIndex((ITaskViewAware) view);
        }
        return -1;
    }

    public void onSwipeUpAnimationSuccess() {
        DebugLog.m98d("RecentsView", "onSwipeUpAnimationSuccess: ");
    }

    public Consumer<MotionEvent> getEventDispatcher(float f) {
        float f2 = f == 0.0f ? 0.0f : -f;
        if (f2 != 0.0f) {
            return new RecentsViewContainer$$ExternalSyntheticLambda4(this, f2);
        }
        RecentsTaskViewHelper recentsTaskViewHelper = this.mRecentsTaskViewHelper;
        Objects.requireNonNull(recentsTaskViewHelper);
        return new RecentsViewContainer$$ExternalSyntheticLambda2(recentsTaskViewHelper);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getEventDispatcher$6(float f, MotionEvent motionEvent) {
        Matrix matrix = new Matrix();
        matrix.setRotate(-f);
        motionEvent.transform(matrix);
        this.mRecentsTaskViewHelper.onTouchEvent(motionEvent);
        matrix.setRotate(f);
        motionEvent.transform(matrix);
    }

    public void fillRemoteWindowTransformParams(ClipAnimationHelper clipAnimationHelper, ClipAnimationHelper.TransformParams transformParams) {
        this.mRecentsTaskViewHelper.fillRemoteWindowTransformParams(clipAnimationHelper, transformParams);
    }

    public Animator setRecentsAttachedToAppWindow(boolean z, boolean z2) {
        return this.mRecentsTaskViewHelper.setRecentsAttachedToAppWindow(z, z2);
    }

    public boolean goingToNewTask(PointF pointF) {
        return this.mRecentsTaskViewHelper.goingToNewTask(pointF);
    }

    public void snapToNearestCenterOfScreenPosition() {
        DebugLog.m98d("RecentsView", "snapToNearestCenterOfScreenPosition: ");
        this.mRecentsTaskViewHelper.snapToNearestCenterOfScreenPosition();
    }

    public long getSnapDuration() {
        return this.mRecentsTaskViewHelper.getSnapDuration();
    }

    public void startHome() {
        DebugLog.m98d("RecentsView", "startHome: hasfocus = " + hasWindowFocus());
        this.mActivity.overridePendingTransition(0, 0);
        this.mActivity.getRootView().postDelayed(new RecentsViewContainer$$ExternalSyntheticLambda0(this), 200);
    }

    /* access modifiers changed from: private */
    /* renamed from: goToHome */
    public void lambda$startHome$7() {
        sendEvent(0, 3);
        sendEvent(1, 3);
    }

    private void sendEvent(int i, int i2) {
        long uptimeMillis = SystemClock.uptimeMillis();
        KeyEvent keyEvent = new KeyEvent(uptimeMillis, uptimeMillis, i, i2, 0, 0, -1, 0, 72, 257);
        keyEvent.setDisplayId(1);
        InputManager.getInstance().injectInputEvent(keyEvent, 0);
    }

    public void showNextTask() {
        TaskView taskView = (TaskView) this.mRecentsTaskViewHelper.getRunningTaskView();
        if (taskView == null) {
            if (this.mRecentsTaskViewHelper.getTaskViewCount() > 0) {
                ((TaskView) this.mRecentsTaskViewHelper.getTaskViewAt(0)).launchTask(true);
            }
        } else if (getNextTaskView() != null) {
            getNextTaskView().launchTask(true);
        } else {
            taskView.launchTask(true);
        }
    }

    public TaskView getNextTaskView() {
        return (TaskView) this.mRecentsTaskViewHelper.getTaskViewAt(getRunningTaskIndex() + 1);
    }

    public ITaskViewAware checkingBeforeStartNewTask() {
        DebugLog.m98d("RecentsView", "checkingBeforeStartNewTask: " + this.mRecentsTaskViewHelper.getCurrentItem());
        RecentsTaskViewHelper recentsTaskViewHelper = this.mRecentsTaskViewHelper;
        return recentsTaskViewHelper.getTaskViewAt(recentsTaskViewHelper.getCurrentItem());
    }

    public void startNewTask(boolean z, Consumer<Boolean> consumer) {
        DebugLog.m98d("RecentsView", "startNewTask: " + this.mRecentsTaskViewHelper.getCurrentItem());
        this.mRecentsTaskViewHelper.startNewTask(z, consumer);
    }

    public void setRunningTaskHidden(boolean z) {
        float f;
        this.mRunningTaskTileHidden = z;
        DebugLog.m98d("RecentsView", "setRunningTaskHidden: needHidden = " + z);
        RecentsTaskViewHelper recentsTaskViewHelper = this.mRecentsTaskViewHelper;
        RecentsViewContainer$$ExternalSyntheticLambda9 recentsViewContainer$$ExternalSyntheticLambda9 = new RecentsViewContainer$$ExternalSyntheticLambda9(this);
        if (z) {
            f = 0.0f;
        } else {
            f = this.mContentAlpha;
        }
        recentsTaskViewHelper.onTaskContentAlphaChanged(recentsViewContainer$$ExternalSyntheticLambda9, f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setRunningTaskHidden$8(TaskParamsOverride taskParamsOverride) {
        Task.TaskKey taskKey = taskParamsOverride.task.key;
        return taskKey != null && taskKey.f124id == this.mRunningTaskId;
    }

    public View updateTaskToLatestScreenshot(int i, ThumbnailData thumbnailData) {
        DebugLog.m98d("RecentsView", "updateTaskToLatestScreenshot: taskId = " + i + " thumbnailData " + thumbnailData.thumbnail);
        this.mRecentsTaskViewHelper.onTaskThumbnailChanged(i, thumbnailData);
        return this;
    }

    public void createRecentsShowHideTransitionAnimation(LauncherState launcherState, LauncherState launcherState2, AnimatorSet animatorSet) {
        RecentsTaskViewHelper recentsTaskViewHelper = this.mRecentsTaskViewHelper;
        if (recentsTaskViewHelper.getTaskViewAt(recentsTaskViewHelper.getCurrentItem()) == null) {
            DebugLog.m101w("RecentsView", "createRecentsShowHideTransitionAnimation: but no target task view");
            return;
        }
        ScaleTranslation overviewScaleTranslation = launcherState.getOverviewScaleTranslation(this.mActivity);
        ScaleTranslation overviewScaleTranslation2 = launcherState2.getOverviewScaleTranslation(this.mActivity);
        float f = overviewScaleTranslation.translationY;
        float f2 = overviewScaleTranslation2.translationY;
        float overviewFullscreenProgress = launcherState.getOverviewFullscreenProgress();
        float overviewFullscreenProgress2 = launcherState2.getOverviewFullscreenProgress();
        DebugLog.m98d("RecentsView", "createRecentsShowHideTransitionAnimation: \n from " + launcherState.toShortString() + " to " + launcherState2.toShortString() + "\n scale: from " + overviewScaleTranslation.scale + " to " + overviewScaleTranslation2.scale + "\n ty: from " + f + " to " + f2 + "\n full: from " + overviewFullscreenProgress + " to " + overviewFullscreenProgress2);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, Utilities.SCALE_PROPERTY, new float[]{overviewScaleTranslation.scale, overviewScaleTranslation2.scale}), ObjectAnimator.ofFloat(this, FrameLayout.TRANSLATION_Y, new float[]{f, f2}), ObjectAnimator.ofFloat(this, IRecentsView.FULLSCREEN_PROGRESS, new float[]{overviewFullscreenProgress, overviewFullscreenProgress2})});
    }

    public void resetTaskVisuals() {
        this.mRecentsTaskViewHelper.resetTaskVisuals();
        if (this.mRunningTaskTileHidden) {
            setRunningTaskHidden(true);
        }
    }

    public boolean onBackPressed() {
        return this.mRecentsTaskViewHelper.onBackPressed();
    }

    public void onStateTransitionStart(LauncherState launcherState) {
        setOverviewStateEnabled(launcherState.overview());
        setFreezeViewVisibility(true);
    }

    public void onStateTransitionComplete(LauncherState launcherState) {
        if (launcherState == StateManager.NORMAL) {
            reset();
            DebugLog.m98d("RecentsView", "RecentsViewContainer:onStateTransitionComplete: reset");
        }
        setFreezeViewVisibility(false);
    }

    public void setOverviewStateEnabled(boolean z) {
        this.mOverviewStateEnabled = z;
        updateTaskStackListenerState();
    }

    public void setFreezeViewVisibility(boolean z) {
        if (this.mFreezeViewVisibility != z) {
            this.mFreezeViewVisibility = z;
            if (!z) {
                setVisibility(this.mContentAlpha > 0.0f ? 0 : 8);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        updateTaskStackListenerState();
    }

    private void updateTaskStackListenerState() {
        boolean z = this.mOverviewStateEnabled && isAttachedToWindow() && getWindowVisibility() == 0;
        if (z != this.mHandleTaskStackChanges) {
            this.mHandleTaskStackChanges = z;
            if (z) {
                reloadIfNeeded();
            }
        }
    }

    private void reloadIfNeeded() {
        if (!this.mRecentsModel.isTaskListValid(this.mTaskListChangeId)) {
            this.mTaskListChangeId = this.mRecentsModel.getTasks(new RecentsViewContainer$$ExternalSyntheticLambda3(this));
        }
    }

    public void reset() {
        DebugLog.m98d("RecentsView", "RecentsViewContainer:reset: ");
        setCurrentTask(-1);
        this.mRecentsTaskViewHelper.onHighResLoadingStateChanged(false);
        this.mRecentsTaskViewHelper.setCurrentItem(0);
        this.mActivity.getSystemUiController().updateUiState(3, 0);
    }

    public void onDeviceProfileChanged(int i, DeviceProfileProvider deviceProfileProvider) {
        reset();
    }

    public ScaleTranslation getScaleTranslation(LauncherState launcherState) {
        if (launcherState == StateManager.NORMAL) {
            if (SysUINavigationMode.getInstance(getContext()).isGestureMode()) {
                return new ScaleTranslation(1.0f, (float) (this.mDeviceProfile.widthPx - getPaddingStart()), 0.0f);
            }
            return new ScaleTranslation(1.1f, 0.0f, 0.0f);
        } else if (launcherState == StateManager.OVERVIEW) {
            return new ScaleTranslation(1.0f, 0.0f, 0.0f);
        } else {
            if (launcherState != StateManager.BACKGROUND) {
                return null;
            }
            int taskViewCount = this.mRecentsTaskViewHelper.getTaskViewCount();
            if (taskViewCount == 0) {
                return new ScaleTranslation(1.0f, 0.0f, 0.0f);
            }
            RecentsTaskViewHelper recentsTaskViewHelper = this.mRecentsTaskViewHelper;
            ITaskViewAware taskViewAt = recentsTaskViewHelper.getTaskViewAt(Math.max(taskViewCount - 1, recentsTaskViewHelper.getCurrentItem()));
            if (taskViewAt == null) {
                Log.w("RecentsView", "getOverviewScaleAndTranslation: dummy task null,  taskCount: " + taskViewCount + " curPage: " + this.mRecentsTaskViewHelper.getCurrentItem());
                return new ScaleTranslation(1.0f, 0.0f, 0.0f);
            }
            updateForFullscreenOverview(taskViewAt);
            return this.mTempClipAnimationHelper.getScaleAndTranslation();
        }
    }

    private void updateForFullscreenOverview(ITaskViewAware iTaskViewAware) {
        this.mTempClipAnimationHelper.fromTaskThumbnailView(iTaskViewAware, (RemoteAnimationTargetCompat) null);
        Rect rect = new Rect();
        getTaskSize(this.mDeviceProfile, rect);
        this.mTempClipAnimationHelper.updateTargetRect(rect);
    }

    private void getTaskSize(DeviceProfile deviceProfile, Rect rect) {
        LayoutCalculator.INSTANCE.lambda$get$0(getContext()).getSwipeUpDestinationAndLength(deviceProfile, getContext(), rect);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || (this.mShowEmptyMessage && drawable == this.mEmptyIcon);
    }

    public void updateEmptyMessage() {
        boolean z = true;
        boolean z2 = this.mRecentsTaskViewHelper.getTaskCount() == 0;
        if (this.mLastMeasureSize.x == getWidth() && this.mLastMeasureSize.y == getHeight()) {
            z = false;
        }
        if (z2 != this.mShowEmptyMessage || z) {
            setContentDescription(z2 ? this.mEmptyMessage : "");
            this.mShowEmptyMessage = z2;
            updateEmptyStateUi(z);
            invalidate();
        }
    }

    private void updateEmptyStateUi(boolean z) {
        boolean z2 = getWidth() > 0 && getHeight() > 0;
        if (z && z2) {
            this.mEmptyTextLayout = null;
            this.mLastMeasureSize.set(getWidth(), getHeight());
        }
        if (this.mShowEmptyMessage && z2 && this.mEmptyTextLayout == null) {
            int i = this.mLastMeasureSize.x;
            int i2 = this.mEmptyMessagePadding;
            int i3 = (i - i2) - i2;
            CharSequence charSequence = this.mEmptyMessage;
            StaticLayout build = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.mEmptyMessagePaint, i3).setAlignment(Layout.Alignment.ALIGN_CENTER).build();
            this.mEmptyTextLayout = build;
            int height = build.getHeight() + this.mEmptyMessagePadding + this.mEmptyIcon.getIntrinsicHeight();
            Point point = this.mLastMeasureSize;
            int i4 = (point.y - height) / 2;
            int intrinsicWidth = (point.x - this.mEmptyIcon.getIntrinsicWidth()) / 2;
            Drawable drawable = this.mEmptyIcon;
            drawable.setBounds(intrinsicWidth, i4, drawable.getIntrinsicWidth() + intrinsicWidth, this.mEmptyIcon.getIntrinsicHeight() + i4);
        }
    }

    public void draw(Canvas canvas) {
        maybeDrawEmptyMessage(canvas);
        super.draw(canvas);
    }

    private void maybeDrawEmptyMessage(Canvas canvas) {
        if (this.mShowEmptyMessage && this.mEmptyTextLayout != null) {
            this.mTempRect.set(this.mInsets.left + getPaddingLeft(), this.mInsets.top + getPaddingTop(), this.mInsets.right + getPaddingRight(), this.mInsets.bottom + getPaddingBottom());
            canvas.save();
            Rect rect = this.mTempRect;
            canvas.translate(((float) getScrollX()) + (((float) (rect.left - rect.right)) / 2.0f), ((float) (rect.top - rect.bottom)) / 2.0f);
            this.mEmptyIcon.draw(canvas);
            canvas.translate((float) this.mEmptyMessagePadding, (float) (this.mEmptyIcon.getBounds().bottom + this.mEmptyMessagePadding));
            this.mEmptyTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.mRecentsTaskViewHelper.dispatchTouchEvent(motionEvent)) {
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003c, code lost:
        if (com.motorola.systemui.cli.navgesture.util.Utilities.squaredHypot((float) (r7.x - r0), (float) (r7.y - r1)) > r6.mSquaredTouchSlop) goto L_0x003e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            float r0 = r7.getX()
            int r0 = (int) r0
            float r1 = r7.getY()
            int r1 = (int) r1
            int r2 = r7.getAction()
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x004b
            if (r2 == r4) goto L_0x0041
            r7 = 2
            if (r2 == r7) goto L_0x001e
            r7 = 3
            if (r2 == r7) goto L_0x001b
            goto L_0x0080
        L_0x001b:
            r6.mTouchDownToStartHome = r3
            goto L_0x0080
        L_0x001e:
            boolean r7 = r6.mTouchDownToStartHome
            if (r7 == 0) goto L_0x0080
            com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$RecentsTaskViewHelper r7 = r6.mRecentsTaskViewHelper
            boolean r7 = r7.isHandlingTouchEvent()
            if (r7 != 0) goto L_0x003e
            android.graphics.Point r7 = r6.mDownPoint
            int r2 = r7.x
            int r2 = r2 - r0
            float r0 = (float) r2
            int r7 = r7.y
            int r7 = r7 - r1
            float r7 = (float) r7
            float r7 = com.motorola.systemui.cli.navgesture.util.Utilities.squaredHypot(r0, r7)
            float r0 = r6.mSquaredTouchSlop
            int r7 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r7 <= 0) goto L_0x0080
        L_0x003e:
            r6.mTouchDownToStartHome = r3
            goto L_0x0080
        L_0x0041:
            boolean r7 = r6.mTouchDownToStartHome
            if (r7 == 0) goto L_0x0048
            r6.startHome()
        L_0x0048:
            r6.mTouchDownToStartHome = r3
            goto L_0x0080
        L_0x004b:
            com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$RecentsTaskViewHelper r2 = r6.mRecentsTaskViewHelper
            boolean r2 = r2.isHandlingTouchEvent()
            if (r2 != 0) goto L_0x007b
            boolean r2 = r6.mShowEmptyMessage
            if (r2 == 0) goto L_0x005a
            r6.mTouchDownToStartHome = r4
            goto L_0x007b
        L_0x005a:
            android.graphics.Rect r2 = r6.mTouchDeadZoneRect
            r2.setEmpty()
            com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$RecentsTaskViewHelper r2 = r6.mRecentsTaskViewHelper
            android.graphics.Rect r5 = r6.mTouchDeadZoneRect
            r2.updateTap2GoHomeDeadZoneRect(r5)
            int r7 = r7.getEdgeFlags()
            r7 = r7 & 256(0x100, float:3.59E-43)
            if (r7 == 0) goto L_0x006f
            r3 = r4
        L_0x006f:
            if (r3 != 0) goto L_0x007b
            android.graphics.Rect r7 = r6.mTouchDeadZoneRect
            boolean r7 = r7.contains(r0, r1)
            if (r7 != 0) goto L_0x007b
            r6.mTouchDownToStartHome = r4
        L_0x007b:
            android.graphics.Point r7 = r6.mDownPoint
            r7.set(r0, r1)
        L_0x0080:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r0 = "RecentsViewContainer#onTouchEvent: handled = "
            r7.append(r0)
            com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$RecentsTaskViewHelper r6 = r6.mRecentsTaskViewHelper
            boolean r6 = r6.isHandlingTouchEvent()
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            java.lang.String r7 = "RecentsView"
            com.motorola.systemui.cli.navgesture.util.DebugLog.m98d(r7, r6)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.navgesture.view.RecentsViewContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.mRecentsTaskViewHelper.getTaskViewCount() == 0) {
            super.onMeasure(i, i2);
            return;
        }
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 0 || mode2 == 0) {
            super.onMeasure(i, i2);
        } else if (size <= 0 || size2 <= 0) {
            super.onMeasure(i, i2);
        } else {
            Rect rect = this.mInsets;
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((size - rect.left) - rect.right, 1073741824);
            Rect rect2 = this.mInsets;
            measureChildren(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec((size2 - rect2.top) - rect2.bottom, 1073741824));
            setMeasuredDimension(size, size2);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        int i5 = -1;
        if (isLayoutRtl()) {
            childCount = -1;
        }
        if (!isLayoutRtl()) {
            i5 = 1;
        }
        int paddingTop = getPaddingTop() + getMeasuredHeight();
        Rect rect = this.mInsets;
        int paddingBottom = (((paddingTop + rect.top) - rect.bottom) - getPaddingBottom()) / 2;
        int paddingLeft = this.mInsets.left + getPaddingLeft();
        for (int i6 = isLayoutRtl() ? childCount - 1 : 0; i6 != childCount; i6 += i5) {
            View childAt = getChildAt(i6);
            if (childAt.getVisibility() != 8) {
                int measuredWidth = childAt.getMeasuredWidth() + paddingLeft;
                int measuredHeight = childAt.getMeasuredHeight();
                int i7 = paddingBottom - (measuredHeight / 2);
                childAt.layout(paddingLeft, i7, measuredWidth, measuredHeight + i7);
                paddingLeft = measuredWidth;
            }
        }
        updateEmptyStateUi(z);
        setPivotY((float) ((((this.mInsets.top + getPaddingTop()) + 0) + ((getHeight() - this.mInsets.bottom) - getPaddingBottom())) / 2));
        setPivotX((float) (((this.mInsets.left + getPaddingLeft()) + ((getWidth() - this.mInsets.right) - getPaddingRight())) / 2));
    }
}
