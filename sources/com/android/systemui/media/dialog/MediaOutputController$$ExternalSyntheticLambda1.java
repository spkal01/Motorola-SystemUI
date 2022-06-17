package com.android.systemui.media.dialog;

import com.android.settingslib.media.MediaDevice;

public final /* synthetic */ class MediaOutputController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ MediaDevice f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MediaOutputController$$ExternalSyntheticLambda1(MediaDevice mediaDevice, int i) {
        this.f$0 = mediaDevice;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.requestSetVolume(this.f$1);
    }
}
