package com.android.systemui.media;

import com.android.systemui.media.MediaDataManager;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaHost.kt */
public final class MediaHost$listener$1 implements MediaDataManager.Listener {
    final /* synthetic */ MediaHost this$0;

    MediaHost$listener$1(MediaHost mediaHost) {
        this.this$0 = mediaHost;
    }

    public void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(mediaData, "data");
        if (z) {
            this.this$0.updateViewVisibility();
        }
    }

    public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(smartspaceMediaData, "data");
        this.this$0.updateViewVisibility();
    }

    public void onMediaDataRemoved(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        this.this$0.updateViewVisibility();
    }

    public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        if (z) {
            this.this$0.updateViewVisibility();
        }
    }
}
