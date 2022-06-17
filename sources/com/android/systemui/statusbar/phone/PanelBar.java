package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.android.systemui.moto.CarrierLabelUpdateMonitor;
import com.android.systemui.moto.MotoFeature;

public abstract class PanelBar extends FrameLayout {
    public static final boolean DEBUG_PANEL = Build.IS_DEBUGGABLE;
    public static final String TAG = PanelBar.class.getSimpleName();
    private boolean mBouncerShowing;
    private boolean mExpanded;
    private boolean mIsPrcCustom;
    PanelViewController mPanel;
    protected float mPanelFraction;
    private int mState = 0;
    private boolean mTracking;

    public void onClosingFinished() {
    }

    public void onExpandingFinished() {
    }

    public void onPanelCollapsed() {
    }

    public void onPanelFullyOpened() {
    }

    public void onPanelPeeked() {
    }

    public boolean panelEnabled() {
        return true;
    }

    public abstract void panelScrimMinFractionChanged(float f);

    /* renamed from: go */
    public void mo22708go(int i) {
        PanelViewController panelViewController;
        if (this.mIsPrcCustom && DEBUG_PANEL && this.mState != i) {
            Log.i(TAG, "PrcPanel Change state from " + this.mState + " to " + i);
        }
        this.mState = i;
        boolean z = false;
        if (this.mIsPrcCustom && i == 0 && (panelViewController = this.mPanel) != null && !panelViewController.isStatusBarShadeLocked()) {
            this.mPanel.updatePanelViewState(0);
        }
        PanelViewController panelViewController2 = this.mPanel;
        if (panelViewController2 != null) {
            if (i == 1) {
                z = true;
            }
            panelViewController2.setIsShadeOpening(z);
        }
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("panel_bar_super_parcelable", super.onSaveInstanceState());
        bundle.putInt("state", this.mState);
        return bundle;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null || !(parcelable instanceof Bundle)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        Bundle bundle = (Bundle) parcelable;
        super.onRestoreInstanceState(bundle.getParcelable("panel_bar_super_parcelable"));
        if (bundle.containsKey("state")) {
            mo22708go(bundle.getInt("state", 0));
        }
    }

    public PanelBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsPrcCustom = MotoFeature.getInstance(context).isCustomPanelView();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setPanel(PanelViewController panelViewController) {
        this.mPanel = panelViewController;
        panelViewController.setBar(this);
    }

    public void setBouncerShowing(boolean z) {
        this.mBouncerShowing = z;
        int i = z ? 4 : 0;
        setImportantForAccessibility(i);
        updateVisibility();
        PanelViewController panelViewController = this.mPanel;
        if (panelViewController != null) {
            panelViewController.getView().setImportantForAccessibility(i);
        }
    }

    public float getExpansionFraction() {
        return this.mPanelFraction;
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    /* access modifiers changed from: protected */
    public void updateVisibility() {
        this.mPanel.getView().setVisibility(shouldPanelBeVisible() ? 0 : 4);
    }

    /* access modifiers changed from: protected */
    public boolean shouldPanelBeVisible() {
        return this.mExpanded || this.mBouncerShowing;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!panelEnabled()) {
            if (motionEvent.getAction() == 0) {
                Log.v(TAG, String.format("onTouch: all panels disabled, ignoring touch at (%d,%d)", new Object[]{Integer.valueOf((int) motionEvent.getX()), Integer.valueOf((int) motionEvent.getY())}));
            }
            return false;
        }
        if (motionEvent.getAction() == 0) {
            PanelViewController panelViewController = this.mPanel;
            if (panelViewController == null) {
                Log.v(TAG, String.format("onTouch: no panel for touch at (%d,%d)", new Object[]{Integer.valueOf((int) motionEvent.getX()), Integer.valueOf((int) motionEvent.getY())}));
                return true;
            } else if (!panelViewController.isEnabled()) {
                Log.v(TAG, String.format("onTouch: panel (%s) is disabled, ignoring touch at (%d,%d)", new Object[]{panelViewController, Integer.valueOf((int) motionEvent.getX()), Integer.valueOf((int) motionEvent.getY())}));
                return true;
            }
        }
        if (this.mIsPrcCustom) {
            if (motionEvent.getAction() == 0 && this.mPanel.isStatusBarShade() && this.mPanel.isNormalState()) {
                if (DEBUG_PANEL) {
                    Log.i(TAG, "PrcPanel Touch Statusbar DOWN");
                }
                if (motionEvent.getX() < ((float) (getWidth() / 2))) {
                    this.mPanel.updatePanelViewState(1);
                } else {
                    this.mPanel.updatePanelViewState(2);
                }
            } else if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.mPanel.isStatusBarShade() && !this.mExpanded && !motionEvent.isFromSource(8194)) {
                if (DEBUG_PANEL) {
                    Log.i(TAG, "PrcPanel Touch Statusbar UP or Cancel");
                }
                this.mPanel.updatePanelViewState(0);
            }
        }
        PanelViewController panelViewController2 = this.mPanel;
        if (panelViewController2 == null || panelViewController2.getView().dispatchTouchEvent(motionEvent)) {
            return true;
        }
        return false;
    }

    public void panelExpansionChanged(float f, boolean z) {
        boolean z2;
        if (!Float.isNaN(f)) {
            PanelViewController panelViewController = this.mPanel;
            this.mExpanded = z;
            this.mPanelFraction = f;
            updateVisibility();
            boolean z3 = true;
            if (z) {
                if (this.mState == 0) {
                    mo22708go(1);
                    onPanelPeeked();
                }
                if (panelViewController.getExpandedFraction() < 1.0f) {
                    z3 = false;
                }
                z2 = false;
            } else {
                z2 = true;
                z3 = false;
            }
            if (z3 && !this.mTracking) {
                mo22708go(2);
                onPanelFullyOpened();
            } else if (z2 && !this.mTracking && this.mState != 0) {
                mo22708go(0);
                onPanelCollapsed();
            }
            CarrierLabelUpdateMonitor.getInstance().panelExpansionChanged(z);
            return;
        }
        throw new IllegalArgumentException("frac cannot be NaN");
    }

    public void collapsePanel(boolean z, boolean z2, float f) {
        boolean z3;
        PanelViewController panelViewController = this.mPanel;
        if (!z || panelViewController.isFullyCollapsed()) {
            panelViewController.resetViews(false);
            panelViewController.setExpandedFraction(0.0f);
            z3 = false;
        } else {
            panelViewController.collapse(z2, f);
            z3 = true;
        }
        if (!z3 && this.mState != 0) {
            mo22708go(0);
            onPanelCollapsed();
        }
    }

    public boolean isClosed() {
        return this.mState == 0;
    }

    public void onTrackingStarted() {
        this.mTracking = true;
    }

    public void onTrackingStopped(boolean z) {
        this.mTracking = false;
    }
}
