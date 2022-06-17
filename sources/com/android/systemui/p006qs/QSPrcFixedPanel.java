package com.android.systemui.p006qs;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import com.android.systemui.p006qs.QSPanelControllerBase;
import com.android.systemui.plugins.p005qs.QSTile;
import java.util.Iterator;

/* renamed from: com.android.systemui.qs.QSPrcFixedPanel */
public class QSPrcFixedPanel extends QSPanel {
    private boolean mDisabledByPolicy;
    private int mMaxTiles = Math.min(4, getResources().getInteger(R$integer.zz_moto_prc_fixed_panel_max_tiles));

    /* access modifiers changed from: protected */
    public boolean displayMediaMarginsOnMedia() {
        return false;
    }

    /* access modifiers changed from: protected */
    public String getDumpableTag() {
        return "QSPrcFixedTilePanel";
    }

    /* access modifiers changed from: protected */
    public boolean mediaNeedsTopMargin() {
        return false;
    }

    public void onTuningChanged(String str, String str2) {
    }

    public void setBrightnessView(View view) {
    }

    /* access modifiers changed from: protected */
    public void updatePadding() {
    }

    public QSPrcFixedPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public void initialize() {
        super.initialize();
    }

    public TileLayout getOrCreateTileLayout() {
        return new PrcFixedTileLayout(this.mContext);
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowDetail() {
        return !this.mExpanded;
    }

    /* access modifiers changed from: protected */
    public void drawTile(QSPanelControllerBase.TileRecord tileRecord, QSTile.State state) {
        super.drawTile(tileRecord, state);
    }

    public void setMaxTiles(int i) {
        this.mMaxTiles = Math.min(i, 4);
    }

    public int getNumFixedTiles() {
        return this.mMaxTiles;
    }

    public void setVisibility(int i) {
        if (this.mDisabledByPolicy) {
            if (getVisibility() != 8) {
                i = 8;
            } else {
                return;
            }
        }
        super.setVisibility(i);
    }

    /* access modifiers changed from: package-private */
    public void addTile(QSPanelControllerBase.TileRecord tileRecord) {
        super.addTile(tileRecord);
    }

    /* access modifiers changed from: package-private */
    public void addTileLayoutToParent() {
        QSPanel.switchToParent((View) this.mTileLayout, this, 0, (String) null);
    }

    /* renamed from: com.android.systemui.qs.QSPrcFixedPanel$PrcFixedTileLayout */
    static class PrcFixedTileLayout extends SideLabelTileLayout {
        PrcFixedTileLayout(Context context) {
            super(context, (AttributeSet) null);
            setClipChildren(false);
            setClipToPadding(false);
            setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            setMaxColumns(this.mContext.getResources().getInteger(R$integer.zz_moto_prc_fixed_qs_num_columns));
        }

        public boolean updateResources() {
            Resources resources = this.mContext.getResources();
            this.mResourceColumns = Math.max(1, resources.getInteger(R$integer.zz_moto_prc_fixed_qs_num_columns));
            this.mCellMarginHorizontal = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_fixed_qs_tile_margin_horizontal);
            this.mCellMarginVertical = resources.getDimensionPixelSize(R$dimen.zz_moto_prc_fixed_qs_tile_margin_vertical);
            this.mSidePadding = useSidePadding() ? this.mCellMarginHorizontal / 2 : 0;
            this.mMaxAllowedRows = resources.getInteger(R$integer.zz_moto_prc_fixed_qs_max_rows);
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
            this.mRecords.size();
            int size = View.MeasureSpec.getSize(i);
            int paddingStart = (size - getPaddingStart()) - getPaddingEnd();
            int size2 = (View.MeasureSpec.getSize(i2) - getPaddingTop()) - getPaddingBottom();
            View.MeasureSpec.getMode(i2);
            int i3 = this.mMaxAllowedRows;
            this.mRows = i3;
            int i4 = this.mColumns;
            this.mCellWidth = ((paddingStart - (this.mCellMarginHorizontal * (i4 - 1))) - (this.mSidePadding * 2)) / i4;
            int i5 = (size2 - (this.mCellMarginVertical * (i3 - 1))) / i3;
            this.mMaxCellHeight = i5;
            int exactly = TileLayout.exactly(i5);
            Iterator<QSPanelControllerBase.TileRecord> it = this.mRecords.iterator();
            View view = this;
            while (it.hasNext()) {
                QSPanelControllerBase.TileRecord next = it.next();
                if (next.tileView.getVisibility() != 8) {
                    next.tileView.measure(TileLayout.exactly(this.mCellWidth), exactly);
                    view = next.tileView.updateAccessibilityOrder(view);
                    this.mCellHeight = next.tileView.getMeasuredHeight();
                }
            }
            int i6 = this.mCellHeight;
            int i7 = this.mCellMarginVertical;
            int i8 = ((i6 + i7) * this.mRows) - i7;
            if (i8 < 0) {
                i8 = 0;
            }
            setMeasuredDimension(size, i8);
        }

        public void setListening(boolean z, UiEventLogger uiEventLogger) {
            super.setListening(z, uiEventLogger);
        }

        public void addTile(QSPanelControllerBase.TileRecord tileRecord) {
            super.addTile(tileRecord);
        }
    }
}
