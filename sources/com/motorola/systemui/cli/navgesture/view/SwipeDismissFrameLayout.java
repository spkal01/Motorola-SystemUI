package com.motorola.systemui.cli.navgesture.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.motorola.systemui.cli.navgesture.util.SwipeDismissHelper;
import com.motorola.systemui.cli.navgesture.view.ISwipeDismissView;

public class SwipeDismissFrameLayout extends FrameLayout implements ISwipeDismissView {
    private SwipeDismissHelper<SwipeDismissFrameLayout> mSwipeDismissHelper;

    public SwipeDismissFrameLayout(Context context) {
        super(context);
    }

    public SwipeDismissFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SwipeDismissFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setSwipeDirection(SwipeDismissHelper.Direction direction) {
        this.mSwipeDismissHelper = new SwipeDismissHelper<>(getContext(), this, direction);
    }

    public void setSwipeDirection(int i) {
        this.mSwipeDismissHelper.setSwipeDirection(i);
    }

    public void setMinDismissThreshold(float f) {
        this.mSwipeDismissHelper.setMinDismissThreshold(f);
    }

    public void setOnDismissedListener(ISwipeDismissView.OnDismissedListener onDismissedListener) {
        this.mSwipeDismissHelper.setOnDismissedListener(onDismissedListener);
    }

    public void setOnSwipeProgressChangedListener(ISwipeDismissView.OnSwipeProgressChangedListener onSwipeProgressChangedListener) {
        this.mSwipeDismissHelper.setOnSwipeProgressChangedListener(onSwipeProgressChangedListener);
    }

    public boolean superOnInterceptTouchEvent(MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeDismissHelper.onInterceptTouchEvent(motionEvent);
    }

    public boolean superOnTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeDismissHelper.onTouchEvent(motionEvent);
    }

    public boolean performClick() {
        return this.mSwipeDismissHelper.performClick();
    }

    public boolean superPerformClick() {
        return super.performClick();
    }

    public void setDismissible(boolean z) {
        this.mSwipeDismissHelper.setDismissible(z);
    }

    public View getView() {
        return this.mSwipeDismissHelper.getView();
    }
}
