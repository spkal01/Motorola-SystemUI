package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.settingslib.display.DisplayDensityUtils;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.moto.CarrierLabelUpdateMonitor;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.events.PrivacyDotViewController;
import com.android.systemui.statusbar.events.PrivacyDotViewStateChangedListener;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.leak.RotationUtils;
import java.util.List;
import java.util.Objects;

public class PhoneStatusBarView extends PanelBar {
    private boolean isStatusBarHide = false;
    StatusBar mBar;
    private BatteryMeterView mBattery;
    private View mCenterIconSpace;
    private DarkIconDispatcher.DarkReceiver mClock;
    private final CommandQueue mCommandQueue = ((CommandQueue) Dependency.get(CommandQueue.class));
    private final StatusBarContentInsetsProvider mContentInsetsProvider = ((StatusBarContentInsetsProvider) Dependency.get(StatusBarContentInsetsProvider.class));
    private int mCutoutSideNudge = 0;
    private View mCutoutSpace;
    private final int mDefaultPadding;
    private DisplayCutout mDisplayCutout;
    /* access modifiers changed from: private */
    public boolean mDotViewShowing;
    private PrivacyDotViewStateChangedListener mDotViewStateChangedListener = new PrivacyDotViewStateChangedListener() {
        public void onPrivacyDotViewStateChanged(boolean z) {
            if (PhoneStatusBarView.this.mDotViewShowing != z) {
                boolean unused = PhoneStatusBarView.this.mDotViewShowing = z;
                PhoneStatusBarView.this.mMainThreadHandler.post(new PhoneStatusBarView$2$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPrivacyDotViewStateChanged$0() {
            PhoneStatusBarView.this.updateSafeInsets();
            PhoneStatusBarView.this.requestLayout();
        }
    };
    private List<StatusBar.ExpansionChangedListener> mExpansionChangedListeners;
    private final boolean mForceHideCutoutSpace;
    private int mGesturalModeMargin;
    private boolean mHasCutout;
    private boolean mHeadsUpVisible;
    private Runnable mHideExpandedRunnable = new Runnable() {
        public void run() {
            PhoneStatusBarView phoneStatusBarView = PhoneStatusBarView.this;
            if (phoneStatusBarView.mPanelFraction == 0.0f) {
                phoneStatusBarView.mBar.makeExpandedInvisible();
            }
        }
    };
    private boolean mInGesturalMode;
    private boolean mInnerPercentage;
    boolean mIsFullyOpenedPanel = false;
    private int mLeftWeight;
    /* access modifiers changed from: private */
    public final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private final int mMarginLeftForCamera;
    private float mMinFraction;
    NavigationModeController.ModeChangedListener mNavigationChangedListener = new PhoneStatusBarView$$ExternalSyntheticLambda0(this);
    private int mRotationOrientation = -1;
    private ScrimController mScrimController;
    private View mStatusBarContentLeft;
    private int mStatusBarHeight;
    private int mStatusBarLandscapeMargin;
    private View mSystemIconArea;
    private int mSystemIconWeight;

    public PhoneStatusBarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Resources resources = context.getResources();
        this.mForceHideCutoutSpace = resources.getBoolean(R$bool.zz_moto_hide_cutout_space_in_status_bar);
        this.mMarginLeftForCamera = resources.getDimensionPixelSize(R$dimen.zz_moto_status_bar_camera_margin_left);
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.rounded_corner_content_padding);
        this.mDefaultPadding = dimensionPixelSize;
        int dimensionPixelSize2 = (resources.getDimensionPixelSize(R$dimen.real_rounded_corner_radius) / 2) - dimensionPixelSize;
        this.mGesturalModeMargin = dimensionPixelSize2;
        if (dimensionPixelSize2 < 0) {
            this.mGesturalModeMargin = 0;
        }
        int dimensionPixelSize3 = (resources.getDimensionPixelSize(R$dimen.zz_moto_status_bar_padding_landscape) - context.getResources().getDimensionPixelSize(R$dimen.status_bar_padding_start)) - dimensionPixelSize;
        this.mStatusBarLandscapeMargin = dimensionPixelSize3;
        if (dimensionPixelSize3 < 0) {
            this.mStatusBarLandscapeMargin = 0;
        }
        int i = this.mGesturalModeMargin;
        int i2 = this.mStatusBarLandscapeMargin;
        if (i < i2) {
            this.mGesturalModeMargin = i2;
        }
        this.mHasCutout = !TextUtils.isEmpty(resources.getString(17039973));
        this.mInnerPercentage = resources.getBoolean(R$bool.config_inner_percentage_while_display_bigger_default);
        this.mLeftWeight = resources.getInteger(R$integer.statusbar_left_weight);
        this.mSystemIconWeight = resources.getInteger(R$integer.statusbar_system_icon_weight);
    }

    public void setBar(StatusBar statusBar) {
        this.mBar = statusBar;
    }

    public void setExpansionChangedListeners(List<StatusBar.ExpansionChangedListener> list) {
        this.mExpansionChangedListeners = list;
    }

    public void setScrimController(ScrimController scrimController) {
        this.mScrimController = scrimController;
    }

    public void onFinishInflate() {
        this.mBattery = (BatteryMeterView) findViewById(R$id.battery);
        if (MotoFeature.getInstance(getContext()).showRightSideClock()) {
            this.mClock = (DarkIconDispatcher.DarkReceiver) findViewById(R$id.right_clock);
        } else {
            this.mClock = (DarkIconDispatcher.DarkReceiver) findViewById(R$id.clock);
        }
        this.mCutoutSpace = findViewById(R$id.cutout_space_view);
        this.mCenterIconSpace = findViewById(R$id.centered_icon_area);
        this.mStatusBarContentLeft = findViewById(R$id.status_bar_contents_left);
        this.mSystemIconArea = findViewById(R$id.system_icon_area);
        updateResources();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        Class cls = DarkIconDispatcher.class;
        super.onAttachedToWindow();
        ((DarkIconDispatcher) Dependency.get(cls)).addDarkReceiver((DarkIconDispatcher.DarkReceiver) this.mBattery);
        ((DarkIconDispatcher) Dependency.get(cls)).addDarkReceiver(this.mClock);
        this.mInGesturalMode = 2 == ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this.mNavigationChangedListener);
        if (updateOrientationAndCutout()) {
            updateLayoutForCutout();
        }
        if (this.mInnerPercentage) {
            this.mBattery.innerStausbarPercentage(isDisplayDensityBiggerThanDefualt());
        }
        ((PrivacyDotViewController) Dependency.get(PrivacyDotViewController.class)).addCallback(this.mDotViewStateChangedListener);
    }

    private boolean isDisplayDensityBiggerThanDefualt() {
        DisplayDensityUtils displayDensityUtils = new DisplayDensityUtils(this.mContext);
        int[] values = displayDensityUtils.getValues();
        int currentIndex = displayDensityUtils.getCurrentIndex();
        int defaultDensity = displayDensityUtils.getDefaultDensity();
        int i = 0;
        while (true) {
            if (i > values.length - 1) {
                i = 0;
                break;
            } else if (values[i] == defaultDensity) {
                break;
            } else {
                i++;
            }
        }
        if (currentIndex <= i) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        Class cls = DarkIconDispatcher.class;
        super.onDetachedFromWindow();
        ((DarkIconDispatcher) Dependency.get(cls)).removeDarkReceiver((DarkIconDispatcher.DarkReceiver) this.mBattery);
        ((DarkIconDispatcher) Dependency.get(cls)).removeDarkReceiver(this.mClock);
        ((PrivacyDotViewController) Dependency.get(PrivacyDotViewController.class)).removeCallback(this.mDotViewStateChangedListener);
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).removeListener(this.mNavigationChangedListener);
        this.mDisplayCutout = null;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateResources();
        if (updateOrientationAndCutout()) {
            updateLayoutForCutout();
            requestLayout();
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (updateOrientationAndCutout()) {
            updateLayoutForCutout();
            requestLayout();
        }
        return super.onApplyWindowInsets(windowInsets);
    }

    private boolean updateOrientationAndCutout() {
        boolean z;
        int exactRotation = RotationUtils.getExactRotation(this.mContext);
        if (exactRotation != this.mRotationOrientation) {
            this.mRotationOrientation = exactRotation;
            z = true;
        } else {
            z = false;
        }
        if (Objects.equals(getRootWindowInsets().getDisplayCutout(), this.mDisplayCutout)) {
            return z;
        }
        this.mDisplayCutout = getRootWindowInsets().getDisplayCutout();
        return true;
    }

    public boolean panelEnabled() {
        return this.mCommandQueue.panelsEnabled();
    }

    public boolean onRequestSendAccessibilityEventInternal(View view, AccessibilityEvent accessibilityEvent) {
        if (!super.onRequestSendAccessibilityEventInternal(view, accessibilityEvent)) {
            return false;
        }
        AccessibilityEvent obtain = AccessibilityEvent.obtain();
        onInitializeAccessibilityEvent(obtain);
        dispatchPopulateAccessibilityEvent(obtain);
        accessibilityEvent.appendRecord(obtain);
        return true;
    }

    public void onPanelPeeked() {
        super.onPanelPeeked();
        this.mBar.makeExpandedVisible(false);
    }

    public void onPanelCollapsed() {
        super.onPanelCollapsed();
        post(this.mHideExpandedRunnable);
        this.mIsFullyOpenedPanel = false;
        this.mContext.sendBroadcast(new Intent("com.motorola.internal.policy.statusbar.NOTIFICATION_VIEW_FULLY_COLLAPSED"));
    }

    public void removePendingHideExpandedRunnables() {
        removeCallbacks(this.mHideExpandedRunnable);
    }

    public void onPanelFullyOpened() {
        super.onPanelFullyOpened();
        if (!this.mIsFullyOpenedPanel) {
            this.mPanel.getView().sendAccessibilityEvent(32);
            PanelViewController panelViewController = this.mPanel;
            if (!(panelViewController == null || !(panelViewController instanceof NotificationPanelViewController) || this.mBar == null || ((NotificationPanelViewController) panelViewController).isQsExpanded() || this.mBar.getBarState() == 1)) {
                this.mContext.sendBroadcast(new Intent("com.motorola.internal.policy.statusbar.NOTIFICATION_VIEW_FULLY_VISIBLE"));
            }
        }
        this.mIsFullyOpenedPanel = true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mBar.interceptTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    public void onTrackingStarted() {
        super.onTrackingStarted();
        this.mBar.onTrackingStarted();
        this.mScrimController.onTrackingStarted();
        removePendingHideExpandedRunnables();
    }

    public void onClosingFinished() {
        super.onClosingFinished();
        this.mBar.onClosingFinished();
    }

    public void onTrackingStopped(boolean z) {
        super.onTrackingStopped(z);
        this.mBar.onTrackingStopped(z);
    }

    public void onExpandingFinished() {
        super.onExpandingFinished();
        this.mScrimController.onExpandingFinished();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mBar.interceptTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    public void panelScrimMinFractionChanged(float f) {
        if (Float.isNaN(f)) {
            throw new IllegalArgumentException("minFraction cannot be NaN");
        } else if (this.mMinFraction != f) {
            this.mMinFraction = f;
            updateScrimFraction();
        }
    }

    public void panelExpansionChanged(float f, boolean z) {
        super.panelExpansionChanged(f, z);
        updateScrimFraction();
        boolean z2 = false;
        CarrierLabelUpdateMonitor.getInstance().panelExpansionChanged(this.mPanelFraction > 0.0f);
        if ((f == 0.0f || f == 1.0f) && this.mBar.getNavigationBarView() != null) {
            this.mBar.getNavigationBarView().onStatusBarPanelStateChanged();
        }
        List<StatusBar.ExpansionChangedListener> list = this.mExpansionChangedListeners;
        if (list != null) {
            for (StatusBar.ExpansionChangedListener onExpansionChanged : list) {
                onExpansionChanged.onExpansionChanged(f, z);
            }
        }
        float f2 = this.mPanelFraction;
        if (f2 <= 0.0f || this.isStatusBarHide) {
            if (f2 != 0.0f) {
                z2 = true;
            }
            this.isStatusBarHide = z2;
            return;
        }
        this.mBar.makeExpandedVisible(true);
        this.isStatusBarHide = true;
    }

    private void updateScrimFraction() {
        float f = this.mPanelFraction;
        float f2 = this.mMinFraction;
        if (f2 < 1.0f) {
            f = Math.max((f - f2) / (1.0f - f2), 0.0f);
        }
        this.mScrimController.setPanelExpansion(f);
    }

    public void updateResources() {
        this.mCutoutSideNudge = getResources().getDimensionPixelSize(R$dimen.display_cutout_margin_consumption);
        updateStatusBarHeight();
    }

    private void updateStatusBarHeight() {
        DisplayCutout displayCutout = this.mDisplayCutout;
        int i = displayCutout == null ? 0 : displayCutout.getWaterfallInsets().top;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.status_bar_height);
        this.mStatusBarHeight = dimensionPixelSize;
        layoutParams.height = dimensionPixelSize - i;
        int i2 = this.mRotationOrientation;
        if (i2 == 1 || i2 == 3) {
            this.mStatusBarContentLeft.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
            this.mSystemIconArea.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
        } else {
            this.mStatusBarContentLeft.setLayoutParams(new LinearLayout.LayoutParams(0, -1, (float) this.mLeftWeight));
            this.mSystemIconArea.setLayoutParams(new LinearLayout.LayoutParams(0, -1, (float) this.mSystemIconWeight));
        }
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R$dimen.status_bar_padding_top);
        int dimensionPixelSize3 = getResources().getDimensionPixelSize(R$dimen.status_bar_padding_start);
        findViewById(R$id.status_bar_contents).setPaddingRelative(dimensionPixelSize3, dimensionPixelSize2, getResources().getDimensionPixelSize(R$dimen.status_bar_padding_end), 0);
        findViewById(R$id.notification_lights_out).setPaddingRelative(0, dimensionPixelSize3, 0, 0);
        setLayoutParams(layoutParams);
    }

    private void updateLayoutForCutout() {
        updateStatusBarHeight();
        updateCutoutLocation(StatusBarWindowView.cornerCutoutMargins(this.mDisplayCutout, getDisplay()));
        updateSafeInsets();
        updateStatusBarMarginIfNeed();
    }

    /* access modifiers changed from: private */
    /* renamed from: handleNavigationModeChange */
    public void lambda$new$0(int i) {
        boolean isGesturalMode = QuickStepContract.isGesturalMode(i);
        if (this.mInGesturalMode != isGesturalMode) {
            this.mInGesturalMode = isGesturalMode;
        }
    }

    private void updateStatusBarMarginIfNeed() {
        int i = this.mRotationOrientation;
        if (i != 1 && i != 3) {
            int i2 = this.mMarginLeftForCamera;
            if (i2 == 0) {
                updateStatusBarMargin(R$id.status_bar_contents, 0, 0);
                updateStatusBarMargin(R$id.notification_lights_out, 0, 0);
                return;
            }
            updateStatusBarMargin(R$id.status_bar_contents, i2, 0);
            updateStatusBarMargin(R$id.notification_lights_out, this.mMarginLeftForCamera, 0);
        } else if (!this.mInGesturalMode) {
            int i3 = R$id.status_bar_contents;
            int i4 = this.mStatusBarLandscapeMargin;
            updateStatusBarMargin(i3, i4, i4);
            updateStatusBarMargin(R$id.notification_lights_out, this.mStatusBarLandscapeMargin, 0);
        } else if (i == 1) {
            updateStatusBarMargin(R$id.status_bar_contents, this.mHasCutout ? this.mStatusBarLandscapeMargin : this.mGesturalModeMargin, this.mGesturalModeMargin);
            updateStatusBarMargin(R$id.notification_lights_out, this.mHasCutout ? this.mStatusBarLandscapeMargin : this.mGesturalModeMargin, 0);
        } else if (i == 3) {
            int i5 = R$id.status_bar_contents;
            int i6 = this.mGesturalModeMargin;
            updateStatusBarMargin(i5, i6, this.mHasCutout ? this.mStatusBarLandscapeMargin : i6);
            updateStatusBarMargin(R$id.notification_lights_out, this.mGesturalModeMargin, 0);
        }
    }

    private void updateStatusBarMargin(int i, int i2, int i3) {
        View findViewById = findViewById(i);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(findViewById.getLayoutParams());
        layoutParams.setMargins(i2, 0, i3, 0);
        findViewById.setLayoutParams(layoutParams);
    }

    private void updateCutoutLocation(Pair<Integer, Integer> pair) {
        if (this.mCutoutSpace != null) {
            if (!getResources().getBoolean(17891635) || this.mRotationOrientation != 0) {
                DisplayCutout displayCutout = this.mDisplayCutout;
                if (displayCutout == null || displayCutout.isEmpty() || pair != null) {
                    this.mCenterIconSpace.setVisibility(0);
                    this.mCutoutSpace.setVisibility(8);
                    return;
                }
                this.mCenterIconSpace.setVisibility(8);
                this.mCutoutSpace.setVisibility(0);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mCutoutSpace.getLayoutParams();
                Rect rect = new Rect();
                ScreenDecorations.DisplayCutoutView.boundsFromDirection(this.mDisplayCutout, 48, rect);
                if (rect.height() - getResources().getDimensionPixelSize(R$dimen.status_bar_padding_top) <= 0 || this.mForceHideCutoutSpace) {
                    this.mCutoutSpace.setVisibility(8);
                    return;
                }
                int i = rect.left;
                int i2 = this.mCutoutSideNudge;
                rect.left = i + i2;
                rect.right -= i2;
                layoutParams.width = rect.width();
                layoutParams.height = rect.height();
                return;
            }
            this.mCenterIconSpace.setVisibility(8);
            this.mCutoutSpace.setVisibility(0);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mCutoutSpace.getLayoutParams();
            int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.zz_moto_cud_layout_size);
            layoutParams2.width = dimensionPixelSize;
            layoutParams2.height = dimensionPixelSize;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x003c, code lost:
        if (r1 < 0) goto L_0x0049;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSafeInsets() {
        /*
            r6 = this;
            com.android.systemui.statusbar.phone.StatusBarContentInsetsProvider r0 = r6.mContentInsetsProvider
            android.content.Context r1 = r6.getContext()
            int r1 = com.android.systemui.util.leak.RotationUtils.getExactRotation(r1)
            android.graphics.Rect r0 = r0.getStatusBarContentInsetsForRotation(r1)
            android.graphics.Point r1 = new android.graphics.Point
            r1.<init>()
            android.content.Context r2 = r6.mContext
            android.view.Display r2 = r2.getDisplay()
            r2.getRealSize(r1)
            int r2 = r1.x
            int r3 = r0.right
            int r2 = r2 - r3
            boolean r3 = r6.mDotViewShowing
            r4 = 0
            if (r3 == 0) goto L_0x003f
            int r3 = r6.mRotationOrientation
            if (r3 != 0) goto L_0x003f
            android.view.View r3 = r6.mCutoutSpace
            if (r3 == 0) goto L_0x003f
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x003f
            int r2 = r0.left
            int r1 = r1.x
            int r3 = r0.right
            int r1 = r1 - r3
            int r1 = r1 - r2
            if (r1 >= 0) goto L_0x004a
            goto L_0x0049
        L_0x003f:
            boolean r1 = r6.mDotViewShowing
            if (r1 != 0) goto L_0x0049
            int r1 = r6.mRotationOrientation
            if (r1 != 0) goto L_0x0049
            int r2 = r0.left
        L_0x0049:
            r1 = r4
        L_0x004a:
            int r0 = r0.left
            int r3 = r6.getPaddingTop()
            int r5 = r6.getPaddingBottom()
            r6.setPadding(r0, r3, r2, r5)
            android.view.View r6 = r6.mSystemIconArea
            r6.setPadding(r4, r4, r1, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarView.updateSafeInsets():void");
    }

    public void setHeadsUpVisible(boolean z) {
        this.mHeadsUpVisible = z;
        updateVisibility();
    }

    /* access modifiers changed from: protected */
    public boolean shouldPanelBeVisible() {
        return this.mHeadsUpVisible || super.shouldPanelBeVisible();
    }
}
