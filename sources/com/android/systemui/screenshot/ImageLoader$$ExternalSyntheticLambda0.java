package com.android.systemui.screenshot;

import androidx.concurrent.futures.CallbackToFutureAdapter;
import java.io.File;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda0 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ File f$0;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda0(File file) {
        this.f$0 = file;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return ImageLoader.lambda$load$1(this.f$0, completer);
    }
}
