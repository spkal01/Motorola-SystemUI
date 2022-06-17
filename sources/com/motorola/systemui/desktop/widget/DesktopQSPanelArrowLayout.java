package com.motorola.systemui.desktop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.R$id;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.p006qs.PagedTileLayout;

public class DesktopQSPanelArrowLayout extends LinearLayout {
    private ImageView mLeftArrow;
    private int mNumPages;
    private int mPosition = -1;
    private PagedTileLayout mQSTileLayout;
    private ImageView mRightArrow;

    public DesktopQSPanelArrowLayout(Context context) {
        super(context);
    }

    public DesktopQSPanelArrowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DesktopQSPanelArrowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DesktopQSPanelArrowLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ImageView imageView = (ImageView) findViewById(R$id.left_arrow);
        this.mLeftArrow = imageView;
        imageView.setOnClickListener(new DesktopQSPanelArrowLayout$$ExternalSyntheticLambda0(this));
        ImageView imageView2 = (ImageView) findViewById(R$id.right_arrow);
        this.mRightArrow = imageView2;
        imageView2.setOnClickListener(new DesktopQSPanelArrowLayout$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$0(View view) {
        handleLeftArrow();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$1(View view) {
        handleRightArrow();
    }

    private void handleRightArrow() {
        int i;
        PagedTileLayout pagedTileLayout = this.mQSTileLayout;
        if (pagedTileLayout != null && (i = this.mPosition) >= 0 && i < this.mNumPages - 1) {
            pagedTileLayout.setCurrentItem(i + 1, true);
        }
    }

    private void handleLeftArrow() {
        int i;
        PagedTileLayout pagedTileLayout = this.mQSTileLayout;
        if (pagedTileLayout != null && (i = this.mPosition) > 0) {
            pagedTileLayout.setCurrentItem(i - 1, true);
        }
    }

    public void setPageTileLayout(PagedTileLayout pagedTileLayout) {
        this.mQSTileLayout = pagedTileLayout;
    }

    public void setNumPages(int i) {
        this.mNumPages = i;
        setVisibility((i <= 1 || !DesktopFeature.isDesktopDisplayContext(getContext())) ? 8 : 0);
    }

    public void setPosition(int i) {
        this.mPosition = i;
        this.mLeftArrow.setEnabled(true);
        this.mRightArrow.setEnabled(true);
        if (i == 0) {
            this.mLeftArrow.setEnabled(false);
        } else if (i == this.mNumPages - 1) {
            this.mRightArrow.setEnabled(false);
        }
    }
}
