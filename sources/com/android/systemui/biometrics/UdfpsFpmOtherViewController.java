package com.android.systemui.biometrics;

import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.StatusBar;

class UdfpsFpmOtherViewController extends UdfpsAnimationViewController<UdfpsFpmOtherView> {
    /* access modifiers changed from: package-private */
    public String getTag() {
        return "UdfpsFpmOtherViewController";
    }

    protected UdfpsFpmOtherViewController(UdfpsFpmOtherView udfpsFpmOtherView, StatusBarStateController statusBarStateController, StatusBar statusBar, DumpManager dumpManager) {
        super(udfpsFpmOtherView, statusBarStateController, statusBar, dumpManager);
    }
}
