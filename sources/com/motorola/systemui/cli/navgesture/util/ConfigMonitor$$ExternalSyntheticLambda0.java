package com.motorola.systemui.cli.navgesture.util;

import java.util.function.Consumer;

public final /* synthetic */ class ConfigMonitor$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ConfigMonitor f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ ConfigMonitor$$ExternalSyntheticLambda0(ConfigMonitor configMonitor, Consumer consumer) {
        this.f$0 = configMonitor;
        this.f$1 = consumer;
    }

    public final void run() {
        this.f$0.lambda$notifyChange$0(this.f$1);
    }
}
