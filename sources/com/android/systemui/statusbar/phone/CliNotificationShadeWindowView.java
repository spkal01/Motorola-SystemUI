package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import com.android.systemui.R$id;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController;

public class CliNotificationShadeWindowView extends FrameLayout implements CliStatusBarWindowController.OnCliViewRequestListener, CliStatusBarWindowController.ICliChildView {
    private static final boolean DEBUG = (!Build.IS_USER);
    private int mLeftInset = 0;
    private int mRightInset = 0;
    private CliStatusBar mService;
    private View mTopView;

    public CliNotificationShadeWindowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setService(CliStatusBar cliStatusBar) {
        this.mService = cliStatusBar;
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        Insets insetsIgnoringVisibility = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
        boolean z = true;
        if (getFitsSystemWindows()) {
            if (insetsIgnoringVisibility.top == getPaddingTop() && insetsIgnoringVisibility.bottom == getPaddingBottom()) {
                z = false;
            }
            if (z) {
                setPadding(0, 0, 0, 0);
            }
        } else {
            if (getPaddingLeft() == 0 && getPaddingRight() == 0 && getPaddingTop() == 0 && getPaddingBottom() == 0) {
                z = false;
            }
            if (z) {
                setPadding(0, 0, 0, 0);
            }
        }
        this.mLeftInset = 0;
        this.mRightInset = 0;
        DisplayCutout displayCutout = getRootWindowInsets().getDisplayCutout();
        if (displayCutout != null) {
            this.mLeftInset = displayCutout.getSafeInsetLeft();
            this.mRightInset = displayCutout.getSafeInsetRight();
        }
        this.mLeftInset = Math.max(insetsIgnoringVisibility.left, this.mLeftInset);
        this.mRightInset = Math.max(insetsIgnoringVisibility.right, this.mRightInset);
        applyMargins();
        return windowInsets;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0013, code lost:
        r3 = (android.widget.FrameLayout.LayoutParams) r2.getLayoutParams();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyMargins() {
        /*
            r7 = this;
            int r0 = r7.getChildCount()
            r1 = 0
        L_0x0005:
            if (r1 >= r0) goto L_0x0031
            android.view.View r2 = r7.getChildAt(r1)
            android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
            boolean r3 = r3 instanceof android.widget.FrameLayout.LayoutParams
            if (r3 == 0) goto L_0x002e
            android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r4 = r3.rightMargin
            int r5 = r7.mRightInset
            if (r4 != r5) goto L_0x0025
            int r4 = r3.leftMargin
            int r6 = r7.mLeftInset
            if (r4 == r6) goto L_0x002e
        L_0x0025:
            r3.rightMargin = r5
            int r4 = r7.mLeftInset
            r3.leftMargin = r4
            r2.requestLayout()
        L_0x002e:
            int r1 = r1 + 1
            goto L_0x0005
        L_0x0031:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.CliNotificationShadeWindowView.applyMargins():void");
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setCliViewRequestListener(this);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (super.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        boolean z = keyEvent.getAction() == 0;
        if (keyEvent.getKeyCode() != 4) {
            return false;
        }
        if (!z) {
            this.mService.onBackPressed();
        }
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        View view;
        boolean z = motionEvent.getActionMasked() == 0;
        if (z && this.mService.mCliStatusBarWindowController.isDozing() && (view = this.mTopView) != null && view.getVisibility() == 0) {
            this.mService.mDozeScrimController.resetCliPulse(((CliStatusBarWindowController.ICliChildView) this.mTopView).getPulseTime());
        }
        if (z) {
            this.mService.userActivity();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void onVisibleChanged(View view, int i, boolean z) {
        if (DEBUG) {
            Log.d("Cli_NotificationShadeWindowView", "view=" + view + ";sbVisibility=" + i + ";interceptTouch=" + z);
        }
        this.mService.mCliPanelDragView.setInterceptTouch(!z);
        if (i == 0) {
            this.mTopView = view;
        } else {
            this.mTopView = this.mService.mCliPanelDragView;
        }
        if (this.mService.mCliStatusBarWindowController.isDozing()) {
            this.mService.mDozeScrimController.resetCliPulse(((CliStatusBarWindowController.ICliChildView) this.mTopView).getPulseTime());
        }
    }

    public void resetCliKeyguard() {
        resetCliKeyguard(this);
    }

    private void resetCliKeyguard(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CliStatusBarWindowController.ICliChildView) {
                ((CliStatusBarWindowController.ICliChildView) childAt).resetCliKeyguard();
            }
            if (childAt instanceof ViewGroup) {
                resetCliKeyguard((ViewGroup) childAt);
            }
        }
    }

    public void setCliViewRequestListener(CliStatusBarWindowController.OnCliViewRequestListener onCliViewRequestListener) {
        setCliViewRequestListener(this, onCliViewRequestListener);
    }

    public CliPanelDragView getNotificationPanelView() {
        return (CliPanelDragView) findViewById(R$id.cli_panel_container);
    }

    private void setCliViewRequestListener(ViewGroup viewGroup, CliStatusBarWindowController.OnCliViewRequestListener onCliViewRequestListener) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CliStatusBarWindowController.ICliChildView) {
                ((CliStatusBarWindowController.ICliChildView) childAt).setCliViewRequestListener(onCliViewRequestListener);
            }
            if (childAt instanceof ViewGroup) {
                setCliViewRequestListener((ViewGroup) childAt, this);
            }
        }
    }
}
