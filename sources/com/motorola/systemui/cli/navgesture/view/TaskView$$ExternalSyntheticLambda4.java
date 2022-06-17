package com.motorola.systemui.cli.navgesture.view;

import android.os.Handler;
import java.util.function.Consumer;

public final /* synthetic */ class TaskView$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ Handler f$1;

    public /* synthetic */ TaskView$$ExternalSyntheticLambda4(Consumer consumer, Handler handler) {
        this.f$0 = consumer;
        this.f$1 = handler;
    }

    public final void run() {
        TaskView.lambda$launchTask$5(this.f$0, this.f$1);
    }
}
