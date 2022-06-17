package com.android.systemui.controls.management;

import android.view.View;
import android.widget.TextView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlAdapter.kt */
final class ZoneHolder extends Holder {
    @NotNull
    private final TextView zone = ((TextView) this.itemView);

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ZoneHolder(@NotNull View view) {
        super(view, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(view, "view");
    }

    public void bindData(@NotNull ElementWrapper elementWrapper) {
        Intrinsics.checkNotNullParameter(elementWrapper, "wrapper");
        this.zone.setText(((ZoneNameWrapper) elementWrapper).getZoneName());
    }
}
