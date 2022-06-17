package com.motorola.gesturetouch;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

class DoubleTapDetector extends EdgeTouchGestureDetector {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private int actionDownCount = 0;
    private GestureDetector.SimpleOnGestureListener gestureListener;
    /* access modifiers changed from: private */
    public boolean isDoubleTapDetected = false;
    /* access modifiers changed from: private */
    public boolean shouldDetect = false;

    public DoubleTapDetector(Context context, SystemGestureObservable systemGestureObservable) {
        C26181 r1 = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                if (!DoubleTapDetector.this.shouldDetect) {
                    return false;
                }
                if (DoubleTapDetector.DEBUG) {
                    Log.i("GestureTouch", "onDoubleTapEvent");
                }
                boolean unused = DoubleTapDetector.this.isDoubleTapDetected = true;
                boolean unused2 = DoubleTapDetector.this.shouldDetect = false;
                return true;
            }
        };
        this.gestureListener = r1;
        systemGestureObservable.registerSystemGestureListener(r1);
    }

    public boolean detectGesture(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.actionDownCount == 0) {
            this.shouldDetect = true;
            this.actionDownCount = 1;
        }
        if (this.isDoubleTapDetected) {
            return true;
        }
        return false;
    }

    public void reset() {
        this.isDoubleTapDetected = false;
        this.shouldDetect = false;
        this.actionDownCount = 0;
    }
}
