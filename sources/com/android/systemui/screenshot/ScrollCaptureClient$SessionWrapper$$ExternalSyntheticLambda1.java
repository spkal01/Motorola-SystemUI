package com.android.systemui.screenshot;

import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.android.systemui.screenshot.ScrollCaptureClient;

public final /* synthetic */ class ScrollCaptureClient$SessionWrapper$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ScrollCaptureClient.SessionWrapper f$0;

    public /* synthetic */ ScrollCaptureClient$SessionWrapper$$ExternalSyntheticLambda1(ScrollCaptureClient.SessionWrapper sessionWrapper) {
        this.f$0 = sessionWrapper;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.lambda$end$3(completer);
    }
}
