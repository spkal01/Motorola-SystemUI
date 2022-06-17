package com.android.systemui.controls.p004ui;

import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.DetailDialog$2$1 */
/* compiled from: DetailDialog.kt */
final class DetailDialog$2$1 implements View.OnClickListener {
    final /* synthetic */ DetailDialog this$0;

    DetailDialog$2$1(DetailDialog detailDialog) {
        this.this$0 = detailDialog;
    }

    public final void onClick(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "$noName_0");
        this.this$0.dismiss();
    }
}
