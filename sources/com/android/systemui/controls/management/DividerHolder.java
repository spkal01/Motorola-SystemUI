package com.android.systemui.controls.management;

import android.view.View;
import com.android.systemui.R$id;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlAdapter.kt */
final class DividerHolder extends Holder {
    @NotNull
    private final View divider;
    @NotNull
    private final View frame;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DividerHolder(@NotNull View view) {
        super(view, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(view, "view");
        View requireViewById = this.itemView.requireViewById(R$id.frame);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "itemView.requireViewById(R.id.frame)");
        this.frame = requireViewById;
        View requireViewById2 = this.itemView.requireViewById(R$id.divider);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "itemView.requireViewById(R.id.divider)");
        this.divider = requireViewById2;
    }

    public void bindData(@NotNull ElementWrapper elementWrapper) {
        Intrinsics.checkNotNullParameter(elementWrapper, "wrapper");
        DividerWrapper dividerWrapper = (DividerWrapper) elementWrapper;
        int i = 0;
        this.frame.setVisibility(dividerWrapper.getShowNone() ? 0 : 8);
        View view = this.divider;
        if (!dividerWrapper.getShowDivider()) {
            i = 8;
        }
        view.setVisibility(i);
    }
}
