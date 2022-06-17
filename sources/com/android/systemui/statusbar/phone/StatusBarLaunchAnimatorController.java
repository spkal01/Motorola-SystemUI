package com.android.systemui.statusbar.phone;

import android.view.ViewGroup;
import com.android.systemui.animation.ActivityLaunchAnimator;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: StatusBarLaunchAnimatorController.kt */
public final class StatusBarLaunchAnimatorController implements ActivityLaunchAnimator.Controller {
    @NotNull
    private final ActivityLaunchAnimator.Controller delegate;
    private final boolean isLaunchForActivity;
    @NotNull
    private final StatusBar statusBar;

    @NotNull
    public ActivityLaunchAnimator.State createAnimatorState() {
        return this.delegate.createAnimatorState();
    }

    @NotNull
    public ViewGroup getLaunchContainer() {
        return this.delegate.getLaunchContainer();
    }

    public void setLaunchContainer(@NotNull ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.delegate.setLaunchContainer(viewGroup);
    }

    public StatusBarLaunchAnimatorController(@NotNull ActivityLaunchAnimator.Controller controller, @NotNull StatusBar statusBar2, boolean z) {
        Intrinsics.checkNotNullParameter(controller, "delegate");
        Intrinsics.checkNotNullParameter(statusBar2, "statusBar");
        this.delegate = controller;
        this.statusBar = statusBar2;
        this.isLaunchForActivity = z;
    }

    public void onIntentStarted(boolean z) {
        this.delegate.onIntentStarted(z);
        if (!z) {
            this.statusBar.collapsePanelOnMainThread();
        }
    }

    public void onLaunchAnimationStart(boolean z) {
        this.delegate.onLaunchAnimationStart(z);
        this.statusBar.getNotificationPanelViewController().setIsLaunchAnimationRunning(true);
        if (!z) {
            this.statusBar.collapsePanelWithDuration(500);
        }
    }

    public void onLaunchAnimationEnd(boolean z) {
        this.delegate.onLaunchAnimationEnd(z);
        this.statusBar.getNotificationPanelViewController().setIsLaunchAnimationRunning(false);
        this.statusBar.onLaunchAnimationEnd(z);
    }

    public void onLaunchAnimationProgress(@NotNull ActivityLaunchAnimator.State state, float f, float f2) {
        Intrinsics.checkNotNullParameter(state, "state");
        this.delegate.onLaunchAnimationProgress(state, f, f2);
        this.statusBar.getNotificationPanelViewController().applyLaunchAnimationProgress(f2);
    }

    public void onLaunchAnimationCancelled() {
        this.delegate.onLaunchAnimationCancelled();
        this.statusBar.onLaunchAnimationCancelled(this.isLaunchForActivity);
    }
}
