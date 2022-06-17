package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.IntentButtonProvider;
import com.android.systemui.statusbar.policy.ExtensionController;

public final /* synthetic */ class KeyguardBottomAreaView$$ExternalSyntheticLambda3 implements ExtensionController.PluginConverter {
    public static final /* synthetic */ KeyguardBottomAreaView$$ExternalSyntheticLambda3 INSTANCE = new KeyguardBottomAreaView$$ExternalSyntheticLambda3();

    private /* synthetic */ KeyguardBottomAreaView$$ExternalSyntheticLambda3() {
    }

    public final Object getInterfaceFromPlugin(Object obj) {
        return ((IntentButtonProvider) obj).getIntentButton();
    }
}
