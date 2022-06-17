package com.android.systemui.wmshell;

import com.android.systemui.wmshell.BubblesManager;
import java.util.function.Consumer;

public final /* synthetic */ class BubblesManager$5$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ BubblesManager.C21965 f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ BubblesManager$5$$ExternalSyntheticLambda11(BubblesManager.C21965 r1, Consumer consumer) {
        this.f$0 = r1;
        this.f$1 = consumer;
    }

    public final void run() {
        this.f$0.lambda$isNotificationShadeExpand$0(this.f$1);
    }
}
