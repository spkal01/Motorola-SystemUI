package com.android.systemui.p006qs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.qs.SideLabelTileLayout */
/* compiled from: SideLabelTileLayout.kt */
public class SideLabelTileLayout extends TileLayout {
    /* access modifiers changed from: protected */
    public boolean useSidePadding() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SideLabelTileLayout(@NotNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    public boolean updateResources() {
        boolean updateResources = super.updateResources();
        this.mMaxAllowedRows = getContext().getResources().getInteger(R$integer.quick_settings_max_rows);
        if (DesktopFeature.isDesktopDisplayContext(getContext())) {
            this.mMaxAllowedRows = getResources().getInteger(R$integer.desktop_qs_max_rows);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getContext().getDisplay().getRealMetrics(displayMetrics);
            int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R$dimen.qs_footer_height);
            while (true) {
                int cellHeight = getCellHeight() + this.mCellMarginVertical;
                int i = this.mMaxAllowedRows;
                if ((cellHeight * i) + dimensionPixelSize <= displayMetrics.heightPixels / 2) {
                    break;
                }
                int i2 = i - 1;
                this.mMaxAllowedRows = i2;
                if (i2 <= 1) {
                    this.mMaxAllowedRows = 1;
                    break;
                }
            }
        }
        return updateResources;
    }

    public final int getPhantomTopPosition(int i) {
        return getRowTop(i / this.mColumns);
    }

    public boolean updateMaxRows(int i, int i2) {
        int i3;
        if (MotoFeature.isCliContext(this.mContext)) {
            int i4 = this.mRows;
            if (this.mCliMediaPanelVisibleState.isVisible()) {
                i3 = getResources().getInteger(R$integer.cli_small_qs_panel_rows);
            } else {
                i3 = getResources().getInteger(R$integer.cli_large_qs_panel_rows);
            }
            this.mRows = i3;
            if (i4 != i3) {
                return true;
            }
            return false;
        }
        int i5 = this.mRows;
        int i6 = this.mMaxAllowedRows;
        this.mRows = i6;
        int i7 = this.mColumns;
        if (i6 > ((i2 + i7) - 1) / i7) {
            this.mRows = ((i2 + i7) - 1) / i7;
        }
        if (i5 != this.mRows) {
            return true;
        }
        return false;
    }
}
