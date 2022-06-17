package com.android.systemui.media;

import com.android.systemui.media.MediaPlayerData;
import java.util.Comparator;

/* compiled from: Comparisons.kt */
public final class MediaPlayerData$special$$inlined$compareByDescending$1<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        return ComparisonsKt__ComparisonsKt.compareValues(((MediaPlayerData.MediaSortKey) t2).getData().isPlaying(), ((MediaPlayerData.MediaSortKey) t).getData().isPlaying());
    }
}
