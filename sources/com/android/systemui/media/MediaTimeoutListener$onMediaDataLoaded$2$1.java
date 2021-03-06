package com.android.systemui.media;

import android.util.Log;
import com.android.systemui.media.MediaTimeoutListener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MediaTimeoutListener.kt */
final class MediaTimeoutListener$onMediaDataLoaded$2$1 implements Runnable {
    final /* synthetic */ String $key;
    final /* synthetic */ MediaTimeoutListener this$0;

    MediaTimeoutListener$onMediaDataLoaded$2$1(MediaTimeoutListener mediaTimeoutListener, String str) {
        this.this$0 = mediaTimeoutListener;
        this.$key = str;
    }

    public final void run() {
        MediaTimeoutListener.PlaybackStateListener playbackStateListener = (MediaTimeoutListener.PlaybackStateListener) this.this$0.mediaListeners.get(this.$key);
        if (Intrinsics.areEqual((Object) playbackStateListener == null ? null : playbackStateListener.getPlaying(), (Object) Boolean.TRUE)) {
            Log.d("MediaTimeout", Intrinsics.stringPlus("deliver delayed playback state for ", this.$key));
            this.this$0.getTimeoutCallback().invoke(this.$key, Boolean.FALSE);
        }
    }
}
