package com.motorola.systemui.cli.navgesture.recents;

import java.util.function.Consumer;

public final /* synthetic */ class RecentTasksList$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ RecentTasksList f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Consumer f$3;

    public /* synthetic */ RecentTasksList$$ExternalSyntheticLambda2(RecentTasksList recentTasksList, int i, boolean z, Consumer consumer) {
        this.f$0 = recentTasksList;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = consumer;
    }

    public final void run() {
        this.f$0.lambda$getTasks$4(this.f$1, this.f$2, this.f$3);
    }
}
