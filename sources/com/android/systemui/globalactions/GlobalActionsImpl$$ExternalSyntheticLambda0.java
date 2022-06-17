package com.android.systemui.globalactions;

import android.app.Dialog;
import android.content.DialogInterface;
import com.android.systemui.scrim.ScrimDrawable;

public final /* synthetic */ class GlobalActionsImpl$$ExternalSyntheticLambda0 implements DialogInterface.OnShowListener {
    public final /* synthetic */ GlobalActionsImpl f$0;
    public final /* synthetic */ ScrimDrawable f$1;
    public final /* synthetic */ Dialog f$2;

    public /* synthetic */ GlobalActionsImpl$$ExternalSyntheticLambda0(GlobalActionsImpl globalActionsImpl, ScrimDrawable scrimDrawable, Dialog dialog) {
        this.f$0 = globalActionsImpl;
        this.f$1 = scrimDrawable;
        this.f$2 = dialog;
    }

    public final void onShow(DialogInterface dialogInterface) {
        this.f$0.lambda$showShutdownUi$1(this.f$1, this.f$2, dialogInterface);
    }
}
