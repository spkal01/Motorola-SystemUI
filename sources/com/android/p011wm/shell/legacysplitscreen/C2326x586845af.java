package com.android.p011wm.shell.legacysplitscreen;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$SplitScreenImpl$$ExternalSyntheticLambda5 */
public final /* synthetic */ class C2326x586845af implements Runnable {
    public final /* synthetic */ LegacySplitScreenController.SplitScreenImpl f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ C2326x586845af(LegacySplitScreenController.SplitScreenImpl splitScreenImpl, boolean z) {
        this.f$0 = splitScreenImpl;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.lambda$setMinimized$2(this.f$1);
    }
}
