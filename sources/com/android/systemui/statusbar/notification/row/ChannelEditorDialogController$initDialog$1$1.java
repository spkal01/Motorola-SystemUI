package com.android.systemui.statusbar.notification.row;

import android.content.DialogInterface;

/* compiled from: ChannelEditorDialogController.kt */
final class ChannelEditorDialogController$initDialog$1$1 implements DialogInterface.OnDismissListener {
    final /* synthetic */ ChannelEditorDialogController this$0;

    ChannelEditorDialogController$initDialog$1$1(ChannelEditorDialogController channelEditorDialogController) {
        this.this$0 = channelEditorDialogController;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        OnChannelEditorDialogFinishedListener onFinishListener = this.this$0.getOnFinishListener();
        if (onFinishListener != null) {
            onFinishListener.onChannelEditorDialogFinished();
        }
    }
}
