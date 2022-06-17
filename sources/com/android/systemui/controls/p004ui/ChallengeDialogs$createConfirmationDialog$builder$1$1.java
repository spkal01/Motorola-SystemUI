package com.android.systemui.controls.p004ui;

import android.content.DialogInterface;
import android.service.controls.actions.ControlAction;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs$createConfirmationDialog$builder$1$1 */
/* compiled from: ChallengeDialogs.kt */
final class ChallengeDialogs$createConfirmationDialog$builder$1$1 implements DialogInterface.OnClickListener {
    final /* synthetic */ ControlViewHolder $cvh;
    final /* synthetic */ ControlAction $lastAction;

    ChallengeDialogs$createConfirmationDialog$builder$1$1(ControlViewHolder controlViewHolder, ControlAction controlAction) {
        this.$cvh = controlViewHolder;
        this.$lastAction = controlAction;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.$cvh.action(ChallengeDialogs.INSTANCE.addChallengeValue(this.$lastAction, "true"));
        dialogInterface.dismiss();
    }
}
