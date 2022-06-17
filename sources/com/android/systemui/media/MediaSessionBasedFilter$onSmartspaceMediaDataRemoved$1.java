package com.android.systemui.media;

/* compiled from: MediaSessionBasedFilter.kt */
final class MediaSessionBasedFilter$onSmartspaceMediaDataRemoved$1 implements Runnable {
    final /* synthetic */ boolean $immediately;
    final /* synthetic */ String $key;
    final /* synthetic */ MediaSessionBasedFilter this$0;

    MediaSessionBasedFilter$onSmartspaceMediaDataRemoved$1(MediaSessionBasedFilter mediaSessionBasedFilter, String str, boolean z) {
        this.this$0 = mediaSessionBasedFilter;
        this.$key = str;
        this.$immediately = z;
    }

    public final void run() {
        this.this$0.dispatchSmartspaceMediaDataRemoved(this.$key, this.$immediately);
    }
}
