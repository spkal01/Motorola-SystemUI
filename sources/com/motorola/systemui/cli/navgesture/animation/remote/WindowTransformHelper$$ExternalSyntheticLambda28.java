package com.motorola.systemui.cli.navgesture.animation.remote;

import android.view.MotionEvent;
import com.motorola.systemui.cli.navgesture.IRecentsView;
import java.util.function.Consumer;

public final /* synthetic */ class WindowTransformHelper$$ExternalSyntheticLambda28 implements Consumer {
    public final /* synthetic */ float f$0;
    public final /* synthetic */ MotionEvent f$1;

    public /* synthetic */ WindowTransformHelper$$ExternalSyntheticLambda28(float f, MotionEvent motionEvent) {
        this.f$0 = f;
        this.f$1 = motionEvent;
    }

    public final void accept(Object obj) {
        ((IRecentsView) obj).getEventDispatcher(this.f$0).accept(this.f$1);
    }
}
