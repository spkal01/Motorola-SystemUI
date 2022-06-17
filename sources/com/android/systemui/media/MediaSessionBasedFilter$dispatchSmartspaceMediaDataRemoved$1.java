package com.android.systemui.media;

import com.android.systemui.media.MediaDataManager;
import java.util.Set;

/* compiled from: MediaSessionBasedFilter.kt */
final class MediaSessionBasedFilter$dispatchSmartspaceMediaDataRemoved$1 implements Runnable {
    final /* synthetic */ boolean $immediately;
    final /* synthetic */ String $key;
    final /* synthetic */ MediaSessionBasedFilter this$0;

    MediaSessionBasedFilter$dispatchSmartspaceMediaDataRemoved$1(MediaSessionBasedFilter mediaSessionBasedFilter, String str, boolean z) {
        this.this$0 = mediaSessionBasedFilter;
        this.$key = str;
        this.$immediately = z;
    }

    public final void run() {
        Set<MediaDataManager.Listener> set = CollectionsKt___CollectionsKt.toSet(this.this$0.listeners);
        String str = this.$key;
        boolean z = this.$immediately;
        for (MediaDataManager.Listener onSmartspaceMediaDataRemoved : set) {
            onSmartspaceMediaDataRemoved.onSmartspaceMediaDataRemoved(str, z);
        }
    }
}
