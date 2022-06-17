package com.android.p011wm.shell.common;

import com.android.p011wm.shell.common.DisplayImeController;

/* renamed from: com.android.wm.shell.common.DisplayImeController$PerDisplay$DisplayWindowInsetsControllerImpl$$ExternalSyntheticLambda4 */
public final /* synthetic */ class C2295xb1a3ff81 implements Runnable {
    public final /* synthetic */ DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ C2295xb1a3ff81(DisplayImeController.PerDisplay.DisplayWindowInsetsControllerImpl displayWindowInsetsControllerImpl, String str) {
        this.f$0 = displayWindowInsetsControllerImpl;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.lambda$topFocusedWindowChanged$0(this.f$1);
    }
}
