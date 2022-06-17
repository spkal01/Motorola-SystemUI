package com.android.systemui.statusbar.phone;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.p005qs.C1129QS;
import com.android.systemui.statusbar.notification.AboveShelfObserver;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.util.ArrayList;
import java.util.Comparator;

public class NotificationsQuickSettingsContainer extends ConstraintLayout implements FragmentHostManager.FragmentListener, AboveShelfObserver.HasViewAboveShelfChangedListener {
    private int mBottomPadding;
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onOverlayChanged() {
            MotoFeature.getInstance(NotificationsQuickSettingsContainer.this.mContext).updateLeftCarrrierName();
            NotificationsQuickSettingsContainer.this.updateCarrierLabel();
        }
    };
    private boolean mCustomizerAnimating;
    private boolean mCustomizing;
    private boolean mDetailShowing;
    private ArrayList<View> mDrawingOrderedChildren = new ArrayList<>();
    private boolean mHasViewsAboveShelf;
    private final Comparator<View> mIndexComparator = Comparator.comparingInt(new NotificationsQuickSettingsContainer$$ExternalSyntheticLambda0(this));
    private View mKeyguardStatusBar;
    private ArrayList<View> mLayoutDrawingOrder = new ArrayList<>();
    private boolean mQsExpanded;
    private FrameLayout mQsFrame;
    private NotificationStackScrollLayout mStackScroller;
    private int mStackScrollerMargin;

    public NotificationsQuickSettingsContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mQsFrame = (FrameLayout) findViewById(R$id.qs_frame);
        NotificationStackScrollLayout notificationStackScrollLayout = (NotificationStackScrollLayout) findViewById(R$id.notification_stack_scroller);
        this.mStackScroller = notificationStackScrollLayout;
        this.mStackScrollerMargin = ((ConstraintLayout.LayoutParams) notificationStackScrollLayout.getLayoutParams()).bottomMargin;
        this.mKeyguardStatusBar = findViewById(R$id.keyguard_header);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this.mConfigurationListener);
        updateCarrierLabel();
    }

    /* access modifiers changed from: private */
    public void updateCarrierLabel() {
        if (MotoFeature.getInstance(this.mContext).isBelowCarrierName()) {
            View findViewById = findViewById(R$id.keyguard_carrier_text);
            findViewById.setVisibility(4);
            ((ViewGroup) findViewById.getParent()).removeView(findViewById);
            addView(findViewById);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone((ConstraintLayout) this);
            constraintSet.constrainWidth(findViewById.getId(), -2);
            constraintSet.constrainHeight(findViewById.getId(), -2);
            int carrierLabelTopMargin = MotoFeature.getInstance(this.mContext).getCarrierLabelTopMargin();
            constraintSet.connect(findViewById.getId(), 3, this.mKeyguardStatusBar.getId(), 3, carrierLabelTopMargin);
            constraintSet.centerHorizontally(findViewById.getId(), 0);
            constraintSet.applyTo(this);
            return;
        }
        View findViewById2 = findViewById(R$id.keyguard_carrier_text);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        layoutParams.addRule(16, R$id.status_icon_area);
        layoutParams.setMarginStart(getResources().getDimensionPixelSize(R$dimen.keyguard_carrier_text_margin));
        layoutParams.addRule(15);
        ViewGroup viewGroup = (ViewGroup) findViewById2.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(findViewById2);
        }
        findViewById2.setVisibility(0);
        findViewById2.setAlpha(1.0f);
        ((ViewGroup) this.mKeyguardStatusBar).addView(findViewById2, 0, layoutParams);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FragmentHostManager.get(this).addTagListener(C1129QS.TAG, this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FragmentHostManager.get(this).removeTagListener(C1129QS.TAG, this);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        int stableInsetBottom = windowInsets.getStableInsetBottom();
        this.mBottomPadding = stableInsetBottom;
        setPadding(0, 0, 0, stableInsetBottom);
        return windowInsets;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mDrawingOrderedChildren.clear();
        this.mLayoutDrawingOrder.clear();
        if (this.mKeyguardStatusBar.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mKeyguardStatusBar);
            this.mLayoutDrawingOrder.add(this.mKeyguardStatusBar);
        }
        if (this.mQsFrame.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mQsFrame);
            this.mLayoutDrawingOrder.add(this.mQsFrame);
        }
        if (this.mStackScroller.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mStackScroller);
            this.mLayoutDrawingOrder.add(this.mStackScroller);
        }
        this.mLayoutDrawingOrder.sort(this.mIndexComparator);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        int indexOf = this.mLayoutDrawingOrder.indexOf(view);
        if (indexOf >= 0) {
            return super.drawChild(canvas, this.mDrawingOrderedChildren.get(indexOf), j);
        }
        return super.drawChild(canvas, view, j);
    }

    public void onFragmentViewCreated(String str, Fragment fragment) {
        ((C1129QS) fragment).setContainer(this);
    }

    public void setQsExpanded(boolean z) {
        if (this.mQsExpanded != z) {
            this.mQsExpanded = z;
            invalidate();
        }
    }

    public void setCustomizerAnimating(boolean z) {
        if (this.mCustomizerAnimating != z) {
            this.mCustomizerAnimating = z;
            invalidate();
        }
    }

    public void setCustomizerShowing(boolean z) {
        this.mCustomizing = z;
        updateBottomMargin();
        this.mStackScroller.setQsCustomizerShowing(z);
    }

    public void setDetailShowing(boolean z) {
        this.mDetailShowing = z;
        updateBottomMargin();
    }

    private void updateBottomMargin() {
        if (this.mCustomizing || this.mDetailShowing) {
            setPadding(0, 0, 0, 0);
            setBottomMargin(this.mStackScroller, 0);
            return;
        }
        setPadding(0, 0, 0, this.mBottomPadding);
        setBottomMargin(this.mStackScroller, this.mStackScrollerMargin);
    }

    private void setBottomMargin(View view, int i) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.bottomMargin = i;
        view.setLayoutParams(layoutParams);
    }

    public void onHasViewsAboveShelfChanged(boolean z) {
        this.mHasViewsAboveShelf = z;
        invalidate();
    }

    public boolean isCustomizerShown() {
        return this.mCustomizing;
    }
}
