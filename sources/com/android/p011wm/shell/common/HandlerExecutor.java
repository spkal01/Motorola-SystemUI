package com.android.p011wm.shell.common;

import android.os.Handler;

/* renamed from: com.android.wm.shell.common.HandlerExecutor */
public class HandlerExecutor implements ShellExecutor {
    private final Handler mHandler;

    public HandlerExecutor(Handler handler) {
        this.mHandler = handler;
    }

    public void execute(Runnable runnable) {
        if (this.mHandler.getLooper().isCurrentThread()) {
            runnable.run();
        } else if (!this.mHandler.post(runnable)) {
            throw new RuntimeException(this.mHandler + " is probably exiting");
        }
    }

    public void executeDelayed(Runnable runnable, long j) {
        if (!this.mHandler.postDelayed(runnable, j)) {
            throw new RuntimeException(this.mHandler + " is probably exiting");
        }
    }

    public void removeCallbacks(Runnable runnable) {
        this.mHandler.removeCallbacks(runnable);
    }

    public boolean hasCallback(Runnable runnable) {
        return this.mHandler.hasCallbacks(runnable);
    }
}
