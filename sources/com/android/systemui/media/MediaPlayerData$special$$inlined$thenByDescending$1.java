package com.android.systemui.media;

import com.android.systemui.media.MediaPlayerData;
import java.util.Comparator;

/* compiled from: Comparisons.kt */
public final class MediaPlayerData$special$$inlined$thenByDescending$1<T> implements Comparator<T> {
    final /* synthetic */ Comparator $this_thenByDescending;
    final /* synthetic */ MediaPlayerData this$0;

    public MediaPlayerData$special$$inlined$thenByDescending$1(Comparator comparator, MediaPlayerData mediaPlayerData) {
        this.$this_thenByDescending = comparator;
        this.this$0 = mediaPlayerData;
    }

    public final int compare(T t, T t2) {
        int compare = this.$this_thenByDescending.compare(t, t2);
        if (compare != 0) {
            return compare;
        }
        boolean shouldPrioritizeSs$frameworks__base__packages__SystemUI__android_common__SystemUI_core = this.this$0.mo14305x1d6e9f0e();
        boolean isSsMediaRec = ((MediaPlayerData.MediaSortKey) t2).isSsMediaRec();
        if (!shouldPrioritizeSs$frameworks__base__packages__SystemUI__android_common__SystemUI_core) {
            isSsMediaRec = !isSsMediaRec;
        }
        MediaPlayerData.MediaSortKey mediaSortKey = (MediaPlayerData.MediaSortKey) t;
        return ComparisonsKt__ComparisonsKt.compareValues(Boolean.valueOf(isSsMediaRec), Boolean.valueOf(this.this$0.mo14305x1d6e9f0e() ? mediaSortKey.isSsMediaRec() : !mediaSortKey.isSsMediaRec()));
    }
}
