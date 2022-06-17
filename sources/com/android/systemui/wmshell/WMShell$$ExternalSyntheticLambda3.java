package com.android.systemui.wmshell;

import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import java.util.function.Consumer;

public final /* synthetic */ class WMShell$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ WMShell f$0;

    public /* synthetic */ WMShell$$ExternalSyntheticLambda3(WMShell wMShell) {
        this.f$0 = wMShell;
    }

    public final void accept(Object obj) {
        this.f$0.initSplitScreen((LegacySplitScreen) obj);
    }
}
