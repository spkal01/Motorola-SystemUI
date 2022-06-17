package com.android.systemui.media.dialog;

import com.android.settingslib.media.MediaDevice;

public final /* synthetic */ class MediaOutputController$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MediaOutputController f$0;
    public final /* synthetic */ MediaDevice f$1;

    public /* synthetic */ MediaOutputController$$ExternalSyntheticLambda2(MediaOutputController mediaOutputController, MediaDevice mediaDevice) {
        this.f$0 = mediaOutputController;
        this.f$1 = mediaDevice;
    }

    public final void run() {
        this.f$0.lambda$connectDevice$0(this.f$1);
    }
}
