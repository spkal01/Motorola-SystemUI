package com.android.p011wm.shell.legacysplitscreen;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$SplitScreenImpl$$ExternalSyntheticLambda7 */
public final /* synthetic */ class C2328x586845b1 implements Runnable {
    public final /* synthetic */ LegacySplitScreenController.SplitScreenImpl f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ C2328x586845b1(LegacySplitScreenController.SplitScreenImpl splitScreenImpl, boolean[] zArr) {
        this.f$0 = splitScreenImpl;
        this.f$1 = zArr;
    }

    public final void run() {
        this.f$0.lambda$isDividerVisible$0(this.f$1);
    }
}
