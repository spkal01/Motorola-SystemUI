package com.android.p011wm.shell.legacysplitscreen;

import android.window.WindowContainerTransaction;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenTransitions$$ExternalSyntheticLambda7 */
public final /* synthetic */ class LegacySplitScreenTransitions$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ LegacySplitScreenTransitions f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ WindowContainerTransaction f$2;

    public /* synthetic */ LegacySplitScreenTransitions$$ExternalSyntheticLambda7(LegacySplitScreenTransitions legacySplitScreenTransitions, boolean z, WindowContainerTransaction windowContainerTransaction) {
        this.f$0 = legacySplitScreenTransitions;
        this.f$1 = z;
        this.f$2 = windowContainerTransaction;
    }

    public final void run() {
        this.f$0.lambda$dismissSplit$6(this.f$1, this.f$2);
    }
}
