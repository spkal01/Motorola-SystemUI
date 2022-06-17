package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;

public final /* synthetic */ class WindowTransformHelper$$ExternalSyntheticLambda27 implements Runnable {
    public final /* synthetic */ WindowTransformHelper f$0;
    public final /* synthetic */ SyncRtSurfaceTransactionApplierCompat f$1;

    public /* synthetic */ WindowTransformHelper$$ExternalSyntheticLambda27(WindowTransformHelper windowTransformHelper, SyncRtSurfaceTransactionApplierCompat syncRtSurfaceTransactionApplierCompat) {
        this.f$0 = windowTransformHelper;
        this.f$1 = syncRtSurfaceTransactionApplierCompat;
    }

    public final void run() {
        this.f$0.lambda$onActivityInit$2(this.f$1);
    }
}
