package com.android.p011wm.shell.common;

import com.android.p011wm.shell.common.DisplayImeController;

/* renamed from: com.android.wm.shell.common.DisplayImeController$PerDisplay$DisplayWindowInsetsControllerImpl$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C2292xb1a3ff7e implements Runnable {
    public final /* synthetic */ DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ C2292xb1a3ff7e(DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl displayWindowInsetsControllerImpl, int i, boolean z) {
        this.f$0 = displayWindowInsetsControllerImpl;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$showInsets$3(this.f$1, this.f$2);
    }
}
