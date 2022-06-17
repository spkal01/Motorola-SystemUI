package com.android.systemui.statusbar.phone;

import android.os.UserHandle;

public final /* synthetic */ class AutoTileManager$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AutoTileManager f$0;
    public final /* synthetic */ UserHandle f$1;

    public /* synthetic */ AutoTileManager$$ExternalSyntheticLambda0(AutoTileManager autoTileManager, UserHandle userHandle) {
        this.f$0 = autoTileManager;
        this.f$1 = userHandle;
    }

    public final void run() {
        this.f$0.lambda$changeUser$0(this.f$1);
    }
}
