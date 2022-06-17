package com.motorola.systemui.cli.navgesture.util;

import android.os.Handler;

public abstract class HandlerRunnable implements Runnable {
    private boolean mCanceled = false;
    private final Runnable mEndRunnable;
    private boolean mEnded = false;
    private final Handler mHandler;

    public HandlerRunnable(Handler handler, Runnable runnable) {
        this.mHandler = handler;
        this.mEndRunnable = runnable;
    }

    public void cancel() {
        this.mHandler.removeCallbacks(this);
        this.mCanceled = true;
        onEnd();
    }

    /* access modifiers changed from: protected */
    public boolean isCanceled() {
        return this.mCanceled;
    }

    public void onEnd() {
        if (!this.mEnded) {
            this.mEnded = true;
            Runnable runnable = this.mEndRunnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    }
}
