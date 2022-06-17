package com.android.systemui.statusbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.MathUtils;
import android.view.View;
import com.android.systemui.ExpandHelper;
import com.android.systemui.R$dimen;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.biometrics.UdfpsKeyguardViewController;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.media.MediaHierarchyManager;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.C1129QS;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.Utils;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: LockscreenShadeTransitionController.kt */
public final class LockscreenShadeTransitionController {
    @NotNull
    private final AmbientState ambientState;
    /* access modifiers changed from: private */
    @Nullable
    public Function1<? super Long, Unit> animationHandlerOnKeyguardDismiss;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    @NotNull
    private final NotificationShadeDepthController depthController;
    @NotNull
    private final DisplayMetrics displayMetrics;
    private float dragDownAmount;
    @Nullable
    private ValueAnimator dragDownAnimator;
    /* access modifiers changed from: private */
    @Nullable
    public NotificationEntry draggedDownEntry;
    @NotNull
    private final FalsingCollector falsingCollector;
    @NotNull
    private final FeatureFlags featureFlags;
    /* access modifiers changed from: private */
    public boolean forceApplyAmount;
    private int fullTransitionDistance;
    @NotNull
    private final KeyguardBypassController keyguardBypassController;
    @NotNull
    private final NotificationLockscreenUserManager lockScreenUserManager;
    @NotNull
    private final LockscreenGestureLogger lockscreenGestureLogger;
    @NotNull
    private final MediaHierarchyManager mediaHierarchyManager;
    /* access modifiers changed from: private */
    public boolean nextHideKeyguardNeedsNoAnimation;
    public NotificationPanelViewController notificationPanelController;
    private NotificationStackScrollLayoutController nsslController;
    private float pulseHeight;
    @Nullable
    private ValueAnimator pulseHeightAnimator;

    /* renamed from: qS */
    public C1129QS f125qS;
    @NotNull
    private final ScrimController scrimController;
    private int scrimTransitionDistance;
    /* access modifiers changed from: private */
    @NotNull
    public final SysuiStatusBarStateController statusBarStateController;
    public StatusBar statusbar;
    @NotNull
    private final DragDownHelper touchHelper;
    @Nullable
    private UdfpsKeyguardViewController udfpsKeyguardViewController;
    private boolean useSplitShade;

    /* renamed from: getDragDownAnimator$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m50x68daf339() {
    }

    /* renamed from: getPulseHeightAnimator$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m51xefeea8b7() {
    }

    public final void goToLockedShade(@Nullable View view) {
        goToLockedShade$default(this, view, false, 2, (Object) null);
    }

    public LockscreenShadeTransitionController(@NotNull SysuiStatusBarStateController sysuiStatusBarStateController, @NotNull LockscreenGestureLogger lockscreenGestureLogger2, @NotNull KeyguardBypassController keyguardBypassController2, @NotNull NotificationLockscreenUserManager notificationLockscreenUserManager, @NotNull FalsingCollector falsingCollector2, @NotNull AmbientState ambientState2, @NotNull DisplayMetrics displayMetrics2, @NotNull MediaHierarchyManager mediaHierarchyManager2, @NotNull ScrimController scrimController2, @NotNull NotificationShadeDepthController notificationShadeDepthController, @NotNull FeatureFlags featureFlags2, @NotNull Context context2, @NotNull ConfigurationController configurationController, @NotNull FalsingManager falsingManager) {
        Intrinsics.checkNotNullParameter(sysuiStatusBarStateController, "statusBarStateController");
        Intrinsics.checkNotNullParameter(lockscreenGestureLogger2, "lockscreenGestureLogger");
        Intrinsics.checkNotNullParameter(keyguardBypassController2, "keyguardBypassController");
        Intrinsics.checkNotNullParameter(notificationLockscreenUserManager, "lockScreenUserManager");
        Intrinsics.checkNotNullParameter(falsingCollector2, "falsingCollector");
        Intrinsics.checkNotNullParameter(ambientState2, "ambientState");
        Intrinsics.checkNotNullParameter(displayMetrics2, "displayMetrics");
        Intrinsics.checkNotNullParameter(mediaHierarchyManager2, "mediaHierarchyManager");
        Intrinsics.checkNotNullParameter(scrimController2, "scrimController");
        Intrinsics.checkNotNullParameter(notificationShadeDepthController, "depthController");
        Intrinsics.checkNotNullParameter(featureFlags2, "featureFlags");
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(configurationController, "configurationController");
        Intrinsics.checkNotNullParameter(falsingManager, "falsingManager");
        this.statusBarStateController = sysuiStatusBarStateController;
        this.lockscreenGestureLogger = lockscreenGestureLogger2;
        this.keyguardBypassController = keyguardBypassController2;
        this.lockScreenUserManager = notificationLockscreenUserManager;
        this.falsingCollector = falsingCollector2;
        this.ambientState = ambientState2;
        this.displayMetrics = displayMetrics2;
        this.mediaHierarchyManager = mediaHierarchyManager2;
        this.scrimController = scrimController2;
        this.depthController = notificationShadeDepthController;
        this.featureFlags = featureFlags2;
        this.context = context2;
        this.touchHelper = new DragDownHelper(falsingManager, falsingCollector2, this, context2);
        updateResources();
        configurationController.addCallback(new ConfigurationController.ConfigurationListener(this) {
            final /* synthetic */ LockscreenShadeTransitionController this$0;

            {
                this.this$0 = r1;
            }

            public void onConfigChanged(@Nullable Configuration configuration) {
                this.this$0.updateResources();
                this.this$0.getTouchHelper().updateResources(this.this$0.context);
            }
        });
    }

    @NotNull
    public final NotificationPanelViewController getNotificationPanelController() {
        NotificationPanelViewController notificationPanelViewController = this.notificationPanelController;
        if (notificationPanelViewController != null) {
            return notificationPanelViewController;
        }
        Intrinsics.throwUninitializedPropertyAccessException("notificationPanelController");
        throw null;
    }

    public final void setNotificationPanelController(@NotNull NotificationPanelViewController notificationPanelViewController) {
        Intrinsics.checkNotNullParameter(notificationPanelViewController, "<set-?>");
        this.notificationPanelController = notificationPanelViewController;
    }

    @NotNull
    public final StatusBar getStatusbar() {
        StatusBar statusBar = this.statusbar;
        if (statusBar != null) {
            return statusBar;
        }
        Intrinsics.throwUninitializedPropertyAccessException("statusbar");
        throw null;
    }

    public final void setStatusbar(@NotNull StatusBar statusBar) {
        Intrinsics.checkNotNullParameter(statusBar, "<set-?>");
        this.statusbar = statusBar;
    }

    @NotNull
    public final C1129QS getQS() {
        C1129QS qs = this.f125qS;
        if (qs != null) {
            return qs;
        }
        Intrinsics.throwUninitializedPropertyAccessException("qS");
        throw null;
    }

    public final void setQS(@NotNull C1129QS qs) {
        Intrinsics.checkNotNullParameter(qs, "<set-?>");
        this.f125qS = qs;
    }

    public final int getDistanceUntilShowingPulsingNotifications() {
        return this.scrimTransitionDistance;
    }

    @Nullable
    public final UdfpsKeyguardViewController getUdfpsKeyguardViewController() {
        return this.udfpsKeyguardViewController;
    }

    public final void setUdfpsKeyguardViewController(@Nullable UdfpsKeyguardViewController udfpsKeyguardViewController2) {
        this.udfpsKeyguardViewController = udfpsKeyguardViewController2;
    }

    @NotNull
    public final DragDownHelper getTouchHelper() {
        return this.touchHelper;
    }

    /* access modifiers changed from: private */
    public final void updateResources() {
        this.scrimTransitionDistance = this.context.getResources().getDimensionPixelSize(R$dimen.lockscreen_shade_scrim_transition_distance);
        this.fullTransitionDistance = this.context.getResources().getDimensionPixelSize(R$dimen.lockscreen_shade_qs_transition_distance);
        this.useSplitShade = Utils.shouldUseSplitNotificationShade(this.featureFlags, this.context.getResources());
    }

    public final void setStackScroller(@NotNull NotificationStackScrollLayoutController notificationStackScrollLayoutController) {
        Intrinsics.checkNotNullParameter(notificationStackScrollLayoutController, "nsslController");
        this.nsslController = notificationStackScrollLayoutController;
        DragDownHelper dragDownHelper = this.touchHelper;
        NotificationStackScrollLayout view = notificationStackScrollLayoutController.getView();
        Intrinsics.checkNotNullExpressionValue(view, "nsslController.view");
        dragDownHelper.setHost(view);
        DragDownHelper dragDownHelper2 = this.touchHelper;
        ExpandHelper.Callback expandHelperCallback = notificationStackScrollLayoutController.getExpandHelperCallback();
        Intrinsics.checkNotNullExpressionValue(expandHelperCallback, "nsslController.expandHelperCallback");
        dragDownHelper2.setExpandCallback(expandHelperCallback);
    }

    public final void bindController(@NotNull NotificationShelfController notificationShelfController) {
        Intrinsics.checkNotNullParameter(notificationShelfController, "notificationShelfController");
        notificationShelfController.setOnClickListener(new LockscreenShadeTransitionController$bindController$1(this));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0011, code lost:
        if (r0.isInLockedDownShade() != false) goto L_0x001b;
     */
    /* renamed from: canDragDown$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean mo18441x90582e2c() {
        /*
            r2 = this;
            com.android.systemui.statusbar.SysuiStatusBarStateController r0 = r2.statusBarStateController
            int r0 = r0.getState()
            r1 = 1
            if (r0 == r1) goto L_0x001b
            com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController r0 = r2.nsslController
            if (r0 == 0) goto L_0x0014
            boolean r0 = r0.isInLockedDownShade()
            if (r0 == 0) goto L_0x0026
            goto L_0x001b
        L_0x0014:
            java.lang.String r2 = "nsslController"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
            r2 = 0
            throw r2
        L_0x001b:
            com.android.systemui.plugins.qs.QS r2 = r2.getQS()
            boolean r2 = r2.isFullyCollapsed()
            if (r2 == 0) goto L_0x0026
            goto L_0x0027
        L_0x0026:
            r1 = 0
        L_0x0027:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.LockscreenShadeTransitionController.mo18441x90582e2c():boolean");
    }

    /* renamed from: onDraggedDown$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo18459xe98064bb(@Nullable View view, int i) {
        if (mo18441x90582e2c()) {
            NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.nsslController;
            if (notificationStackScrollLayoutController == null) {
                Intrinsics.throwUninitializedPropertyAccessException("nsslController");
                throw null;
            } else if (notificationStackScrollLayoutController.isInLockedDownShade()) {
                this.statusBarStateController.setLeaveOpenOnKeyguardHide(true);
                getStatusbar().dismissKeyguardThenExecute(new LockscreenShadeTransitionController$onDraggedDown$1(this), (Runnable) null, false);
            } else {
                this.lockscreenGestureLogger.write(187, (int) (((float) i) / this.displayMetrics.density), 0);
                this.lockscreenGestureLogger.log(LockscreenGestureLogger.LockscreenUiEvent.LOCKSCREEN_PULL_SHADE_OPEN);
                if (!this.ambientState.isDozing() || view != null) {
                    goToLockedShadeInternal(view, new C1453x21fef5f(view, this), new C1454xbbc01eb0(this));
                }
            }
        } else {
            setDragDownAmountAnimated$default(this, 0.0f, 0, (Function0) null, 6, (Object) null);
        }
    }

    /* renamed from: onDragDownReset$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo18457x3c3ba3a0() {
        NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.nsslController;
        if (notificationStackScrollLayoutController != null) {
            notificationStackScrollLayoutController.setDimmed(true, true);
            NotificationStackScrollLayoutController notificationStackScrollLayoutController2 = this.nsslController;
            if (notificationStackScrollLayoutController2 != null) {
                notificationStackScrollLayoutController2.resetScrollPosition();
                NotificationStackScrollLayoutController notificationStackScrollLayoutController3 = this.nsslController;
                if (notificationStackScrollLayoutController3 != null) {
                    notificationStackScrollLayoutController3.resetCheckSnoozeLeavebehind();
                    setDragDownAmountAnimated$default(this, 0.0f, 0, (Function0) null, 6, (Object) null);
                    return;
                }
                Intrinsics.throwUninitializedPropertyAccessException("nsslController");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("nsslController");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("nsslController");
        throw null;
    }

    /* renamed from: onCrossedThreshold$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo18456xc13cda11(boolean z) {
        NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.nsslController;
        if (notificationStackScrollLayoutController != null) {
            notificationStackScrollLayoutController.setDimmed(!z, true);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("nsslController");
            throw null;
        }
    }

    /* renamed from: onDragDownStarted$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo18458xb7e85712() {
        NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.nsslController;
        if (notificationStackScrollLayoutController != null) {
            notificationStackScrollLayoutController.cancelLongPress();
            NotificationStackScrollLayoutController notificationStackScrollLayoutController2 = this.nsslController;
            if (notificationStackScrollLayoutController2 != null) {
                notificationStackScrollLayoutController2.checkSnoozeLeavebehind();
                ValueAnimator valueAnimator = this.dragDownAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    return;
                }
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("nsslController");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("nsslController");
        throw null;
    }

    /* renamed from: isFalsingCheckNeeded$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final boolean mo18455x3e48de2d() {
        return this.statusBarStateController.getState() == 1;
    }

    /* renamed from: isDragDownEnabledForView$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final boolean mo18454x229f8ab3(@Nullable ExpandableView expandableView) {
        if (mo18452x7adb072c()) {
            return true;
        }
        NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.nsslController;
        if (notificationStackScrollLayoutController == null) {
            Intrinsics.throwUninitializedPropertyAccessException("nsslController");
            throw null;
        } else if (!notificationStackScrollLayoutController.isInLockedDownShade()) {
            return false;
        } else {
            if (expandableView == null) {
                return true;
            }
            if (expandableView instanceof ExpandableNotificationRow) {
                return ((ExpandableNotificationRow) expandableView).getEntry().isSensitive();
            }
            return false;
        }
    }

    /* renamed from: isDragDownDisableForPrc$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final boolean mo18453x2e6bd406() {
        return MotoFeature.getInstance(this.context).isCustomPanelView() && !(Settings.Global.getInt(this.context.getContentResolver(), "device_provisioned") != 0);
    }

    /* renamed from: isDragDownAnywhereEnabled$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final boolean mo18452x7adb072c() {
        if (this.statusBarStateController.getState() != 1 || this.keyguardBypassController.getBypassEnabled() || !getQS().isFullyCollapsed()) {
            return false;
        }
        return true;
    }

    /* renamed from: getDragDownAmount$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final float mo18444x30ab342a() {
        return this.dragDownAmount;
    }

    /* renamed from: setDragDownAmount$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo18462xcfc05636(float f) {
        if (!(this.dragDownAmount == f) || this.forceApplyAmount) {
            this.dragDownAmount = f;
            NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.nsslController;
            if (notificationStackScrollLayoutController == null) {
                Intrinsics.throwUninitializedPropertyAccessException("nsslController");
                throw null;
            } else if (!notificationStackScrollLayoutController.isInLockedDownShade() || this.forceApplyAmount) {
                NotificationStackScrollLayoutController notificationStackScrollLayoutController2 = this.nsslController;
                if (notificationStackScrollLayoutController2 != null) {
                    notificationStackScrollLayoutController2.setTransitionToFullShadeAmount(this.dragDownAmount);
                    getNotificationPanelController().setTransitionToFullShadeAmount(this.dragDownAmount, false, 0);
                    float f2 = 0.0f;
                    getQS().setTransitionToFullShadeAmount(this.useSplitShade ? 0.0f : this.dragDownAmount, false);
                    if (!this.useSplitShade) {
                        f2 = this.dragDownAmount;
                    }
                    this.mediaHierarchyManager.setTransitionToFullShadeAmount(f2);
                    transitionToShadeAmountCommon(this.dragDownAmount);
                    return;
                }
                Intrinsics.throwUninitializedPropertyAccessException("nsslController");
                throw null;
            }
        }
    }

    private final void transitionToShadeAmountCommon(float f) {
        float saturate = MathUtils.saturate(f / ((float) this.scrimTransitionDistance));
        this.scrimController.setTransitionToFullShadeProgress(saturate);
        getNotificationPanelController().setKeyguardOnlyContentAlpha(1.0f - saturate);
        this.depthController.setTransitionToFullShadeProgress(saturate);
        UdfpsKeyguardViewController udfpsKeyguardViewController2 = this.udfpsKeyguardViewController;
        if (udfpsKeyguardViewController2 != null) {
            udfpsKeyguardViewController2.setTransitionToFullShadeProgress(saturate);
        }
    }

    static /* synthetic */ void setDragDownAmountAnimated$default(LockscreenShadeTransitionController lockscreenShadeTransitionController, float f, long j, Function0 function0, int i, Object obj) {
        if ((i & 2) != 0) {
            j = 0;
        }
        if ((i & 4) != 0) {
            function0 = null;
        }
        lockscreenShadeTransitionController.setDragDownAmountAnimated(f, j, function0);
    }

    private final void setDragDownAmountAnimated(float f, long j, Function0<Unit> function0) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.dragDownAmount, f});
        ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.setDuration(375);
        ofFloat.addUpdateListener(new LockscreenShadeTransitionController$setDragDownAmountAnimated$1(this));
        if (j > 0) {
            ofFloat.setStartDelay(j);
        }
        if (function0 != null) {
            ofFloat.addListener(new LockscreenShadeTransitionController$setDragDownAmountAnimated$2(function0));
        }
        ofFloat.start();
        this.dragDownAnimator = ofFloat;
    }

    private final void animateAppear(long j) {
        this.forceApplyAmount = true;
        mo18462xcfc05636(1.0f);
        setDragDownAmountAnimated((float) this.fullTransitionDistance, j, new LockscreenShadeTransitionController$animateAppear$1(this));
    }

    public static /* synthetic */ void goToLockedShade$default(LockscreenShadeTransitionController lockscreenShadeTransitionController, View view, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = true;
        }
        lockscreenShadeTransitionController.goToLockedShade(view, z);
    }

    public final void goToLockedShade(@Nullable View view, boolean z) {
        LockscreenShadeTransitionController$goToLockedShade$1 lockscreenShadeTransitionController$goToLockedShade$1;
        if (this.statusBarStateController.getState() == 1) {
            if (z) {
                lockscreenShadeTransitionController$goToLockedShade$1 = null;
            } else {
                lockscreenShadeTransitionController$goToLockedShade$1 = new LockscreenShadeTransitionController$goToLockedShade$1(this);
            }
            goToLockedShadeInternal(view, lockscreenShadeTransitionController$goToLockedShade$1, (Runnable) null);
        }
    }

    private final void goToLockedShadeInternal(View view, Function1<? super Long, Unit> function1, Runnable runnable) {
        NotificationEntry notificationEntry;
        if (!getStatusbar().isShadeDisabled()) {
            int currentUserId = this.lockScreenUserManager.getCurrentUserId();
            LockscreenShadeTransitionController$goToLockedShadeInternal$1 lockscreenShadeTransitionController$goToLockedShadeInternal$1 = null;
            if (view instanceof ExpandableNotificationRow) {
                notificationEntry = ((ExpandableNotificationRow) view).getEntry();
                notificationEntry.setUserExpanded(true, true);
                notificationEntry.setGroupExpansionChanging(true);
                currentUserId = notificationEntry.getSbn().getUserId();
            } else {
                notificationEntry = null;
            }
            NotificationLockscreenUserManager notificationLockscreenUserManager = this.lockScreenUserManager;
            boolean z = false;
            boolean z2 = !notificationLockscreenUserManager.userAllowsPrivateNotificationsInPublic(notificationLockscreenUserManager.getCurrentUserId()) || !this.lockScreenUserManager.shouldShowLockscreenNotifications() || this.falsingCollector.shouldEnforceBouncer();
            if (!this.keyguardBypassController.getBypassEnabled()) {
                z = z2;
            }
            if (!this.lockScreenUserManager.isLockscreenPublicMode(currentUserId) || !z) {
                this.statusBarStateController.setState(2);
                if (function1 != null) {
                    function1.invoke(0L);
                } else {
                    performDefaultGoToFullShadeAnimation(0);
                }
            } else {
                this.statusBarStateController.setLeaveOpenOnKeyguardHide(true);
                if (function1 != null) {
                    lockscreenShadeTransitionController$goToLockedShadeInternal$1 = new LockscreenShadeTransitionController$goToLockedShadeInternal$1(this, function1);
                }
                getStatusbar().showBouncerWithDimissAndCancelIfKeyguard(lockscreenShadeTransitionController$goToLockedShadeInternal$1, new C1452x13216739(this, runnable));
                this.draggedDownEntry = notificationEntry;
            }
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public final void onHideKeyguard(long j, int i) {
        Function1<? super Long, Unit> function1 = this.animationHandlerOnKeyguardDismiss;
        if (function1 != null) {
            Intrinsics.checkNotNull(function1);
            function1.invoke(Long.valueOf(j));
            this.animationHandlerOnKeyguardDismiss = null;
        } else if (this.nextHideKeyguardNeedsNoAnimation) {
            this.nextHideKeyguardNeedsNoAnimation = false;
        } else if (i != 2) {
            performDefaultGoToFullShadeAnimation(j);
        }
        NotificationEntry notificationEntry = this.draggedDownEntry;
        if (notificationEntry != null) {
            notificationEntry.setUserLocked(false);
            this.draggedDownEntry = null;
        }
    }

    private final void performDefaultGoToFullShadeAnimation(long j) {
        getNotificationPanelController().animateToFullShade(j);
        animateAppear(j);
    }

    public static /* synthetic */ void setPulseHeight$default(LockscreenShadeTransitionController lockscreenShadeTransitionController, float f, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        lockscreenShadeTransitionController.setPulseHeight(f, z);
    }

    public final void setPulseHeight(float f, boolean z) {
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.pulseHeight, f});
            ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            ofFloat.setDuration(375);
            ofFloat.addUpdateListener(new LockscreenShadeTransitionController$setPulseHeight$1(this));
            ofFloat.start();
            this.pulseHeightAnimator = ofFloat;
            return;
        }
        this.pulseHeight = f;
        NotificationStackScrollLayoutController notificationStackScrollLayoutController = this.nsslController;
        if (notificationStackScrollLayoutController != null) {
            getNotificationPanelController().setOverStrechAmount(notificationStackScrollLayoutController.setPulseHeight(f));
            if (!this.keyguardBypassController.getBypassEnabled()) {
                f = 0.0f;
            }
            transitionToShadeAmountCommon(f);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("nsslController");
        throw null;
    }

    public final void finishPulseAnimation(boolean z) {
        if (z) {
            setPulseHeight(0.0f, true);
            return;
        }
        getNotificationPanelController().onPulseExpansionFinished();
        setPulseHeight(0.0f, false);
    }

    public final void onPulseExpansionStarted() {
        ValueAnimator valueAnimator = this.pulseHeightAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }
}
