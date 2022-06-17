package com.android.keyguard;

import android.app.Presentation;
import android.content.DialogInterface;

public final /* synthetic */ class KeyguardDisplayManager$$ExternalSyntheticLambda0 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ KeyguardDisplayManager f$0;
    public final /* synthetic */ Presentation f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ KeyguardDisplayManager$$ExternalSyntheticLambda0(KeyguardDisplayManager keyguardDisplayManager, Presentation presentation, int i) {
        this.f$0 = keyguardDisplayManager;
        this.f$1 = presentation;
        this.f$2 = i;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$showPresentation$1(this.f$1, this.f$2, dialogInterface);
    }
}
