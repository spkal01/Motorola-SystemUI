package com.android.systemui.controls.p004ui;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.controls.ui.ControlViewHolder$setErrorStatus$1 */
/* compiled from: ControlViewHolder.kt */
final class ControlViewHolder$setErrorStatus$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ String $text;
    final /* synthetic */ ControlViewHolder this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ControlViewHolder$setErrorStatus$1(ControlViewHolder controlViewHolder, String str) {
        super(0);
        this.this$0 = controlViewHolder;
        this.$text = str;
    }

    public final void invoke() {
        ControlViewHolder controlViewHolder = this.this$0;
        String str = this.$text;
        Intrinsics.checkNotNullExpressionValue(str, "text");
        controlViewHolder.setStatusText(str, true);
    }
}
