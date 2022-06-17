package com.android.systemui.util.wakelock;

public final /* synthetic */ class DelayedWakeLock$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DelayedWakeLock f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ DelayedWakeLock$$ExternalSyntheticLambda0(DelayedWakeLock delayedWakeLock, String str) {
        this.f$0 = delayedWakeLock;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.lambda$release$0(this.f$1);
    }
}
