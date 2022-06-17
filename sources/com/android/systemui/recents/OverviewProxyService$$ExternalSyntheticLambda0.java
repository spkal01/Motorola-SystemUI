package com.android.systemui.recents;

import android.os.IBinder;

public final /* synthetic */ class OverviewProxyService$$ExternalSyntheticLambda0 implements IBinder.DeathRecipient {
    public final /* synthetic */ OverviewProxyService f$0;

    public /* synthetic */ OverviewProxyService$$ExternalSyntheticLambda0(OverviewProxyService overviewProxyService) {
        this.f$0 = overviewProxyService;
    }

    public final void binderDied() {
        this.f$0.cleanupAfterDeath();
    }
}
