package com.android.systemui.wmshell;

import android.util.ArraySet;
import com.android.systemui.wmshell.BubblesManager;
import java.util.function.Consumer;

public final /* synthetic */ class BubblesManager$5$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BubblesManager.C21965 f$0;
    public final /* synthetic */ ArraySet f$1;
    public final /* synthetic */ Consumer f$2;

    public /* synthetic */ BubblesManager$5$$ExternalSyntheticLambda0(BubblesManager.C21965 r1, ArraySet arraySet, Consumer consumer) {
        this.f$0 = r1;
        this.f$1 = arraySet;
        this.f$2 = consumer;
    }

    public final void run() {
        this.f$0.lambda$getShouldRestoredEntries$2(this.f$1, this.f$2);
    }
}
