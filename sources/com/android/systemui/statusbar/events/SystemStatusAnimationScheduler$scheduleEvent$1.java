package com.android.systemui.statusbar.events;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import com.android.systemui.statusbar.events.SystemStatusAnimationScheduler;
import com.android.systemui.util.concurrency.DelayableExecutor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SystemStatusAnimationScheduler.kt */
final class SystemStatusAnimationScheduler$scheduleEvent$1 implements Runnable {
    final /* synthetic */ SystemStatusAnimationScheduler this$0;

    SystemStatusAnimationScheduler$scheduleEvent$1(SystemStatusAnimationScheduler systemStatusAnimationScheduler) {
        this.this$0 = systemStatusAnimationScheduler;
    }

    public final void run() {
        this.this$0.cancelExecutionRunnable = null;
        this.this$0.animationState = 1;
        this.this$0.statusBarWindowController.setForceStatusBarVisible(true);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        ofFloat.setDuration(250);
        ofFloat.addListener(this.this$0.systemAnimatorAdapter);
        ofFloat.addUpdateListener(this.this$0.systemUpdateListener);
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat2.setDuration(250);
        SystemStatusAnimationScheduler systemStatusAnimationScheduler = this.this$0;
        StatusEvent access$getScheduledEvent$p = systemStatusAnimationScheduler.scheduledEvent;
        Intrinsics.checkNotNull(access$getScheduledEvent$p);
        ofFloat2.addListener(new SystemStatusAnimationScheduler.ChipAnimatorAdapter(systemStatusAnimationScheduler, 2, access$getScheduledEvent$p.getViewCreator()));
        ofFloat2.addUpdateListener(this.this$0.chipUpdateListener);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(new Animator[]{ofFloat, ofFloat2});
        animatorSet.start();
        DelayableExecutor access$getExecutor$p = this.this$0.executor;
        final SystemStatusAnimationScheduler systemStatusAnimationScheduler2 = this.this$0;
        access$getExecutor$p.executeDelayed(new Runnable() {
            public final void run() {
                Animator access$notifyTransitionToPersistentDot;
                systemStatusAnimationScheduler2.animationState = 3;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat.setDuration(250);
                ofFloat.addListener(systemStatusAnimationScheduler2.systemAnimatorAdapter);
                ofFloat.addUpdateListener(systemStatusAnimationScheduler2.systemUpdateListener);
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                ofFloat2.setDuration(250);
                int i = systemStatusAnimationScheduler2.getHasPersistentDot() ? 4 : 0;
                SystemStatusAnimationScheduler systemStatusAnimationScheduler = systemStatusAnimationScheduler2;
                StatusEvent access$getScheduledEvent$p = systemStatusAnimationScheduler.scheduledEvent;
                Intrinsics.checkNotNull(access$getScheduledEvent$p);
                ofFloat2.addListener(new SystemStatusAnimationScheduler.ChipAnimatorAdapter(systemStatusAnimationScheduler, i, access$getScheduledEvent$p.getViewCreator()));
                ofFloat2.addUpdateListener(systemStatusAnimationScheduler2.chipUpdateListener);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(ofFloat2).before(ofFloat);
                if (systemStatusAnimationScheduler2.getHasPersistentDot() && (access$notifyTransitionToPersistentDot = systemStatusAnimationScheduler2.notifyTransitionToPersistentDot()) != null) {
                    animatorSet.playTogether(new Animator[]{ofFloat, access$notifyTransitionToPersistentDot});
                }
                animatorSet.start();
                systemStatusAnimationScheduler2.statusBarWindowController.setForceStatusBarVisible(false);
                systemStatusAnimationScheduler2.scheduledEvent = null;
            }
        }, 1500);
    }
}
