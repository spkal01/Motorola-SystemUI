package com.motorola.systemui.cli.navgesture.view;

import android.os.Handler;
import java.util.function.Consumer;

public final /* synthetic */ class TaskView$$ExternalSyntheticLambda7 implements Consumer {
    public final /* synthetic */ Consumer f$0;
    public final /* synthetic */ Handler f$1;

    public /* synthetic */ TaskView$$ExternalSyntheticLambda7(Consumer consumer, Handler handler) {
        this.f$0 = consumer;
        this.f$1 = handler;
    }

    public final void accept(Object obj) {
        TaskView.lambda$launchTask$7(this.f$0, this.f$1, (Boolean) obj);
    }
}
