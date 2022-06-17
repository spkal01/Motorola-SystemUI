package com.android.systemui.controls.p004ui;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.service.controls.Control;

/* renamed from: com.android.systemui.controls.ui.StatusBehavior$showNotFoundDialog$builder$1$1 */
/* compiled from: StatusBehavior.kt */
final class StatusBehavior$showNotFoundDialog$builder$1$1 implements DialogInterface.OnClickListener {
    final /* synthetic */ ControlViewHolder $cvh;
    final /* synthetic */ ControlWithState $cws;
    final /* synthetic */ AlertDialog.Builder $this_apply;

    StatusBehavior$showNotFoundDialog$builder$1$1(ControlWithState controlWithState, AlertDialog.Builder builder, ControlViewHolder controlViewHolder) {
        this.$cws = controlWithState;
        this.$this_apply = builder;
        this.$cvh = controlViewHolder;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        try {
            Control control = this.$cws.getControl();
            if (control != null) {
                PendingIntent appIntent = control.getAppIntent();
                if (appIntent != null) {
                    appIntent.send();
                }
            }
            this.$this_apply.getContext().sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        } catch (PendingIntent.CanceledException unused) {
            this.$cvh.setErrorStatus();
        }
        dialogInterface.dismiss();
    }
}
