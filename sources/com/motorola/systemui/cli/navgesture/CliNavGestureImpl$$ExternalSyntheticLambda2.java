package com.motorola.systemui.cli.navgesture;

import com.motorola.systemui.cli.navgesture.inputconsumers.OtherActivityInputConsumer;
import java.util.function.Consumer;

public final /* synthetic */ class CliNavGestureImpl$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ CliNavGestureImpl f$0;

    public /* synthetic */ CliNavGestureImpl$$ExternalSyntheticLambda2(CliNavGestureImpl cliNavGestureImpl) {
        this.f$0 = cliNavGestureImpl;
    }

    public final void accept(Object obj) {
        this.f$0.onConsumerInactive((OtherActivityInputConsumer) obj);
    }
}
