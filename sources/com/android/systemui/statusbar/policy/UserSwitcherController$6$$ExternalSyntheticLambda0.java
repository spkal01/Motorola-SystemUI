package com.android.systemui.statusbar.policy;

public final /* synthetic */ class UserSwitcherController$6$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ UserSwitcherController f$0;

    public /* synthetic */ UserSwitcherController$6$$ExternalSyntheticLambda0(UserSwitcherController userSwitcherController) {
        this.f$0 = userSwitcherController;
    }

    public final void run() {
        this.f$0.notifyAdapters();
    }
}
