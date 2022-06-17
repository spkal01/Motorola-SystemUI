package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.ActivityStarter;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ ActivityStarter.Callback f$0;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda12(ActivityStarter.Callback callback) {
        this.f$0 = callback;
    }

    public final void run() {
        StatusBar.lambda$startActivityDismissingKeyguard$22(this.f$0);
    }
}
