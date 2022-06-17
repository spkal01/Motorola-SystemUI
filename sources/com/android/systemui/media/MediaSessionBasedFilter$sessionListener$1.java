package com.android.systemui.media;

import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MediaSessionBasedFilter.kt */
public final class MediaSessionBasedFilter$sessionListener$1 implements MediaSessionManager.OnActiveSessionsChangedListener {
    final /* synthetic */ MediaSessionBasedFilter this$0;

    MediaSessionBasedFilter$sessionListener$1(MediaSessionBasedFilter mediaSessionBasedFilter) {
        this.this$0 = mediaSessionBasedFilter;
    }

    public void onActiveSessionsChanged(@NotNull List<MediaController> list) {
        Intrinsics.checkNotNullParameter(list, "controllers");
        this.this$0.handleControllersChanged(list);
    }
}
