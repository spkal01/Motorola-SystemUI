package com.motorola.systemui.cli.navgesture.view;

import android.view.MotionEvent;
import android.view.View;

public interface ISwipeDismissView {

    public interface OnDismissedListener {
        void onDismissed(ISwipeDismissView iSwipeDismissView);
    }

    public interface OnSwipeProgressChangedListener {
        void onSwipeCancelled(ISwipeDismissView iSwipeDismissView);

        void onSwipeProgressChanged(ISwipeDismissView iSwipeDismissView, float f, float f2);
    }

    View getView();

    boolean superOnInterceptTouchEvent(MotionEvent motionEvent);

    boolean superOnTouchEvent(MotionEvent motionEvent);

    boolean superPerformClick();
}
