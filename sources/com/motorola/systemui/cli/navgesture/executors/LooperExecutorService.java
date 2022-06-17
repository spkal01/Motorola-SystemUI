package com.motorola.systemui.cli.navgesture.executors;

import android.os.Handler;
import android.os.Looper;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

class LooperExecutorService extends AbstractExecutorService implements LooperExecutor {
    private final Handler mHandler;

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public LooperExecutorService(Looper looper) {
        this.mHandler = new Handler(looper);
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void execute(Runnable runnable) {
        if (this.mHandler.getLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            this.mHandler.post(runnable);
        }
    }

    public void executeNext(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean awaitTermination(long j, TimeUnit timeUnit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}
