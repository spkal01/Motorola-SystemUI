package com.motorola.gesturetouch;

import android.content.Context;
import android.view.MotionEvent;

class SwipeUpDetector extends EdgeTouchGestureDetector {
    private float initialX = 0.0f;
    private float initialY = 0.0f;
    private boolean mIsSwipeDetected;

    public SwipeUpDetector(Context context) {
    }

    public boolean detectGesture(MotionEvent motionEvent) {
        return isSwipUpDetected(motionEvent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0020, code lost:
        if (r2 != 3) goto L_0x0047;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isSwipUpDetected(android.view.MotionEvent r6) {
        /*
            r5 = this;
            float r0 = r6.getX()
            float r1 = r5.initialX
            float r0 = r0 - r1
            float r0 = java.lang.Math.abs(r0)
            float r1 = r5.initialY
            float r2 = r6.getY()
            float r1 = r1 - r2
            int r2 = r6.getAction()
            r3 = 0
            if (r2 == 0) goto L_0x0039
            r6 = 1
            if (r2 == r6) goto L_0x0036
            r4 = 2
            if (r2 == r4) goto L_0x0023
            r6 = 3
            if (r2 == r6) goto L_0x0036
            goto L_0x0047
        L_0x0023:
            r2 = 1112014848(0x42480000, float:50.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0047
            r0 = 1123024896(0x42f00000, float:120.0)
            int r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0047
            boolean r0 = r5.mIsSwipeDetected
            if (r0 != 0) goto L_0x0047
            r5.mIsSwipeDetected = r6
            return r6
        L_0x0036:
            r5.mIsSwipeDetected = r3
            goto L_0x0047
        L_0x0039:
            float r0 = r6.getX()
            r5.initialX = r0
            float r6 = r6.getY()
            r5.initialY = r6
            r5.mIsSwipeDetected = r3
        L_0x0047:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.gesturetouch.SwipeUpDetector.isSwipUpDetected(android.view.MotionEvent):boolean");
    }

    public void reset() {
        this.initialX = 0.0f;
        this.initialY = 0.0f;
        this.mIsSwipeDetected = false;
    }
}
