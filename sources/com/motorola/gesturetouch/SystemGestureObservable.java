package com.motorola.gesturetouch;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class SystemGestureObservable extends GestureDetector.SimpleOnGestureListener {
    private GestureDetector mGestureDetector;
    private ArrayList<GestureDetector.SimpleOnGestureListener> mGestureListeners = new ArrayList<>();

    public SystemGestureObservable(Context context) {
        this.mGestureDetector = new GestureDetector(context, this);
    }

    public void registerSystemGestureListener(GestureDetector.SimpleOnGestureListener simpleOnGestureListener) {
        this.mGestureListeners.add(simpleOnGestureListener);
    }

    public void unregisterSystemGestureListener(GestureDetector.SimpleOnGestureListener simpleOnGestureListener) {
        this.mGestureListeners.remove(simpleOnGestureListener);
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        Iterator<GestureDetector.SimpleOnGestureListener> it = this.mGestureListeners.iterator();
        boolean z = false;
        while (it.hasNext()) {
            z = it.next().onDoubleTapEvent(motionEvent);
        }
        return z;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        Iterator<GestureDetector.SimpleOnGestureListener> it = this.mGestureListeners.iterator();
        boolean z = false;
        while (it.hasNext()) {
            z = it.next().onSingleTapConfirmed(motionEvent);
        }
        return z;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        this.mGestureDetector.onTouchEvent(motionEvent);
    }
}
