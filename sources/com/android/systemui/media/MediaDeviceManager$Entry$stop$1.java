package com.android.systemui.media;

import android.media.session.MediaController;
import com.android.systemui.media.MediaDeviceManager;

/* compiled from: MediaDeviceManager.kt */
final class MediaDeviceManager$Entry$stop$1 implements Runnable {
    final /* synthetic */ MediaDeviceManager.Entry this$0;

    MediaDeviceManager$Entry$stop$1(MediaDeviceManager.Entry entry) {
        this.this$0 = entry;
    }

    public final void run() {
        this.this$0.started = false;
        MediaController controller = this.this$0.getController();
        if (controller != null) {
            controller.unregisterCallback(this.this$0);
        }
        this.this$0.getLocalMediaManager().stopScan();
        this.this$0.getLocalMediaManager().unregisterCallback(this.this$0);
    }
}
