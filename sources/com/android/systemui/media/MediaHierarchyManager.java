package com.android.systemui.media;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import com.android.systemui.R$dimen;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.animation.UniqueObjectHostView;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaHierarchyManager.kt */
public final class MediaHierarchyManager {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public float animationCrossFadeProgress;
    /* access modifiers changed from: private */
    public boolean animationPending;
    /* access modifiers changed from: private */
    public float animationStartAlpha;
    /* access modifiers changed from: private */
    @NotNull
    public Rect animationStartBounds = new Rect();
    /* access modifiers changed from: private */
    public float animationStartCrossFadeProgress;
    /* access modifiers changed from: private */
    public ValueAnimator animator;
    @NotNull
    private final KeyguardBypassController bypassController;
    private float carouselAlpha;
    private boolean collapsingShadeFromQS;
    @NotNull
    private final Context context;
    private int crossFadeAnimationEndLocation = -1;
    private int crossFadeAnimationStartLocation = -1;
    private int currentAttachmentLocation;
    /* access modifiers changed from: private */
    @NotNull
    public Rect currentBounds = new Rect();
    private int desiredLocation;
    private int distanceForFullShadeTransition;
    private boolean dozeAnimationRunning;
    private float fullShadeTransitionProgress;
    private boolean fullyAwake;
    private boolean goingToSleep;
    /* access modifiers changed from: private */
    public boolean isCrossFadeAnimatorRunning;
    @NotNull
    private final KeyguardStateController keyguardStateController;
    /* access modifiers changed from: private */
    @NotNull
    public final MediaCarouselController mediaCarouselController;
    @NotNull
    private final MediaHost[] mediaHosts;
    @NotNull
    private final NotificationLockscreenUserManager notifLockscreenUserManager;
    private int previousLocation;
    private boolean qsExpanded;
    private float qsExpansion;
    /* access modifiers changed from: private */
    @Nullable
    public ViewGroupOverlay rootOverlay;
    /* access modifiers changed from: private */
    @Nullable
    public View rootView;
    /* access modifiers changed from: private */
    @NotNull
    public final Runnable startAnimation;
    @NotNull
    private final StatusBarKeyguardViewManager statusBarKeyguardViewManager;
    @NotNull
    private final SysuiStatusBarStateController statusBarStateController;
    /* access modifiers changed from: private */
    public int statusbarState;
    /* access modifiers changed from: private */
    @NotNull
    public Rect targetBounds = new Rect();

    /* access modifiers changed from: private */
    public final float calculateAlphaFromCrossFade(float f, boolean z) {
        if (f <= 0.5f) {
            return 1.0f - (f / 0.5f);
        }
        if (z) {
            return 1.0f;
        }
        return (f - 0.5f) / 0.5f;
    }

    public MediaHierarchyManager(@NotNull Context context2, @NotNull SysuiStatusBarStateController sysuiStatusBarStateController, @NotNull KeyguardStateController keyguardStateController2, @NotNull KeyguardBypassController keyguardBypassController, @NotNull MediaCarouselController mediaCarouselController2, @NotNull NotificationLockscreenUserManager notificationLockscreenUserManager, @NotNull ConfigurationController configurationController, @NotNull WakefulnessLifecycle wakefulnessLifecycle, @NotNull StatusBarKeyguardViewManager statusBarKeyguardViewManager2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(sysuiStatusBarStateController, "statusBarStateController");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkNotNullParameter(keyguardBypassController, "bypassController");
        Intrinsics.checkNotNullParameter(mediaCarouselController2, "mediaCarouselController");
        Intrinsics.checkNotNullParameter(notificationLockscreenUserManager, "notifLockscreenUserManager");
        Intrinsics.checkNotNullParameter(configurationController, "configurationController");
        Intrinsics.checkNotNullParameter(wakefulnessLifecycle, "wakefulnessLifecycle");
        Intrinsics.checkNotNullParameter(statusBarKeyguardViewManager2, "statusBarKeyguardViewManager");
        this.context = context2;
        this.statusBarStateController = sysuiStatusBarStateController;
        this.keyguardStateController = keyguardStateController2;
        this.bypassController = keyguardBypassController;
        this.mediaCarouselController = mediaCarouselController2;
        this.notifLockscreenUserManager = notificationLockscreenUserManager;
        this.statusBarKeyguardViewManager = statusBarKeyguardViewManager2;
        this.statusbarState = sysuiStatusBarStateController.getState();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.addUpdateListener(new MediaHierarchyManager$animator$1$1(this, ofFloat));
        ofFloat.addListener(new MediaHierarchyManager$animator$1$2(this));
        Unit unit = Unit.INSTANCE;
        this.animator = ofFloat;
        this.mediaHosts = new MediaHost[3];
        this.previousLocation = -1;
        this.desiredLocation = -1;
        this.currentAttachmentLocation = -1;
        this.startAnimation = new MediaHierarchyManager$startAnimation$1(this);
        this.animationCrossFadeProgress = 1.0f;
        this.carouselAlpha = 1.0f;
        updateConfiguration();
        configurationController.addCallback(new ConfigurationController.ConfigurationListener(this) {
            final /* synthetic */ MediaHierarchyManager this$0;

            {
                this.this$0 = r1;
            }

            public void onDensityOrFontScaleChanged() {
                this.this$0.updateConfiguration();
            }
        });
        sysuiStatusBarStateController.addCallback(new StatusBarStateController.StateListener(this) {
            final /* synthetic */ MediaHierarchyManager this$0;

            {
                this.this$0 = r1;
            }

            public void onStatePreChange(int i, int i2) {
                boolean z = true;
                if (!(this.this$0.statusbarState == 1 && i2 == 0)) {
                    z = false;
                }
                this.this$0.statusbarState = i2;
                MediaHierarchyManager.updateDesiredLocation$default(this.this$0, z, false, 2, (Object) null);
            }

            public void onStateChanged(int i) {
                this.this$0.updateTargetState();
                if (i == 2 && this.this$0.isLockScreenShadeVisibleToUser()) {
                    this.this$0.mediaCarouselController.logSmartspaceImpression(this.this$0.getQsExpanded());
                }
                this.this$0.mediaCarouselController.getMediaCarouselScrollHandler().setVisibleToUser(this.this$0.isVisibleToUser());
            }

            /* JADX WARNING: Code restructure failed: missing block: B:9:0x0017, code lost:
                if ((r3 == 1.0f) == false) goto L_0x001b;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDozeAmountChanged(float r3, float r4) {
                /*
                    r2 = this;
                    com.android.systemui.media.MediaHierarchyManager r2 = r2.this$0
                    r4 = 0
                    int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                    r0 = 1
                    r1 = 0
                    if (r4 != 0) goto L_0x000b
                    r4 = r0
                    goto L_0x000c
                L_0x000b:
                    r4 = r1
                L_0x000c:
                    if (r4 != 0) goto L_0x001a
                    r4 = 1065353216(0x3f800000, float:1.0)
                    int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                    if (r3 != 0) goto L_0x0016
                    r3 = r0
                    goto L_0x0017
                L_0x0016:
                    r3 = r1
                L_0x0017:
                    if (r3 != 0) goto L_0x001a
                    goto L_0x001b
                L_0x001a:
                    r0 = r1
                L_0x001b:
                    r2.setDozeAnimationRunning(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaHierarchyManager.C10222.onDozeAmountChanged(float, float):void");
            }

            public void onDozingChanged(boolean z) {
                if (!z) {
                    this.this$0.setDozeAnimationRunning(false);
                    if (this.this$0.isLockScreenVisibleToUser()) {
                        this.this$0.mediaCarouselController.logSmartspaceImpression(this.this$0.getQsExpanded());
                    }
                } else {
                    MediaHierarchyManager.updateDesiredLocation$default(this.this$0, false, false, 3, (Object) null);
                    this.this$0.setQsExpanded(false);
                    this.this$0.closeGuts();
                }
                this.this$0.mediaCarouselController.getMediaCarouselScrollHandler().setVisibleToUser(this.this$0.isVisibleToUser());
            }

            public void onExpandedChanged(boolean z) {
                if (this.this$0.isHomeScreenShadeVisibleToUser()) {
                    this.this$0.mediaCarouselController.logSmartspaceImpression(this.this$0.getQsExpanded());
                }
                this.this$0.mediaCarouselController.getMediaCarouselScrollHandler().setVisibleToUser(this.this$0.isVisibleToUser());
            }
        });
        wakefulnessLifecycle.addObserver(new WakefulnessLifecycle.Observer(this) {
            final /* synthetic */ MediaHierarchyManager this$0;

            {
                this.this$0 = r1;
            }

            public void onFinishedGoingToSleep() {
                this.this$0.setGoingToSleep(false);
            }

            public void onStartedGoingToSleep() {
                this.this$0.setGoingToSleep(true);
                this.this$0.setFullyAwake(false);
            }

            public void onFinishedWakingUp() {
                this.this$0.setGoingToSleep(false);
                this.this$0.setFullyAwake(true);
            }

            public void onStartedWakingUp() {
                this.this$0.setGoingToSleep(false);
            }
        });
        mediaCarouselController2.setUpdateUserVisibility(new Function0<Unit>(this) {
            final /* synthetic */ MediaHierarchyManager this$0;

            {
                this.this$0 = r1;
            }

            public final void invoke() {
                this.this$0.mediaCarouselController.getMediaCarouselScrollHandler().setVisibleToUser(this.this$0.isVisibleToUser());
            }
        });
    }

    private final ViewGroup getMediaFrame() {
        return this.mediaCarouselController.getMediaFrame();
    }

    private final boolean getHasActiveMedia() {
        MediaHost mediaHost = this.mediaHosts[1];
        return Intrinsics.areEqual((Object) mediaHost == null ? null : Boolean.valueOf(mediaHost.getVisible()), (Object) Boolean.TRUE);
    }

    public final void setQsExpansion(float f) {
        if (!(this.qsExpansion == f)) {
            this.qsExpansion = f;
            updateDesiredLocation$default(this, false, false, 3, (Object) null);
            if (getQSTransformationProgress() >= 0.0f) {
                updateTargetState();
                applyTargetStateIfNotAnimating();
            }
        }
    }

    public final boolean getQsExpanded() {
        return this.qsExpanded;
    }

    public final void setQsExpanded(boolean z) {
        if (this.qsExpanded != z) {
            this.qsExpanded = z;
            this.mediaCarouselController.getMediaCarouselScrollHandler().setQsExpanded(z);
        }
        if (z && (isLockScreenShadeVisibleToUser() || isHomeScreenShadeVisibleToUser())) {
            this.mediaCarouselController.logSmartspaceImpression(z);
        }
        this.mediaCarouselController.getMediaCarouselScrollHandler().setVisibleToUser(isVisibleToUser());
    }

    private final void setFullShadeTransitionProgress(float f) {
        if (!(this.fullShadeTransitionProgress == f)) {
            this.fullShadeTransitionProgress = f;
            if (!this.bypassController.getBypassEnabled() && this.statusbarState == 1) {
                updateDesiredLocation$default(this, isCurrentlyFading(), false, 2, (Object) null);
                if (f >= 0.0f) {
                    updateTargetState();
                    setCarouselAlpha(calculateAlphaFromCrossFade(this.fullShadeTransitionProgress, true));
                    applyTargetStateIfNotAnimating();
                }
            }
        }
    }

    private final boolean isTransitioningToFullShade() {
        if ((this.fullShadeTransitionProgress == 0.0f) || this.bypassController.getBypassEnabled() || this.statusbarState != 1) {
            return false;
        }
        return true;
    }

    public final void setTransitionToFullShadeAmount(float f) {
        setFullShadeTransitionProgress(MathUtils.saturate(f / ((float) this.distanceForFullShadeTransition)));
    }

    public final void setCollapsingShadeFromQS(boolean z) {
        if (this.collapsingShadeFromQS != z) {
            this.collapsingShadeFromQS = z;
            updateDesiredLocation$default(this, true, false, 2, (Object) null);
        }
    }

    private final boolean getBlockLocationChanges() {
        return this.goingToSleep || this.dozeAnimationRunning;
    }

    /* access modifiers changed from: private */
    public final void setGoingToSleep(boolean z) {
        if (this.goingToSleep != z) {
            this.goingToSleep = z;
            if (!z) {
                updateDesiredLocation$default(this, false, false, 3, (Object) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void setFullyAwake(boolean z) {
        if (this.fullyAwake != z) {
            this.fullyAwake = z;
            if (z) {
                updateDesiredLocation$default(this, true, false, 2, (Object) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void setDozeAnimationRunning(boolean z) {
        if (this.dozeAnimationRunning != z) {
            this.dozeAnimationRunning = z;
            if (!z) {
                updateDesiredLocation$default(this, false, false, 3, (Object) null);
            }
        }
    }

    private final void setCarouselAlpha(float f) {
        if (!(this.carouselAlpha == f)) {
            this.carouselAlpha = f;
            CrossFadeHelper.fadeIn(getMediaFrame(), f);
        }
    }

    /* access modifiers changed from: private */
    public final void updateConfiguration() {
        this.distanceForFullShadeTransition = this.context.getResources().getDimensionPixelSize(R$dimen.lockscreen_shade_media_transition_distance);
    }

    @NotNull
    public final UniqueObjectHostView register(@NotNull MediaHost mediaHost) {
        Intrinsics.checkNotNullParameter(mediaHost, "mediaObject");
        UniqueObjectHostView createUniqueObjectHost = createUniqueObjectHost();
        mediaHost.setHostView(createUniqueObjectHost);
        mediaHost.addVisibilityChangeListener(new MediaHierarchyManager$register$1(mediaHost, this));
        this.mediaHosts[mediaHost.getLocation()] = mediaHost;
        if (mediaHost.getLocation() == this.desiredLocation) {
            this.desiredLocation = -1;
        }
        if (mediaHost.getLocation() == this.currentAttachmentLocation) {
            this.currentAttachmentLocation = -1;
        }
        updateDesiredLocation$default(this, false, false, 3, (Object) null);
        return createUniqueObjectHost;
    }

    public final void closeGuts() {
        MediaCarouselController.closeGuts$default(this.mediaCarouselController, false, 1, (Object) null);
    }

    private final UniqueObjectHostView createUniqueObjectHost() {
        UniqueObjectHostView uniqueObjectHostView = new UniqueObjectHostView(this.context);
        uniqueObjectHostView.addOnAttachStateChangeListener(new MediaHierarchyManager$createUniqueObjectHost$1(this, uniqueObjectHostView));
        return uniqueObjectHostView;
    }

    static /* synthetic */ void updateDesiredLocation$default(MediaHierarchyManager mediaHierarchyManager, boolean z, boolean z2, int i, Object obj) {
        if ((i & 1) != 0) {
            z = false;
        }
        if ((i & 2) != 0) {
            z2 = false;
        }
        mediaHierarchyManager.updateDesiredLocation(z, z2);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001d, code lost:
        r11 = r9.statusbarState;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void updateDesiredLocation(boolean r10, boolean r11) {
        /*
            r9 = this;
            int r1 = r9.calculateLocation()
            int r0 = r9.desiredLocation
            if (r1 != r0) goto L_0x000a
            if (r11 == 0) goto L_0x0082
        L_0x000a:
            r2 = 0
            r3 = 1
            if (r0 < 0) goto L_0x0013
            if (r1 == r0) goto L_0x0013
            r9.previousLocation = r0
            goto L_0x0032
        L_0x0013:
            if (r11 == 0) goto L_0x0032
            com.android.systemui.statusbar.phone.KeyguardBypassController r11 = r9.bypassController
            boolean r11 = r11.getBypassEnabled()
            if (r11 != 0) goto L_0x0026
            int r11 = r9.statusbarState
            if (r11 == r3) goto L_0x0024
            r0 = 3
            if (r11 != r0) goto L_0x0026
        L_0x0024:
            r11 = r3
            goto L_0x0027
        L_0x0026:
            r11 = r2
        L_0x0027:
            if (r1 != 0) goto L_0x0032
            int r0 = r9.previousLocation
            r4 = 2
            if (r0 != r4) goto L_0x0032
            if (r11 != 0) goto L_0x0032
            r9.previousLocation = r3
        L_0x0032:
            int r11 = r9.desiredLocation
            r0 = -1
            if (r11 != r0) goto L_0x0039
            r11 = r3
            goto L_0x003a
        L_0x0039:
            r11 = r2
        L_0x003a:
            r9.desiredLocation = r1
            if (r10 != 0) goto L_0x0048
            int r10 = r9.previousLocation
            boolean r10 = r9.shouldAnimateTransition(r1, r10)
            if (r10 == 0) goto L_0x0048
            r10 = r3
            goto L_0x0049
        L_0x0048:
            r10 = r2
        L_0x0049:
            int r0 = r9.previousLocation
            kotlin.Pair r0 = r9.getAnimationParams(r0, r1)
            java.lang.Object r4 = r0.component1()
            java.lang.Number r4 = (java.lang.Number) r4
            long r4 = r4.longValue()
            java.lang.Object r0 = r0.component2()
            java.lang.Number r0 = (java.lang.Number) r0
            long r6 = r0.longValue()
            com.android.systemui.media.MediaHost r8 = r9.getHost(r1)
            int r0 = r9.calculateTransformationType()
            if (r0 != r3) goto L_0x006e
            r2 = r3
        L_0x006e:
            if (r2 == 0) goto L_0x0078
            boolean r0 = r9.isCurrentlyInGuidedTransformation()
            if (r0 != 0) goto L_0x0078
            if (r10 != 0) goto L_0x007f
        L_0x0078:
            com.android.systemui.media.MediaCarouselController r0 = r9.mediaCarouselController
            r2 = r8
            r3 = r10
            r0.onDesiredLocationChanged(r1, r2, r3, r4, r6)
        L_0x007f:
            r9.performTransitionToNewLocation(r11, r10)
        L_0x0082:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaHierarchyManager.updateDesiredLocation(boolean, boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:48:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void performTransitionToNewLocation(boolean r7, boolean r8) {
        /*
            r6 = this;
            int r0 = r6.previousLocation
            if (r0 < 0) goto L_0x00b2
            if (r7 == 0) goto L_0x0008
            goto L_0x00b2
        L_0x0008:
            int r7 = r6.desiredLocation
            com.android.systemui.media.MediaHost r7 = r6.getHost(r7)
            int r0 = r6.previousLocation
            com.android.systemui.media.MediaHost r0 = r6.getHost(r0)
            if (r7 == 0) goto L_0x00ae
            if (r0 != 0) goto L_0x001a
            goto L_0x00ae
        L_0x001a:
            r6.updateTargetState()
            boolean r7 = r6.isCurrentlyInGuidedTransformation()
            if (r7 == 0) goto L_0x0028
            r6.applyTargetStateIfNotAnimating()
            goto L_0x00ad
        L_0x0028:
            if (r8 == 0) goto L_0x00aa
            boolean r7 = r6.isCrossFadeAnimatorRunning
            float r8 = r6.animationCrossFadeProgress
            android.animation.ValueAnimator r1 = r6.animator
            r1.cancel()
            int r1 = r6.currentAttachmentLocation
            int r2 = r6.previousLocation
            if (r1 != r2) goto L_0x004e
            com.android.systemui.util.animation.UniqueObjectHostView r1 = r0.getHostView()
            boolean r1 = r1.isAttachedToWindow()
            if (r1 != 0) goto L_0x0044
            goto L_0x004e
        L_0x0044:
            android.graphics.Rect r1 = r6.animationStartBounds
            android.graphics.Rect r0 = r0.getCurrentBounds()
            r1.set(r0)
            goto L_0x0055
        L_0x004e:
            android.graphics.Rect r0 = r6.animationStartBounds
            android.graphics.Rect r1 = r6.currentBounds
            r0.set(r1)
        L_0x0055:
            int r0 = r6.calculateTransformationType()
            r1 = 1
            if (r0 != r1) goto L_0x005e
            r0 = r1
            goto L_0x005f
        L_0x005e:
            r0 = 0
        L_0x005f:
            r2 = 0
            int r3 = r6.previousLocation
            r4 = 1065353216(0x3f800000, float:1.0)
            if (r7 == 0) goto L_0x007c
            int r7 = r6.currentAttachmentLocation
            int r5 = r6.crossFadeAnimationEndLocation
            if (r7 != r5) goto L_0x0071
            if (r0 == 0) goto L_0x0086
            float r8 = r4 - r8
            goto L_0x0087
        L_0x0071:
            int r7 = r6.crossFadeAnimationStartLocation
            int r2 = r6.desiredLocation
            if (r7 != r2) goto L_0x007a
            float r8 = r4 - r8
            goto L_0x0088
        L_0x007a:
            r0 = r1
            goto L_0x0088
        L_0x007c:
            if (r0 == 0) goto L_0x0086
            float r7 = r6.carouselAlpha
            float r4 = r4 - r7
            r7 = 1073741824(0x40000000, float:2.0)
            float r8 = r4 / r7
            goto L_0x0087
        L_0x0086:
            r8 = r2
        L_0x0087:
            r7 = r3
        L_0x0088:
            r6.isCrossFadeAnimatorRunning = r0
            r6.crossFadeAnimationStartLocation = r7
            int r7 = r6.desiredLocation
            r6.crossFadeAnimationEndLocation = r7
            float r0 = r6.carouselAlpha
            r6.animationStartAlpha = r0
            r6.animationStartCrossFadeProgress = r8
            r6.adjustAnimatorForTransition(r7, r3)
            boolean r7 = r6.animationPending
            if (r7 != 0) goto L_0x00ad
            android.view.View r7 = r6.rootView
            if (r7 != 0) goto L_0x00a2
            goto L_0x00ad
        L_0x00a2:
            r6.animationPending = r1
            java.lang.Runnable r6 = r6.startAnimation
            r7.postOnAnimation(r6)
            goto L_0x00ad
        L_0x00aa:
            r6.cancelAnimationAndApplyDesiredState()
        L_0x00ad:
            return
        L_0x00ae:
            r6.cancelAnimationAndApplyDesiredState()
            return
        L_0x00b2:
            r6.cancelAnimationAndApplyDesiredState()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaHierarchyManager.performTransitionToNewLocation(boolean, boolean):void");
    }

    private final boolean shouldAnimateTransition(int i, int i2) {
        if (isCurrentlyInGuidedTransformation()) {
            return false;
        }
        if (i2 == 2 && this.desiredLocation == 1 && this.statusbarState == 0) {
            return false;
        }
        if (i == 1 && i2 == 2 && (this.statusBarStateController.leaveOpenOnKeyguardHide() || this.statusbarState == 2)) {
            return true;
        }
        if (this.statusbarState == 1 && (i == 2 || i2 == 2)) {
            return false;
        }
        if (MediaHierarchyManagerKt.isShownNotFaded(getMediaFrame()) || this.animator.isRunning() || this.animationPending) {
            return true;
        }
        return false;
    }

    private final void adjustAnimatorForTransition(int i, int i2) {
        Pair<Long, Long> animationParams = getAnimationParams(i2, i);
        long longValue = animationParams.component1().longValue();
        long longValue2 = animationParams.component2().longValue();
        ValueAnimator valueAnimator = this.animator;
        valueAnimator.setDuration(longValue);
        valueAnimator.setStartDelay(longValue2);
    }

    private final Pair<Long, Long> getAnimationParams(int i, int i2) {
        long j;
        long j2 = 0;
        if (i == 2 && i2 == 1) {
            if (this.statusbarState == 0 && this.keyguardStateController.isKeyguardFadingAway()) {
                j2 = this.keyguardStateController.getKeyguardFadingAwayDelay();
            }
            j = 224;
        } else {
            j = (i == 1 && i2 == 2) ? 464 : 200;
        }
        return TuplesKt.m104to(Long.valueOf(j), Long.valueOf(j2));
    }

    /* access modifiers changed from: private */
    public final void applyTargetStateIfNotAnimating() {
        if (!this.animator.isRunning()) {
            applyState$default(this, this.targetBounds, this.carouselAlpha, false, 4, (Object) null);
        }
    }

    /* access modifiers changed from: private */
    public final void updateTargetState() {
        if (!isCurrentlyInGuidedTransformation() || isCurrentlyFading()) {
            MediaHost host = getHost(this.desiredLocation);
            Rect currentBounds2 = host == null ? null : host.getCurrentBounds();
            if (currentBounds2 != null) {
                this.targetBounds.set(currentBounds2);
                return;
            }
            return;
        }
        float transformationProgress = getTransformationProgress();
        MediaHost host2 = getHost(this.desiredLocation);
        Intrinsics.checkNotNull(host2);
        MediaHost host3 = getHost(this.previousLocation);
        Intrinsics.checkNotNull(host3);
        if (!host2.getVisible()) {
            host2 = host3;
        } else if (!host3.getVisible()) {
            host3 = host2;
        }
        this.targetBounds = interpolateBounds$default(this, host3.getCurrentBounds(), host2.getCurrentBounds(), transformationProgress, (Rect) null, 8, (Object) null);
    }

    static /* synthetic */ Rect interpolateBounds$default(MediaHierarchyManager mediaHierarchyManager, Rect rect, Rect rect2, float f, Rect rect3, int i, Object obj) {
        if ((i & 8) != 0) {
            rect3 = null;
        }
        return mediaHierarchyManager.interpolateBounds(rect, rect2, f, rect3);
    }

    /* access modifiers changed from: private */
    public final Rect interpolateBounds(Rect rect, Rect rect2, float f, Rect rect3) {
        int lerp = (int) MathUtils.lerp((float) rect.left, (float) rect2.left, f);
        int lerp2 = (int) MathUtils.lerp((float) rect.top, (float) rect2.top, f);
        int lerp3 = (int) MathUtils.lerp((float) rect.right, (float) rect2.right, f);
        int lerp4 = (int) MathUtils.lerp((float) rect.bottom, (float) rect2.bottom, f);
        if (rect3 == null) {
            rect3 = new Rect();
        }
        rect3.set(lerp, lerp2, lerp3, lerp4);
        return rect3;
    }

    private final boolean isCurrentlyInGuidedTransformation() {
        return getTransformationProgress() >= 0.0f;
    }

    public final int calculateTransformationType() {
        if (isTransitioningToFullShade()) {
            return 1;
        }
        int i = this.previousLocation;
        if ((i == 2 && this.desiredLocation == 0) || (i == 0 && this.desiredLocation == 2)) {
            return 1;
        }
        if (i == 2 && this.desiredLocation == 1) {
            return 1;
        }
        return 0;
    }

    private final float getTransformationProgress() {
        float qSTransformationProgress = getQSTransformationProgress();
        if (this.statusbarState != 1 && qSTransformationProgress >= 0.0f) {
            return qSTransformationProgress;
        }
        if (isTransitioningToFullShade()) {
            return this.fullShadeTransitionProgress;
        }
        return -1.0f;
    }

    private final float getQSTransformationProgress() {
        MediaHost host = getHost(this.desiredLocation);
        MediaHost host2 = getHost(this.previousLocation);
        if (!getHasActiveMedia()) {
            return -1.0f;
        }
        Integer num = null;
        Integer valueOf = host == null ? null : Integer.valueOf(host.getLocation());
        if (valueOf == null || valueOf.intValue() != 0) {
            return -1.0f;
        }
        if (host2 != null) {
            num = Integer.valueOf(host2.getLocation());
        }
        if (num == null || num.intValue() != 1) {
            return -1.0f;
        }
        if (host2.getVisible() || this.statusbarState != 1) {
            return this.qsExpansion;
        }
        return -1.0f;
    }

    private final MediaHost getHost(int i) {
        if (i < 0) {
            return null;
        }
        return this.mediaHosts[i];
    }

    private final void cancelAnimationAndApplyDesiredState() {
        this.animator.cancel();
        MediaHost host = getHost(this.desiredLocation);
        if (host != null) {
            applyState(host.getCurrentBounds(), 1.0f, true);
        }
    }

    static /* synthetic */ void applyState$default(MediaHierarchyManager mediaHierarchyManager, Rect rect, float f, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = false;
        }
        mediaHierarchyManager.applyState(rect, f, z);
    }

    private final void applyState(Rect rect, float f, boolean z) {
        int i;
        this.currentBounds.set(rect);
        float f2 = 1.0f;
        if (!isCurrentlyFading()) {
            f = 1.0f;
        }
        setCarouselAlpha(f);
        boolean z2 = !isCurrentlyInGuidedTransformation() || isCurrentlyFading();
        if (z2) {
            i = -1;
        } else {
            i = this.previousLocation;
        }
        if (!z2) {
            f2 = getTransformationProgress();
        }
        this.mediaCarouselController.setCurrentState(i, resolveLocationForFading(), f2, z);
        updateHostAttachment();
        if (this.currentAttachmentLocation == -1000) {
            ViewGroup mediaFrame = getMediaFrame();
            Rect rect2 = this.currentBounds;
            mediaFrame.setLeftTopRightBottom(rect2.left, rect2.top, rect2.right, rect2.bottom);
        }
    }

    private final void updateHostAttachment() {
        UniqueObjectHostView hostView;
        int resolveLocationForFading = resolveLocationForFading();
        boolean z = true;
        boolean z2 = !isCurrentlyFading();
        if (this.isCrossFadeAnimatorRunning) {
            MediaHost host = getHost(resolveLocationForFading);
            Boolean bool = null;
            if (Intrinsics.areEqual((Object) host == null ? null : Boolean.valueOf(host.getVisible()), (Object) Boolean.TRUE)) {
                MediaHost host2 = getHost(resolveLocationForFading);
                if (!(host2 == null || (hostView = host2.getHostView()) == null)) {
                    bool = Boolean.valueOf(hostView.isShown());
                }
                if (Intrinsics.areEqual((Object) bool, (Object) Boolean.FALSE) && resolveLocationForFading != this.desiredLocation) {
                    z2 = true;
                }
            }
        }
        if (!isTransitionRunning() || this.rootOverlay == null || !z2) {
            z = false;
        }
        if (z) {
            resolveLocationForFading = -1000;
        }
        int i = resolveLocationForFading;
        if (this.currentAttachmentLocation != i) {
            this.currentAttachmentLocation = i;
            ViewGroup viewGroup = (ViewGroup) getMediaFrame().getParent();
            if (viewGroup != null) {
                viewGroup.removeView(getMediaFrame());
            }
            if (z) {
                ViewGroupOverlay viewGroupOverlay = this.rootOverlay;
                Intrinsics.checkNotNull(viewGroupOverlay);
                viewGroupOverlay.add(getMediaFrame());
            } else {
                MediaHost host3 = getHost(i);
                Intrinsics.checkNotNull(host3);
                UniqueObjectHostView hostView2 = host3.getHostView();
                hostView2.addView(getMediaFrame());
                int paddingLeft = hostView2.getPaddingLeft();
                int paddingTop = hostView2.getPaddingTop();
                getMediaFrame().setLeftTopRightBottom(paddingLeft, paddingTop, this.currentBounds.width() + paddingLeft, this.currentBounds.height() + paddingTop);
            }
            if (this.isCrossFadeAnimatorRunning) {
                MediaCarouselController.onDesiredLocationChanged$default(this.mediaCarouselController, i, getHost(i), false, 0, 0, 24, (Object) null);
            }
        }
    }

    private final int resolveLocationForFading() {
        if (!this.isCrossFadeAnimatorRunning) {
            return this.desiredLocation;
        }
        if (((double) this.animationCrossFadeProgress) > 0.5d || this.previousLocation == -1) {
            return this.crossFadeAnimationEndLocation;
        }
        return this.crossFadeAnimationStartLocation;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0015, code lost:
        if ((getTransformationProgress() == 1.0f) != false) goto L_0x0017;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean isTransitionRunning() {
        /*
            r4 = this;
            boolean r0 = r4.isCurrentlyInGuidedTransformation()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0017
            float r0 = r4.getTransformationProgress()
            r3 = 1065353216(0x3f800000, float:1.0)
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0014
            r0 = r2
            goto L_0x0015
        L_0x0014:
            r0 = r1
        L_0x0015:
            if (r0 == 0) goto L_0x0023
        L_0x0017:
            android.animation.ValueAnimator r0 = r4.animator
            boolean r0 = r0.isRunning()
            if (r0 != 0) goto L_0x0023
            boolean r4 = r4.animationPending
            if (r4 == 0) goto L_0x0024
        L_0x0023:
            r1 = r2
        L_0x0024:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaHierarchyManager.isTransitionRunning():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0013, code lost:
        r0 = r7.statusbarState;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int calculateLocation() {
        /*
            r7 = this;
            boolean r0 = r7.getBlockLocationChanges()
            if (r0 == 0) goto L_0x0009
            int r7 = r7.desiredLocation
            return r7
        L_0x0009:
            com.android.systemui.statusbar.phone.KeyguardBypassController r0 = r7.bypassController
            boolean r0 = r0.getBypassEnabled()
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x001c
            int r0 = r7.statusbarState
            if (r0 == r1) goto L_0x001a
            r3 = 3
            if (r0 != r3) goto L_0x001c
        L_0x001a:
            r0 = r1
            goto L_0x001d
        L_0x001c:
            r0 = r2
        L_0x001d:
            com.android.systemui.statusbar.NotificationLockscreenUserManager r3 = r7.notifLockscreenUserManager
            boolean r3 = r3.shouldShowLockscreenNotifications()
            float r4 = r7.qsExpansion
            r5 = 0
            int r5 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            r6 = 2
            if (r5 <= 0) goto L_0x002f
            if (r0 != 0) goto L_0x002f
        L_0x002d:
            r1 = r2
            goto L_0x004e
        L_0x002f:
            r5 = 1053609165(0x3ecccccd, float:0.4)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x0039
            if (r0 == 0) goto L_0x0039
            goto L_0x002d
        L_0x0039:
            boolean r4 = r7.getHasActiveMedia()
            if (r4 != 0) goto L_0x0040
            goto L_0x002d
        L_0x0040:
            if (r0 == 0) goto L_0x0049
            boolean r4 = r7.isTransformingToFullShadeAndInQQS()
            if (r4 == 0) goto L_0x0049
            goto L_0x004e
        L_0x0049:
            if (r0 == 0) goto L_0x004e
            if (r3 == 0) goto L_0x004e
            r1 = r6
        L_0x004e:
            if (r1 != r6) goto L_0x0071
            com.android.systemui.media.MediaHost r0 = r7.getHost(r1)
            if (r0 != 0) goto L_0x0058
            r0 = 0
            goto L_0x0060
        L_0x0058:
            boolean r0 = r0.getVisible()
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
        L_0x0060:
            java.lang.Boolean r3 = java.lang.Boolean.TRUE
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r0, (java.lang.Object) r3)
            if (r0 != 0) goto L_0x0071
            com.android.systemui.statusbar.SysuiStatusBarStateController r0 = r7.statusBarStateController
            boolean r0 = r0.isDozing()
            if (r0 != 0) goto L_0x0071
            return r2
        L_0x0071:
            if (r1 != r6) goto L_0x007c
            int r0 = r7.desiredLocation
            if (r0 != 0) goto L_0x007c
            boolean r0 = r7.collapsingShadeFromQS
            if (r0 == 0) goto L_0x007c
            return r2
        L_0x007c:
            if (r1 == r6) goto L_0x0087
            int r0 = r7.desiredLocation
            if (r0 != r6) goto L_0x0087
            boolean r7 = r7.fullyAwake
            if (r7 != 0) goto L_0x0087
            return r6
        L_0x0087:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaHierarchyManager.calculateLocation():int");
    }

    private final boolean isTransformingToFullShadeAndInQQS() {
        if (isTransitioningToFullShade() && this.fullShadeTransitionProgress > 0.5f) {
            return true;
        }
        return false;
    }

    private final boolean isCurrentlyFading() {
        if (isTransitioningToFullShade()) {
            return true;
        }
        return this.isCrossFadeAnimatorRunning;
    }

    /* access modifiers changed from: private */
    public final boolean isVisibleToUser() {
        return isLockScreenVisibleToUser() || isLockScreenShadeVisibleToUser() || isHomeScreenShadeVisibleToUser();
    }

    /* access modifiers changed from: private */
    public final boolean isLockScreenVisibleToUser() {
        if (this.statusBarStateController.isDozing() || this.statusBarKeyguardViewManager.isBouncerShowing() || this.statusBarStateController.getState() != 1 || !this.notifLockscreenUserManager.shouldShowLockscreenNotifications() || !this.statusBarStateController.isExpanded() || this.qsExpanded) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public final boolean isLockScreenShadeVisibleToUser() {
        if (!this.statusBarStateController.isDozing() && !this.statusBarKeyguardViewManager.isBouncerShowing()) {
            if (this.statusBarStateController.getState() == 2) {
                return true;
            }
            if (this.statusBarStateController.getState() != 1 || !this.qsExpanded) {
                return false;
            }
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public final boolean isHomeScreenShadeVisibleToUser() {
        return !this.statusBarStateController.isDozing() && this.statusBarStateController.getState() == 0 && this.statusBarStateController.isExpanded();
    }

    /* compiled from: MediaHierarchyManager.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
