package com.motorola.gesturetouch;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

class SingleTapDetector extends EdgeTouchGestureDetector {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private GestureDetector.SimpleOnGestureListener gestureListener;
    /* access modifiers changed from: private */
    public boolean isSingleTapDetected = false;
    private SystemGestureObservable mObservable;

    public SingleTapDetector(Context context, SystemGestureObservable systemGestureObservable, boolean z) {
        C26391 r1 = new GestureDetector.SimpleOnGestureListener() {
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (SingleTapDetector.DEBUG) {
                    Log.i("GestureTouch", "onSingleTapConfirmed");
                }
                boolean unused = SingleTapDetector.this.isSingleTapDetected = true;
                return true;
            }
        };
        this.gestureListener = r1;
        this.mObservable = systemGestureObservable;
        if (z) {
            systemGestureObservable.registerSystemGestureListener(r1);
        }
    }

    public void registerSingleTapGestureLisntener(boolean z) {
        if (z) {
            this.mObservable.registerSystemGestureListener(this.gestureListener);
        } else {
            this.mObservable.unregisterSystemGestureListener(this.gestureListener);
        }
    }

    public boolean detectGesture(MotionEvent motionEvent) {
        return this.isSingleTapDetected;
    }

    public void reset() {
        this.isSingleTapDetected = false;
    }
}
