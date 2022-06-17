package com.android.systemui.controls.management;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsEditingActivity.kt */
public final class ControlsEditingActivity$setUpList$1$1 extends GridLayoutManager {
    ControlsEditingActivity$setUpList$1$1(Context context) {
        super(context, 2);
    }

    public int getRowCountForAccessibility(@NotNull RecyclerView.Recycler recycler, @NotNull RecyclerView.State state) {
        Intrinsics.checkNotNullParameter(recycler, "recycler");
        Intrinsics.checkNotNullParameter(state, "state");
        int rowCountForAccessibility = super.getRowCountForAccessibility(recycler, state);
        return rowCountForAccessibility > 0 ? rowCountForAccessibility - 1 : rowCountForAccessibility;
    }
}
