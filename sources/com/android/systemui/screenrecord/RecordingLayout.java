package com.android.systemui.screenrecord;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.android.systemui.R$dimen;

public class RecordingLayout extends LinearLayout {
    private View.OnTouchListener mInterceptTouchListen;
    private int mMoveDistance;
    public int mTouchStartX;
    public int mTouchStartY;

    public RecordingLayout(Context context) {
        super(context);
    }

    public RecordingLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMoveDistance = getResources().getDimensionPixelSize(R$dimen.screenrecord_window_move_distance);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            View.OnTouchListener onTouchListener = this.mInterceptTouchListen;
            if (onTouchListener != null && onTouchListener.onTouch(this, motionEvent)) {
                return true;
            }
            this.mTouchStartX = (int) motionEvent.getRawX();
            this.mTouchStartY = (int) motionEvent.getRawY();
        }
        if (motionEvent.getActionMasked() == 2) {
            int abs = Math.abs(((int) motionEvent.getRawX()) - this.mTouchStartX);
            int abs2 = Math.abs(((int) motionEvent.getRawY()) - this.mTouchStartY);
            int i = this.mMoveDistance;
            if (abs > i || abs2 > i) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void setInterceptTouchListen(View.OnTouchListener onTouchListener) {
        this.mInterceptTouchListen = onTouchListener;
    }
}
