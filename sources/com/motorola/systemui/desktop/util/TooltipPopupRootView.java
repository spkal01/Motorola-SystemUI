package com.motorola.systemui.desktop.util;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

class TooltipPopupRootView extends LinearLayout {
    private static boolean DEBUG = (!Build.IS_USER);
    private boolean mIsInTouch = false;
    private boolean mIsOnHover = false;
    private TooltipPopup mTooltipPopup;

    public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
        return true;
    }

    public TooltipPopupRootView(Context context) {
        super(context);
    }

    public TooltipPopupRootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TooltipPopupRootView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TooltipPopupRootView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (DEBUG) {
            Log.d("TooltipPopupRootView", "onInterceptTouchEvent = " + MotionEvent.actionToString(motionEvent.getAction()));
        }
        int action = motionEvent.getAction();
        if (action == 1 || action == 3) {
            this.mIsInTouch = false;
            checkIfDismissPopup();
        } else {
            if (this.mTooltipPopup.isFocusable()) {
                this.mTooltipPopup.rePostTimeOut();
            }
            this.mIsInTouch = true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        if (motionEvent.isFromSource(8194)) {
            int action = motionEvent.getAction();
            if (DEBUG) {
                Log.d("TooltipPopupRootView", "onHoverEvent = " + MotionEvent.actionToString(action));
            }
            if (action != 7) {
                if (action == 9) {
                    this.mIsOnHover = true;
                    if (this.mTooltipPopup.isFocusable()) {
                        this.mTooltipPopup.rePostTimeOut();
                    }
                } else if (action == 10) {
                    this.mIsOnHover = false;
                    checkIfDismissPopup();
                }
            } else if (this.mTooltipPopup.isFocusable()) {
                this.mTooltipPopup.rePostTimeOut();
            }
        }
        return true;
    }

    public boolean isOnTouchOrOnHover() {
        return this.mIsOnHover || this.mIsInTouch;
    }

    private void checkIfDismissPopup() {
        if (!isOnTouchOrOnHover()) {
            this.mTooltipPopup.hide(true, true);
        }
    }

    public void setTooltipPopup(TooltipPopup tooltipPopup) {
        this.mTooltipPopup = tooltipPopup;
    }
}
