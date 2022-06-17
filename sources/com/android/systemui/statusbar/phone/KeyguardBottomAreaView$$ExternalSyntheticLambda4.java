package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.IntentButtonProvider;
import com.android.systemui.statusbar.policy.ExtensionController;

public final /* synthetic */ class KeyguardBottomAreaView$$ExternalSyntheticLambda4 implements ExtensionController.PluginConverter {
    public static final /* synthetic */ KeyguardBottomAreaView$$ExternalSyntheticLambda4 INSTANCE = new KeyguardBottomAreaView$$ExternalSyntheticLambda4();

    private /* synthetic */ KeyguardBottomAreaView$$ExternalSyntheticLambda4() {
    }

    public final Object getInterfaceFromPlugin(Object obj) {
        return ((IntentButtonProvider) obj).getIntentButton();
    }
}
