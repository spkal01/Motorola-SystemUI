package com.android.systemui.screenshot;

import android.view.ScrollCaptureResponse;
import androidx.concurrent.futures.CallbackToFutureAdapter;

public final /* synthetic */ class ScrollCaptureController$$ExternalSyntheticLambda0 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ScrollCaptureController f$0;
    public final /* synthetic */ ScrollCaptureResponse f$1;

    public /* synthetic */ ScrollCaptureController$$ExternalSyntheticLambda0(ScrollCaptureController scrollCaptureController, ScrollCaptureResponse scrollCaptureResponse) {
        this.f$0 = scrollCaptureController;
        this.f$1 = scrollCaptureResponse;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.lambda$run$1(this.f$1, completer);
    }
}
