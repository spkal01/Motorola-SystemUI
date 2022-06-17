package com.android.systemui.doze;

public final /* synthetic */ class DozeUi$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ DozeHost f$0;

    public /* synthetic */ DozeUi$$ExternalSyntheticLambda1(DozeHost dozeHost) {
        this.f$0 = dozeHost;
    }

    public final void run() {
        this.f$0.dozeTimeTick();
    }
}
