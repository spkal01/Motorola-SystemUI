package com.android.systemui.controls.management;

import android.content.Intent;
import android.view.ViewGroup;
import android.view.Window;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.systemui.R$dimen;

/* compiled from: ControlsAnimations.kt */
public final class ControlsAnimations$observerForAnimations$1 implements LifecycleObserver {
    final /* synthetic */ Intent $intent;
    final /* synthetic */ ViewGroup $view;
    final /* synthetic */ Window $window;
    private boolean showAnimation;

    ControlsAnimations$observerForAnimations$1(Intent intent, ViewGroup viewGroup, Window window) {
        this.$intent = intent;
        this.$view = viewGroup;
        this.$window = window;
        boolean z = false;
        this.showAnimation = intent.getBooleanExtra("extra_animate", false);
        viewGroup.setTransitionGroup(true);
        viewGroup.setTransitionAlpha(0.0f);
        if (ControlsAnimations.translationY == -1.0f ? true : z) {
            ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
            ControlsAnimations.translationY = (float) viewGroup.getContext().getResources().getDimensionPixelSize(R$dimen.global_actions_controls_y_translation);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public final void setup() {
        Window window = this.$window;
        ViewGroup viewGroup = this.$view;
        window.setAllowEnterTransitionOverlap(true);
        ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
        window.setEnterTransition(controlsAnimations.enterWindowTransition(viewGroup.getId()));
        window.setExitTransition(controlsAnimations.exitWindowTransition(viewGroup.getId()));
        window.setReenterTransition(controlsAnimations.enterWindowTransition(viewGroup.getId()));
        window.setReturnTransition(controlsAnimations.exitWindowTransition(viewGroup.getId()));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public final void enterAnimation() {
        if (this.showAnimation) {
            ControlsAnimations.INSTANCE.enterAnimation(this.$view).start();
            this.showAnimation = false;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public final void resetAnimation() {
        this.$view.setTranslationY(0.0f);
    }
}
