package com.android.p011wm.shell.legacysplitscreen;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$SplitScreenImpl$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C2324x586845ad implements Runnable {
    public final /* synthetic */ LegacySplitScreenController.SplitScreenImpl f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ C2324x586845ad(LegacySplitScreenController.SplitScreenImpl splitScreenImpl, Consumer consumer) {
        this.f$0 = splitScreenImpl;
        this.f$1 = consumer;
    }

    public final void run() {
        this.f$0.lambda$registerInSplitScreenListener$5(this.f$1);
    }
}
