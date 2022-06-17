package com.android.systemui;

import android.content.Context;
import com.android.systemui.SystemUIAppComponentFactory;

public final /* synthetic */ class SystemUIAppComponentFactory$$ExternalSyntheticLambda1 implements SystemUIAppComponentFactory.ContextAvailableCallback {
    public final /* synthetic */ SystemUIAppComponentFactory f$0;

    public /* synthetic */ SystemUIAppComponentFactory$$ExternalSyntheticLambda1(SystemUIAppComponentFactory systemUIAppComponentFactory) {
        this.f$0 = systemUIAppComponentFactory;
    }

    public final void onContextAvailable(Context context) {
        this.f$0.lambda$instantiateApplicationCompat$0(context);
    }
}
