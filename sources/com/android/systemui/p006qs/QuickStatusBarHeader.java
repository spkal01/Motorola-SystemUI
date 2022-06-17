package com.android.systemui.p006qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import com.android.settingslib.Utils;
import com.android.settingslib.display.DisplayDensityUtils;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.R$bool;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSDetail;
import com.android.systemui.p006qs.TouchAnimator;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import com.android.systemui.statusbar.phone.StatusIconContainer;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.statusbar.policy.DateView;
import com.android.systemui.util.leak.RotationUtils;
import java.util.List;

/* renamed from: com.android.systemui.qs.QuickStatusBarHeader */
public class QuickStatusBarHeader extends FrameLayout {
    private int mActiveSubsCount;
    private boolean mAirplaneMode;
    private TouchAnimator mAlphaAnimator;
    private BatteryMeterView mBatteryRemainingIcon;
    private View mClockIconsSeparator;
    private View mClockIconsView;
    private Clock mClockView;
    private boolean mConfigShowBatteryEstimate;
    private View mContainer;
    private int mCutOutPaddingLeft;
    private int mCutOutPaddingRight;
    private View mDateAndTimeView;
    private View mDateContainer;
    private Space mDatePrivacySeparator;
    private View mDatePrivacyView;
    private View mDateView;
    private boolean mExpanded;
    private boolean mHasCenterCutout;
    protected QuickQSPanel mHeaderQsPanel;
    private int mHeaderStatus;
    /* access modifiers changed from: private */
    public StatusIconContainer mIconContainer;
    private TouchAnimator mIconsAlphaAnimator;
    private TouchAnimator mIconsAlphaAnimatorFixed;
    private boolean mIsNightMode;
    private boolean mIsPrcCustom;
    /* access modifiers changed from: private */
    public boolean mIsSingleCarrier;
    private float mKeyguardExpansionFraction;
    private final int mMarginLeftForCamera;
    private View mMotoQSCarriersContainer;
    private Clock mPrcClock;
    private View mPrcDate;
    private TouchAnimator mPrcNotificationAnimator;
    private TouchAnimator mPrcQsAnimator;
    private View mPrivacyChip;
    private View mPrivacyContainer;
    private View mQSCarriers;
    private QSExpansionPathInterpolator mQSExpansionPathInterpolator;
    private boolean mQsDisabled;
    private View mRightLayout;
    private int mRotationOrientation;
    private int mRoundedCornerPadding;
    /* access modifiers changed from: private */
    public List<String> mRssiIgnoredSlots;
    private View mSecurityHeaderView;
    /* access modifiers changed from: private */
    public boolean mShowClockIconsSeparator;
    private boolean mShowMotoQSCarrierGroup;
    private int mTextColorPrimary;
    private StatusBarIconController.TintedIconManager mTintedIconManager;
    private int mTopViewMeasureHeight;
    private TouchAnimator mTranslationAnimator;
    private float mViewAlpha;
    private int mWaterfallTopInset;

    public QuickStatusBarHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRoundedCornerPadding = 0;
        this.mViewAlpha = 1.0f;
        this.mTextColorPrimary = 0;
        this.mRotationOrientation = -1;
        this.mMarginLeftForCamera = this.mContext.getResources().getDimensionPixelSize(R$dimen.zz_moto_status_bar_camera_margin_left);
        this.mRotationOrientation = RotationUtils.getExactRotation(this.mContext);
        this.mIsPrcCustom = MotoFeature.getInstance(this.mContext).isCustomPanelView();
    }

    public int getOffsetTranslation() {
        return this.mTopViewMeasureHeight;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderQsPanel = (QuickQSPanel) findViewById(R$id.quick_qs_panel);
        this.mDatePrivacyView = findViewById(R$id.quick_status_bar_date_privacy);
        this.mClockIconsView = findViewById(R$id.quick_qs_status_icons);
        this.mQSCarriers = findViewById(R$id.carrier_group);
        this.mMotoQSCarriersContainer = findViewById(R$id.carrier_group_moto_container);
        this.mContainer = findViewById(R$id.qs_container);
        this.mIconContainer = (StatusIconContainer) findViewById(R$id.statusIcons);
        this.mPrivacyChip = findViewById(R$id.privacy_chip);
        this.mDateView = findViewById(R$id.date);
        this.mSecurityHeaderView = findViewById(R$id.header_text_container);
        this.mClockIconsSeparator = findViewById(R$id.separator);
        this.mRightLayout = findViewById(R$id.rightLayout);
        this.mDateContainer = findViewById(R$id.date_container);
        this.mPrivacyContainer = findViewById(R$id.privacy_container);
        this.mClockView = (Clock) findViewById(R$id.clock);
        this.mDatePrivacySeparator = (Space) findViewById(R$id.space);
        this.mBatteryRemainingIcon = (BatteryMeterView) findViewById(R$id.batteryRemainingIcon);
        if (this.mIsPrcCustom) {
            this.mIsNightMode = (getContext().getResources().getConfiguration().uiMode & 48) == 32;
            Clock clock = (Clock) findViewById(R$id.clock_new);
            this.mPrcClock = clock;
            clock.setIsHeaderClockForPRC(true);
            this.mClockView.setClockOwnerShip(Clock.OwnerShip.PRC_QUICKSETTINGS);
            ((DateView) this.mDateView).setTextColor(getResources().getColor(R$color.prcQSPanelDate));
            this.mPrcDate = findViewById(R$id.date_new);
        }
        View findViewById = findViewById(R$id.clockanddate_container);
        this.mDateAndTimeView = findViewById;
        if (this.mIsPrcCustom) {
            updateHeaderUI();
        } else {
            findViewById.setVisibility(8);
        }
        lambda$updateAirplaneMode$2();
        this.mBatteryRemainingIcon.setIgnoreTunerUpdates(true);
        this.mBatteryRemainingIcon.setPercentShowMode(3);
        this.mBatteryRemainingIcon.setIsExpandedBattery(true);
        this.mIconsAlphaAnimatorFixed = new TouchAnimator.Builder().addFloat(this.mIconContainer, "alpha", 0.0f, 1.0f).addFloat(this.mBatteryRemainingIcon, "alpha", 0.0f, 1.0f).build();
    }

    private void updateIconsForPrc() {
        if (this.mIsPrcCustom) {
            BatteryMeterView batteryMeterView = this.mBatteryRemainingIcon;
            if (batteryMeterView != null) {
                batteryMeterView.onDarkChanged(new Rect(), this.mIsNightMode ^ true ? 1.0f : 0.0f, -1);
            }
            Clock clock = this.mPrcClock;
            if (clock != null) {
                clock.onColorsChanged(this.mIsNightMode);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onAttach(StatusBarIconController.TintedIconManager tintedIconManager, QSExpansionPathInterpolator qSExpansionPathInterpolator, List<String> list) {
        this.mTintedIconManager = tintedIconManager;
        this.mRssiIgnoredSlots = list;
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(getContext(), 16842806);
        if (this.mIsPrcCustom) {
            colorAttrDefaultColor = getResources().getColor(R$color.prcQSPanelDate);
        }
        tintedIconManager.setTint(colorAttrDefaultColor);
        this.mQSExpansionPathInterpolator = qSExpansionPathInterpolator;
        updateAnimators();
    }

    /* access modifiers changed from: package-private */
    public void setIsSingleCarrier(boolean z) {
        this.mIsSingleCarrier = z;
        updateAlphaAnimator();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mDatePrivacyView.getMeasuredHeight() != this.mTopViewMeasureHeight) {
            this.mTopViewMeasureHeight = this.mDatePrivacyView.getMeasuredHeight();
            updateAnimators();
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int exactRotation = RotationUtils.getExactRotation(this.mContext);
        boolean z = true;
        if (exactRotation != this.mRotationOrientation) {
            this.mRotationOrientation = exactRotation;
            this.mShowMotoQSCarrierGroup = exactRotation == 0 && this.mActiveSubsCount == 2;
        }
        if (this.mIsPrcCustom) {
            ((DateView) this.mDateView).setTextColor(getResources().getColor(R$color.prcQSPanelDate));
            this.mIsNightMode = (configuration.uiMode & 48) == 32;
        }
        lambda$updateAirplaneMode$2();
        if (configuration.orientation != 2) {
            z = false;
        }
        setDatePrivacyContainersWidth(z);
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        lambda$updateAirplaneMode$2();
    }

    private void setDatePrivacyContainersWidth(boolean z) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mDateContainer.getLayoutParams();
        int i = -2;
        layoutParams.width = z ? -2 : 0;
        float f = 0.0f;
        layoutParams.weight = z ? 0.0f : 1.0f;
        this.mDateContainer.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mPrivacyContainer.getLayoutParams();
        if (!z) {
            i = 0;
        }
        layoutParams2.width = i;
        layoutParams2.weight = z ? 0.0f : 1.0f;
        this.mPrivacyContainer.setLayoutParams(layoutParams2);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mSecurityHeaderView.getLayoutParams();
        if (z) {
            f = 1.0f;
        }
        layoutParams3.weight = f;
        this.mSecurityHeaderView.setLayoutParams(layoutParams3);
    }

    private void updateBatteryMode() {
        if (!this.mConfigShowBatteryEstimate || this.mHasCenterCutout) {
            this.mBatteryRemainingIcon.setPercentShowMode(1);
        } else {
            this.mBatteryRemainingIcon.setPercentShowMode(3);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: updateResources */
    public void lambda$updateAirplaneMode$2() {
        Resources resources = this.mContext.getResources();
        this.mConfigShowBatteryEstimate = resources.getBoolean(R$bool.config_showBatteryEstimateQSBH);
        this.mRoundedCornerPadding = resources.getDimensionPixelSize(R$dimen.rounded_corner_content_padding);
        int dimensionPixelSize = resources.getDimensionPixelSize(17105483);
        this.mDatePrivacyView.getLayoutParams().height = Math.max(dimensionPixelSize, this.mDatePrivacyView.getMinimumHeight());
        View view = this.mDatePrivacyView;
        view.setLayoutParams(view.getLayoutParams());
        this.mClockIconsView.getLayoutParams().height = Math.max(dimensionPixelSize, this.mClockIconsView.getMinimumHeight());
        if (this.mShowMotoQSCarrierGroup && !this.mIsPrcCustom) {
            this.mClockIconsView.getLayoutParams().height += this.mMotoQSCarriersContainer.getMinimumHeight();
        }
        View view2 = this.mClockIconsView;
        view2.setLayoutParams(view2.getLayoutParams());
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (this.mQsDisabled) {
            layoutParams.height = this.mClockIconsView.getLayoutParams().height;
        } else {
            layoutParams.height = -2;
        }
        setLayoutParams(layoutParams);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, 16842806);
        if (this.mIsPrcCustom) {
            colorAttrDefaultColor = getResources().getColor(R$color.prcQSPanelDate);
        }
        if (colorAttrDefaultColor != this.mTextColorPrimary) {
            int colorAttrDefaultColor2 = Utils.getColorAttrDefaultColor(this.mContext, 16842808);
            this.mTextColorPrimary = colorAttrDefaultColor;
            this.mClockView.setTextColor(colorAttrDefaultColor);
            StatusBarIconController.TintedIconManager tintedIconManager = this.mTintedIconManager;
            if (tintedIconManager != null) {
                tintedIconManager.setTint(colorAttrDefaultColor);
            }
            BatteryMeterView batteryMeterView = this.mBatteryRemainingIcon;
            int i = this.mTextColorPrimary;
            batteryMeterView.updateColors(i, colorAttrDefaultColor2, i);
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mHeaderQsPanel.getLayoutParams();
        marginLayoutParams.topMargin = this.mContext.getResources().getDimensionPixelSize(R$dimen.qqs_layout_margin_top);
        this.mHeaderQsPanel.setLayoutParams(marginLayoutParams);
        updateBatteryMode();
        updateHeadersPadding();
        updateAnimators();
        updateQSCarrierGroup();
        if (this.mIsPrcCustom) {
            ((DateView) this.mPrcDate).setTextColor(getResources().getColor(R$color.prcQSPanelDate));
            updateIconsForPrc();
        }
    }

    private void updateAnimators() {
        updateAlphaAnimator();
        TouchAnimator.Builder addFloat = new TouchAnimator.Builder().addFloat(this.mContainer, "translationY", 0.0f, (float) this.mTopViewMeasureHeight);
        QSExpansionPathInterpolator qSExpansionPathInterpolator = this.mQSExpansionPathInterpolator;
        this.mTranslationAnimator = addFloat.setInterpolator(qSExpansionPathInterpolator != null ? qSExpansionPathInterpolator.getYInterpolator() : null).build();
    }

    private void updateAlphaAnimator() {
        TouchAnimator.Builder listener = new TouchAnimator.Builder().addFloat(this.mSecurityHeaderView, "alpha", 0.0f, 1.0f).addFloat(this.mClockView, "alpha", 0.0f, 1.0f).setListener(new TouchAnimator.ListenerAdapter() {
            public void onAnimationAtEnd() {
                super.onAnimationAtEnd();
                if (!QuickStatusBarHeader.this.mIsSingleCarrier && QuickStatusBarHeader.this.mRssiIgnoredSlots != null) {
                    QuickStatusBarHeader.this.mIconContainer.addIgnoredSlots(QuickStatusBarHeader.this.mRssiIgnoredSlots);
                }
            }

            public void onAnimationStarted() {
                QuickStatusBarHeader.this.setSeparatorVisibility(false);
                if (!QuickStatusBarHeader.this.mIsSingleCarrier && QuickStatusBarHeader.this.mRssiIgnoredSlots != null) {
                    QuickStatusBarHeader.this.mIconContainer.addIgnoredSlots(QuickStatusBarHeader.this.mRssiIgnoredSlots);
                }
            }

            public void onAnimationAtStart() {
                super.onAnimationAtStart();
                QuickStatusBarHeader quickStatusBarHeader = QuickStatusBarHeader.this;
                quickStatusBarHeader.setSeparatorVisibility(quickStatusBarHeader.mShowClockIconsSeparator);
                if (QuickStatusBarHeader.this.mRssiIgnoredSlots != null) {
                    QuickStatusBarHeader.this.mIconContainer.removeIgnoredSlots(QuickStatusBarHeader.this.mRssiIgnoredSlots);
                }
            }
        });
        if (this.mIsPrcCustom || (MotoFeature.getInstance(getContext()).showRightSideClock() && isDisplayDensityBiggerThanDefualt() && this.mRotationOrientation == 0)) {
            listener.addFloat(this.mDateView, "alpha", 0.0f, 1.0f);
        } else {
            this.mDateView.setAlpha(1.0f);
        }
        if (this.mShowMotoQSCarrierGroup) {
            listener.addFloat(this.mMotoQSCarriersContainer, "alpha", 0.0f, 1.0f);
        } else {
            listener.addFloat(this.mQSCarriers, "alpha", 0.0f, 1.0f);
        }
        this.mAlphaAnimator = listener.build();
        if (this.mIsPrcCustom) {
            this.mPrcQsAnimator = new TouchAnimator.Builder().addFloat(this.mRightLayout, "alpha", 0.0f, 1.0f).build();
            this.mPrcNotificationAnimator = new TouchAnimator.Builder().addFloat(this.mRightLayout, "alpha", 0.0f, 0.0f, 1.0f).addFloat(this.mPrcClock, "alpha", 0.0f, 0.0f, 1.0f).addFloat(this.mPrcDate, "alpha", 0.0f, 0.0f, 1.0f).build();
        }
    }

    /* access modifiers changed from: package-private */
    public void setChipVisibility(boolean z) {
        this.mPrivacyChip.setVisibility(z ? 0 : 8);
        if (!z || this.mIsPrcCustom) {
            this.mIconsAlphaAnimator = null;
            this.mIconContainer.setAlpha(1.0f);
            this.mBatteryRemainingIcon.setAlpha(1.0f);
            return;
        }
        TouchAnimator touchAnimator = this.mIconsAlphaAnimatorFixed;
        this.mIconsAlphaAnimator = touchAnimator;
        touchAnimator.setPosition(this.mKeyguardExpansionFraction);
    }

    public void setNotificationExpansion(float f) {
        TouchAnimator touchAnimator = this.mPrcNotificationAnimator;
        if (touchAnimator != null) {
            int i = this.mHeaderStatus;
            if (i == 1 || i == 3) {
                touchAnimator.setPosition(f);
            }
        }
    }

    public void setExpanded(boolean z, QuickQSPanelController quickQSPanelController) {
        if (this.mExpanded != z) {
            this.mExpanded = z;
            quickQSPanelController.setExpanded(z);
            updateEverything();
        }
    }

    public void setExpansion(boolean z, float f, float f2) {
        int i;
        if (z) {
            f = 1.0f;
        }
        TouchAnimator touchAnimator = this.mAlphaAnimator;
        if (touchAnimator != null) {
            touchAnimator.setPosition(f);
        }
        TouchAnimator touchAnimator2 = this.mTranslationAnimator;
        if (touchAnimator2 != null) {
            touchAnimator2.setPosition(f);
        }
        TouchAnimator touchAnimator3 = this.mIconsAlphaAnimator;
        if (touchAnimator3 != null) {
            touchAnimator3.setPosition(f);
        }
        if (z) {
            setTranslationY(f2);
        } else {
            setTranslationY(0.0f);
        }
        TouchAnimator touchAnimator4 = this.mPrcQsAnimator;
        if (!(touchAnimator4 == null || (i = this.mHeaderStatus) == 1 || i == 3)) {
            touchAnimator4.setPosition(f);
        }
        this.mKeyguardExpansionFraction = f;
    }

    public void disable(int i, int i2, boolean z) {
        boolean z2 = true;
        int i3 = 0;
        if ((i2 & 1) == 0) {
            z2 = false;
        }
        if (z2 != this.mQsDisabled) {
            this.mQsDisabled = z2;
            this.mHeaderQsPanel.setDisabledByPolicy(z2);
            View view = this.mClockIconsView;
            if (this.mQsDisabled) {
                i3 = 8;
            }
            view.setVisibility(i3);
            lambda$updateAirplaneMode$2();
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        Pair<Integer, Integer> cornerCutoutMargins = StatusBarWindowView.cornerCutoutMargins(displayCutout, getDisplay());
        Pair<Integer, Integer> paddingNeededForCutoutAndRoundedCorner = StatusBarWindowView.paddingNeededForCutoutAndRoundedCorner(displayCutout, cornerCutoutMargins, -1);
        int i = 0;
        this.mDatePrivacyView.setPadding(((Integer) paddingNeededForCutoutAndRoundedCorner.first).intValue(), 0, ((Integer) paddingNeededForCutoutAndRoundedCorner.second).intValue(), 0);
        this.mClockIconsView.setPadding(((Integer) paddingNeededForCutoutAndRoundedCorner.first).intValue(), 0, ((Integer) paddingNeededForCutoutAndRoundedCorner.second).intValue(), 0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mDatePrivacySeparator.getLayoutParams();
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mClockIconsSeparator.getLayoutParams();
        boolean z = cornerCutoutMargins != null && (((Integer) cornerCutoutMargins.first).intValue() == 0 || ((Integer) cornerCutoutMargins.second).intValue() == 0);
        if (displayCutout != null) {
            Rect boundingRectTop = displayCutout.getBoundingRectTop();
            if (boundingRectTop.isEmpty() || z || MotoFeature.getInstance(getContext()).showRightSideClock()) {
                layoutParams.width = 0;
                this.mDatePrivacySeparator.setVisibility(8);
                layoutParams2.width = 0;
                setSeparatorVisibility(false);
                this.mShowClockIconsSeparator = false;
                this.mHasCenterCutout = false;
            } else {
                layoutParams.width = boundingRectTop.width();
                this.mDatePrivacySeparator.setVisibility(0);
                layoutParams2.width = boundingRectTop.width();
                this.mShowClockIconsSeparator = true;
                setSeparatorVisibility(this.mKeyguardExpansionFraction == 0.0f);
                this.mHasCenterCutout = true;
            }
        }
        this.mDatePrivacySeparator.setLayoutParams(layoutParams);
        this.mClockIconsSeparator.setLayoutParams(layoutParams2);
        this.mCutOutPaddingLeft = ((Integer) paddingNeededForCutoutAndRoundedCorner.first).intValue();
        this.mCutOutPaddingRight = ((Integer) paddingNeededForCutoutAndRoundedCorner.second).intValue();
        if (displayCutout != null) {
            i = displayCutout.getWaterfallInsets().top;
        }
        this.mWaterfallTopInset = i;
        updateBatteryMode();
        updateHeadersPadding();
        updateQSCarrierGroup();
        return super.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: private */
    public void setSeparatorVisibility(boolean z) {
        int i = 8;
        int i2 = 0;
        if (this.mClockIconsSeparator.getVisibility() != (z ? 0 : 8)) {
            this.mClockIconsSeparator.setVisibility(z ? 0 : 8);
            if (this.mShowMotoQSCarrierGroup) {
                View view = this.mMotoQSCarriersContainer;
                if (!z) {
                    i = 0;
                }
                view.setVisibility(i);
            } else {
                View view2 = this.mQSCarriers;
                if (!z) {
                    i = 0;
                }
                view2.setVisibility(i);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mClockView.getLayoutParams();
            layoutParams.width = z ? 0 : -2;
            float f = 1.0f;
            layoutParams.weight = z ? 1.0f : 0.0f;
            this.mClockView.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mRightLayout.getLayoutParams();
            if (!z && !this.mShowMotoQSCarrierGroup) {
                i2 = -2;
            }
            layoutParams2.width = i2;
            if (!z && !this.mShowMotoQSCarrierGroup) {
                f = 0.0f;
            }
            layoutParams2.weight = f;
            this.mRightLayout.setLayoutParams(layoutParams2);
        }
    }

    private void updateHeadersPadding() {
        if (this.mMarginLeftForCamera > 0) {
            int i = this.mRotationOrientation;
            if (i == 1 || i == 3) {
                setContentMargins(this.mDatePrivacyView, 0, 0);
            } else {
                setContentMargins(this.mDatePrivacyView, this.mMarginLeftForCamera - this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_padding_start), 0);
            }
        }
        setContentMargins(this.mClockIconsView, 0, 0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        int i2 = layoutParams.leftMargin;
        int i3 = layoutParams.rightMargin;
        int i4 = this.mCutOutPaddingLeft;
        int max = i4 > 0 ? Math.max(Math.max(i4, this.mRoundedCornerPadding) - i2, 0) : 0;
        int i5 = this.mCutOutPaddingRight;
        int max2 = i5 > 0 ? Math.max(Math.max(i5, this.mRoundedCornerPadding) - i3, 0) : 0;
        this.mDatePrivacyView.setPadding(max, this.mWaterfallTopInset, max2, 0);
        this.mClockIconsView.setPadding(max, this.mWaterfallTopInset, max2, 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEverything$0() {
        setClickable(!this.mExpanded);
    }

    public void updateEverything() {
        post(new QuickStatusBarHeader$$ExternalSyntheticLambda1(this));
    }

    public void setCallback(QSDetail.Callback callback) {
        this.mHeaderQsPanel.setCallback(callback);
    }

    private void setContentMargins(View view, int i, int i2) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.setMarginStart(i);
        marginLayoutParams.setMarginEnd(i2);
        view.setLayoutParams(marginLayoutParams);
    }

    public void setExpandedScrollAmount(int i) {
        this.mClockIconsView.setScrollY(i);
        this.mDatePrivacyView.setScrollY(i);
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

    public void updateHeaderStatus(int i) {
        if (this.mIsPrcCustom) {
            this.mHeaderStatus = i;
            updateHeaderUI();
        }
    }

    private void updateHeaderUI() {
        int i = 8;
        this.mClockIconsSeparator.setVisibility(8);
        int i2 = this.mHeaderStatus;
        float f = 1.0f;
        if (i2 == 1 || i2 == 3) {
            this.mDatePrivacyView.setVisibility(8);
            this.mClockView.setVisibility(8);
            this.mDateAndTimeView.setVisibility(0);
            this.mQSCarriers.setVisibility(8);
            this.mMotoQSCarriersContainer.setVisibility(8);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mRightLayout.getLayoutParams();
            layoutParams.width = 0;
            layoutParams.weight = 1.0f;
            this.mRightLayout.setLayoutParams(layoutParams);
            updateLayout(true);
            return;
        }
        this.mDatePrivacyView.setVisibility(0);
        this.mClockView.setVisibility(0);
        this.mDateAndTimeView.setVisibility(8);
        this.mQSCarriers.setVisibility(this.mShowMotoQSCarrierGroup ? 8 : 0);
        View view = this.mMotoQSCarriersContainer;
        if (this.mShowMotoQSCarrierGroup) {
            i = 0;
        }
        view.setVisibility(i);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mClockView.getLayoutParams();
        int i3 = -2;
        layoutParams2.width = -2;
        layoutParams2.weight = 0.0f;
        this.mClockView.setLayoutParams(layoutParams2);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mRightLayout.getLayoutParams();
        boolean z = this.mShowMotoQSCarrierGroup;
        if (z) {
            i3 = 0;
        }
        layoutParams3.width = i3;
        if (!z) {
            f = 0.0f;
        }
        layoutParams3.weight = f;
        this.mRightLayout.setLayoutParams(layoutParams3);
        updateLayout(false);
    }

    private void updateLayout(boolean z) {
        View view = this.mContainer;
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            if (z) {
                layoutParams.topMargin = getResources().getDimensionPixelSize(17105483);
            } else {
                layoutParams.topMargin = 0;
            }
            this.mContainer.setLayoutParams(layoutParams);
        }
    }

    private void updateQSCarrierGroup() {
        if (this.mIsPrcCustom) {
            updateHeaderUI();
            return;
        }
        int i = 8;
        int i2 = 0;
        this.mQSCarriers.setVisibility(this.mShowMotoQSCarrierGroup ? 8 : 0);
        View view = this.mMotoQSCarriersContainer;
        if (this.mShowMotoQSCarrierGroup) {
            i = 0;
        }
        view.setVisibility(i);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mRightLayout.getLayoutParams();
        boolean z = this.mShowMotoQSCarrierGroup;
        if (!z) {
            i2 = -2;
        }
        layoutParams.width = i2;
        layoutParams.weight = z ? 1.0f : 0.0f;
        this.mRightLayout.setLayoutParams(layoutParams);
    }

    public void updateActiveSubsCount(int i) {
        this.mActiveSubsCount = i;
        if (needUpdateTopPaddingForCarrierGroup()) {
            post(new QuickStatusBarHeader$$ExternalSyntheticLambda2(this));
        }
    }

    public void updateAirplaneMode(boolean z) {
        this.mAirplaneMode = z;
        if (needUpdateTopPaddingForCarrierGroup()) {
            post(new QuickStatusBarHeader$$ExternalSyntheticLambda0(this));
        }
    }

    private boolean needUpdateTopPaddingForCarrierGroup() {
        boolean z = this.mRotationOrientation == 0 && this.mActiveSubsCount == 2 && !this.mAirplaneMode;
        if (this.mShowMotoQSCarrierGroup == z) {
            return false;
        }
        this.mShowMotoQSCarrierGroup = z;
        return true;
    }
}
