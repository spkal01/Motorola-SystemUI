package com.motorola.systemui.cli.navgesture.animation.remote;

import android.view.MotionEvent;
import java.util.function.Consumer;

public final /* synthetic */ class WindowTransformHelper$$ExternalSyntheticLambda33 implements Consumer {
    public final /* synthetic */ WindowTransformHelper f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ WindowTransformHelper$$ExternalSyntheticLambda33(WindowTransformHelper windowTransformHelper, float f) {
        this.f$0 = windowTransformHelper;
        this.f$1 = f;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$getRecentsViewDispatcher$8(this.f$1, (MotionEvent) obj);
    }
}
