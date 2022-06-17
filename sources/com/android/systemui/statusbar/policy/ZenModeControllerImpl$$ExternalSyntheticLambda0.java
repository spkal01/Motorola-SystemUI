package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.ZenModeController;
import java.util.function.Consumer;

public final /* synthetic */ class ZenModeControllerImpl$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ ZenModeControllerImpl$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((ZenModeController.Callback) obj).onZenChanged(this.f$0);
    }
}
