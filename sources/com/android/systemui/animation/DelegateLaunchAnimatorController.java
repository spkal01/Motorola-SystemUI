package com.android.systemui.animation;

import android.view.ViewGroup;
import com.android.systemui.animation.ActivityLaunchAnimator;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: DelegateLaunchAnimatorController.kt */
public class DelegateLaunchAnimatorController implements ActivityLaunchAnimator.Controller {
    @NotNull
    private final ActivityLaunchAnimator.Controller delegate;

    @NotNull
    public ActivityLaunchAnimator.State createAnimatorState() {
        return this.delegate.createAnimatorState();
    }

    @NotNull
    public ViewGroup getLaunchContainer() {
        return this.delegate.getLaunchContainer();
    }

    public void onIntentStarted(boolean z) {
        this.delegate.onIntentStarted(z);
    }

    public void onLaunchAnimationCancelled() {
        this.delegate.onLaunchAnimationCancelled();
    }

    public void onLaunchAnimationProgress(@NotNull ActivityLaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, "state");
        this.delegate.onLaunchAnimationProgress(state, f, f2);
    }

    public void setLaunchContainer(@NotNull ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.delegate.setLaunchContainer(viewGroup);
    }

    public DelegateLaunchAnimatorController(@NotNull ActivityLaunchAnimator.Controller controller) {
        Intrinsics.checkNotNullParameter(controller, "delegate");
        this.delegate = controller;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final ActivityLaunchAnimator.Controller getDelegate() {
        return this.delegate;
    }
}
