package com.android.systemui.media;

import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.statusbar.phone.NotificationListenerWithPlugins;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaSessionBasedFilter.kt */
public final class MediaSessionBasedFilter implements MediaDataManager.Listener {
    @NotNull
    private final Executor backgroundExecutor;
    @NotNull
    private final Executor foregroundExecutor;
    /* access modifiers changed from: private */
    @NotNull
    public final Map<String, Set<MediaSession.Token>> keyedTokens = new LinkedHashMap();
    /* access modifiers changed from: private */
    @NotNull
    public final Set<MediaDataManager.Listener> listeners = new LinkedHashSet();
    /* access modifiers changed from: private */
    @NotNull
    public final LinkedHashMap<String, List<MediaController>> packageControllers = new LinkedHashMap<>();
    /* access modifiers changed from: private */
    @NotNull
    public final MediaSessionBasedFilter$sessionListener$1 sessionListener = new MediaSessionBasedFilter$sessionListener$1(this);
    /* access modifiers changed from: private */
    @NotNull
    public final MediaSessionManager sessionManager;
    /* access modifiers changed from: private */
    @NotNull
    public final Set<MediaSession.Token> tokensWithNotifications = new LinkedHashSet();

    public MediaSessionBasedFilter(@NotNull final Context context, @NotNull MediaSessionManager mediaSessionManager, @NotNull Executor executor, @NotNull Executor executor2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(mediaSessionManager, "sessionManager");
        Intrinsics.checkNotNullParameter(executor, "foregroundExecutor");
        Intrinsics.checkNotNullParameter(executor2, "backgroundExecutor");
        this.sessionManager = mediaSessionManager;
        this.foregroundExecutor = executor;
        this.backgroundExecutor = executor2;
        executor2.execute(new Runnable() {
            public final void run() {
                ComponentName componentName = new ComponentName(context, NotificationListenerWithPlugins.class);
                this.sessionManager.addOnActiveSessionsChangedListener(this.sessionListener, componentName);
                MediaSessionBasedFilter mediaSessionBasedFilter = this;
                List<MediaController> activeSessions = mediaSessionBasedFilter.sessionManager.getActiveSessions(componentName);
                Intrinsics.checkNotNullExpressionValue(activeSessions, "sessionManager.getActiveSessions(name)");
                mediaSessionBasedFilter.handleControllersChanged(activeSessions);
            }
        });
    }

    public final boolean addListener(@NotNull MediaDataManager.Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        return this.listeners.add(listener);
    }

    public void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData, "data");
        this.backgroundExecutor.execute(new MediaSessionBasedFilter$onMediaDataLoaded$1(mediaData, str2, str, this, z));
    }

    public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(smartspaceMediaData, "data");
        this.backgroundExecutor.execute(new MediaSessionBasedFilter$onSmartspaceMediaDataLoaded$1(this, str, smartspaceMediaData));
    }

    public void onMediaDataRemoved(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        this.backgroundExecutor.execute(new MediaSessionBasedFilter$onMediaDataRemoved$1(this, str));
    }

    public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        this.backgroundExecutor.execute(new MediaSessionBasedFilter$onSmartspaceMediaDataRemoved$1(this, str, z));
    }

    /* access modifiers changed from: private */
    public final void dispatchMediaDataLoaded(String str, String str2, MediaData mediaData, boolean z) {
        this.foregroundExecutor.execute(new MediaSessionBasedFilter$dispatchMediaDataLoaded$1(this, str, str2, mediaData, z));
    }

    /* access modifiers changed from: private */
    public final void dispatchMediaDataRemoved(String str) {
        this.foregroundExecutor.execute(new MediaSessionBasedFilter$dispatchMediaDataRemoved$1(this, str));
    }

    /* access modifiers changed from: private */
    public final void dispatchSmartspaceMediaDataLoaded(String str, SmartspaceMediaData smartspaceMediaData) {
        this.foregroundExecutor.execute(new MediaSessionBasedFilter$dispatchSmartspaceMediaDataLoaded$1(this, str, smartspaceMediaData));
    }

    /* access modifiers changed from: private */
    public final void dispatchSmartspaceMediaDataRemoved(String str, boolean z) {
        this.foregroundExecutor.execute(new MediaSessionBasedFilter$dispatchSmartspaceMediaDataRemoved$1(this, str, z));
    }

    /* access modifiers changed from: private */
    public final void handleControllersChanged(List<MediaController> list) {
        Boolean bool;
        this.packageControllers.clear();
        for (MediaController mediaController : list) {
            List list2 = this.packageControllers.get(mediaController.getPackageName());
            if (list2 == null) {
                bool = null;
            } else {
                bool = Boolean.valueOf(list2.add(mediaController));
            }
            if (bool == null) {
                List list3 = (List) this.packageControllers.put(mediaController.getPackageName(), CollectionsKt__CollectionsKt.mutableListOf(mediaController));
            }
        }
        Set<MediaSession.Token> set = this.tokensWithNotifications;
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (MediaController sessionToken : list) {
            arrayList.add(sessionToken.getSessionToken());
        }
        set.retainAll(arrayList);
    }
}
