package com.android.systemui.controls.p004ui;

import android.view.View;

/* renamed from: com.android.systemui.controls.ui.StatusBehavior$bind$msg$1 */
/* compiled from: StatusBehavior.kt */
final class StatusBehavior$bind$msg$1 implements View.OnClickListener {
    final /* synthetic */ ControlWithState $cws;
    final /* synthetic */ StatusBehavior this$0;

    StatusBehavior$bind$msg$1(StatusBehavior statusBehavior, ControlWithState controlWithState) {
        this.this$0 = statusBehavior;
        this.$cws = controlWithState;
    }

    public final void onClick(View view) {
        StatusBehavior statusBehavior = this.this$0;
        statusBehavior.showNotFoundDialog(statusBehavior.getCvh(), this.$cws);
    }
}
