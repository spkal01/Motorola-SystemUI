package com.android.systemui.statusbar.notification;

import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.Utils;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: NotificationSectionsFeatureManager.kt */
public final class NotificationSectionsFeatureManager {
    @NotNull
    private final Context context;
    @NotNull
    private final DeviceConfigProxy proxy;

    public NotificationSectionsFeatureManager(@NotNull DeviceConfigProxy deviceConfigProxy, @NotNull Context context2) {
        Intrinsics.checkNotNullParameter(deviceConfigProxy, "proxy");
        Intrinsics.checkNotNullParameter(context2, "context");
        this.proxy = deviceConfigProxy;
        this.context = context2;
    }

    public final boolean isFilteringEnabled() {
        return NotificationSectionsFeatureManagerKt.usePeopleFiltering(this.proxy);
    }

    public final boolean isMediaControlsEnabled() {
        return Utils.useQsMediaPlayer(this.context);
    }

    @NotNull
    public final int[] getNotificationBuckets() {
        if (isFilteringEnabled() && isMediaControlsEnabled()) {
            return new int[]{2, 3, 1, 4, 5, 6};
        }
        if (!isFilteringEnabled() && isMediaControlsEnabled()) {
            return new int[]{2, 3, 1, 5, 6};
        }
        if (!isFilteringEnabled() || isMediaControlsEnabled()) {
            return new int[]{5, 6};
        }
        return new int[]{2, 3, 4, 5, 6};
    }

    public final int getNumberOfBuckets() {
        return getNotificationBuckets().length;
    }

    @VisibleForTesting
    public final void clearCache() {
        NotificationSectionsFeatureManagerKt.sUsePeopleFiltering = null;
    }
}
