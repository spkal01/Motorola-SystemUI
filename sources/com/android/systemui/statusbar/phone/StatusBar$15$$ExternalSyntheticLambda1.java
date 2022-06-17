package com.android.systemui.statusbar.phone;

public final /* synthetic */ class StatusBar$15$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ StatusBarKeyguardViewManager f$0;

    public /* synthetic */ StatusBar$15$$ExternalSyntheticLambda1(StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        this.f$0 = statusBarKeyguardViewManager;
    }

    public final void run() {
        this.f$0.readyForKeyguardDone();
    }
}
