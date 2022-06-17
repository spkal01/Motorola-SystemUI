package com.android.systemui.statusbar.events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Process;
import android.provider.DeviceConfig;
import android.view.View;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.util.Assert;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.SystemClock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SystemStatusAnimationScheduler.kt */
public final class SystemStatusAnimationScheduler implements CallbackController<SystemStatusAnimationCallback>, Dumpable {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public int animationState;
    /* access modifiers changed from: private */
    @Nullable
    public Runnable cancelExecutionRunnable;
    /* access modifiers changed from: private */
    @NotNull
    public final SystemEventChipAnimationController chipAnimationController;
    /* access modifiers changed from: private */
    @NotNull
    public final ValueAnimator.AnimatorUpdateListener chipUpdateListener;
    @NotNull
    private final SystemEventCoordinator coordinator;
    @NotNull
    private final DumpManager dumpManager;
    /* access modifiers changed from: private */
    @NotNull
    public final DelayableExecutor executor;
    private boolean hasPersistentDot;
    @NotNull
    private final Set<SystemStatusAnimationCallback> listeners = new LinkedHashSet();
    /* access modifiers changed from: private */
    @Nullable
    public StatusEvent scheduledEvent;
    /* access modifiers changed from: private */
    @NotNull
    public final StatusBarWindowController statusBarWindowController;
    /* access modifiers changed from: private */
    @NotNull
    public final SystemStatusAnimationScheduler$systemAnimatorAdapter$1 systemAnimatorAdapter;
    @NotNull
    private final SystemClock systemClock;
    /* access modifiers changed from: private */
    @NotNull
    public final ValueAnimator.AnimatorUpdateListener systemUpdateListener;

    public SystemStatusAnimationScheduler(@NotNull SystemEventCoordinator systemEventCoordinator, @NotNull SystemEventChipAnimationController systemEventChipAnimationController, @NotNull StatusBarWindowController statusBarWindowController2, @NotNull DumpManager dumpManager2, @NotNull SystemClock systemClock2, @NotNull DelayableExecutor delayableExecutor) {
        Intrinsics.checkNotNullParameter(systemEventCoordinator, "coordinator");
        Intrinsics.checkNotNullParameter(systemEventChipAnimationController, "chipAnimationController");
        Intrinsics.checkNotNullParameter(statusBarWindowController2, "statusBarWindowController");
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        Intrinsics.checkNotNullParameter(systemClock2, "systemClock");
        Intrinsics.checkNotNullParameter(delayableExecutor, "executor");
        this.coordinator = systemEventCoordinator;
        this.chipAnimationController = systemEventChipAnimationController;
        this.statusBarWindowController = statusBarWindowController2;
        this.dumpManager = dumpManager2;
        this.systemClock = systemClock2;
        this.executor = delayableExecutor;
        systemEventCoordinator.attachScheduler(this);
        dumpManager2.registerDumpable("SystemStatusAnimationScheduler", this);
        this.systemUpdateListener = new SystemStatusAnimationScheduler$systemUpdateListener$1(this);
        this.systemAnimatorAdapter = new SystemStatusAnimationScheduler$systemAnimatorAdapter$1(this);
        this.chipUpdateListener = new SystemStatusAnimationScheduler$chipUpdateListener$1(this);
    }

    /* compiled from: SystemStatusAnimationScheduler.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    private final boolean isImmersiveIndicatorEnabled() {
        return DeviceConfig.getBoolean("privacy", "enable_immersive_indicator", true);
    }

    public final int getAnimationState() {
        return this.animationState;
    }

    public final boolean getHasPersistentDot() {
        return this.hasPersistentDot;
    }

    public final void onStatusEvent(@NotNull StatusEvent statusEvent) {
        int i;
        Intrinsics.checkNotNullParameter(statusEvent, "event");
        if (!isTooEarly() && isImmersiveIndicatorEnabled()) {
            Assert.isMainThread();
            int priority = statusEvent.getPriority();
            StatusEvent statusEvent2 = this.scheduledEvent;
            if (priority <= (statusEvent2 == null ? -1 : statusEvent2.getPriority()) || (i = this.animationState) == 3 || i == 4 || !statusEvent.getForceVisible()) {
                StatusEvent statusEvent3 = this.scheduledEvent;
                if (Intrinsics.areEqual((Object) statusEvent3 == null ? null : Boolean.valueOf(statusEvent3.shouldUpdateFromEvent(statusEvent)), (Object) Boolean.TRUE)) {
                    StatusEvent statusEvent4 = this.scheduledEvent;
                    if (statusEvent4 != null) {
                        statusEvent4.updateFromEvent(statusEvent);
                    }
                    if (statusEvent.getForceVisible()) {
                        this.hasPersistentDot = true;
                        notifyTransitionToPersistentDot();
                        return;
                    }
                    return;
                }
                return;
            }
            scheduleEvent(statusEvent);
        }
    }

    private final void clearDotIfVisible() {
        notifyHidePersistentDot();
    }

    public final void setShouldShowPersistentPrivacyIndicator(boolean z) {
        if (this.hasPersistentDot != z && isImmersiveIndicatorEnabled()) {
            this.hasPersistentDot = z;
            if (!z) {
                clearDotIfVisible();
            }
        }
    }

    private final boolean isTooEarly() {
        return this.systemClock.uptimeMillis() - Process.getStartUptimeMillis() < 5000;
    }

    private final void scheduleEvent(StatusEvent statusEvent) {
        this.scheduledEvent = statusEvent;
        if (statusEvent.getForceVisible()) {
            this.hasPersistentDot = true;
        }
        if (statusEvent.getShowAnimation() || !statusEvent.getForceVisible()) {
            this.cancelExecutionRunnable = this.executor.executeDelayed(new SystemStatusAnimationScheduler$scheduleEvent$1(this), 0);
            return;
        }
        notifyTransitionToPersistentDot();
        this.scheduledEvent = null;
    }

    /* access modifiers changed from: private */
    public final Animator notifyTransitionToPersistentDot() {
        Set<SystemStatusAnimationCallback> set = this.listeners;
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = set.iterator();
        while (true) {
            String str = null;
            if (!it.hasNext()) {
                break;
            }
            SystemStatusAnimationCallback systemStatusAnimationCallback = (SystemStatusAnimationCallback) it.next();
            StatusEvent statusEvent = this.scheduledEvent;
            if (statusEvent != null) {
                str = statusEvent.getContentDescription();
            }
            Animator onSystemStatusAnimationTransitionToPersistentDot = systemStatusAnimationCallback.onSystemStatusAnimationTransitionToPersistentDot(str);
            if (onSystemStatusAnimationTransitionToPersistentDot != null) {
                arrayList.add(onSystemStatusAnimationTransitionToPersistentDot);
            }
        }
        if (!(!arrayList.isEmpty())) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        return animatorSet;
    }

    private final Animator notifyHidePersistentDot() {
        Set<SystemStatusAnimationCallback> set = this.listeners;
        ArrayList arrayList = new ArrayList();
        for (SystemStatusAnimationCallback onHidePersistentDot : set) {
            Animator onHidePersistentDot2 = onHidePersistentDot.onHidePersistentDot();
            if (onHidePersistentDot2 != null) {
                arrayList.add(onHidePersistentDot2);
            }
        }
        if (this.animationState == 4) {
            this.animationState = 0;
        }
        if (!(!arrayList.isEmpty())) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public final void notifySystemStart() {
        for (SystemStatusAnimationCallback onSystemChromeAnimationStart : this.listeners) {
            onSystemChromeAnimationStart.onSystemChromeAnimationStart();
        }
    }

    /* access modifiers changed from: private */
    public final void notifySystemFinish() {
        for (SystemStatusAnimationCallback onSystemChromeAnimationEnd : this.listeners) {
            onSystemChromeAnimationEnd.onSystemChromeAnimationEnd();
        }
    }

    /* access modifiers changed from: private */
    public final void notifySystemAnimationUpdate(ValueAnimator valueAnimator) {
        for (SystemStatusAnimationCallback onSystemChromeAnimationUpdate : this.listeners) {
            onSystemChromeAnimationUpdate.onSystemChromeAnimationUpdate(valueAnimator);
        }
    }

    public void addCallback(@NotNull SystemStatusAnimationCallback systemStatusAnimationCallback) {
        Intrinsics.checkNotNullParameter(systemStatusAnimationCallback, "listener");
        Assert.isMainThread();
        if (this.listeners.isEmpty()) {
            this.coordinator.startObserving();
        }
        this.listeners.add(systemStatusAnimationCallback);
    }

    public void removeCallback(@NotNull SystemStatusAnimationCallback systemStatusAnimationCallback) {
        Intrinsics.checkNotNullParameter(systemStatusAnimationCallback, "listener");
        Assert.isMainThread();
        this.listeners.remove(systemStatusAnimationCallback);
        if (this.listeners.isEmpty()) {
            this.coordinator.stopObserving();
        }
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println(Intrinsics.stringPlus("Scheduled event: ", this.scheduledEvent));
        printWriter.println(Intrinsics.stringPlus("Has persistent privacy dot: ", Boolean.valueOf(this.hasPersistentDot)));
        printWriter.println(Intrinsics.stringPlus("Animation state: ", Integer.valueOf(this.animationState)));
        printWriter.println("Listeners:");
        if (this.listeners.isEmpty()) {
            printWriter.println("(none)");
            return;
        }
        for (SystemStatusAnimationCallback stringPlus : this.listeners) {
            printWriter.println(Intrinsics.stringPlus("  ", stringPlus));
        }
    }

    /* compiled from: SystemStatusAnimationScheduler.kt */
    public final class ChipAnimatorAdapter extends AnimatorListenerAdapter {
        private final int endState;
        final /* synthetic */ SystemStatusAnimationScheduler this$0;
        @NotNull
        private final Function1<Context, View> viewCreator;

        public ChipAnimatorAdapter(SystemStatusAnimationScheduler systemStatusAnimationScheduler, @NotNull int i, Function1<? super Context, ? extends View> function1) {
            Intrinsics.checkNotNullParameter(systemStatusAnimationScheduler, "this$0");
            Intrinsics.checkNotNullParameter(function1, "viewCreator");
            this.this$0 = systemStatusAnimationScheduler;
            this.endState = i;
            this.viewCreator = function1;
        }

        public void onAnimationEnd(@Nullable Animator animator) {
            int i;
            this.this$0.chipAnimationController.onChipAnimationEnd(this.this$0.getAnimationState());
            SystemStatusAnimationScheduler systemStatusAnimationScheduler = this.this$0;
            if (this.endState != 4 || systemStatusAnimationScheduler.getHasPersistentDot()) {
                i = this.endState;
            } else {
                i = 0;
            }
            systemStatusAnimationScheduler.animationState = i;
        }

        public void onAnimationStart(@Nullable Animator animator) {
            this.this$0.chipAnimationController.onChipAnimationStart(this.viewCreator, this.this$0.getAnimationState());
        }
    }
}
