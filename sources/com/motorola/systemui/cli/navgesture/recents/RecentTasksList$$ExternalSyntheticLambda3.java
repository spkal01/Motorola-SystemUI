package com.motorola.systemui.cli.navgesture.recents;

import com.motorola.systemui.cli.navgesture.recents.RecentTasksList;
import java.util.function.Consumer;

public final /* synthetic */ class RecentTasksList$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ RecentTasksList f$0;
    public final /* synthetic */ RecentTasksList.TaskLoadResult f$1;
    public final /* synthetic */ Consumer f$2;

    public /* synthetic */ RecentTasksList$$ExternalSyntheticLambda3(RecentTasksList recentTasksList, RecentTasksList.TaskLoadResult taskLoadResult, Consumer consumer) {
        this.f$0 = recentTasksList;
        this.f$1 = taskLoadResult;
        this.f$2 = consumer;
    }

    public final void run() {
        this.f$0.lambda$getTasks$3(this.f$1, this.f$2);
    }
}
