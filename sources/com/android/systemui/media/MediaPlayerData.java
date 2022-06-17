package com.android.systemui.media;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import com.android.systemui.util.time.SystemClock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import kotlin.Triple;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaCarouselController.kt */
public final class MediaPlayerData {
    @NotNull
    private final MediaData EMPTY;
    @NotNull
    private final Comparator<MediaSortKey> comparator;
    @NotNull
    private final Map<String, MediaSortKey> mediaData = new LinkedHashMap();
    @NotNull
    private final TreeMap<MediaSortKey, MediaControlPanel> mediaPlayers;
    private boolean shouldPrioritizeSs;
    @Nullable
    private SmartspaceMediaData smartspaceMediaData;

    public MediaPlayerData() {
        MediaData mediaData2 = r1;
        MediaData mediaData3 = new MediaData(-1, false, 0, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, CollectionsKt__CollectionsKt.emptyList(), CollectionsKt__CollectionsKt.emptyList(), "INVALID", (MediaSession.Token) null, (PendingIntent) null, (MediaDeviceData) null, true, (Runnable) null, false, false, (String) null, false, (Boolean) null, false, 0, 8323072, (DefaultConstructorMarker) null);
        this.EMPTY = mediaData2;
        MediaPlayerData$special$$inlined$thenByDescending$4 mediaPlayerData$special$$inlined$thenByDescending$4 = new MediaPlayerData$special$$inlined$thenByDescending$4(new MediaPlayerData$special$$inlined$thenByDescending$3(new MediaPlayerData$special$$inlined$thenByDescending$2(new MediaPlayerData$special$$inlined$thenByDescending$1(new MediaPlayerData$special$$inlined$compareByDescending$1(), this))));
        this.comparator = mediaPlayerData$special$$inlined$thenByDescending$4;
        this.mediaPlayers = new TreeMap<>(mediaPlayerData$special$$inlined$thenByDescending$4);
    }

    /* renamed from: getShouldPrioritizeSs$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final boolean mo14305x1d6e9f0e() {
        return this.shouldPrioritizeSs;
    }

    @Nullable
    /* renamed from: getSmartspaceMediaData$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final SmartspaceMediaData mo14306xc76e19e1() {
        return this.smartspaceMediaData;
    }

    /* compiled from: MediaCarouselController.kt */
    public static final class MediaSortKey {
        @NotNull
        private final MediaData data;
        private final boolean isSsMediaRec;
        private final long updateTime;

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MediaSortKey)) {
                return false;
            }
            MediaSortKey mediaSortKey = (MediaSortKey) obj;
            return this.isSsMediaRec == mediaSortKey.isSsMediaRec && Intrinsics.areEqual((Object) this.data, (Object) mediaSortKey.data) && this.updateTime == mediaSortKey.updateTime;
        }

        public int hashCode() {
            boolean z = this.isSsMediaRec;
            if (z) {
                z = true;
            }
            return ((((z ? 1 : 0) * true) + this.data.hashCode()) * 31) + Long.hashCode(this.updateTime);
        }

        @NotNull
        public String toString() {
            return "MediaSortKey(isSsMediaRec=" + this.isSsMediaRec + ", data=" + this.data + ", updateTime=" + this.updateTime + ')';
        }

        public MediaSortKey(boolean z, @NotNull MediaData mediaData, long j) {
            Intrinsics.checkNotNullParameter(mediaData, "data");
            this.isSsMediaRec = z;
            this.data = mediaData;
            this.updateTime = j;
        }

        public final boolean isSsMediaRec() {
            return this.isSsMediaRec;
        }

        @NotNull
        public final MediaData getData() {
            return this.data;
        }

        public final long getUpdateTime() {
            return this.updateTime;
        }
    }

    public final void addMediaPlayer(@NotNull String str, @NotNull MediaData mediaData2, @NotNull MediaControlPanel mediaControlPanel, @NotNull SystemClock systemClock) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData2, "data");
        Intrinsics.checkNotNullParameter(mediaControlPanel, "player");
        Intrinsics.checkNotNullParameter(systemClock, "clock");
        removeMediaPlayer(str);
        MediaSortKey mediaSortKey = new MediaSortKey(false, mediaData2, systemClock.currentTimeMillis());
        this.mediaData.put(str, mediaSortKey);
        this.mediaPlayers.put(mediaSortKey, mediaControlPanel);
    }

    public final void addMediaRecommendation(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData2, @NotNull MediaControlPanel mediaControlPanel, boolean z, @NotNull SystemClock systemClock) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(smartspaceMediaData2, "data");
        Intrinsics.checkNotNullParameter(mediaControlPanel, "player");
        Intrinsics.checkNotNullParameter(systemClock, "clock");
        this.shouldPrioritizeSs = z;
        removeMediaPlayer(str);
        MediaSortKey mediaSortKey = new MediaSortKey(true, this.EMPTY, systemClock.currentTimeMillis());
        this.mediaData.put(str, mediaSortKey);
        this.mediaPlayers.put(mediaSortKey, mediaControlPanel);
        this.smartspaceMediaData = smartspaceMediaData2;
    }

    public final void moveIfExists(@Nullable String str, @NotNull String str2) {
        MediaSortKey remove;
        Intrinsics.checkNotNullParameter(str2, "newKey");
        if (str != null && !Intrinsics.areEqual((Object) str, (Object) str2) && (remove = this.mediaData.remove(str)) != null) {
            removeMediaPlayer(str2);
            MediaSortKey put = this.mediaData.put(str2, remove);
        }
    }

    @Nullable
    public final MediaControlPanel getMediaPlayer(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        MediaSortKey mediaSortKey = this.mediaData.get(str);
        if (mediaSortKey == null) {
            return null;
        }
        return this.mediaPlayers.get(mediaSortKey);
    }

    public final int getMediaPlayerIndex(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        MediaSortKey mediaSortKey = this.mediaData.get(str);
        Set<Map.Entry<MediaSortKey, MediaControlPanel>> entrySet = this.mediaPlayers.entrySet();
        Intrinsics.checkNotNullExpressionValue(entrySet, "mediaPlayers.entries");
        int i = 0;
        for (T next : entrySet) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt__CollectionsKt.throwIndexOverflow();
            }
            if (Intrinsics.areEqual(((Map.Entry) next).getKey(), (Object) mediaSortKey)) {
                return i;
            }
            i = i2;
        }
        return -1;
    }

    @Nullable
    public final MediaControlPanel removeMediaPlayer(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        MediaSortKey remove = this.mediaData.remove(str);
        if (remove == null) {
            return null;
        }
        if (remove.isSsMediaRec()) {
            this.smartspaceMediaData = null;
        }
        return this.mediaPlayers.remove(remove);
    }

    @NotNull
    public final List<Triple<String, MediaData, Boolean>> mediaData() {
        Set<Map.Entry<String, MediaSortKey>> entrySet = this.mediaData.entrySet();
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(entrySet, 10));
        for (Map.Entry entry : entrySet) {
            arrayList.add(new Triple(entry.getKey(), ((MediaSortKey) entry.getValue()).getData(), Boolean.valueOf(((MediaSortKey) entry.getValue()).isSsMediaRec())));
        }
        return arrayList;
    }

    @NotNull
    public final Collection<MediaControlPanel> players() {
        Collection<MediaControlPanel> values = this.mediaPlayers.values();
        Intrinsics.checkNotNullExpressionValue(values, "mediaPlayers.values");
        return values;
    }

    @NotNull
    public final Set<MediaSortKey> playerKeys() {
        Set<MediaSortKey> keySet = this.mediaPlayers.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet, "mediaPlayers.keys");
        return keySet;
    }

    public final int firstActiveMediaIndex() {
        Set<Map.Entry<MediaSortKey, MediaControlPanel>> entrySet = this.mediaPlayers.entrySet();
        Intrinsics.checkNotNullExpressionValue(entrySet, "mediaPlayers.entries");
        int i = 0;
        for (T next : entrySet) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt__CollectionsKt.throwIndexOverflow();
            }
            Map.Entry entry = (Map.Entry) next;
            if (!((MediaSortKey) entry.getKey()).isSsMediaRec() && ((MediaSortKey) entry.getKey()).getData().getActive()) {
                return i;
            }
            i = i2;
        }
        return -1;
    }

    @Nullable
    public final String smartspaceMediaKey() {
        for (Map.Entry entry : this.mediaData.entrySet()) {
            if (((MediaSortKey) entry.getValue()).isSsMediaRec()) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public final void clear() {
        this.mediaData.clear();
        this.mediaPlayers.clear();
    }

    public final boolean hasActiveMediaOrRecommendationCard() {
        SmartspaceMediaData smartspaceMediaData2 = this.smartspaceMediaData;
        if (smartspaceMediaData2 != null) {
            Boolean valueOf = smartspaceMediaData2 == null ? null : Boolean.valueOf(smartspaceMediaData2.isActive());
            Intrinsics.checkNotNull(valueOf);
            if (valueOf.booleanValue()) {
                return true;
            }
        }
        if (firstActiveMediaIndex() != -1) {
            return true;
        }
        return false;
    }
}
