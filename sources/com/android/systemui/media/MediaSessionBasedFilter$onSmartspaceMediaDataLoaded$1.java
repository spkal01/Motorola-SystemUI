package com.android.systemui.media;

/* compiled from: MediaSessionBasedFilter.kt */
final class MediaSessionBasedFilter$onSmartspaceMediaDataLoaded$1 implements Runnable {
    final /* synthetic */ SmartspaceMediaData $data;
    final /* synthetic */ String $key;
    final /* synthetic */ MediaSessionBasedFilter this$0;

    MediaSessionBasedFilter$onSmartspaceMediaDataLoaded$1(MediaSessionBasedFilter mediaSessionBasedFilter, String str, SmartspaceMediaData smartspaceMediaData) {
        this.this$0 = mediaSessionBasedFilter;
        this.$key = str;
        this.$data = smartspaceMediaData;
    }

    public final void run() {
        this.this$0.dispatchSmartspaceMediaDataLoaded(this.$key, this.$data);
    }
}
