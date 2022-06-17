package com.android.p011wm.shell.common;

import com.android.p011wm.shell.common.DisplayImeController;

/* renamed from: com.android.wm.shell.common.DisplayImeController$PerDisplay$DisplayWindowInsetsControllerImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2291xb1a3ff7d implements Runnable {
    public final /* synthetic */ DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ C2291xb1a3ff7d(DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl displayWindowInsetsControllerImpl, int i, boolean z) {
        this.f$0 = displayWindowInsetsControllerImpl;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$hideInsets$4(this.f$1, this.f$2);
    }
}
