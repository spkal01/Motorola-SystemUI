package com.android.p011wm.shell.pip.phone;

import com.android.p011wm.shell.pip.phone.PipController;

/* renamed from: com.android.wm.shell.pip.phone.PipController$PipImpl$$ExternalSyntheticLambda9 */
public final /* synthetic */ class PipController$PipImpl$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ PipController.PipImpl f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ PipController$PipImpl$$ExternalSyntheticLambda9(PipController.PipImpl pipImpl, boolean z, int i) {
        this.f$0 = pipImpl;
        this.f$1 = z;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$onSystemUiStateChanged$5(this.f$1, this.f$2);
    }
}
