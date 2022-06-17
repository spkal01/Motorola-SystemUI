package com.android.systemui.statusbar.phone;

import android.content.DialogInterface;
import com.android.systemui.statusbar.phone.SystemUIDialog;

public final /* synthetic */ class SystemUIDialog$$ExternalSyntheticLambda0 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ SystemUIDialog.DismissReceiver f$0;

    public /* synthetic */ SystemUIDialog$$ExternalSyntheticLambda0(SystemUIDialog.DismissReceiver dismissReceiver) {
        this.f$0 = dismissReceiver;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.unregister();
    }
}
