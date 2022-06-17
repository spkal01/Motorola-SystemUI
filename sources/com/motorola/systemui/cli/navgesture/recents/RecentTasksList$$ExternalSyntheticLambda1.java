package com.motorola.systemui.cli.navgesture.recents;

import java.util.function.Consumer;

public final /* synthetic */ class RecentTasksList$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ RecentTasksList f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Consumer f$2;

    public /* synthetic */ RecentTasksList$$ExternalSyntheticLambda1(RecentTasksList recentTasksList, int i, Consumer consumer) {
        this.f$0 = recentTasksList;
        this.f$1 = i;
        this.f$2 = consumer;
    }

    public final void run() {
        this.f$0.lambda$getTaskKeys$1(this.f$1, this.f$2);
    }
}
