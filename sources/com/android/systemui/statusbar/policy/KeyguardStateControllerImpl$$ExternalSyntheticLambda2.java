package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.function.Consumer;

public final /* synthetic */ class KeyguardStateControllerImpl$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ KeyguardStateControllerImpl$$ExternalSyntheticLambda2 INSTANCE = new KeyguardStateControllerImpl$$ExternalSyntheticLambda2();

    private /* synthetic */ KeyguardStateControllerImpl$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((KeyguardStateController.Callback) obj).onKeyguardDismissAmountChanged();
    }
}
