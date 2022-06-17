package com.android.p011wm.shell.common;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.common.ExecutorUtils$$ExternalSyntheticLambda0 */
public final /* synthetic */ class ExecutorUtils$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ RemoteCallable f$1;

    public /* synthetic */ ExecutorUtils$$ExternalSyntheticLambda0(Consumer consumer, RemoteCallable remoteCallable) {
        this.f$0 = consumer;
        this.f$1 = remoteCallable;
    }

    public final void run() {
        this.f$0.accept(this.f$1);
    }
}
