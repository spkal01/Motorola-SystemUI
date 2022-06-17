package com.android.systemui.p006qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.ScrollView;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.FalsingManager;

/* renamed from: com.android.systemui.qs.NonInterceptingScrollView */
public class NonInterceptingScrollView extends ScrollView {
    private float mDownY;
    private FalsingManager mFalsingManager;
    private boolean mScrollEnabled = true;
    private final int mTouchSlop;

    public NonInterceptingScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked == 1 && this.mFalsingManager != null && MotoFeature.getInstance(getContext()).isCustomPanelView()) {
                this.mFalsingManager.isFalseTouch(15);
            }
        } else if (canScrollVertically(1)) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        } else if (!canScrollVertically(-1)) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        ViewParent parent;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            if (canScrollVertically(1) && (parent = getParent()) != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            this.mDownY = motionEvent.getY();
        } else if (actionMasked == 2 && ((float) ((int) motionEvent.getY())) - this.mDownY < ((float) (-this.mTouchSlop)) && !canScrollVertically(1)) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean canScrollVertically(int i) {
        return this.mScrollEnabled && super.canScrollVertically(i);
    }

    public boolean canScrollHorizontally(int i) {
        return this.mScrollEnabled && super.canScrollHorizontally(i);
    }

    public int getScrollRange() {
        if (getChildCount() > 0) {
            return Math.max(0, getChildAt(0).getHeight() - ((getHeight() - this.mPaddingBottom) - this.mPaddingTop));
        }
        return 0;
    }

    public void setScrollingEnabled(boolean z) {
        this.mScrollEnabled = z;
    }

    public void setFalsingManager(FalsingManager falsingManager) {
        this.mFalsingManager = falsingManager;
    }
}
