package com.android.systemui.media;

import android.media.session.MediaController;

/* compiled from: SeekBarViewModel.kt */
final class SeekBarViewModel$onSeek$1 implements Runnable {
    final /* synthetic */ long $position;
    final /* synthetic */ SeekBarViewModel this$0;

    SeekBarViewModel$onSeek$1(SeekBarViewModel seekBarViewModel, long j) {
        this.this$0 = seekBarViewModel;
        this.$position = j;
    }

    public final void run() {
        MediaController.TransportControls transportControls;
        if (this.this$0.isFalseSeek) {
            this.this$0.setScrubbing(false);
            this.this$0.checkPlaybackPosition();
            return;
        }
        this.this$0.getLogSmartspaceClick().invoke();
        MediaController access$getController$p = this.this$0.controller;
        if (!(access$getController$p == null || (transportControls = access$getController$p.getTransportControls()) == null)) {
            transportControls.seekTo(this.$position);
        }
        this.this$0.playbackState = null;
        this.this$0.setScrubbing(false);
    }
}
