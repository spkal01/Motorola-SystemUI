package com.android.systemui.p006qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import com.android.systemui.p006qs.QSPanelControllerBase;
import com.android.systemui.plugins.p005qs.QSTileView;

/* renamed from: com.android.systemui.qs.PrcUnfixedTileLayout */
public class PrcUnfixedTileLayout extends TileLayout {
    static final boolean DEBUG = (!Build.IS_USER);
    private int mNavBarHeight;

    public PrcUnfixedTileLayout(Context context) {
        super(context);
    }

    public PrcUnfixedTileLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setClipChildren(false);
        setClipToPadding(false);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        setMaxColumns(100);
    }

    public boolean updateResources() {
        Resources resources = this.mContext.getResources();
        this.mNavBarHeight = resources.getDimensionPixelSize(17105362);
        this.mResourceColumns = Math.max(1, resources.getInteger(R$integer.zz_moto_prc_qs_num_columns));
        this.mCellMarginHorizontal = resources.getDimensionPixelSize(R$dimen.zz_moto_qs_tile_margin_horizontal);
        this.mCellMarginVertical = resources.getDimensionPixelSize(R$dimen.zz_moto_qs_tile_margin_vertical);
        this.mSidePadding = useSidePadding() ? this.mCellMarginHorizontal / 2 : 0;
        this.mMaxAllowedRows = 30;
        if (!updateColumns()) {
            return false;
        }
        requestLayout();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateResources();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = this.mRecords.size();
        int size2 = View.MeasureSpec.getSize(i);
        int paddingStart = (size2 - getPaddingStart()) - getPaddingEnd();
        if (View.MeasureSpec.getMode(i2) == 0) {
            int i3 = this.mColumns;
            this.mRows = (((size + 2) + i3) - 1) / i3;
        }
        int i4 = this.mColumns;
        int i5 = ((paddingStart - (this.mCellMarginHorizontal * (i4 - 1))) - (this.mSidePadding * 2)) / i4;
        this.mCellWidth = i5;
        this.mMaxCellHeight = i5;
        updateMaxRows(10000, this.mRecords.size());
        int exactly = TileLayout.exactly(getCellHeight());
        int i6 = 0;
        View view = this;
        for (int i7 = 0; i7 < this.mRecords.size(); i7++) {
            QSPanelControllerBase.TileRecord tileRecord = this.mRecords.get(i7);
            if (tileRecord.tileView.getVisibility() != 8) {
                if (i7 < 2) {
                    tileRecord.tileView.updateBigTypePrc(true);
                    tileRecord.tileView.measure(TileLayout.exactly((this.mCellWidth * 2) + this.mCellMarginHorizontal), exactly);
                } else {
                    tileRecord.tileView.updateBigTypePrc(false);
                    tileRecord.tileView.measure(TileLayout.exactly(this.mCellWidth), exactly);
                }
                view = tileRecord.tileView.updateAccessibilityOrder(view);
                this.mCellHeight = tileRecord.tileView.getMeasuredHeight();
            }
        }
        int i8 = ((this.mCellHeight + this.mCellMarginVertical) * this.mRows) + this.mNavBarHeight;
        if (i8 >= 0) {
            i6 = i8;
        }
        setMeasuredDimension(size2, i6);
    }

    public boolean updateColumns() {
        int i = this.mColumns;
        int min = Math.min(this.mResourceColumns, this.mMaxColumns);
        this.mColumns = min;
        return i != min;
    }

    /* access modifiers changed from: protected */
    public int getBigTileColumnStart(int i) {
        return getPaddingStart() + this.mSidePadding + (i * (this.mCellWidth + this.mCellMarginHorizontal));
    }

    /* access modifiers changed from: protected */
    public void layoutTileRecords(int i) {
        boolean z = getLayoutDirection() == 1;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i2 < i) {
            if (i3 == this.mColumns) {
                i4++;
                i3 = 0;
            }
            if (i4 != 0 || i2 >= 2) {
                QSPanelControllerBase.TileRecord tileRecord = this.mRecords.get(i2);
                int rowTop = getRowTop(i4);
                int columnStart = getColumnStart(z ? (this.mColumns - i3) - 1 : i3);
                QSTileView qSTileView = tileRecord.tileView;
                qSTileView.layout(columnStart, rowTop, this.mCellWidth + columnStart, qSTileView.getMeasuredHeight() + rowTop);
            } else {
                QSPanelControllerBase.TileRecord tileRecord2 = this.mRecords.get(i2);
                int rowTop2 = getRowTop(i4);
                int bigTileColumnStart = getBigTileColumnStart(z ? (this.mColumns - i3) - 1 : i3);
                int i5 = (this.mCellWidth * 2) + bigTileColumnStart + this.mCellMarginHorizontal;
                QSTileView qSTileView2 = tileRecord2.tileView;
                qSTileView2.layout(bigTileColumnStart, rowTop2, i5, qSTileView2.getMeasuredHeight() + rowTop2);
                i3++;
            }
            i2++;
            i3++;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        layoutTileRecords(this.mRecords.size());
    }

    public boolean updateMaxRows(int i, int i2) {
        int i3 = i + this.mCellMarginVertical;
        int i4 = this.mRows;
        int cellHeight = i3 / (getCellHeight() + this.mCellMarginVertical);
        this.mRows = cellHeight;
        int i5 = this.mMinRows;
        if (cellHeight < i5) {
            this.mRows = i5;
        } else {
            int i6 = this.mMaxAllowedRows;
            if (cellHeight >= i6) {
                this.mRows = i6;
            }
        }
        int i7 = this.mRows;
        int i8 = i2 + 2;
        int i9 = this.mColumns;
        if (i7 > ((i8 + i9) - 1) / i9) {
            this.mRows = ((i8 + i9) - 1) / i9;
        }
        if (i4 != this.mRows) {
            return true;
        }
        return false;
    }

    public void addTile(QSPanelControllerBase.TileRecord tileRecord) {
        this.mRecords.add(tileRecord);
        tileRecord.tile.setListening(this, this.mListening);
        addTileView(tileRecord);
    }

    public void setListening(boolean z, UiEventLogger uiEventLogger) {
        super.setListening(z, uiEventLogger);
    }

    public void setExpansion(float f, float f2) {
        if (f <= 0.0f || f >= 1.0f) {
            boolean z = f == 1.0f || f2 < 0.0f;
            setImportantForAccessibility(4);
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setSelected(z);
            }
            setImportantForAccessibility(0);
        }
    }

    public int getOneCellHeight() {
        return this.mCellHeight;
    }

    public int getOneCellMarginVertical() {
        return this.mCellMarginVertical;
    }

    public int getTileLayoutHeight() {
        return (this.mCellHeight + this.mCellMarginVertical) * this.mRows;
    }
}
