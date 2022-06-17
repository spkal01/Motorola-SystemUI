package com.android.systemui.controls.p004ui;

import android.content.DialogInterface;

/* renamed from: com.android.systemui.controls.ui.StatusBehavior$showNotFoundDialog$builder$1$2 */
/* compiled from: StatusBehavior.kt */
final class StatusBehavior$showNotFoundDialog$builder$1$2 implements DialogInterface.OnClickListener {
    public static final StatusBehavior$showNotFoundDialog$builder$1$2 INSTANCE = new StatusBehavior$showNotFoundDialog$builder$1$2();

    StatusBehavior$showNotFoundDialog$builder$1$2() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
    }
}
