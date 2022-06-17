package com.android.systemui.media;

import android.util.Log;
import com.android.systemui.media.MediaTimeoutListener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MediaTimeoutListener.kt */
final class MediaTimeoutListener$PlaybackStateListener$processState$1 implements Runnable {
    final /* synthetic */ MediaTimeoutListener.PlaybackStateListener this$0;
    final /* synthetic */ MediaTimeoutListener this$1;

    MediaTimeoutListener$PlaybackStateListener$processState$1(MediaTimeoutListener.PlaybackStateListener playbackStateListener, MediaTimeoutListener mediaTimeoutListener) {
        this.this$0 = playbackStateListener;
        this.this$1 = mediaTimeoutListener;
    }

    public final void run() {
        this.this$0.cancellation = null;
        Log.v("MediaTimeout", Intrinsics.stringPlus("Execute timeout for ", this.this$0.getKey()));
        this.this$0.setTimedOut(true);
        this.this$1.getTimeoutCallback().invoke(this.this$0.getKey(), Boolean.valueOf(this.this$0.getTimedOut()));
    }
}
