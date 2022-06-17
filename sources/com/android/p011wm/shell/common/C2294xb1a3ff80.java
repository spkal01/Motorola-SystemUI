package com.android.p011wm.shell.common;

import android.view.InsetsSourceControl;
import android.view.InsetsState;
import com.android.p011wm.shell.common.DisplayImeController;

/* renamed from: com.android.wm.shell.common.DisplayImeController$PerDisplay$DisplayWindowInsetsControllerImpl$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C2294xb1a3ff80 implements Runnable {
    public final /* synthetic */ DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl f$0;
    public final /* synthetic */ InsetsState f$1;
    public final /* synthetic */ InsetsSourceControl[] f$2;

    public /* synthetic */ C2294xb1a3ff80(DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl displayWindowInsetsControllerImpl, InsetsState insetsState, InsetsSourceControl[] insetsSourceControlArr) {
        this.f$0 = displayWindowInsetsControllerImpl;
        this.f$1 = insetsState;
        this.f$2 = insetsSourceControlArr;
    }

    public final void run() {
        this.f$0.lambda$insetsControlChanged$2(this.f$1, this.f$2);
    }
}
