package com.android.systemui.util.concurrency;

import com.android.systemui.util.concurrency.RepeatableExecutorImpl;

public final /* synthetic */ class RepeatableExecutorImpl$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ RepeatableExecutorImpl.ExecutionToken f$0;

    public /* synthetic */ RepeatableExecutorImpl$$ExternalSyntheticLambda0(RepeatableExecutorImpl.ExecutionToken executionToken) {
        this.f$0 = executionToken;
    }

    public final void run() {
        this.f$0.cancel();
    }
}
