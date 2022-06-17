package com.android.systemui.controls.management;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlAdapter.kt */
public final class MarginItemDecorator extends RecyclerView.ItemDecoration {
    private final int sideMargins;
    private final int topMargin;

    public MarginItemDecorator(int i, int i2) {
        this.topMargin = i;
        this.sideMargins = i2;
    }

    public void getItemOffsets(@NotNull Rect rect, @NotNull View view, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.State state) {
        Intrinsics.checkNotNullParameter(rect, "outRect");
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(recyclerView, "parent");
        Intrinsics.checkNotNullParameter(state, "state");
        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
        if (childAdapterPosition != -1) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            Integer valueOf = adapter == null ? null : Integer.valueOf(adapter.getItemViewType(childAdapterPosition));
            if (valueOf != null && valueOf.intValue() == 1) {
                rect.top = this.topMargin * 2;
                int i = this.sideMargins;
                rect.left = i;
                rect.right = i;
                rect.bottom = 0;
            } else if (valueOf != null && valueOf.intValue() == 0 && childAdapterPosition == 0) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
                rect.top = -((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
                rect.left = 0;
                rect.right = 0;
                rect.bottom = 0;
            }
        }
    }
}
