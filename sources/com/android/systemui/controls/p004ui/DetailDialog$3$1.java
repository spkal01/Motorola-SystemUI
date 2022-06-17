package com.android.systemui.controls.p004ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.DetailDialog$3$1 */
/* compiled from: DetailDialog.kt */
final class DetailDialog$3$1 implements View.OnClickListener {
    final /* synthetic */ ImageView $this_apply;
    final /* synthetic */ DetailDialog this$0;

    DetailDialog$3$1(DetailDialog detailDialog, ImageView imageView) {
        this.this$0 = detailDialog;
        this.$this_apply = imageView;
    }

    public final void onClick(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "v");
        this.this$0.removeDetailTask();
        this.this$0.dismiss();
        this.$this_apply.getContext().sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        this.this$0.getPendingIntent().send();
    }
}
