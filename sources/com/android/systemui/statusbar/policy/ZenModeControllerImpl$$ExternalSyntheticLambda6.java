package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.ZenModeController;
import java.util.function.Consumer;

public final /* synthetic */ class ZenModeControllerImpl$$ExternalSyntheticLambda6 implements Consumer {
    public static final /* synthetic */ ZenModeControllerImpl$$ExternalSyntheticLambda6 INSTANCE = new ZenModeControllerImpl$$ExternalSyntheticLambda6();

    private /* synthetic */ ZenModeControllerImpl$$ExternalSyntheticLambda6() {
    }

    public final void accept(Object obj) {
        ((ZenModeController.Callback) obj).onNextAlarmChanged();
    }
}
