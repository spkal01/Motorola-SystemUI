package com.motorola.gesturetouch;

import android.content.Context;
import android.view.MotionEvent;

public class GestureDetectionController {
    private DoubleTapDetector mDoubleTapDetector;
    private int mGestureType = 0;
    EdgeTouchPillController mPillController;
    private SingleTapDetector mSingleTapDetector;
    private boolean mStartDetect;
    private SwipeDownDetector mSwipeDownDetector;
    private SwipeUpDetector mSwipeUpDetector;
    private SystemGestureObservable mSystemGestureObservable;

    public GestureDetectionController(Context context, EdgeTouchPillController edgeTouchPillController) {
        this.mPillController = edgeTouchPillController;
        this.mSystemGestureObservable = new SystemGestureObservable(context);
        this.mSingleTapDetector = new SingleTapDetector(context, this.mSystemGestureObservable, edgeTouchPillController.isNeedShowWhatNew());
        this.mDoubleTapDetector = new DoubleTapDetector(context, this.mSystemGestureObservable);
        this.mSwipeUpDetector = new SwipeUpDetector(context);
        this.mSwipeDownDetector = new SwipeDownDetector(context);
    }

    public void startDetection(MotionEvent motionEvent) {
        this.mSystemGestureObservable.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == 0) {
            this.mGestureType = 0;
            this.mStartDetect = true;
        }
        if (this.mStartDetect) {
            if (this.mPillController.isNeedShowWhatNew() && this.mSingleTapDetector.detectGesture(motionEvent)) {
                this.mGestureType = 1;
            } else if (this.mDoubleTapDetector.detectGesture(motionEvent)) {
                this.mGestureType = 2;
            } else if (this.mPillController.isPillSwipeUpDownEnabled() && this.mSwipeUpDetector.detectGesture(motionEvent)) {
                this.mGestureType = 3;
            } else if (this.mPillController.isPillSwipeUpDownEnabled() && this.mSwipeDownDetector.detectGesture(motionEvent)) {
                this.mGestureType = 4;
            }
        }
        if (this.mGestureType != 0) {
            resetAllDetector();
            this.mPillController.handleGestureAction(this.mGestureType);
            this.mGestureType = 0;
            this.mStartDetect = false;
        }
    }

    public void registerSignalTapDetected(boolean z) {
        SingleTapDetector singleTapDetector = this.mSingleTapDetector;
        if (singleTapDetector != null) {
            singleTapDetector.registerSingleTapGestureLisntener(z);
        }
    }

    public void resetAllDetector() {
        this.mSingleTapDetector.reset();
        this.mDoubleTapDetector.reset();
        this.mSwipeUpDetector.reset();
        this.mSwipeDownDetector.reset();
    }
}
