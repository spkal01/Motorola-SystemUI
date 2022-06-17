package com.android.systemui.recents;

import android.os.Bundle;
import com.android.p011wm.shell.splitscreen.SplitScreen;
import java.util.function.Consumer;

public final /* synthetic */ class OverviewProxyService$3$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ Bundle f$0;

    public /* synthetic */ OverviewProxyService$3$$ExternalSyntheticLambda2(Bundle bundle) {
        this.f$0 = bundle;
    }

    public final void accept(Object obj) {
        this.f$0.putBinder("extra_shell_split_screen", ((SplitScreen) obj).createExternalInterface().asBinder());
    }
}
