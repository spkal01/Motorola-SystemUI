package com.android.systemui.globalactions;

import com.android.systemui.plugins.GlobalActions;
import java.util.function.Supplier;
import javax.inject.Provider;

public final /* synthetic */ class GlobalActionsComponent$$ExternalSyntheticLambda1 implements Supplier {
    public final /* synthetic */ Provider f$0;

    public /* synthetic */ GlobalActionsComponent$$ExternalSyntheticLambda1(Provider provider) {
        this.f$0 = provider;
    }

    public final Object get() {
        return (GlobalActions) this.f$0.get();
    }
}
