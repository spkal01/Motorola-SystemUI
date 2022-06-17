package com.android.systemui.controls.p004ui;

import android.content.DialogInterface;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$showDetail$1$1$1$1$1 */
/* compiled from: ControlActionCoordinatorImpl.kt */
final class ControlActionCoordinatorImpl$showDetail$1$1$1$1$1 implements DialogInterface.OnDismissListener {
    final /* synthetic */ ControlActionCoordinatorImpl this$0;

    ControlActionCoordinatorImpl$showDetail$1$1$1$1$1(ControlActionCoordinatorImpl controlActionCoordinatorImpl) {
        this.this$0 = controlActionCoordinatorImpl;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.this$0.dialog = null;
    }
}
