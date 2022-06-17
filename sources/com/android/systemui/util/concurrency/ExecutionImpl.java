package com.android.systemui.util.concurrency;

import android.os.Looper;

/* compiled from: Execution.kt */
public final class ExecutionImpl implements Execution {
    private final Looper mainLooper = Looper.getMainLooper();

    public void assertIsMainThread() {
        if (!this.mainLooper.isCurrentThread()) {
            throw new IllegalStateException("should be called from the main thread. Main thread name=" + this.mainLooper.getThread().getName() + " Thread.currentThread()=" + Thread.currentThread().getName());
        }
    }
}
