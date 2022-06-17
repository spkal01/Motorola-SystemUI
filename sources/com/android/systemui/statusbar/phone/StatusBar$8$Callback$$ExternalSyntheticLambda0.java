package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.StatusBar;

public final /* synthetic */ class StatusBar$8$Callback$$ExternalSyntheticLambda0 implements NotificationShadeWindowController.OtherwisedCollapsedListener {
    public final /* synthetic */ StatusBar.C19338.Callback f$0;

    public /* synthetic */ StatusBar$8$Callback$$ExternalSyntheticLambda0(StatusBar.C19338.Callback callback) {
        this.f$0 = callback;
    }

    public final void setWouldOtherwiseCollapse(boolean z) {
        this.f$0.lambda$onHoldStatusBarOpenChange$1(z);
    }
}
