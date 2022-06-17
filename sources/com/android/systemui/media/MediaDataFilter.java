package com.android.systemui.media;

import android.app.PendingIntent;
import android.app.smartspace.SmartspaceAction;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.util.Log;
import androidx.appcompat.R$styleable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.util.time.SystemClock;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaDataFilter.kt */
public final class MediaDataFilter implements MediaDataManager.Listener {
    @NotNull
    private final Set<MediaDataManager.Listener> _listeners = new LinkedHashSet();
    @NotNull
    private final LinkedHashMap<String, MediaData> allEntries = new LinkedHashMap<>();
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    /* access modifiers changed from: private */
    @NotNull
    public final Executor executor;
    @NotNull
    private final NotificationLockscreenUserManager lockscreenUserManager;
    public MediaCarouselController mediaCarouselController;
    public MediaDataManager mediaDataManager;
    @NotNull
    private final MediaResumeListener mediaResumeListener;
    @Nullable
    private String reactivatedKey;
    @NotNull
    private SmartspaceMediaData smartspaceMediaData = MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA();
    @NotNull
    private final SystemClock systemClock;
    /* access modifiers changed from: private */
    @NotNull
    public final LinkedHashMap<String, MediaData> userEntries = new LinkedHashMap<>();
    @NotNull
    private final CurrentUserTracker userTracker;

    public MediaDataFilter(@NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull MediaResumeListener mediaResumeListener2, @NotNull NotificationLockscreenUserManager notificationLockscreenUserManager, @NotNull Executor executor2, @NotNull SystemClock systemClock2) {
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(mediaResumeListener2, "mediaResumeListener");
        Intrinsics.checkNotNullParameter(notificationLockscreenUserManager, "lockscreenUserManager");
        Intrinsics.checkNotNullParameter(executor2, "executor");
        Intrinsics.checkNotNullParameter(systemClock2, "systemClock");
        this.broadcastDispatcher = broadcastDispatcher2;
        this.mediaResumeListener = mediaResumeListener2;
        this.lockscreenUserManager = notificationLockscreenUserManager;
        this.executor = executor2;
        this.systemClock = systemClock2;
        C10161 r3 = new CurrentUserTracker(this, broadcastDispatcher2) {
            final /* synthetic */ MediaDataFilter this$0;

            {
                this.this$0 = r1;
            }

            public void onUserSwitched(int i) {
                this.this$0.executor.execute(new MediaDataFilter$1$onUserSwitched$1(this.this$0, i));
            }
        };
        this.userTracker = r3;
        r3.startTracking();
    }

    @NotNull
    /* renamed from: getListeners$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final Set<MediaDataManager.Listener> mo14182xef59304f() {
        return CollectionsKt___CollectionsKt.toSet(this._listeners);
    }

    @NotNull
    /* renamed from: getMediaDataManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final MediaDataManager mo14184x7abd45b() {
        MediaDataManager mediaDataManager2 = this.mediaDataManager;
        if (mediaDataManager2 != null) {
            return mediaDataManager2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mediaDataManager");
        throw null;
    }

    /* renamed from: setMediaDataManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo14190x3600a367(@NotNull MediaDataManager mediaDataManager2) {
        Intrinsics.checkNotNullParameter(mediaDataManager2, "<set-?>");
        this.mediaDataManager = mediaDataManager2;
    }

    @NotNull
    /* renamed from: getMediaCarouselController$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final MediaCarouselController mo14183xa7c29830() {
        MediaCarouselController mediaCarouselController2 = this.mediaCarouselController;
        if (mediaCarouselController2 != null) {
            return mediaCarouselController2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mediaCarouselController");
        throw null;
    }

    public void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData, "data");
        if (str2 != null && !Intrinsics.areEqual((Object) str2, (Object) str)) {
            this.allEntries.remove(str2);
        }
        this.allEntries.put(str, mediaData);
        if (this.lockscreenUserManager.isCurrentProfile(mediaData.getUserId())) {
            if (str2 != null && !Intrinsics.areEqual((Object) str2, (Object) str)) {
                this.userEntries.remove(str2);
            }
            this.userEntries.put(str, mediaData);
            for (MediaDataManager.Listener onMediaDataLoaded$default : mo14182xef59304f()) {
                MediaDataManager.Listener.DefaultImpls.onMediaDataLoaded$default(onMediaDataLoaded$default, str, str2, mediaData, false, z2, 8, (Object) null);
            }
        }
    }

    public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData2, boolean z) {
        String str2 = str;
        SmartspaceMediaData smartspaceMediaData3 = smartspaceMediaData2;
        Intrinsics.checkNotNullParameter(str2, "key");
        Intrinsics.checkNotNullParameter(smartspaceMediaData3, "data");
        if (!smartspaceMediaData2.isActive()) {
            Log.d("MediaDataFilter", "Inactive recommendation data. Skip triggering.");
            return;
        }
        this.smartspaceMediaData = smartspaceMediaData3;
        SortedMap<String, MediaData> sortedMap = MapsKt__MapsJVMKt.toSortedMap(this.userEntries, new MediaDataFilter$onSmartspaceMediaDataLoaded$$inlined$compareBy$1(this));
        long timeSinceActiveForMostRecentMedia = timeSinceActiveForMostRecentMedia(sortedMap);
        long smartspace_max_age = MediaDataFilterKt.getSMARTSPACE_MAX_AGE();
        SmartspaceAction cardAction = smartspaceMediaData2.getCardAction();
        if (cardAction != null) {
            long j = cardAction.getExtras().getLong("resumable_media_max_age_seconds", 0);
            if (j > 0) {
                smartspace_max_age = TimeUnit.SECONDS.toMillis(j);
            }
        }
        boolean z2 = true;
        if (timeSinceActiveForMostRecentMedia < smartspace_max_age) {
            String lastKey = sortedMap.lastKey();
            Log.d("MediaDataFilter", "reactivating " + lastKey + " instead of smartspace");
            this.reactivatedKey = lastKey;
            if (mo14183xa7c29830().getMediaPlayerData().firstActiveMediaIndex() != -1) {
                z2 = false;
            }
            MediaData mediaData = (MediaData) sortedMap.get(lastKey);
            Intrinsics.checkNotNull(mediaData);
            MediaData copy$default = MediaData.copy$default(mediaData, 0, false, 0, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, (List) null, (List) null, (String) null, (MediaSession.Token) null, (PendingIntent) null, (MediaDeviceData) null, true, (Runnable) null, false, false, (String) null, false, (Boolean) null, false, 0, 8372223, (Object) null);
            for (MediaDataManager.Listener onMediaDataLoaded$default : mo14182xef59304f()) {
                Intrinsics.checkNotNullExpressionValue(lastKey, "lastActiveKey");
                MediaDataManager.Listener.DefaultImpls.onMediaDataLoaded$default(onMediaDataLoaded$default, lastKey, lastKey, copy$default, false, z2, 8, (Object) null);
            }
            z2 = false;
        }
        if (!smartspaceMediaData2.isValid()) {
            Log.d("MediaDataFilter", "Invalid recommendation data. Skip showing the rec card");
            return;
        }
        for (MediaDataManager.Listener onSmartspaceMediaDataLoaded : mo14182xef59304f()) {
            onSmartspaceMediaDataLoaded.onSmartspaceMediaDataLoaded(str2, smartspaceMediaData3, z2);
        }
    }

    public void onMediaDataRemoved(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        this.allEntries.remove(str);
        if (((MediaData) this.userEntries.remove(str)) != null) {
            for (MediaDataManager.Listener onMediaDataRemoved : mo14182xef59304f()) {
                onMediaDataRemoved.onMediaDataRemoved(str);
            }
        }
    }

    public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        String str2 = this.reactivatedKey;
        if (str2 != null) {
            this.reactivatedKey = null;
            Log.d("MediaDataFilter", Intrinsics.stringPlus("expiring reactivated key ", str2));
            MediaData mediaData = this.userEntries.get(str2);
            if (mediaData != null) {
                for (MediaDataManager.Listener onMediaDataLoaded$default : mo14182xef59304f()) {
                    MediaDataManager.Listener.DefaultImpls.onMediaDataLoaded$default(onMediaDataLoaded$default, str2, str2, mediaData, z, false, 16, (Object) null);
                }
            }
        }
        if (this.smartspaceMediaData.isActive()) {
            this.smartspaceMediaData = SmartspaceMediaData.copy$default(MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA(), this.smartspaceMediaData.getTargetId(), false, this.smartspaceMediaData.isValid(), (String) null, (SmartspaceAction) null, (List) null, 0, R$styleable.AppCompatTheme_windowFixedWidthMajor, (Object) null);
        }
        for (MediaDataManager.Listener onSmartspaceMediaDataRemoved : mo14182xef59304f()) {
            onSmartspaceMediaDataRemoved.onSmartspaceMediaDataRemoved(str, z);
        }
    }

    @VisibleForTesting
    /* renamed from: handleUserSwitched$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo14185xa9aae04c(int i) {
        Set<MediaDataManager.Listener> listeners$frameworks__base__packages__SystemUI__android_common__SystemUI_core = mo14182xef59304f();
        Set<String> keySet = this.userEntries.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet, "userEntries.keys");
        List<T> mutableList = CollectionsKt___CollectionsKt.toMutableList(keySet);
        this.userEntries.clear();
        for (T t : mutableList) {
            Log.d("MediaDataFilter", "Removing " + t + " after user change");
            for (MediaDataManager.Listener onMediaDataRemoved : listeners$frameworks__base__packages__SystemUI__android_common__SystemUI_core) {
                Intrinsics.checkNotNullExpressionValue(t, "it");
                onMediaDataRemoved.onMediaDataRemoved(t);
            }
        }
        for (Map.Entry next : this.allEntries.entrySet()) {
            String str = (String) next.getKey();
            MediaData mediaData = (MediaData) next.getValue();
            if (this.lockscreenUserManager.isCurrentProfile(mediaData.getUserId())) {
                Log.d("MediaDataFilter", "Re-adding " + str + " after user change");
                this.userEntries.put(str, mediaData);
                for (MediaDataManager.Listener onMediaDataLoaded$default : listeners$frameworks__base__packages__SystemUI__android_common__SystemUI_core) {
                    MediaDataManager.Listener.DefaultImpls.onMediaDataLoaded$default(onMediaDataLoaded$default, str, (String) null, mediaData, false, false, 24, (Object) null);
                }
            }
        }
    }

    public final void onSwipeToDismiss() {
        Log.d("MediaDataFilter", "Media carousel swiped away");
        Set<String> keySet = this.userEntries.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet, "userEntries.keys");
        for (T t : CollectionsKt___CollectionsKt.toSet(keySet)) {
            MediaDataManager mediaDataManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core = mo14184x7abd45b();
            Intrinsics.checkNotNullExpressionValue(t, "it");
            mediaDataManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core.mo14208x855293df(t, true, true);
        }
        if (this.smartspaceMediaData.isActive()) {
            this.smartspaceMediaData = SmartspaceMediaData.copy$default(MediaDataManagerKt.getEMPTY_SMARTSPACE_MEDIA_DATA(), this.smartspaceMediaData.getTargetId(), false, this.smartspaceMediaData.isValid(), (String) null, (SmartspaceAction) null, (List) null, 0, R$styleable.AppCompatTheme_windowFixedWidthMajor, (Object) null);
        }
        mo14184x7abd45b().dismissSmartspaceRecommendation(this.smartspaceMediaData.getTargetId(), 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean hasActiveMedia() {
        /*
            r4 = this;
            java.util.LinkedHashMap<java.lang.String, com.android.systemui.media.MediaData> r0 = r4.userEntries
            boolean r1 = r0.isEmpty()
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x000c
        L_0x000a:
            r0 = r3
            goto L_0x002d
        L_0x000c:
            java.util.Set r0 = r0.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0014:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x000a
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r1 = r1.getValue()
            com.android.systemui.media.MediaData r1 = (com.android.systemui.media.MediaData) r1
            boolean r1 = r1.getActive()
            if (r1 == 0) goto L_0x0014
            r0 = r2
        L_0x002d:
            if (r0 != 0) goto L_0x0039
            com.android.systemui.media.SmartspaceMediaData r4 = r4.smartspaceMediaData
            boolean r4 = r4.isActive()
            if (r4 == 0) goto L_0x0038
            goto L_0x0039
        L_0x0038:
            r2 = r3
        L_0x0039:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.MediaDataFilter.hasActiveMedia():boolean");
    }

    public final boolean hasAnyMedia() {
        return (this.userEntries.isEmpty() ^ true) || this.smartspaceMediaData.isActive();
    }

    public final boolean addListener(@NotNull MediaDataManager.Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        return this._listeners.add(listener);
    }

    public final boolean removeListener(@NotNull MediaDataManager.Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        return this._listeners.remove(listener);
    }

    private final long timeSinceActiveForMostRecentMedia(SortedMap<String, MediaData> sortedMap) {
        if (sortedMap.isEmpty()) {
            return Long.MAX_VALUE;
        }
        long elapsedRealtime = this.systemClock.elapsedRealtime();
        MediaData mediaData = (MediaData) sortedMap.get(sortedMap.lastKey());
        if (mediaData == null) {
            return Long.MAX_VALUE;
        }
        return elapsedRealtime - mediaData.getLastActive();
    }
}
