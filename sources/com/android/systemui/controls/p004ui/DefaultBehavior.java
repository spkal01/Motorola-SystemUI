package com.android.systemui.controls.p004ui;

import android.service.controls.Control;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.DefaultBehavior */
/* compiled from: DefaultBehavior.kt */
public final class DefaultBehavior implements Behavior {
    public ControlViewHolder cvh;

    @NotNull
    public final ControlViewHolder getCvh() {
        ControlViewHolder controlViewHolder = this.cvh;
        if (controlViewHolder != null) {
            return controlViewHolder;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cvh");
        throw null;
    }

    public final void setCvh(@NotNull ControlViewHolder controlViewHolder) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "<set-?>");
        this.cvh = controlViewHolder;
    }

    public void initialize(@NotNull ControlViewHolder controlViewHolder) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        setCvh(controlViewHolder);
    }

    public void bind(@NotNull ControlWithState controlWithState, int i) {
        CharSequence statusText;
        Intrinsics.checkNotNullParameter(controlWithState, "cws");
        ControlViewHolder cvh2 = getCvh();
        Control control = controlWithState.getControl();
        CharSequence charSequence = "";
        if (!(control == null || (statusText = control.getStatusText()) == null)) {
            charSequence = statusText;
        }
        ControlViewHolder.setStatusText$default(cvh2, charSequence, false, 2, (Object) null);
        ControlViewHolder.m28x1a61c355(getCvh(), false, i, false, 4, (Object) null);
    }
}
