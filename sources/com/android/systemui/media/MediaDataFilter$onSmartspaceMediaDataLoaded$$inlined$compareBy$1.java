package com.android.systemui.media;

import java.util.Comparator;

/* compiled from: Comparisons.kt */
public final class MediaDataFilter$onSmartspaceMediaDataLoaded$$inlined$compareBy$1<T> implements Comparator<T> {
    final /* synthetic */ MediaDataFilter this$0;

    public MediaDataFilter$onSmartspaceMediaDataLoaded$$inlined$compareBy$1(MediaDataFilter mediaDataFilter) {
        this.this$0 = mediaDataFilter;
    }

    public final int compare(T t, T t2) {
        MediaData mediaData = (MediaData) this.this$0.userEntries.get((String) t);
        int i = -1;
        Comparable valueOf = mediaData == null ? -1 : Long.valueOf(mediaData.getLastActive());
        MediaData mediaData2 = (MediaData) this.this$0.userEntries.get((String) t2);
        if (mediaData2 != null) {
            i = Long.valueOf(mediaData2.getLastActive());
        }
        return ComparisonsKt__ComparisonsKt.compareValues(valueOf, i);
    }
}
