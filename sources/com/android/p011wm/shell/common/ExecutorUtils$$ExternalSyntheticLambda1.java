package com.android.p011wm.shell.common;

import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.common.ExecutorUtils$$ExternalSyntheticLambda1 */
public final /* synthetic */ class ExecutorUtils$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ RemoteCallable f$1;

    public /* synthetic */ ExecutorUtils$$ExternalSyntheticLambda1(Consumer consumer, RemoteCallable remoteCallable) {
        this.f$0 = consumer;
        this.f$1 = remoteCallable;
    }

    public final void run() {
        this.f$0.accept(this.f$1);
    }
}
