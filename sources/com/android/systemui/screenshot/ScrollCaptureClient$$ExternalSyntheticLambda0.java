package com.android.systemui.screenshot;

import androidx.concurrent.futures.CallbackToFutureAdapter;

public final /* synthetic */ class ScrollCaptureClient$$ExternalSyntheticLambda0 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ScrollCaptureClient f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ScrollCaptureClient$$ExternalSyntheticLambda0(ScrollCaptureClient scrollCaptureClient, int i, int i2) {
        this.f$0 = scrollCaptureClient;
        this.f$1 = i;
        this.f$2 = i2;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.lambda$request$0(this.f$1, this.f$2, completer);
    }
}
