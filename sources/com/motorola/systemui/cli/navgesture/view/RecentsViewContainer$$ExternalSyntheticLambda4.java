package com.motorola.systemui.cli.navgesture.view;

import android.view.MotionEvent;
import java.util.function.Consumer;

public final /* synthetic */ class RecentsViewContainer$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ RecentsViewContainer f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ RecentsViewContainer$$ExternalSyntheticLambda4(RecentsViewContainer recentsViewContainer, float f) {
        this.f$0 = recentsViewContainer;
        this.f$1 = f;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$getEventDispatcher$6(this.f$1, (MotionEvent) obj);
    }
}
