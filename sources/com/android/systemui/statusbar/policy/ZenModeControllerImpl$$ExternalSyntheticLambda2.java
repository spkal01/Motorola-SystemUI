package com.android.systemui.statusbar.policy;

import android.service.notification.ZenModeConfig;
import com.android.systemui.statusbar.policy.ZenModeController;
import java.util.function.Consumer;

public final /* synthetic */ class ZenModeControllerImpl$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ ZenModeConfig.ZenRule f$0;

    public /* synthetic */ ZenModeControllerImpl$$ExternalSyntheticLambda2(ZenModeConfig.ZenRule zenRule) {
        this.f$0 = zenRule;
    }

    public final void accept(Object obj) {
        ((ZenModeController.Callback) obj).onManualRuleChanged(this.f$0);
    }
}
