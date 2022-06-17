package com.android.systemui.statusbar.events;

import android.content.Context;
import com.android.systemui.privacy.PrivacyItemController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.time.SystemClock;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: SystemEventCoordinator.kt */
public final class SystemEventCoordinator {
    @NotNull
    private final BatteryController batteryController;
    @NotNull
    private final SystemEventCoordinator$batteryStateListener$1 batteryStateListener = new SystemEventCoordinator$batteryStateListener$1(this);
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    @NotNull
    private final PrivacyItemController privacyController;
    @NotNull
    private final SystemEventCoordinator$privacyStateListener$1 privacyStateListener = new SystemEventCoordinator$privacyStateListener$1(this);
    private SystemStatusAnimationScheduler scheduler;
    /* access modifiers changed from: private */
    @NotNull
    public final SystemClock systemClock;

    public SystemEventCoordinator(@NotNull SystemClock systemClock2, @NotNull BatteryController batteryController2, @NotNull PrivacyItemController privacyItemController, @NotNull Context context2) {
        Intrinsics.checkNotNullParameter(systemClock2, "systemClock");
        Intrinsics.checkNotNullParameter(batteryController2, "batteryController");
        Intrinsics.checkNotNullParameter(privacyItemController, "privacyController");
        Intrinsics.checkNotNullParameter(context2, "context");
        this.systemClock = systemClock2;
        this.batteryController = batteryController2;
        this.privacyController = privacyItemController;
        this.context = context2;
    }

    public final void startObserving() {
        this.privacyController.addCallback((PrivacyItemController.Callback) this.privacyStateListener);
    }

    public final void stopObserving() {
        this.privacyController.removeCallback((PrivacyItemController.Callback) this.privacyStateListener);
    }

    public final void attachScheduler(@NotNull SystemStatusAnimationScheduler systemStatusAnimationScheduler) {
        Intrinsics.checkNotNullParameter(systemStatusAnimationScheduler, "s");
        this.scheduler = systemStatusAnimationScheduler;
    }

    public final void notifyPluggedIn() {
        SystemStatusAnimationScheduler systemStatusAnimationScheduler = this.scheduler;
        if (systemStatusAnimationScheduler != null) {
            systemStatusAnimationScheduler.onStatusEvent(new BatteryEvent());
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("scheduler");
            throw null;
        }
    }

    public final void notifyPrivacyItemsEmpty() {
        SystemStatusAnimationScheduler systemStatusAnimationScheduler = this.scheduler;
        if (systemStatusAnimationScheduler != null) {
            systemStatusAnimationScheduler.setShouldShowPersistentPrivacyIndicator(false);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("scheduler");
            throw null;
        }
    }

    public final void notifyPrivacyItemsChanged(boolean z) {
        PrivacyEvent privacyEvent = new PrivacyEvent(z);
        privacyEvent.setPrivacyItems(this.privacyStateListener.getCurrentPrivacyItems());
        privacyEvent.setContentDescription((String) new SystemEventCoordinator$notifyPrivacyItemsChanged$1(this, privacyEvent).invoke());
        SystemStatusAnimationScheduler systemStatusAnimationScheduler = this.scheduler;
        if (systemStatusAnimationScheduler != null) {
            systemStatusAnimationScheduler.onStatusEvent(privacyEvent);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("scheduler");
            throw null;
        }
    }
}
