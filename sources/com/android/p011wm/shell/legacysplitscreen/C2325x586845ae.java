package com.android.p011wm.shell.legacysplitscreen;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$SplitScreenImpl$$ExternalSyntheticLambda4 */
public final /* synthetic */ class C2325x586845ae implements Runnable {
    public final /* synthetic */ LegacySplitScreenController.SplitScreenImpl f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ C2325x586845ae(LegacySplitScreenController.SplitScreenImpl splitScreenImpl, boolean z) {
        this.f$0 = splitScreenImpl;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.lambda$onKeyguardVisibilityChanged$1(this.f$1);
    }
}
