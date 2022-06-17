package com.android.systemui.screenshot;

import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.android.systemui.screenshot.ImageExporter;
import java.util.concurrent.Executor;

public final /* synthetic */ class ImageExporter$$ExternalSyntheticLambda1 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ Executor f$0;
    public final /* synthetic */ ImageExporter.Task f$1;

    public /* synthetic */ ImageExporter$$ExternalSyntheticLambda1(Executor executor, ImageExporter.Task task) {
        this.f$0 = executor;
        this.f$1 = task;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.execute(new ImageExporter$$ExternalSyntheticLambda2(completer, this.f$1));
    }
}
