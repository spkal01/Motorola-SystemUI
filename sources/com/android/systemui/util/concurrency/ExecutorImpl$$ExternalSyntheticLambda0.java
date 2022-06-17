package com.android.systemui.util.concurrency;

import android.os.Handler;
import android.os.Message;

public final /* synthetic */ class ExecutorImpl$$ExternalSyntheticLambda0 implements Handler.Callback {
    public final /* synthetic */ ExecutorImpl f$0;

    public /* synthetic */ ExecutorImpl$$ExternalSyntheticLambda0(ExecutorImpl executorImpl) {
        this.f$0 = executorImpl;
    }

    public final boolean handleMessage(Message message) {
        return this.f$0.onHandleMessage(message);
    }
}
