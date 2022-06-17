package com.android.systemui.media;

import android.media.session.MediaController;
import com.android.systemui.media.MediaDeviceManager;

/* compiled from: MediaDeviceManager.kt */
final class MediaDeviceManager$Entry$start$1 implements Runnable {
    final /* synthetic */ MediaDeviceManager.Entry this$0;

    MediaDeviceManager$Entry$start$1(MediaDeviceManager.Entry entry) {
        this.this$0 = entry;
    }

    public final void run() {
        this.this$0.getLocalMediaManager().registerCallback(this.this$0);
        this.this$0.getLocalMediaManager().startScan();
        MediaDeviceManager.Entry entry = this.this$0;
        MediaController controller = entry.getController();
        MediaController.PlaybackInfo playbackInfo = controller == null ? null : controller.getPlaybackInfo();
        entry.playbackType = playbackInfo == null ? 0 : playbackInfo.getPlaybackType();
        MediaController controller2 = this.this$0.getController();
        if (controller2 != null) {
            controller2.registerCallback(this.this$0);
        }
        this.this$0.updateCurrent();
        this.this$0.started = true;
    }
}
