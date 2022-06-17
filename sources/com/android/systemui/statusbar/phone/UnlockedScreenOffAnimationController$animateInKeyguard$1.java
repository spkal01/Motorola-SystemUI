package com.android.systemui.statusbar.phone;

import com.android.systemui.keyguard.KeyguardViewMediator;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UnlockedScreenOffAnimationController.kt */
final class UnlockedScreenOffAnimationController$animateInKeyguard$1 implements Runnable {
    final /* synthetic */ Runnable $after;
    final /* synthetic */ UnlockedScreenOffAnimationController this$0;

    UnlockedScreenOffAnimationController$animateInKeyguard$1(UnlockedScreenOffAnimationController unlockedScreenOffAnimationController, Runnable runnable) {
        this.this$0 = unlockedScreenOffAnimationController;
        this.$after = runnable;
    }

    public final void run() {
        this.this$0.aodUiAnimationPlaying = false;
        ((KeyguardViewMediator) this.this$0.keyguardViewMediatorLazy.get()).maybeHandlePendingLock();
        StatusBar access$getStatusBar$p = this.this$0.statusBar;
        if (access$getStatusBar$p != null) {
            access$getStatusBar$p.updateIsKeyguard();
            this.$after.run();
            this.this$0.decidedToAnimateGoingToSleep = null;
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("statusBar");
        throw null;
    }
}
