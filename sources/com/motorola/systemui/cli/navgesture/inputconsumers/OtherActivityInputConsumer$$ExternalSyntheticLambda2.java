package com.motorola.systemui.cli.navgesture.inputconsumers;

import com.android.systemui.shared.system.ActivityManagerWrapper;

public final /* synthetic */ class OtherActivityInputConsumer$$ExternalSyntheticLambda2 implements Runnable {
    public static final /* synthetic */ OtherActivityInputConsumer$$ExternalSyntheticLambda2 INSTANCE = new OtherActivityInputConsumer$$ExternalSyntheticLambda2();

    private /* synthetic */ OtherActivityInputConsumer$$ExternalSyntheticLambda2() {
    }

    public final void run() {
        ActivityManagerWrapper.getInstance().cancelRecentsAnimation(true);
    }
}
