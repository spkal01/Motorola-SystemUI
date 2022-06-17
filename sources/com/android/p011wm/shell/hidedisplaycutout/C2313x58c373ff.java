package com.android.p011wm.shell.hidedisplaycutout;

import android.content.res.Configuration;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutoutController;

/* renamed from: com.android.wm.shell.hidedisplaycutout.HideDisplayCutoutController$HideDisplayCutoutImpl$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2313x58c373ff implements Runnable {
    public final /* synthetic */ HideDisplayCutoutController.HideDisplayCutoutImpl f$0;
    public final /* synthetic */ Configuration f$1;

    public /* synthetic */ C2313x58c373ff(HideDisplayCutoutController.HideDisplayCutoutImpl hideDisplayCutoutImpl, Configuration configuration) {
        this.f$0 = hideDisplayCutoutImpl;
        this.f$1 = configuration;
    }

    public final void run() {
        this.f$0.lambda$onConfigurationChanged$0(this.f$1);
    }
}
