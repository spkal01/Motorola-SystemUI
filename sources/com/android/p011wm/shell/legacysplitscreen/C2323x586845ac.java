package com.android.p011wm.shell.legacysplitscreen;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import java.util.function.BiConsumer;

/* renamed from: com.android.wm.shell.legacysplitscreen.LegacySplitScreenController$SplitScreenImpl$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C2323x586845ac implements Runnable {
    public final /* synthetic */ LegacySplitScreenController.SplitScreenImpl f$0;
    public final /* synthetic */ BiConsumer f$1;

    public /* synthetic */ C2323x586845ac(LegacySplitScreenController.SplitScreenImpl splitScreenImpl, BiConsumer biConsumer) {
        this.f$0 = splitScreenImpl;
        this.f$1 = biConsumer;
    }

    public final void run() {
        this.f$0.lambda$registerBoundsChangeListener$7(this.f$1);
    }
}
