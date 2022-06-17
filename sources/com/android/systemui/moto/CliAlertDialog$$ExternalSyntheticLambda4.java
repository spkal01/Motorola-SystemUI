package com.android.systemui.moto;

import android.content.DialogInterface;
import android.view.View;

public final /* synthetic */ class CliAlertDialog$$ExternalSyntheticLambda4 implements View.OnClickListener {
    public final /* synthetic */ CliAlertDialog f$0;
    public final /* synthetic */ DialogInterface.OnClickListener f$1;

    public /* synthetic */ CliAlertDialog$$ExternalSyntheticLambda4(CliAlertDialog cliAlertDialog, DialogInterface.OnClickListener onClickListener) {
        this.f$0 = cliAlertDialog;
        this.f$1 = onClickListener;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setPositiveButton$4(this.f$1, view);
    }
}
