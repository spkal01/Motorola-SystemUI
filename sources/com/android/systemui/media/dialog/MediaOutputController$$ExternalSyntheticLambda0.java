package com.android.systemui.media.dialog;

import com.android.systemui.plugins.ActivityStarter;

public final /* synthetic */ class MediaOutputController$$ExternalSyntheticLambda0 implements ActivityStarter.OnDismissAction {
    public final /* synthetic */ MediaOutputController f$0;

    public /* synthetic */ MediaOutputController$$ExternalSyntheticLambda0(MediaOutputController mediaOutputController) {
        this.f$0 = mediaOutputController;
    }

    public final boolean onDismiss() {
        return this.f$0.lambda$launchBluetoothPairing$2();
    }
}
