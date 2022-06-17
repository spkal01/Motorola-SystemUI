package com.android.systemui.recents;

import android.os.Bundle;
import com.android.p011wm.shell.onehanded.OneHanded;
import java.util.function.Consumer;

public final /* synthetic */ class OverviewProxyService$3$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ Bundle f$0;

    public /* synthetic */ OverviewProxyService$3$$ExternalSyntheticLambda0(Bundle bundle) {
        this.f$0 = bundle;
    }

    public final void accept(Object obj) {
        this.f$0.putBinder("extra_shell_one_handed", ((OneHanded) obj).createExternalInterface().asBinder());
    }
}
