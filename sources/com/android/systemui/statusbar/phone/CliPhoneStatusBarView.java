package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.StatusBarIconController;

public class CliPhoneStatusBarView extends FrameLayout {
    private DarkIconDispatcher.DarkReceiver mBattery;
    private CliStatusBar mCliBar;
    private DarkIconDispatcher.DarkReceiver mCliLockIcon;
    private StatusBarIconController.DarkIconManager mDarkIconManager;
    private View mPanelView;
    private View mStatusBarView;

    public CliPhoneStatusBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setBar(CliStatusBar cliStatusBar) {
        this.mCliBar = cliStatusBar;
    }

    public void setPanelView(View view) {
        this.mPanelView = view;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        Class cls = DarkIconDispatcher.class;
        super.onAttachedToWindow();
        ((DarkIconDispatcher) Dependency.get(cls)).addDarkReceiver(this.mBattery);
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).addIconGroup(this.mDarkIconManager);
        ((DarkIconDispatcher) Dependency.get(cls)).addDarkReceiver(this.mCliLockIcon);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        Class cls = DarkIconDispatcher.class;
        super.onDetachedFromWindow();
        ((DarkIconDispatcher) Dependency.get(cls)).removeDarkReceiver(this.mBattery);
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).removeIconGroup(this.mDarkIconManager);
        ((DarkIconDispatcher) Dependency.get(cls)).removeDarkReceiver(this.mCliLockIcon);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mStatusBarView = findViewById(R$id.cli_system_icons);
        this.mBattery = (DarkIconDispatcher.DarkReceiver) findViewById(R$id.cli_battery);
        this.mCliLockIcon = (DarkIconDispatcher.DarkReceiver) findViewById(R$id.cli_locked);
        StatusBarIconController.DarkIconManager darkIconManager = new StatusBarIconController.DarkIconManager((LinearLayout) findViewById(R$id.cli_statusIcons), (FeatureFlags) Dependency.get(FeatureFlags.class));
        this.mDarkIconManager = darkIconManager;
        darkIconManager.setShouldLog(true);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        View view = this.mPanelView;
        return view == null || view.dispatchTouchEvent(motionEvent);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void onCliDensityOrFontScaleChanged() {
        updateStatusBarView();
    }

    private void updateStatusBarView() {
        Class cls = StatusBarIconController.class;
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.cli_status_bar_height);
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R$dimen.signal_cluster_battery_padding);
        ViewGroup.LayoutParams layoutParams = this.mStatusBarView.getLayoutParams();
        layoutParams.height = dimensionPixelSize;
        this.mStatusBarView.setLayoutParams(layoutParams);
        StatusIconContainer statusIconContainer = (StatusIconContainer) this.mStatusBarView.findViewById(R$id.cli_statusIcons);
        statusIconContainer.setPaddingRelative(0, 0, dimensionPixelSize2, 0);
        ViewGroup.LayoutParams layoutParams2 = getLayoutParams();
        layoutParams2.height = dimensionPixelSize;
        setLayoutParams(layoutParams2);
        ((StatusBarIconController) Dependency.get(cls)).removeIconGroup(this.mDarkIconManager);
        this.mDarkIconManager = null;
        this.mDarkIconManager = new StatusBarIconController.DarkIconManager(statusIconContainer, (FeatureFlags) Dependency.get(FeatureFlags.class));
        ((StatusBarIconController) Dependency.get(cls)).addIconGroup(this.mDarkIconManager);
        ((BatteryMeterView) findViewById(R$id.cli_battery)).onDensityOrFontScaleChanged();
    }
}
