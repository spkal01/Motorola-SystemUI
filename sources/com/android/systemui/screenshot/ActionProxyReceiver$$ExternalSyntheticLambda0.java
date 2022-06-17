package com.android.systemui.screenshot;

import android.content.Context;
import android.content.Intent;

public final /* synthetic */ class ActionProxyReceiver$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ActionProxyReceiver f$0;
    public final /* synthetic */ Intent f$1;
    public final /* synthetic */ Context f$2;

    public /* synthetic */ ActionProxyReceiver$$ExternalSyntheticLambda0(ActionProxyReceiver actionProxyReceiver, Intent intent, Context context) {
        this.f$0 = actionProxyReceiver;
        this.f$1 = intent;
        this.f$2 = context;
    }

    public final void run() {
        this.f$0.lambda$onReceive$0(this.f$1, this.f$2);
    }
}
