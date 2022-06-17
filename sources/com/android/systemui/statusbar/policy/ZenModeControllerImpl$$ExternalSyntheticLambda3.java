package com.android.systemui.statusbar.policy;

import android.service.notification.ZenModeConfig;
import com.android.systemui.statusbar.policy.ZenModeController;
import java.util.function.Consumer;

public final /* synthetic */ class ZenModeControllerImpl$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ ZenModeConfig f$0;

    public /* synthetic */ ZenModeControllerImpl$$ExternalSyntheticLambda3(ZenModeConfig zenModeConfig) {
        this.f$0 = zenModeConfig;
    }

    public final void accept(Object obj) {
        ((ZenModeController.Callback) obj).onConfigChanged(this.f$0);
    }
}
