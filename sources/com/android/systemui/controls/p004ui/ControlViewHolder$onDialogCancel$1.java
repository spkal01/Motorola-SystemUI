package com.android.systemui.controls.p004ui;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.controls.ui.ControlViewHolder$onDialogCancel$1 */
/* compiled from: ControlViewHolder.kt */
final class ControlViewHolder$onDialogCancel$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ ControlViewHolder this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ControlViewHolder$onDialogCancel$1(ControlViewHolder controlViewHolder) {
        super(0);
        this.this$0 = controlViewHolder;
    }

    public final void invoke() {
        this.this$0.lastChallengeDialog = null;
    }
}
