package com.android.systemui.recents;

import android.os.Bundle;
import com.android.p011wm.shell.startingsurface.StartingSurface;
import java.util.function.Consumer;

public final /* synthetic */ class OverviewProxyService$3$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ Bundle f$0;

    public /* synthetic */ OverviewProxyService$3$$ExternalSyntheticLambda3(Bundle bundle) {
        this.f$0 = bundle;
    }

    public final void accept(Object obj) {
        this.f$0.putBinder("extra_shell_starting_window", ((StartingSurface) obj).createExternalInterface().asBinder());
    }
}
