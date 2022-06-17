package com.android.systemui.media;

import com.android.systemui.media.MediaDataManager;
import java.util.Set;

/* compiled from: MediaSessionBasedFilter.kt */
final class MediaSessionBasedFilter$dispatchMediaDataLoaded$1 implements Runnable {
    final /* synthetic */ boolean $immediately;
    final /* synthetic */ MediaData $info;
    final /* synthetic */ String $key;
    final /* synthetic */ String $oldKey;
    final /* synthetic */ MediaSessionBasedFilter this$0;

    MediaSessionBasedFilter$dispatchMediaDataLoaded$1(MediaSessionBasedFilter mediaSessionBasedFilter, String str, String str2, MediaData mediaData, boolean z) {
        this.this$0 = mediaSessionBasedFilter;
        this.$key = str;
        this.$oldKey = str2;
        this.$info = mediaData;
        this.$immediately = z;
    }

    public final void run() {
        Set<MediaDataManager.Listener> set = CollectionsKt___CollectionsKt.toSet(this.this$0.listeners);
        String str = this.$key;
        String str2 = this.$oldKey;
        MediaData mediaData = this.$info;
        boolean z = this.$immediately;
        for (MediaDataManager.Listener onMediaDataLoaded$default : set) {
            MediaDataManager.Listener.DefaultImpls.onMediaDataLoaded$default(onMediaDataLoaded$default, str, str2, mediaData, z, false, 16, (Object) null);
        }
    }
}
