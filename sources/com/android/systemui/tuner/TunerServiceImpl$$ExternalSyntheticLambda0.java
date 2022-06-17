package com.android.systemui.tuner;

import android.content.DialogInterface;

public final /* synthetic */ class TunerServiceImpl$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TunerServiceImpl f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ TunerServiceImpl$$ExternalSyntheticLambda0(TunerServiceImpl tunerServiceImpl, Runnable runnable) {
        this.f$0 = tunerServiceImpl;
        this.f$1 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showResetRequest$2(this.f$1, dialogInterface, i);
    }
}
