package com.android.systemui.statusbar.phone;

public final /* synthetic */ class StatusBar$15$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ShadeController f$0;

    public /* synthetic */ StatusBar$15$$ExternalSyntheticLambda0(ShadeController shadeController) {
        this.f$0 = shadeController;
    }

    public final void run() {
        this.f$0.runPostCollapseRunnables();
    }
}
