package com.android.systemui.controls.controller;

import android.util.Log;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsProviderLifecycleManager.kt */
final class ControlsProviderLifecycleManager$bindService$1 implements Runnable {
    final /* synthetic */ boolean $bind;
    final /* synthetic */ ControlsProviderLifecycleManager this$0;

    ControlsProviderLifecycleManager$bindService$1(ControlsProviderLifecycleManager controlsProviderLifecycleManager, boolean z) {
        this.this$0 = controlsProviderLifecycleManager;
        this.$bind = z;
    }

    public final void run() {
        this.this$0.requiresBound = this.$bind;
        if (!this.$bind) {
            Log.d(this.this$0.TAG, Intrinsics.stringPlus("Unbinding service ", this.this$0.intent));
            this.this$0.bindTryCount = 0;
            if (this.this$0.wrapper != null) {
                ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.this$0;
                controlsProviderLifecycleManager.context.unbindService(controlsProviderLifecycleManager.serviceConnection);
            }
            this.this$0.wrapper = null;
        } else if (this.this$0.bindTryCount != 5) {
            Log.d(this.this$0.TAG, Intrinsics.stringPlus("Binding service ", this.this$0.intent));
            ControlsProviderLifecycleManager controlsProviderLifecycleManager2 = this.this$0;
            controlsProviderLifecycleManager2.bindTryCount = controlsProviderLifecycleManager2.bindTryCount + 1;
            try {
                this.this$0.context.bindServiceAsUser(this.this$0.intent, this.this$0.serviceConnection, ControlsProviderLifecycleManager.BIND_FLAGS, this.this$0.getUser());
            } catch (SecurityException e) {
                Log.e(this.this$0.TAG, "Failed to bind to service", e);
            }
        }
    }
}
