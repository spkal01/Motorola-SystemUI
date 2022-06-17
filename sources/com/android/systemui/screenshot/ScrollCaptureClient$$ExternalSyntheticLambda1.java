package com.android.systemui.screenshot;

import android.view.IScrollCaptureConnection;
import android.view.ScrollCaptureResponse;
import androidx.concurrent.futures.CallbackToFutureAdapter;

public final /* synthetic */ class ScrollCaptureClient$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ScrollCaptureClient f$0;
    public final /* synthetic */ IScrollCaptureConnection f$1;
    public final /* synthetic */ ScrollCaptureResponse f$2;
    public final /* synthetic */ float f$3;

    public /* synthetic */ ScrollCaptureClient$$ExternalSyntheticLambda1(ScrollCaptureClient scrollCaptureClient, IScrollCaptureConnection iScrollCaptureConnection, ScrollCaptureResponse scrollCaptureResponse, float f) {
        this.f$0 = scrollCaptureClient;
        this.f$1 = iScrollCaptureConnection;
        this.f$2 = scrollCaptureResponse;
        this.f$3 = f;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.lambda$start$1(this.f$1, this.f$2, this.f$3, completer);
    }
}
