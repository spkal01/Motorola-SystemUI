package com.android.systemui.media;

import com.android.systemui.media.MediaDeviceManager;

/* compiled from: MediaDeviceManager.kt */
final class MediaDeviceManager$Entry$onSelectedDeviceStateChanged$1 implements Runnable {
    final /* synthetic */ MediaDeviceManager.Entry this$0;

    MediaDeviceManager$Entry$onSelectedDeviceStateChanged$1(MediaDeviceManager.Entry entry) {
        this.this$0 = entry;
    }

    public final void run() {
        this.this$0.updateCurrent();
    }
}
