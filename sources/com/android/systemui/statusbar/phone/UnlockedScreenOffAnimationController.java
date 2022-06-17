package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;
import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import android.view.View;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.LightRevealScrim;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.Lazy;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: UnlockedScreenOffAnimationController.kt */
public final class UnlockedScreenOffAnimationController implements WakefulnessLifecycle.Observer {
    @NotNull
    private final AmbientDisplayConfiguration ambientDisplayConfiguration;
    /* access modifiers changed from: private */
    public boolean aodUiAnimationPlaying;
    @NotNull
    private final Context context;
    /* access modifiers changed from: private */
    @Nullable
    public Boolean decidedToAnimateGoingToSleep;
    @NotNull
    private final Lazy<DozeParameters> dozeParameters;
    @NotNull
    private final Handler handler = new Handler();
    @NotNull
    private final KeyguardStateController keyguardStateController;
    /* access modifiers changed from: private */
    @NotNull
    public final Lazy<KeyguardViewMediator> keyguardViewMediatorLazy;
    /* access modifiers changed from: private */
    public boolean lightRevealAnimationPlaying;
    private final ValueAnimator lightRevealAnimator;
    /* access modifiers changed from: private */
    public LightRevealScrim lightRevealScrim;
    /* access modifiers changed from: private */
    public StatusBar statusBar;
    @NotNull
    private final StatusBarStateControllerImpl statusBarStateControllerImpl;
    @NotNull
    private final WakefulnessLifecycle wakefulnessLifecycle;

    public UnlockedScreenOffAnimationController(@NotNull Context context2, @NotNull WakefulnessLifecycle wakefulnessLifecycle2, @NotNull StatusBarStateControllerImpl statusBarStateControllerImpl2, @NotNull Lazy<KeyguardViewMediator> lazy, @NotNull KeyguardStateController keyguardStateController2, @NotNull Lazy<DozeParameters> lazy2, @NotNull AmbientDisplayConfiguration ambientDisplayConfiguration2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(wakefulnessLifecycle2, "wakefulnessLifecycle");
        Intrinsics.checkNotNullParameter(statusBarStateControllerImpl2, "statusBarStateControllerImpl");
        Intrinsics.checkNotNullParameter(lazy, "keyguardViewMediatorLazy");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkNotNullParameter(lazy2, "dozeParameters");
        Intrinsics.checkNotNullParameter(ambientDisplayConfiguration2, "ambientDisplayConfiguration");
        this.context = context2;
        this.wakefulnessLifecycle = wakefulnessLifecycle2;
        this.statusBarStateControllerImpl = statusBarStateControllerImpl2;
        this.keyguardViewMediatorLazy = lazy;
        this.keyguardStateController = keyguardStateController2;
        this.dozeParameters = lazy2;
        this.ambientDisplayConfiguration = ambientDisplayConfiguration2;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        ofFloat.setDuration(750);
        ofFloat.setInterpolator(Interpolators.LINEAR);
        ofFloat.addUpdateListener(new UnlockedScreenOffAnimationController$lightRevealAnimator$1$1(this));
        ofFloat.addListener(new UnlockedScreenOffAnimationController$lightRevealAnimator$1$2(this));
        Unit unit = Unit.INSTANCE;
        this.lightRevealAnimator = ofFloat;
    }

    public final void initialize(@NotNull StatusBar statusBar2, @NotNull LightRevealScrim lightRevealScrim2) {
        Intrinsics.checkNotNullParameter(statusBar2, "statusBar");
        Intrinsics.checkNotNullParameter(lightRevealScrim2, "lightRevealScrim");
        this.lightRevealScrim = lightRevealScrim2;
        this.statusBar = statusBar2;
        this.wakefulnessLifecycle.addObserver(this);
    }

    public final void animateInKeyguard(@NotNull View view, @NotNull Runnable runnable) {
        Intrinsics.checkNotNullParameter(view, "keyguardView");
        Intrinsics.checkNotNullParameter(runnable, "after");
        view.setAlpha(0.0f);
        view.setVisibility(0);
        float y = view.getY();
        view.setY(y - (((float) view.getHeight()) * 0.1f));
        AnimatableProperty animatableProperty = AnimatableProperty.f131Y;
        PropertyAnimator.cancelAnimation(view, animatableProperty);
        long j = (long) 500;
        PropertyAnimator.setProperty(view, animatableProperty, y, new AnimationProperties().setDuration(j), true);
        view.animate().setDuration(j).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).alpha(1.0f).withEndAction(new UnlockedScreenOffAnimationController$animateInKeyguard$1(this, runnable)).start();
    }

    public void onStartedWakingUp() {
        this.decidedToAnimateGoingToSleep = null;
        this.lightRevealAnimator.cancel();
        this.handler.removeCallbacksAndMessages((Object) null);
    }

    public void onFinishedWakingUp() {
        this.lightRevealAnimationPlaying = false;
        this.aodUiAnimationPlaying = false;
        if (this.dozeParameters.get().canControlUnlockedScreenOff()) {
            StatusBar statusBar2 = this.statusBar;
            if (statusBar2 != null) {
                statusBar2.updateIsKeyguard(true);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("statusBar");
                throw null;
            }
        }
    }

    public void onStartedGoingToSleep() {
        if (this.dozeParameters.get().shouldControlUnlockedScreenOff()) {
            this.decidedToAnimateGoingToSleep = Boolean.TRUE;
            this.lightRevealAnimationPlaying = true;
            this.lightRevealAnimator.start();
            if (this.ambientDisplayConfiguration.enabledEx(this.context.getUserId()) == 1) {
                this.handler.postDelayed(new UnlockedScreenOffAnimationController$onStartedGoingToSleep$1(this), 600);
            } else {
                this.decidedToAnimateGoingToSleep = Boolean.FALSE;
            }
        }
    }

    public final boolean shouldPlayUnlockedScreenOffAnimation() {
        StatusBar statusBar2;
        if (!Intrinsics.areEqual((Object) this.decidedToAnimateGoingToSleep, (Object) Boolean.FALSE) && this.dozeParameters.get().canControlUnlockedScreenOff() && this.statusBarStateControllerImpl.getState() == 0 && (statusBar2 = this.statusBar) != null) {
            if (statusBar2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("statusBar");
                throw null;
            } else if (statusBar2.getNotificationPanelViewController().isFullyCollapsed()) {
                if (this.keyguardStateController.isKeyguardScreenRotationAllowed() || this.context.getResources().getConfiguration().orientation == 1) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public final boolean isScreenOffAnimationPlaying() {
        return this.lightRevealAnimationPlaying || this.aodUiAnimationPlaying;
    }

    public final boolean isScreenOffLightRevealAnimationPlaying() {
        return this.lightRevealAnimationPlaying;
    }
}
