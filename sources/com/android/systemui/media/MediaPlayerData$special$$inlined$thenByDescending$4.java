package com.android.systemui.media;

import com.android.systemui.media.MediaPlayerData;
import java.util.Comparator;

/* compiled from: Comparisons.kt */
public final class MediaPlayerData$special$$inlined$thenByDescending$4<T> implements Comparator<T> {
    final /* synthetic */ Comparator $this_thenByDescending;

    public MediaPlayerData$special$$inlined$thenByDescending$4(Comparator comparator) {
        this.$this_thenByDescending = comparator;
    }

    public final int compare(T t, T t2) {
        int compare = this.$this_thenByDescending.compare(t, t2);
        return compare != 0 ? compare : ComparisonsKt__ComparisonsKt.compareValues(Long.valueOf(((MediaPlayerData.MediaSortKey) t2).getUpdateTime()), Long.valueOf(((MediaPlayerData.MediaSortKey) t).getUpdateTime()));
    }
}
