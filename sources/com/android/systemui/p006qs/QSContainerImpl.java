package com.android.systemui.p006qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import com.android.systemui.Dumpable;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.customize.QSCustomizer;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* renamed from: com.android.systemui.qs.QSContainerImpl */
public class QSContainerImpl extends FrameLayout implements Dumpable {
    private boolean mClippingEnabled;
    private int mContentPadding = -1;
    private int mExactlyHeight = -1;
    private int mFancyClippingBottom;
    private final Path mFancyClippingPath = new Path();
    private final float[] mFancyClippingRadii = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private int mFancyClippingTop;
    private View mFooterView;
    private QuickStatusBarHeader mHeader;
    private int mHeightOverride = -1;
    private boolean mIsPrcCustom;
    private boolean mIsShowFooterInContainer;
    private int mNavBarInset = 0;
    private int mOrientation;
    private QSPrcPanelContainer mPrcContainer;
    private QSCustomizer mQSCustomizer;
    private View mQSDetail;
    private NonInterceptingScrollView mQSPanelContainer;
    private View mQSPanelView;
    private QSPrcPanel mQSPrcpanel;
    private boolean mQsDisabled;
    private float mQsExpansion;
    private ViewGroup mSecurityFooterContainer;
    private int mSideMargins;
    private final Point mSizePoint = new Point();
    private PrcUnfixedTileLayout mUnfixedTilePanel;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean performClick() {
        return true;
    }

    public QSContainerImpl(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mQSPanelContainer = (NonInterceptingScrollView) findViewById(R$id.expanded_qs_scroll_view);
        this.mQSDetail = findViewById(R$id.qs_detail);
        this.mHeader = (QuickStatusBarHeader) findViewById(R$id.header);
        this.mQSCustomizer = (QSCustomizer) findViewById(R$id.qs_customize);
        setImportantForAccessibility(2);
        this.mPrcContainer = (QSPrcPanelContainer) findViewById(R$id.qs_prc_panel_container);
        this.mQSPanelView = findViewById(R$id.quick_settings_panel);
        boolean isCustomPanelView = MotoFeature.getInstance(getContext()).isCustomPanelView();
        this.mIsPrcCustom = isCustomPanelView;
        if (!isCustomPanelView) {
            this.mPrcContainer.setVisibility(8);
        } else {
            this.mQSPanelView.setVisibility(8);
        }
        this.mOrientation = getContext().getResources().getConfiguration().orientation;
        this.mFooterView = findViewById(R$id.qs_footer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R$id.prc_security_footer_container);
        this.mSecurityFooterContainer = viewGroup;
        if (!this.mIsPrcCustom) {
            viewGroup.setVisibility(8);
            return;
        }
        this.mQSPrcpanel = (QSPrcPanel) findViewById(R$id.qs_prc_panel);
        PrcUnfixedTileLayout prcUnfixedTileLayout = (PrcUnfixedTileLayout) findViewById(R$id.prc_unfixed_qs_panel);
        this.mUnfixedTilePanel = prcUnfixedTileLayout;
        prcUnfixedTileLayout.addOnLayoutChangeListener(new QSContainerImpl$$ExternalSyntheticLambda1(this));
        this.mSecurityFooterContainer.addOnLayoutChangeListener(new QSContainerImpl$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$1(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (this.mOrientation == 1 && i8 != i4) {
            post(new QSContainerImpl$$ExternalSyntheticLambda3(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$0() {
        updateQSFooterPrc(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$3(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (this.mOrientation == 1 && i8 != i4) {
            post(new QSContainerImpl$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$2() {
        updateQSFooterPrc(false);
    }

    private void updateSizePoint() {
        int i = getContext().getResources().getConfiguration().orientation;
        if (this.mOrientation != i) {
            this.mOrientation = i;
            this.mSizePoint.set(0, 0);
            if (this.mIsPrcCustom) {
                updateQSFooterPrc(true);
            }
        }
    }

    private boolean isShowFooterInContainerPrc() {
        Resources resources = this.mContext.getResources();
        int unFixedTileTop = this.mQSPrcpanel.getUnFixedTileTop();
        int height = this.mUnfixedTilePanel.getHeight();
        int dimensionPixelSize = resources.getDimensionPixelSize(17105483);
        int displayHeight = getDisplayHeight();
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_height);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_padding_vertical);
        if ((((displayHeight - dimensionPixelSize) - height) - unFixedTileTop) - (this.mSecurityFooterContainer.getChildCount() != 0 ? this.mSecurityFooterContainer.getHeight() : 0) < dimensionPixelSize2 + (dimensionPixelSize3 * 2) + resources.getDimensionPixelSize(17105362)) {
            return true;
        }
        return false;
    }

    public void updateQSFooterPrc(boolean z) {
        boolean isShowFooterInContainerPrc = isShowFooterInContainerPrc();
        if (this.mIsShowFooterInContainer != isShowFooterInContainerPrc || z) {
            this.mIsShowFooterInContainer = isShowFooterInContainerPrc;
            if (this.mOrientation == 2 || !isShowFooterInContainerPrc) {
                this.mPrcContainer.setQSFooterView(this.mFooterView);
            } else {
                switchQSFooterView(this.mFooterView);
            }
        }
    }

    private void switchQSFooterView(View view) {
        if (view != null) {
            this.mPrcContainer.removeQSFooter(view);
            Resources resources = this.mContext.getResources();
            int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_height);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_padding_vertical);
            int dimensionPixelSize3 = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_tile_margin_horizontal);
            int dimensionPixelSize4 = resources.getDimensionPixelSize(17105362);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
            layoutParams.topMargin = ((getDisplayHeight() - dimensionPixelSize4) - dimensionPixelSize) - dimensionPixelSize2;
            layoutParams.width = -1;
            layoutParams.height = dimensionPixelSize;
            int i = dimensionPixelSize3 / 2;
            layoutParams.setMarginStart(this.mSideMargins + i);
            layoutParams.setMarginEnd(this.mSideMargins + i);
            addView(view, layoutParams);
        }
    }

    public ViewGroup getSecurityFooterContainer() {
        return this.mSecurityFooterContainer;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mSizePoint.set(0, 0);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        this.mNavBarInset = windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom;
        if (this.mIsPrcCustom) {
            this.mNavBarInset = 0;
        }
        NonInterceptingScrollView nonInterceptingScrollView = this.mQSPanelContainer;
        nonInterceptingScrollView.setPaddingRelative(nonInterceptingScrollView.getPaddingStart(), this.mQSPanelContainer.getPaddingTop(), this.mQSPanelContainer.getPaddingEnd(), this.mNavBarInset);
        return super.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mQSPanelContainer.getLayoutParams();
        int displayHeight = ((getDisplayHeight() - marginLayoutParams.topMargin) - marginLayoutParams.bottomMargin) - getPaddingBottom();
        if (this.mIsPrcCustom && this.mOrientation == 1) {
            int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_height);
            int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R$dimen.zz_moto_prc_footer_view_padding_vertical);
            int dimensionPixelSize3 = this.mContext.getResources().getDimensionPixelSize(17105362);
            if (!this.mIsShowFooterInContainer) {
                dimensionPixelSize = 0;
                dimensionPixelSize2 = 0;
            }
            displayHeight = ((displayHeight - dimensionPixelSize) - dimensionPixelSize3) - (dimensionPixelSize2 * 2);
            this.mQSPanelContainer.setClipChildren(true);
        }
        int i3 = this.mPaddingLeft + this.mPaddingRight + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
        int childMeasureSpec = FrameLayout.getChildMeasureSpec(i, i3, marginLayoutParams.width);
        int i4 = this.mExactlyHeight;
        if (i4 > 0) {
            this.mQSPanelContainer.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(i4 - getPaddingBottom(), Integer.MIN_VALUE));
        } else {
            this.mQSPanelContainer.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(displayHeight, Integer.MIN_VALUE));
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mQSPanelContainer.getMeasuredWidth() + i3, 1073741824);
        int i5 = this.mExactlyHeight;
        if (i5 <= 0) {
            i5 = getDisplayHeight();
        }
        super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(i5, 1073741824));
        QSCustomizer qSCustomizer = this.mQSCustomizer;
        int i6 = this.mExactlyHeight;
        if (i6 <= 0) {
            i6 = getDisplayHeight();
        }
        qSCustomizer.measure(i, View.MeasureSpec.makeMeasureSpec(i6, 1073741824));
    }

    public void dispatchDraw(Canvas canvas) {
        if (!this.mFancyClippingPath.isEmpty()) {
            canvas.translate(0.0f, -getTranslationY());
            canvas.clipOutPath(this.mFancyClippingPath);
            canvas.translate(0.0f, getTranslationY());
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        if (view != this.mQSPanelContainer) {
            super.measureChildWithMargins(view, i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateExpansion();
        updateClippingPath();
    }

    public void disable(int i, int i2, boolean z) {
        boolean z2 = true;
        if ((i2 & 1) == 0) {
            z2 = false;
        }
        if (z2 != this.mQsDisabled) {
            this.mQsDisabled = z2;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateResources(QSPanelController qSPanelController, QuickStatusBarHeaderController quickStatusBarHeaderController) {
        updateSizePoint();
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(17105483);
        boolean z = false;
        if (DesktopFeature.isDesktopSupported() && DesktopFeature.isDesktopDisplayContext(getContext())) {
            dimensionPixelSize = 0;
        }
        this.mQSPanelContainer.setPaddingRelative(getPaddingStart(), dimensionPixelSize, getPaddingEnd(), getPaddingBottom());
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R$dimen.notification_side_paddings);
        int dimensionPixelSize3 = getResources().getDimensionPixelSize(R$dimen.notification_shade_content_margin_horizontal);
        if (!(dimensionPixelSize3 == this.mContentPadding && dimensionPixelSize2 == this.mSideMargins)) {
            z = true;
        }
        this.mContentPadding = dimensionPixelSize3;
        this.mSideMargins = dimensionPixelSize2;
        if (z) {
            updatePaddingsAndMargins(qSPanelController, quickStatusBarHeaderController);
        }
    }

    public void setHeightOverride(int i) {
        this.mHeightOverride = i;
        updateExpansion();
    }

    public void updateExpansion() {
        int calculateContainerHeight = calculateContainerHeight();
        int calculateContainerBottom = calculateContainerBottom();
        setBottom(getTop() + calculateContainerHeight);
        this.mQSDetail.setBottom(getTop() + calculateContainerBottom);
        this.mQSDetail.setBottom((getTop() + calculateContainerBottom) - ((ViewGroup.MarginLayoutParams) this.mQSDetail.getLayoutParams()).bottomMargin);
    }

    /* access modifiers changed from: protected */
    public int calculateContainerHeight() {
        int i = this.mHeightOverride;
        if (i == -1) {
            i = getMeasuredHeight();
        }
        if (this.mQSCustomizer.isCustomizing()) {
            return this.mQSCustomizer.getHeight();
        }
        return this.mHeader.getHeight() + Math.round(this.mQsExpansion * ((float) (i - this.mHeader.getHeight())));
    }

    /* access modifiers changed from: package-private */
    public int calculateContainerBottom() {
        int i = this.mHeightOverride;
        if (i == -1) {
            i = getMeasuredHeight();
        }
        if (this.mQSCustomizer.isCustomizing()) {
            return this.mQSCustomizer.getHeight();
        }
        return this.mHeader.getHeight() + Math.round(this.mQsExpansion * ((float) (((i + this.mQSPanelContainer.getScrollRange()) - this.mQSPanelContainer.getScrollY()) - this.mHeader.getHeight())));
    }

    public void setExpansion(float f) {
        this.mQsExpansion = f;
        this.mQSPanelContainer.setScrollingEnabled(f > 0.0f);
        updateExpansion();
    }

    private void updatePaddingsAndMargins(QSPanelController qSPanelController, QuickStatusBarHeaderController quickStatusBarHeaderController) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt != this.mQSCustomizer) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                int i2 = this.mSideMargins;
                layoutParams.rightMargin = i2;
                layoutParams.leftMargin = i2;
                if (childAt == this.mQSPanelContainer) {
                    int i3 = this.mContentPadding;
                    qSPanelController.setContentMargins(i3, i3);
                    qSPanelController.setPageMargin(this.mSideMargins);
                } else if (childAt == this.mHeader) {
                    int i4 = this.mContentPadding;
                    quickStatusBarHeaderController.setContentMargins(i4, i4);
                } else if (childAt != this.mFooterView) {
                    childAt.setPaddingRelative(this.mContentPadding, childAt.getPaddingTop(), this.mContentPadding, childAt.getPaddingBottom());
                }
            }
        }
    }

    private int getDisplayHeight() {
        if (this.mSizePoint.y == 0) {
            Display display = getDisplay();
            if (display == null) {
                display = getContext().getDisplay();
            }
            display.getRealSize(this.mSizePoint);
        }
        return this.mSizePoint.y;
    }

    public void setFancyClipping(int i, int i2, int i3, boolean z) {
        float[] fArr = this.mFancyClippingRadii;
        boolean z2 = false;
        float f = (float) i3;
        boolean z3 = true;
        if (fArr[0] != f) {
            fArr[0] = f;
            fArr[1] = f;
            fArr[2] = f;
            fArr[3] = f;
            z2 = true;
        }
        if (this.mFancyClippingTop != i) {
            this.mFancyClippingTop = i;
            z2 = true;
        }
        if (this.mFancyClippingBottom != i2) {
            this.mFancyClippingBottom = i2;
            z2 = true;
        }
        if (this.mClippingEnabled != z) {
            this.mClippingEnabled = z;
        } else {
            z3 = z2;
        }
        if (z3) {
            updateClippingPath();
        }
    }

    private void updateClippingPath() {
        this.mFancyClippingPath.reset();
        if (!this.mClippingEnabled) {
            invalidate();
            return;
        }
        this.mFancyClippingPath.addRoundRect(0.0f, (float) this.mFancyClippingTop, (float) getWidth(), (float) this.mFancyClippingBottom, this.mFancyClippingRadii, Path.Direction.CW);
        invalidate();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(getClass().getSimpleName() + " updateClippingPath: top(" + this.mFancyClippingTop + ") bottom(" + this.mFancyClippingBottom + ") mClippingEnabled(" + this.mClippingEnabled + ")");
    }

    public void setExactlyHeight(int i) {
        this.mExactlyHeight = i;
    }
}
