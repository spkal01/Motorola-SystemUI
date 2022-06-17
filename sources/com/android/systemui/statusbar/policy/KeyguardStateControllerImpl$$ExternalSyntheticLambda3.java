package com.android.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.function.Consumer;

public final /* synthetic */ class KeyguardStateControllerImpl$$ExternalSyntheticLambda3 implements Consumer {
    public static final /* synthetic */ KeyguardStateControllerImpl$$ExternalSyntheticLambda3 INSTANCE = new KeyguardStateControllerImpl$$ExternalSyntheticLambda3();

    private /* synthetic */ KeyguardStateControllerImpl$$ExternalSyntheticLambda3() {
    }

    public final void accept(Object obj) {
        ((KeyguardStateController.Callback) obj).onUnlockedChanged();
    }
}
