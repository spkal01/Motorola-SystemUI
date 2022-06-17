package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.ZenModeController;
import java.util.function.Consumer;

public final /* synthetic */ class ZenModeControllerImpl$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ ZenModeControllerImpl$$ExternalSyntheticLambda4(boolean z) {
        this.f$0 = z;
    }

    public final void accept(Object obj) {
        ((ZenModeController.Callback) obj).onZenAvailableChanged(this.f$0);
    }
}
