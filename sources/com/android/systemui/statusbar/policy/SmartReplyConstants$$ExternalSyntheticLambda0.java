package com.android.systemui.statusbar.policy;

import java.util.concurrent.Executor;

public final /* synthetic */ class SmartReplyConstants$$ExternalSyntheticLambda0 implements Executor {
    public final /* synthetic */ SmartReplyConstants f$0;

    public /* synthetic */ SmartReplyConstants$$ExternalSyntheticLambda0(SmartReplyConstants smartReplyConstants) {
        this.f$0 = smartReplyConstants;
    }

    public final void execute(Runnable runnable) {
        this.f$0.postToHandler(runnable);
    }
}
