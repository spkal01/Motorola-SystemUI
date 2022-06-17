package com.android.p011wm.shell.splitscreen;

import com.android.p011wm.shell.splitscreen.SplitScreenController;

/* renamed from: com.android.wm.shell.splitscreen.SplitScreenController$ISplitScreenImpl$2$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2390xfa6a0f5c implements Runnable {
    public final /* synthetic */ SplitScreenController.ISplitScreenImpl.C23892 f$0;
    public final /* synthetic */ SplitScreenController f$1;

    public /* synthetic */ C2390xfa6a0f5c(SplitScreenController.ISplitScreenImpl.C23892 r1, SplitScreenController splitScreenController) {
        this.f$0 = r1;
        this.f$1 = splitScreenController;
    }

    public final void run() {
        this.f$0.lambda$binderDied$0(this.f$1);
    }
}
