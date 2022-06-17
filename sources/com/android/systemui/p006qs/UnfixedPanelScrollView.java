package com.android.systemui.p006qs;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.ScrollView;

/* renamed from: com.android.systemui.qs.UnfixedPanelScrollView */
public class UnfixedPanelScrollView extends ScrollView {
    static final boolean DEBUG = (!Build.IS_USER);
    private float mDownY;
    private boolean mScrollEnabled = false;
    private final int mTouchSlop;

    public UnfixedPanelScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        motionEvent.getActionMasked();
        return super.onTouchEvent(motionEvent);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            if (canScrollVertically(1)) {
                requestInterceptTouchEvent(true);
            }
            this.mDownY = motionEvent.getY();
        } else if (actionMasked == 1) {
            requestInterceptTouchEvent(false);
        } else if (actionMasked == 2 && isNeedRequestInterceptTouchEvent(((float) ((int) motionEvent.getY())) - this.mDownY)) {
            requestInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    private void requestInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    private boolean isNeedRequestInterceptTouchEvent(float f) {
        if (!this.mScrollEnabled) {
            return false;
        }
        int scrollY = getScrollY();
        int measuredHeight = getChildAt(0).getMeasuredHeight() - getHeight();
        if (scrollY > 0 && scrollY < measuredHeight) {
            return true;
        }
        if (scrollY == 0 && f < 0.0f) {
            return true;
        }
        if (scrollY != measuredHeight || f <= 0.0f) {
            return false;
        }
        return true;
    }

    public boolean canScrollVertically(int i) {
        return this.mScrollEnabled && super.canScrollVertically(i);
    }

    public void setScrollingEnabled(boolean z) {
        if (DEBUG) {
            Log.i("UnfixedPanelScrollView", "qsPrcPanel setScrollingEnabled = " + z);
        }
        this.mScrollEnabled = z;
    }
}
