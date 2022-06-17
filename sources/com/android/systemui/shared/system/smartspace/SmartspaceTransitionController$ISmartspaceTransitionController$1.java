package com.android.systemui.shared.system.smartspace;

import com.android.systemui.shared.system.smartspace.ISmartspaceTransitionController;
import org.jetbrains.annotations.Nullable;

/* compiled from: SmartspaceTransitionController.kt */
public final class SmartspaceTransitionController$ISmartspaceTransitionController$1 extends ISmartspaceTransitionController.Stub {
    final /* synthetic */ SmartspaceTransitionController this$0;

    SmartspaceTransitionController$ISmartspaceTransitionController$1(SmartspaceTransitionController smartspaceTransitionController) {
        this.this$0 = smartspaceTransitionController;
    }

    public void setSmartspace(@Nullable ISmartspaceCallback iSmartspaceCallback) {
        this.this$0.setLauncherSmartspace(iSmartspaceCallback);
        this.this$0.updateLauncherSmartSpaceState();
    }
}
