package com.android.p011wm.shell.common;

import java.util.concurrent.CountDownLatch;

/* renamed from: com.android.wm.shell.common.ShellExecutor$$ExternalSyntheticLambda0 */
public final /* synthetic */ class ShellExecutor$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Runnable f$0;
    public final /* synthetic */ CountDownLatch f$1;

    public /* synthetic */ ShellExecutor$$ExternalSyntheticLambda0(Runnable runnable, CountDownLatch countDownLatch) {
        this.f$0 = runnable;
        this.f$1 = countDownLatch;
    }

    public final void run() {
        ShellExecutor.lambda$executeBlocking$0(this.f$0, this.f$1);
    }
}
