package com.android.systemui.recents;

import dagger.Lazy;
import java.util.function.Consumer;

public final /* synthetic */ class OverviewProxyService$$ExternalSyntheticLambda8 implements Consumer {
    public final /* synthetic */ OverviewProxyService f$0;

    public /* synthetic */ OverviewProxyService$$ExternalSyntheticLambda8(OverviewProxyService overviewProxyService) {
        this.f$0 = overviewProxyService;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$cleanupAfterDeath$2((Lazy) obj);
    }
}
