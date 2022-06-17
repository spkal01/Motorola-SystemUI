package com.android.systemui.animation;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.app.PendingIntent;
import android.app.TaskInfo;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.util.MathUtils;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.ScreenDecorationsUtils;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ActivityLaunchAnimator.kt */
public final class ActivityLaunchAnimator {
    @NotNull
    public static final PathInterpolator CONTENT_FADE_OUT_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @NotNull
    public static final PathInterpolator NAV_FADE_IN_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f);
    /* access modifiers changed from: private */
    @NotNull
    public static final PathInterpolator NAV_FADE_OUT_INTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 1.0f, 1.0f);
    /* access modifiers changed from: private */
    @NotNull
    public static final PorterDuffXfermode SRC_MODE = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    /* access modifiers changed from: private */
    @NotNull
    public static final PathInterpolator WINDOW_FADE_IN_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.6f, 1.0f);
    /* access modifiers changed from: private */
    public final Interpolator animationInterpolator;
    /* access modifiers changed from: private */
    public final Interpolator animationInterpolatorX;
    /* access modifiers changed from: private */
    @NotNull
    public final Callback callback;
    /* access modifiers changed from: private */
    @NotNull
    public final float[] cornerRadii;
    private final IPackageManager packageManager = AppGlobals.getPackageManager();

    /* compiled from: ActivityLaunchAnimator.kt */
    public interface Callback {
        int getBackgroundColor(@NotNull TaskInfo taskInfo);

        void hideKeyguardWithAnimation(@NotNull IRemoteAnimationRunner iRemoteAnimationRunner);

        boolean isOnKeyguard();

        void setBlursDisabledForAppLaunch(boolean z);
    }

    /* compiled from: ActivityLaunchAnimator.kt */
    public interface PendingIntentStarter {
        int startPendingIntent(@Nullable RemoteAnimationAdapter remoteAnimationAdapter) throws PendingIntent.CanceledException;
    }

    public static final float getProgress(float f, long j, long j2) {
        return Companion.getProgress(f, j, j2);
    }

    public ActivityLaunchAnimator(@NotNull Callback callback2, @NotNull Context context) {
        Intrinsics.checkNotNullParameter(callback2, "callback");
        Intrinsics.checkNotNullParameter(context, "context");
        this.callback = callback2;
        this.animationInterpolator = AnimationUtils.loadInterpolator(context, R$interpolator.launch_animation_interpolator_y);
        this.animationInterpolatorX = AnimationUtils.loadInterpolator(context, R$interpolator.launch_animation_interpolator_x);
        this.cornerRadii = new float[8];
    }

    /* compiled from: ActivityLaunchAnimator.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final float getProgress(float f, long j, long j2) {
            return MathUtils.constrain(((f * ((float) 500)) - ((float) j)) / ((float) j2), 0.0f, 1.0f);
        }
    }

    public final void startIntentWithAnimation(@Nullable Controller controller, boolean z, @Nullable String str, @NotNull Function1<? super RemoteAnimationAdapter, Integer> function1) {
        Intrinsics.checkNotNullParameter(function1, "intentStarter");
        boolean z2 = false;
        RemoteAnimationAdapter remoteAnimationAdapter = null;
        if (controller == null || !z) {
            Log.d("ActivityLaunchAnimator", "Starting intent with no animation");
            function1.invoke(null);
            if (controller != null) {
                callOnIntentStartedOnMainThread(controller, false);
                return;
            }
            return;
        }
        Log.d("ActivityLaunchAnimator", "Starting intent with a launch animation");
        IRemoteAnimationRunner runner = new Runner(this, controller);
        boolean isOnKeyguard = this.callback.isOnKeyguard();
        if (!isOnKeyguard) {
            remoteAnimationAdapter = new RemoteAnimationAdapter(runner, 500, 350);
        }
        if (!(str == null || remoteAnimationAdapter == null)) {
            try {
                ActivityTaskManager.getService().registerRemoteAnimationForNextActivityStart(str, remoteAnimationAdapter);
            } catch (RemoteException e) {
                Log.w("ActivityLaunchAnimator", "Unable to register the remote animation", e);
            }
        }
        int intValue = function1.invoke(remoteAnimationAdapter).intValue();
        if (intValue == 2 || intValue == 0 || (intValue == 3 && isOnKeyguard)) {
            z2 = true;
        }
        Log.d("ActivityLaunchAnimator", "launchResult=" + intValue + " willAnimate=" + z2 + " isOnKeyguard=" + isOnKeyguard);
        callOnIntentStartedOnMainThread(controller, z2);
        if (z2) {
            runner.mo11550xc73dc4a();
            if (isOnKeyguard) {
                this.callback.hideKeyguardWithAnimation(runner);
            }
        }
    }

    private final void callOnIntentStartedOnMainThread(Controller controller, boolean z) {
        if (!Intrinsics.areEqual((Object) Looper.myLooper(), (Object) Looper.getMainLooper())) {
            controller.getLaunchContainer().getContext().getMainExecutor().execute(new ActivityLaunchAnimator$callOnIntentStartedOnMainThread$1(controller, z));
        } else {
            controller.onIntentStarted(z);
        }
    }

    public final void startPendingIntentWithAnimation(@Nullable Controller controller, boolean z, @Nullable String str, @NotNull PendingIntentStarter pendingIntentStarter) throws PendingIntent.CanceledException {
        Intrinsics.checkNotNullParameter(pendingIntentStarter, "intentStarter");
        startIntentWithAnimation(controller, z, str, new ActivityLaunchAnimator$startPendingIntentWithAnimation$1(pendingIntentStarter));
    }

    @NotNull
    @VisibleForTesting
    public final Runner createRunner(@NotNull Controller controller) {
        Intrinsics.checkNotNullParameter(controller, "controller");
        return new Runner(this, controller);
    }

    /* compiled from: ActivityLaunchAnimator.kt */
    public interface Controller {
        @NotNull
        public static final Companion Companion = Companion.$$INSTANCE;

        /* compiled from: ActivityLaunchAnimator.kt */
        public static final class DefaultImpls {
            public static void onIntentStarted(@NotNull Controller controller, boolean z) {
                Intrinsics.checkNotNullParameter(controller, "this");
            }

            public static void onLaunchAnimationCancelled(@NotNull Controller controller) {
                Intrinsics.checkNotNullParameter(controller, "this");
            }
        }

        @Nullable
        static Controller fromView(@NotNull View view, @Nullable Integer num) {
            return Companion.fromView(view, num);
        }

        @NotNull
        State createAnimatorState();

        @NotNull
        ViewGroup getLaunchContainer();

        void onIntentStarted(boolean z);

        void onLaunchAnimationCancelled();

        void onLaunchAnimationEnd(boolean z);

        void onLaunchAnimationProgress(@NotNull State state, float f, float f2);

        void onLaunchAnimationStart(boolean z);

        void setLaunchContainer(@NotNull ViewGroup viewGroup);

        /* compiled from: ActivityLaunchAnimator.kt */
        public static final class Companion {
            static final /* synthetic */ Companion $$INSTANCE = new Companion();

            private Companion() {
            }

            @Nullable
            public final Controller fromView(@NotNull View view, @Nullable Integer num) {
                Intrinsics.checkNotNullParameter(view, "view");
                if (view.getParent() instanceof ViewGroup) {
                    return new GhostedViewLaunchAnimatorController(view, num);
                }
                Log.wtf("ActivityLaunchAnimator", "Skipping animation as view " + view + " is not attached to a ViewGroup", new Exception());
                return null;
            }
        }
    }

    /* compiled from: ActivityLaunchAnimator.kt */
    public static class State {
        private int bottom;
        private float bottomCornerRadius;
        private int left;
        private int right;
        private final int startBottom;
        private final float startCenterX = getCenterX();
        private final float startCenterY = getCenterY();
        private final int startHeight = getHeight();
        private final int startLeft;
        private final int startRight;
        private final int startTop;
        private final int startWidth = getWidth();
        private int top;
        private float topCornerRadius;
        private boolean visible = true;

        public State(int i, int i2, int i3, int i4, float f, float f2) {
            this.top = i;
            this.bottom = i2;
            this.left = i3;
            this.right = i4;
            this.topCornerRadius = f;
            this.bottomCornerRadius = f2;
            this.startTop = i;
            this.startBottom = i2;
            this.startLeft = i3;
            this.startRight = i4;
        }

        public final int getTop() {
            return this.top;
        }

        public final void setTop(int i) {
            this.top = i;
        }

        public final int getBottom() {
            return this.bottom;
        }

        public final void setBottom(int i) {
            this.bottom = i;
        }

        public final int getLeft() {
            return this.left;
        }

        public final void setLeft(int i) {
            this.left = i;
        }

        public final int getRight() {
            return this.right;
        }

        public final void setRight(int i) {
            this.right = i;
        }

        public final float getTopCornerRadius() {
            return this.topCornerRadius;
        }

        public final void setTopCornerRadius(float f) {
            this.topCornerRadius = f;
        }

        public final float getBottomCornerRadius() {
            return this.bottomCornerRadius;
        }

        public final void setBottomCornerRadius(float f) {
            this.bottomCornerRadius = f;
        }

        public final float getStartCenterX() {
            return this.startCenterX;
        }

        public final float getStartCenterY() {
            return this.startCenterY;
        }

        public final int getWidth() {
            return this.right - this.left;
        }

        public final int getHeight() {
            return this.bottom - this.top;
        }

        public int getTopChange() {
            return this.top - this.startTop;
        }

        public int getBottomChange() {
            return this.bottom - this.startBottom;
        }

        public final int getLeftChange() {
            return this.left - this.startLeft;
        }

        public final int getRightChange() {
            return this.right - this.startRight;
        }

        public final float getWidthRatio() {
            return ((float) getWidth()) / ((float) this.startWidth);
        }

        public final float getHeightRatio() {
            return ((float) getHeight()) / ((float) this.startHeight);
        }

        public final float getCenterX() {
            return ((float) this.left) + (((float) getWidth()) / 2.0f);
        }

        public final float getCenterY() {
            return ((float) this.top) + (((float) getHeight()) / 2.0f);
        }

        public final boolean getVisible() {
            return this.visible;
        }

        public final void setVisible(boolean z) {
            this.visible = z;
        }
    }

    @VisibleForTesting
    /* compiled from: ActivityLaunchAnimator.kt */
    public final class Runner extends IRemoteAnimationRunner.Stub {
        /* access modifiers changed from: private */
        @Nullable
        public ValueAnimator animator;
        /* access modifiers changed from: private */
        public boolean cancelled;
        private final Context context;
        /* access modifiers changed from: private */
        @NotNull
        public final Controller controller;
        @NotNull
        private final Matrix invertMatrix = new Matrix();
        @NotNull
        private final ViewGroup launchContainer;
        @NotNull
        private final Matrix matrix = new Matrix();
        @NotNull
        private Runnable onTimeout = new ActivityLaunchAnimator$Runner$onTimeout$1(this);
        final /* synthetic */ ActivityLaunchAnimator this$0;
        private boolean timedOut;
        @NotNull
        private final SyncRtSurfaceTransactionApplier transactionApplier;
        @NotNull
        private Rect windowCrop = new Rect();
        @NotNull
        private RectF windowCropF = new RectF();

        public Runner(@NotNull ActivityLaunchAnimator activityLaunchAnimator, Controller controller2) {
            Intrinsics.checkNotNullParameter(activityLaunchAnimator, "this$0");
            Intrinsics.checkNotNullParameter(controller2, "controller");
            this.this$0 = activityLaunchAnimator;
            this.controller = controller2;
            ViewGroup launchContainer2 = controller2.getLaunchContainer();
            this.launchContainer = launchContainer2;
            this.context = launchContainer2.getContext();
            this.transactionApplier = new SyncRtSurfaceTransactionApplier(launchContainer2);
        }

        /* renamed from: postTimeout$frameworks__base__packages__SystemUI__animation__android_common__SystemUIAnimationLib */
        public final void mo11550xc73dc4a() {
            this.launchContainer.postDelayed(this.onTimeout, 1000);
        }

        private final void removeTimeout() {
            this.launchContainer.removeCallbacks(this.onTimeout);
        }

        public void onAnimationStart(int i, @Nullable RemoteAnimationTarget[] remoteAnimationTargetArr, @Nullable RemoteAnimationTarget[] remoteAnimationTargetArr2, @Nullable RemoteAnimationTarget[] remoteAnimationTargetArr3, @Nullable IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            removeTimeout();
            if (this.timedOut) {
                if (iRemoteAnimationFinishedCallback != null) {
                    invoke(iRemoteAnimationFinishedCallback);
                }
            } else if (!this.cancelled) {
                this.context.getMainExecutor().execute(new ActivityLaunchAnimator$Runner$onAnimationStart$1(this, remoteAnimationTargetArr, remoteAnimationTargetArr3, iRemoteAnimationFinishedCallback));
            }
        }

        /* access modifiers changed from: private */
        public final void startAnimation(RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            RemoteAnimationTarget remoteAnimationTarget;
            RemoteAnimationTarget remoteAnimationTarget2;
            RemoteAnimationTarget[] remoteAnimationTargetArr3 = remoteAnimationTargetArr;
            RemoteAnimationTarget[] remoteAnimationTargetArr4 = remoteAnimationTargetArr2;
            IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback2 = iRemoteAnimationFinishedCallback;
            Log.d("ActivityLaunchAnimator", "Remote animation started");
            if (remoteAnimationTargetArr3 != null) {
                int length = remoteAnimationTargetArr3.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    RemoteAnimationTarget remoteAnimationTarget3 = remoteAnimationTargetArr3[i];
                    if (remoteAnimationTarget3.mode == 0) {
                        remoteAnimationTarget = remoteAnimationTarget3;
                        break;
                    }
                    i++;
                }
            }
            remoteAnimationTarget = null;
            if (remoteAnimationTarget == null) {
                Log.d("ActivityLaunchAnimator", "Aborting the animation as no window is opening");
                removeTimeout();
                if (iRemoteAnimationFinishedCallback2 != null) {
                    invoke(iRemoteAnimationFinishedCallback2);
                }
                this.controller.onLaunchAnimationCancelled();
                return;
            }
            if (remoteAnimationTargetArr4 != null) {
                int length2 = remoteAnimationTargetArr4.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length2) {
                        break;
                    }
                    RemoteAnimationTarget remoteAnimationTarget4 = remoteAnimationTargetArr4[i2];
                    if (remoteAnimationTarget4.windowType == 2019) {
                        remoteAnimationTarget2 = remoteAnimationTarget4;
                        break;
                    }
                    i2++;
                }
            }
            remoteAnimationTarget2 = null;
            State createAnimatorState = this.controller.createAnimatorState();
            int top = createAnimatorState.getTop();
            int bottom = createAnimatorState.getBottom();
            int left = createAnimatorState.getLeft();
            int right = createAnimatorState.getRight();
            float f = ((float) (left + right)) / 2.0f;
            int i3 = right - left;
            float topCornerRadius = createAnimatorState.getTopCornerRadius();
            float bottomCornerRadius = createAnimatorState.getBottomCornerRadius();
            Rect rect = remoteAnimationTarget.screenSpaceBounds;
            int i4 = rect.top;
            int i5 = rect.bottom;
            int i6 = rect.left;
            int i7 = rect.right;
            float f2 = ((float) (i6 + i7)) / 2.0f;
            int i8 = i7 - i6;
            int[] locationOnScreen = this.launchContainer.getLocationOnScreen();
            boolean z = i4 <= locationOnScreen[1] && i5 >= locationOnScreen[1] + this.launchContainer.getHeight() && i6 <= locationOnScreen[0] && i7 >= locationOnScreen[0] + this.launchContainer.getWidth();
            float windowCornerRadius = z ? ScreenDecorationsUtils.getWindowCornerRadius(this.context.getResources()) : 0.0f;
            Callback access$getCallback$p = this.this$0.callback;
            ActivityManager.RunningTaskInfo runningTaskInfo = remoteAnimationTarget.taskInfo;
            Intrinsics.checkNotNullExpressionValue(runningTaskInfo, "window.taskInfo");
            int backgroundColor = access$getCallback$p.getBackgroundColor(runningTaskInfo);
            GradientDrawable gradientDrawable = r7;
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setColor(backgroundColor);
            gradientDrawable2.setAlpha(0);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.setDuration(500);
            ofFloat.setInterpolator(Interpolators.LINEAR);
            int i9 = i5;
            ActivityLaunchAnimator$Runner$startAnimation$1 activityLaunchAnimator$Runner$startAnimation$1 = r0;
            ActivityLaunchAnimator$Runner$startAnimation$1 activityLaunchAnimator$Runner$startAnimation$12 = new ActivityLaunchAnimator$Runner$startAnimation$1(this.this$0, this, z, this.launchContainer.getOverlay(), gradientDrawable2, iRemoteAnimationFinishedCallback);
            ofFloat.addListener(activityLaunchAnimator$Runner$startAnimation$1);
            ValueAnimator valueAnimator = ofFloat;
            valueAnimator.addUpdateListener(new ActivityLaunchAnimator$Runner$startAnimation$2(this, this.this$0, f, f2, i3, i8, createAnimatorState, top, i4, bottom, i9, topCornerRadius, windowCornerRadius, bottomCornerRadius, remoteAnimationTarget, gradientDrawable, remoteAnimationTarget2, left, right));
            valueAnimator.start();
        }

        /* access modifiers changed from: private */
        public final void applyStateToWindow(RemoteAnimationTarget remoteAnimationTarget, State state) {
            Rect rect = remoteAnimationTarget.screenSpaceBounds;
            int i = rect.left;
            int i2 = rect.right;
            float f = ((float) (i + i2)) / 2.0f;
            int i3 = rect.top;
            int i4 = rect.bottom;
            float f2 = (float) (i4 - i3);
            float max = Math.max(((float) state.getWidth()) / ((float) (i2 - i)), ((float) state.getHeight()) / f2);
            this.matrix.reset();
            this.matrix.setScale(max, max, f, ((float) (i3 + i4)) / 2.0f);
            float f3 = (f2 * max) - f2;
            this.matrix.postTranslate(state.getCenterX() - f, ((float) (state.getTop() - rect.top)) + (f3 / 2.0f));
            float left = ((float) state.getLeft()) - ((float) rect.left);
            float top = ((float) state.getTop()) - ((float) rect.top);
            this.windowCropF.set(left, top, ((float) state.getWidth()) + left, ((float) state.getHeight()) + top);
            this.matrix.invert(this.invertMatrix);
            this.invertMatrix.mapRect(this.windowCropF);
            this.windowCrop.set(MathKt__MathJVMKt.roundToInt(this.windowCropF.left), MathKt__MathJVMKt.roundToInt(this.windowCropF.top), MathKt__MathJVMKt.roundToInt(this.windowCropF.right), MathKt__MathJVMKt.roundToInt(this.windowCropF.bottom));
            SyncRtSurfaceTransactionApplier.SurfaceParams build = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(remoteAnimationTarget.leash).withAlpha(1.0f).withMatrix(this.matrix).withWindowCrop(this.windowCrop).withLayer(remoteAnimationTarget.prefixOrderIndex).withCornerRadius(Math.max(state.getTopCornerRadius(), state.getBottomCornerRadius()) / max).withVisibility(true).build();
            this.transactionApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{build});
        }

        /* access modifiers changed from: private */
        public final void applyStateToWindowBackgroundLayer(GradientDrawable gradientDrawable, State state, float f) {
            gradientDrawable.setBounds(state.getLeft(), state.getTop(), state.getRight(), state.getBottom());
            this.this$0.cornerRadii[0] = state.getTopCornerRadius();
            this.this$0.cornerRadii[1] = state.getTopCornerRadius();
            this.this$0.cornerRadii[2] = state.getTopCornerRadius();
            this.this$0.cornerRadii[3] = state.getTopCornerRadius();
            this.this$0.cornerRadii[4] = state.getBottomCornerRadius();
            this.this$0.cornerRadii[5] = state.getBottomCornerRadius();
            this.this$0.cornerRadii[6] = state.getBottomCornerRadius();
            this.this$0.cornerRadii[7] = state.getBottomCornerRadius();
            gradientDrawable.setCornerRadii(this.this$0.cornerRadii);
            Companion companion = ActivityLaunchAnimator.Companion;
            float progress = companion.getProgress(f, 0, 150);
            if (progress < 1.0f) {
                gradientDrawable.setAlpha(MathKt__MathJVMKt.roundToInt(ActivityLaunchAnimator.CONTENT_FADE_OUT_INTERPOLATOR.getInterpolation(progress) * ((float) 255)));
                gradientDrawable.setXfermode((Xfermode) null);
                return;
            }
            gradientDrawable.setAlpha(MathKt__MathJVMKt.roundToInt((((float) 1) - ActivityLaunchAnimator.WINDOW_FADE_IN_INTERPOLATOR.getInterpolation(companion.getProgress(f, 150, 183))) * ((float) 255)));
            gradientDrawable.setXfermode(ActivityLaunchAnimator.SRC_MODE);
        }

        /* access modifiers changed from: private */
        public final void applyStateToNavigationBar(RemoteAnimationTarget remoteAnimationTarget, State state, float f) {
            SurfaceControl surfaceControl = remoteAnimationTarget.leash;
            if (surfaceControl == null || surfaceControl.isValid()) {
                Companion companion = ActivityLaunchAnimator.Companion;
                float progress = companion.getProgress(f, 234, 133);
                SyncRtSurfaceTransactionApplier.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(remoteAnimationTarget.leash);
                if (progress > 0.0f) {
                    this.matrix.reset();
                    this.matrix.setTranslate(0.0f, (float) (state.getTop() - remoteAnimationTarget.sourceContainerBounds.top));
                    this.windowCrop.set(state.getLeft(), 0, state.getRight(), state.getHeight());
                    builder.withAlpha(ActivityLaunchAnimator.NAV_FADE_IN_INTERPOLATOR.getInterpolation(progress)).withMatrix(this.matrix).withWindowCrop(this.windowCrop).withVisibility(true);
                } else {
                    builder.withAlpha(1.0f - ActivityLaunchAnimator.NAV_FADE_OUT_INTERPOLATOR.getInterpolation(companion.getProgress(f, 0, 133)));
                }
                this.transactionApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{builder.build()});
            }
        }

        /* access modifiers changed from: private */
        public final void onAnimationTimedOut() {
            if (!this.cancelled) {
                Log.d("ActivityLaunchAnimator", "Remote animation timed out");
                this.timedOut = true;
                this.controller.onLaunchAnimationCancelled();
            }
        }

        public void onAnimationCancelled() {
            if (!this.timedOut) {
                Log.d("ActivityLaunchAnimator", "Remote animation was cancelled");
                this.cancelled = true;
                removeTimeout();
                this.context.getMainExecutor().execute(new ActivityLaunchAnimator$Runner$onAnimationCancelled$1(this));
            }
        }

        /* access modifiers changed from: private */
        public final void invoke(IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            try {
                iRemoteAnimationFinishedCallback.onAnimationFinished();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /* access modifiers changed from: private */
        public final float lerp(int i, int i2, float f) {
            return MathUtils.lerp((float) i, (float) i2, f);
        }
    }
}
