package com.android.p011wm.shell.common;

import android.view.InsetsState;
import com.android.p011wm.shell.common.DisplayImeController;

/* renamed from: com.android.wm.shell.common.DisplayImeController$PerDisplay$DisplayWindowInsetsControllerImpl$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C2293xb1a3ff7f implements Runnable {
    public final /* synthetic */ DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl f$0;
    public final /* synthetic */ InsetsState f$1;

    public /* synthetic */ C2293xb1a3ff7f(DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl displayWindowInsetsControllerImpl, InsetsState insetsState) {
        this.f$0 = displayWindowInsetsControllerImpl;
        this.f$1 = insetsState;
    }

    public final void run() {
        this.f$0.lambda$insetsChanged$1(this.f$1);
    }
}
