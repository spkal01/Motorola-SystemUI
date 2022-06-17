package com.motorola.systemui.cli.navgesture.view;

import java.util.function.Consumer;

public final /* synthetic */ class TaskView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ TaskView$$ExternalSyntheticLambda2(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void run() {
        this.f$0.accept(Boolean.TRUE);
    }
}
