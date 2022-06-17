package com.android.systemui.media;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.media.MediaDeviceManager;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaDataCombineLatest.kt */
public final class MediaDataCombineLatest implements MediaDataManager.Listener, MediaDeviceManager.Listener {
    @NotNull
    private final Map<String, Pair<MediaData, MediaDeviceData>> entries = new LinkedHashMap();
    @NotNull
    private final Set<MediaDataManager.Listener> listeners = new LinkedHashSet();

    public void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData, "data");
        MediaDeviceData mediaDeviceData = null;
        if (str2 == null || Intrinsics.areEqual((Object) str2, (Object) str) || !this.entries.containsKey(str2)) {
            Map<String, Pair<MediaData, MediaDeviceData>> map = this.entries;
            Pair pair = map.get(str);
            if (pair != null) {
                mediaDeviceData = (MediaDeviceData) pair.getSecond();
            }
            map.put(str, TuplesKt.m104to(mediaData, mediaDeviceData));
            update(str, str);
            return;
        }
        Map<String, Pair<MediaData, MediaDeviceData>> map2 = this.entries;
        Pair remove = map2.remove(str2);
        if (remove != null) {
            mediaDeviceData = (MediaDeviceData) remove.getSecond();
        }
        map2.put(str, TuplesKt.m104to(mediaData, mediaDeviceData));
        update(str, str2);
    }

    public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(smartspaceMediaData, "data");
        for (T onSmartspaceMediaDataLoaded$default : CollectionsKt___CollectionsKt.toSet(this.listeners)) {
            MediaDataManager.Listener.DefaultImpls.onSmartspaceMediaDataLoaded$default(onSmartspaceMediaDataLoaded$default, str, smartspaceMediaData, false, 4, (Object) null);
        }
    }

    public void onMediaDataRemoved(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        remove(str);
    }

    public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        for (T onSmartspaceMediaDataRemoved : CollectionsKt___CollectionsKt.toSet(this.listeners)) {
            onSmartspaceMediaDataRemoved.onSmartspaceMediaDataRemoved(str, z);
        }
    }

    public void onMediaDeviceChanged(@NotNull String str, @Nullable String str2, @Nullable MediaDeviceData mediaDeviceData) {
        Intrinsics.checkNotNullParameter(str, "key");
        MediaData mediaData = null;
        if (str2 == null || Intrinsics.areEqual((Object) str2, (Object) str) || !this.entries.containsKey(str2)) {
            Map<String, Pair<MediaData, MediaDeviceData>> map = this.entries;
            Pair pair = map.get(str);
            if (pair != null) {
                mediaData = (MediaData) pair.getFirst();
            }
            map.put(str, TuplesKt.m104to(mediaData, mediaDeviceData));
            update(str, str);
            return;
        }
        Map<String, Pair<MediaData, MediaDeviceData>> map2 = this.entries;
        Pair remove = map2.remove(str2);
        if (remove != null) {
            mediaData = (MediaData) remove.getFirst();
        }
        map2.put(str, TuplesKt.m104to(mediaData, mediaDeviceData));
        update(str, str2);
    }

    public void onKeyRemoved(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        remove(str);
    }

    public final boolean addListener(@NotNull MediaDataManager.Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        return this.listeners.add(listener);
    }

    private final void update(String str, String str2) {
        Pair pair = this.entries.get(str);
        if (pair == null) {
            pair = TuplesKt.m104to(null, null);
        }
        MediaData mediaData = (MediaData) pair.component1();
        MediaDeviceData mediaDeviceData = (MediaDeviceData) pair.component2();
        if (mediaData != null && mediaDeviceData != null) {
            MediaData copy$default = MediaData.copy$default(mediaData, 0, false, 0, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, (List) null, (List) null, (String) null, (MediaSession.Token) null, (PendingIntent) null, mediaDeviceData, false, (Runnable) null, false, false, (String) null, false, (Boolean) null, false, 0, 8380415, (Object) null);
            for (T onMediaDataLoaded$default : CollectionsKt___CollectionsKt.toSet(this.listeners)) {
                MediaDataManager.Listener.DefaultImpls.onMediaDataLoaded$default(onMediaDataLoaded$default, str, str2, copy$default, false, false, 24, (Object) null);
            }
        }
    }

    private final void remove(String str) {
        if (this.entries.remove(str) != null) {
            for (T onMediaDataRemoved : CollectionsKt___CollectionsKt.toSet(this.listeners)) {
                onMediaDataRemoved.onMediaDataRemoved(str);
            }
        }
    }
}
