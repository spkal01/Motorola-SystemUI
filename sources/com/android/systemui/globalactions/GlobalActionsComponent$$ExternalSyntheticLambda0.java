package com.android.systemui.globalactions;

import com.android.systemui.plugins.GlobalActions;
import java.util.function.Consumer;

public final /* synthetic */ class GlobalActionsComponent$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ GlobalActionsComponent f$0;

    public /* synthetic */ GlobalActionsComponent$$ExternalSyntheticLambda0(GlobalActionsComponent globalActionsComponent) {
        this.f$0 = globalActionsComponent;
    }

    public final void accept(Object obj) {
        this.f$0.onExtensionCallback((GlobalActions) obj);
    }
}
