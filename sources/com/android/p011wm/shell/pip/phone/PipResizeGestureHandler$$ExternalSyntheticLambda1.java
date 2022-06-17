package com.android.p011wm.shell.pip.phone;

import android.graphics.Rect;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.pip.phone.PipResizeGestureHandler$$ExternalSyntheticLambda1 */
public final /* synthetic */ class PipResizeGestureHandler$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ PipResizeGestureHandler f$0;

    public /* synthetic */ PipResizeGestureHandler$$ExternalSyntheticLambda1(PipResizeGestureHandler pipResizeGestureHandler) {
        this.f$0 = pipResizeGestureHandler;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$finishResize$1((Rect) obj);
    }
}
