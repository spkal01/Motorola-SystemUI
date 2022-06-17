package com.android.p011wm.shell.common;

import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

/* renamed from: com.android.wm.shell.common.ShellExecutor$$ExternalSyntheticLambda1 */
public final /* synthetic */ class ShellExecutor$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ Object[] f$0;
    public final /* synthetic */ Supplier f$1;
    public final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ ShellExecutor$$ExternalSyntheticLambda1(Object[] objArr, Supplier supplier, CountDownLatch countDownLatch) {
        this.f$0 = objArr;
        this.f$1 = supplier;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        ShellExecutor.lambda$executeBlockingForResult$1(this.f$0, this.f$1, this.f$2);
    }
}
