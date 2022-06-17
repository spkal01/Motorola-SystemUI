package com.motorola.systemui.cli.navgesture.util;

import android.os.Handler;
import android.os.SystemClock;

public class TimeoutRecorder implements Runnable {
    private Handler mHandler = new Handler();
    private TimeoutListener mTimeoutListener;
    private boolean mTimeoutPending = false;
    private long mTimeoutTriggerTime;
    private boolean mWaitingForCallback;

    public interface TimeoutListener {
        void onTimeout(TimeoutRecorder timeoutRecorder);
    }

    public void setOnTimeoutListener(TimeoutListener timeoutListener) {
        this.mTimeoutListener = timeoutListener;
    }

    public void setTimeout(long j) {
        long uptimeMillis = SystemClock.uptimeMillis();
        this.mTimeoutPending = true;
        long j2 = this.mTimeoutTriggerTime;
        long j3 = j + uptimeMillis;
        this.mTimeoutTriggerTime = j3;
        if (this.mWaitingForCallback && j2 > j3) {
            this.mHandler.removeCallbacks(this);
            this.mWaitingForCallback = false;
        }
        if (!this.mWaitingForCallback) {
            this.mHandler.postDelayed(this, this.mTimeoutTriggerTime - uptimeMillis);
            this.mWaitingForCallback = true;
        }
    }

    public void cancelTimeout() {
        this.mTimeoutPending = false;
    }

    public void run() {
        this.mWaitingForCallback = false;
        if (this.mTimeoutPending) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = this.mTimeoutTriggerTime;
            if (j > uptimeMillis) {
                this.mHandler.postDelayed(this, Math.max(0, j - uptimeMillis));
                this.mWaitingForCallback = true;
                return;
            }
            this.mTimeoutPending = false;
            TimeoutListener timeoutListener = this.mTimeoutListener;
            if (timeoutListener != null) {
                timeoutListener.onTimeout(this);
            }
        }
    }
}
