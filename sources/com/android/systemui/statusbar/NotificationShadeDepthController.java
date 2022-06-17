package com.android.systemui.statusbar;

import android.animation.Animator;
import android.app.WallpaperManager;
import android.os.SystemClock;
import android.util.IndentingPrintWriter;
import android.util.MathUtils;
import android.view.Choreographer;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.PanelExpansionListener;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotificationShadeDepthController.kt */
public final class NotificationShadeDepthController implements PanelExpansionListener, Dumpable {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @NotNull
    public final BiometricUnlockController biometricUnlockController;
    /* access modifiers changed from: private */
    @Nullable
    public View blurRoot;
    /* access modifiers changed from: private */
    @NotNull
    public final BlurUtils blurUtils;
    private boolean blursDisabledForAppLaunch;
    @NotNull
    private DepthAnimation brightnessMirrorSpring = new DepthAnimation(this);
    private boolean brightnessMirrorVisible;
    @NotNull
    private final Choreographer choreographer;
    /* access modifiers changed from: private */
    @NotNull
    public final DozeParameters dozeParameters;
    private boolean isBlurred;
    private boolean isClosed = true;
    private boolean isOpen;
    /* access modifiers changed from: private */
    @Nullable
    public Animator keyguardAnimator;
    @NotNull
    private final NotificationShadeDepthController$keyguardStateCallback$1 keyguardStateCallback;
    /* access modifiers changed from: private */
    @NotNull
    public final KeyguardStateController keyguardStateController;
    /* access modifiers changed from: private */
    public int lastAppliedBlur;
    /* access modifiers changed from: private */
    @NotNull
    public List<DepthListener> listeners = new ArrayList();
    /* access modifiers changed from: private */
    @Nullable
    public Animator notificationAnimator;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationShadeWindowController notificationShadeWindowController;
    /* access modifiers changed from: private */
    public int prevShadeDirection;
    /* access modifiers changed from: private */
    public float prevShadeVelocity;
    private long prevTimestamp = -1;
    /* access modifiers changed from: private */
    public boolean prevTracking;
    private float qsPanelExpansion;
    public View root;
    /* access modifiers changed from: private */
    public boolean scrimsVisible;
    @NotNull
    private DepthAnimation shadeAnimation = new DepthAnimation(this);
    /* access modifiers changed from: private */
    public float shadeExpansion;
    @NotNull
    private DepthAnimation shadeSpring = new DepthAnimation(this);
    @NotNull
    private final NotificationShadeDepthController$statusBarStateCallback$1 statusBarStateCallback;
    @NotNull
    private final StatusBarStateController statusBarStateController;
    private float transitionToFullShadeProgress;
    @NotNull
    private final Choreographer.FrameCallback updateBlurCallback = new NotificationShadeDepthController$updateBlurCallback$1(this);
    /* access modifiers changed from: private */
    public boolean updateScheduled;
    /* access modifiers changed from: private */
    public int wakeAndUnlockBlurRadius;
    /* access modifiers changed from: private */
    @NotNull
    public final WallpaperManager wallpaperManager;

    /* compiled from: NotificationShadeDepthController.kt */
    public interface DepthListener {
        void onBlurRadiusChanged(int i) {
        }

        void onWallpaperZoomOutChanged(float f);
    }

    public static /* synthetic */ void getBrightnessMirrorSpring$annotations() {
    }

    public static /* synthetic */ void getShadeSpring$annotations() {
    }

    public static /* synthetic */ void getUpdateBlurCallback$annotations() {
    }

    public NotificationShadeDepthController(@NotNull StatusBarStateController statusBarStateController2, @NotNull BlurUtils blurUtils2, @NotNull BiometricUnlockController biometricUnlockController2, @NotNull KeyguardStateController keyguardStateController2, @NotNull Choreographer choreographer2, @NotNull WallpaperManager wallpaperManager2, @NotNull NotificationShadeWindowController notificationShadeWindowController2, @NotNull DozeParameters dozeParameters2, @NotNull DumpManager dumpManager) {
        Intrinsics.checkNotNullParameter(statusBarStateController2, "statusBarStateController");
        Intrinsics.checkNotNullParameter(blurUtils2, "blurUtils");
        Intrinsics.checkNotNullParameter(biometricUnlockController2, "biometricUnlockController");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkNotNullParameter(choreographer2, "choreographer");
        Intrinsics.checkNotNullParameter(wallpaperManager2, "wallpaperManager");
        Intrinsics.checkNotNullParameter(notificationShadeWindowController2, "notificationShadeWindowController");
        Intrinsics.checkNotNullParameter(dozeParameters2, "dozeParameters");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        this.statusBarStateController = statusBarStateController2;
        this.blurUtils = blurUtils2;
        this.biometricUnlockController = biometricUnlockController2;
        this.keyguardStateController = keyguardStateController2;
        this.choreographer = choreographer2;
        this.wallpaperManager = wallpaperManager2;
        this.notificationShadeWindowController = notificationShadeWindowController2;
        this.dozeParameters = dozeParameters2;
        NotificationShadeDepthController$keyguardStateCallback$1 notificationShadeDepthController$keyguardStateCallback$1 = new NotificationShadeDepthController$keyguardStateCallback$1(this);
        this.keyguardStateCallback = notificationShadeDepthController$keyguardStateCallback$1;
        NotificationShadeDepthController$statusBarStateCallback$1 notificationShadeDepthController$statusBarStateCallback$1 = new NotificationShadeDepthController$statusBarStateCallback$1(this);
        this.statusBarStateCallback = notificationShadeDepthController$statusBarStateCallback$1;
        String name = NotificationShadeDepthController.class.getName();
        Intrinsics.checkNotNullExpressionValue(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        keyguardStateController2.addCallback(notificationShadeDepthController$keyguardStateCallback$1);
        statusBarStateController2.addCallback(notificationShadeDepthController$statusBarStateCallback$1);
        notificationShadeWindowController2.setScrimsVisibilityListener(new Consumer<Integer>(this) {
            final /* synthetic */ NotificationShadeDepthController this$0;

            {
                this.this$0 = r1;
            }

            public final void accept(Integer num) {
                this.this$0.setScrimsVisible(num != null && num.intValue() == 2);
            }
        });
        this.shadeAnimation.setStiffness(200.0f);
        this.shadeAnimation.setDampingRatio(1.0f);
    }

    /* compiled from: NotificationShadeDepthController.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    @NotNull
    public final View getRoot() {
        View view = this.root;
        if (view != null) {
            return view;
        }
        Intrinsics.throwUninitializedPropertyAccessException("root");
        throw null;
    }

    public final void setRoot(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "<set-?>");
        this.root = view;
    }

    @NotNull
    public final DepthAnimation getShadeSpring() {
        return this.shadeSpring;
    }

    @NotNull
    public final DepthAnimation getShadeAnimation() {
        return this.shadeAnimation;
    }

    @NotNull
    public final DepthAnimation getBrightnessMirrorSpring() {
        return this.brightnessMirrorSpring;
    }

    public final void setBrightnessMirrorVisible(boolean z) {
        this.brightnessMirrorVisible = z;
        DepthAnimation.animateTo$default(this.brightnessMirrorSpring, z ? this.blurUtils.blurRadiusOfRatio(1.0f) : 0, (View) null, 2, (Object) null);
    }

    public final float getQsPanelExpansion() {
        return this.qsPanelExpansion;
    }

    public final void setQsPanelExpansion(float f) {
        if (!(this.qsPanelExpansion == f)) {
            this.qsPanelExpansion = f;
            scheduleUpdate$default(this, (View) null, 1, (Object) null);
        }
    }

    public final float getTransitionToFullShadeProgress() {
        return this.transitionToFullShadeProgress;
    }

    public final void setTransitionToFullShadeProgress(float f) {
        if (!(this.transitionToFullShadeProgress == f)) {
            this.transitionToFullShadeProgress = f;
            scheduleUpdate$default(this, (View) null, 1, (Object) null);
        }
    }

    public final boolean getBlursDisabledForAppLaunch() {
        return this.blursDisabledForAppLaunch;
    }

    public final void setBlursDisabledForAppLaunch(boolean z) {
        if (this.blursDisabledForAppLaunch != z) {
            this.blursDisabledForAppLaunch = z;
            scheduleUpdate$default(this, (View) null, 1, (Object) null);
            if (!(this.shadeSpring.getRadius() == 0 && this.shadeAnimation.getRadius() == 0) && z) {
                DepthAnimation.animateTo$default(this.shadeSpring, 0, (View) null, 2, (Object) null);
                this.shadeSpring.finishIfRunning();
                DepthAnimation.animateTo$default(this.shadeAnimation, 0, (View) null, 2, (Object) null);
                this.shadeAnimation.finishIfRunning();
            }
        }
    }

    /* access modifiers changed from: private */
    public final void setScrimsVisible(boolean z) {
        if (this.scrimsVisible != z) {
            this.scrimsVisible = z;
            scheduleUpdate$default(this, (View) null, 1, (Object) null);
        }
    }

    /* access modifiers changed from: private */
    public final void setWakeAndUnlockBlurRadius(int i) {
        if (this.wakeAndUnlockBlurRadius != i) {
            this.wakeAndUnlockBlurRadius = i;
            scheduleUpdate$default(this, (View) null, 1, (Object) null);
        }
    }

    public final void addListener(@NotNull DepthListener depthListener) {
        Intrinsics.checkNotNullParameter(depthListener, "listener");
        this.listeners.add(depthListener);
    }

    public final void removeListener(@NotNull DepthListener depthListener) {
        Intrinsics.checkNotNullParameter(depthListener, "listener");
        this.listeners.remove(depthListener);
    }

    public void onPanelExpansionChanged(float f, boolean z) {
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        if (!(this.shadeExpansion == f) || this.prevTracking != z) {
            long j = this.prevTimestamp;
            float f2 = 1.0f;
            if (j < 0) {
                this.prevTimestamp = elapsedRealtimeNanos;
            } else {
                f2 = MathUtils.constrain((float) (((double) (elapsedRealtimeNanos - j)) / 1.0E9d), 1.0E-5f, 1.0f);
            }
            float f3 = f - this.shadeExpansion;
            int signum = (int) Math.signum(f3);
            float constrain = MathUtils.constrain((f3 * 100.0f) / f2, -3000.0f, 3000.0f);
            updateShadeAnimationBlur(f, z, constrain, signum);
            this.prevShadeDirection = signum;
            this.prevShadeVelocity = constrain;
            this.shadeExpansion = f;
            this.prevTracking = z;
            this.prevTimestamp = elapsedRealtimeNanos;
            updateShadeBlur();
            return;
        }
        this.prevTimestamp = elapsedRealtimeNanos;
    }

    /* access modifiers changed from: private */
    public final void updateShadeAnimationBlur(float f, boolean z, float f2, int i) {
        if (!shouldApplyShadeBlur()) {
            animateBlur(false, 0.0f);
            this.isClosed = true;
            this.isOpen = false;
        } else if (f > 0.0f) {
            if (this.isClosed) {
                animateBlur(true, f2);
                this.isClosed = false;
            }
            if (z && !this.isBlurred) {
                animateBlur(true, 0.0f);
            }
            if (!z && i < 0 && this.isBlurred) {
                animateBlur(false, f2);
            }
            if (!(f == 1.0f)) {
                this.isOpen = false;
            } else if (!this.isOpen) {
                this.isOpen = true;
                if (!this.isBlurred) {
                    animateBlur(true, f2);
                }
            }
        } else if (!this.isClosed) {
            this.isClosed = true;
            if (this.isBlurred) {
                animateBlur(false, f2);
            }
        }
    }

    private final void animateBlur(boolean z, float f) {
        this.isBlurred = z;
        float f2 = (!z || !shouldApplyShadeBlur()) ? 0.0f : 1.0f;
        this.shadeAnimation.setStartVelocity(f);
        DepthAnimation.animateTo$default(this.shadeAnimation, this.blurUtils.blurRadiusOfRatio(f2), (View) null, 2, (Object) null);
    }

    /* access modifiers changed from: private */
    public final void updateShadeBlur() {
        DepthAnimation.animateTo$default(this.shadeSpring, shouldApplyShadeBlur() ? this.blurUtils.blurRadiusOfRatio(this.shadeExpansion) : 0, (View) null, 2, (Object) null);
    }

    static /* synthetic */ void scheduleUpdate$default(NotificationShadeDepthController notificationShadeDepthController, View view, int i, Object obj) {
        if ((i & 1) != 0) {
            view = null;
        }
        notificationShadeDepthController.scheduleUpdate(view);
    }

    /* access modifiers changed from: private */
    public final void scheduleUpdate(View view) {
        if (!this.updateScheduled) {
            this.updateScheduled = true;
            this.blurRoot = view;
            this.choreographer.postFrameCallback(this.updateBlurCallback);
        }
    }

    private final boolean shouldApplyShadeBlur() {
        int state = this.statusBarStateController.getState();
        return (state == 0 || state == 2) && !this.keyguardStateController.isKeyguardFadingAway();
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("StatusBarWindowBlurController:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println(Intrinsics.stringPlus("shadeRadius: ", Integer.valueOf(getShadeSpring().getRadius())));
        indentingPrintWriter.println(Intrinsics.stringPlus("shadeAnimation: ", Integer.valueOf(getShadeAnimation().getRadius())));
        indentingPrintWriter.println(Intrinsics.stringPlus("brightnessMirrorRadius: ", Integer.valueOf(getBrightnessMirrorSpring().getRadius())));
        indentingPrintWriter.println(Intrinsics.stringPlus("wakeAndUnlockBlur: ", Integer.valueOf(this.wakeAndUnlockBlurRadius)));
        indentingPrintWriter.println(Intrinsics.stringPlus("blursDisabledForAppLaunch: ", Boolean.valueOf(getBlursDisabledForAppLaunch())));
        indentingPrintWriter.println(Intrinsics.stringPlus("qsPanelExpansion: ", Float.valueOf(getQsPanelExpansion())));
        indentingPrintWriter.println(Intrinsics.stringPlus("transitionToFullShadeProgress: ", Float.valueOf(getTransitionToFullShadeProgress())));
        indentingPrintWriter.println(Intrinsics.stringPlus("lastAppliedBlur: ", Integer.valueOf(this.lastAppliedBlur)));
    }

    /* compiled from: NotificationShadeDepthController.kt */
    public final class DepthAnimation {
        /* access modifiers changed from: private */
        public int pendingRadius = -1;
        private int radius;
        @NotNull
        private SpringAnimation springAnimation;
        final /* synthetic */ NotificationShadeDepthController this$0;
        /* access modifiers changed from: private */
        @Nullable
        public View view;

        public DepthAnimation(NotificationShadeDepthController notificationShadeDepthController) {
            Intrinsics.checkNotNullParameter(notificationShadeDepthController, "this$0");
            this.this$0 = notificationShadeDepthController;
            SpringAnimation springAnimation2 = new SpringAnimation(this, new C1476x870e2248(this, notificationShadeDepthController));
            this.springAnimation = springAnimation2;
            springAnimation2.setSpring(new SpringForce(0.0f));
            this.springAnimation.getSpring().setDampingRatio(1.0f);
            this.springAnimation.getSpring().setStiffness(10000.0f);
            this.springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener(this) {
                final /* synthetic */ DepthAnimation this$0;

                {
                    this.this$0 = r1;
                }

                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    this.this$0.pendingRadius = -1;
                }
            });
        }

        public final int getRadius() {
            return this.radius;
        }

        public final void setRadius(int i) {
            this.radius = i;
        }

        public final float getRatio() {
            return this.this$0.blurUtils.ratioOfBlurRadius(this.radius);
        }

        public static /* synthetic */ void animateTo$default(DepthAnimation depthAnimation, int i, View view2, int i2, Object obj) {
            if ((i2 & 2) != 0) {
                view2 = null;
            }
            depthAnimation.animateTo(i, view2);
        }

        public final void animateTo(int i, @Nullable View view2) {
            if (this.pendingRadius != i || !Intrinsics.areEqual((Object) this.view, (Object) view2)) {
                this.view = view2;
                this.pendingRadius = i;
                this.springAnimation.animateToFinalPosition((float) i);
            }
        }

        public final void finishIfRunning() {
            if (this.springAnimation.isRunning()) {
                this.springAnimation.skipToEnd();
            }
        }

        public final void setStiffness(float f) {
            this.springAnimation.getSpring().setStiffness(f);
        }

        public final void setDampingRatio(float f) {
            this.springAnimation.getSpring().setDampingRatio(f);
        }

        public final void setStartVelocity(float f) {
            this.springAnimation.setStartVelocity(f);
        }
    }
}
