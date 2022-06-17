package com.android.systemui.util.leak;

/* renamed from: com.android.systemui.util.leak.GarbageMonitor$BackgroundHeapCheckHandler$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2120xcff8442a implements Runnable {
    public final /* synthetic */ GarbageMonitor f$0;

    public /* synthetic */ C2120xcff8442a(GarbageMonitor garbageMonitor) {
        this.f$0 = garbageMonitor;
    }

    public final void run() {
        this.f$0.reinspectGarbageAfterGc();
    }
}
