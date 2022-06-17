package com.motorola.systemui.cli.navgesture.recents;

import java.util.ArrayList;
import java.util.function.Consumer;

public final /* synthetic */ class RecentTasksList$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ RecentTasksList$$ExternalSyntheticLambda5(Consumer consumer, ArrayList arrayList) {
        this.f$0 = consumer;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.accept(this.f$1);
    }
}
