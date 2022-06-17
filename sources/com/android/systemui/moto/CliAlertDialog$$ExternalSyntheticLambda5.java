package com.android.systemui.moto;

import android.content.DialogInterface;
import android.view.View;

public final /* synthetic */ class CliAlertDialog$$ExternalSyntheticLambda5 implements View.OnClickListener {
    public final /* synthetic */ CliAlertDialog f$0;
    public final /* synthetic */ DialogInterface.OnClickListener f$1;

    public /* synthetic */ CliAlertDialog$$ExternalSyntheticLambda5(CliAlertDialog cliAlertDialog, DialogInterface.OnClickListener onClickListener) {
        this.f$0 = cliAlertDialog;
        this.f$1 = onClickListener;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setNegativeButton$5(this.f$1, view);
    }
}
