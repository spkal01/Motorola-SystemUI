package com.android.systemui.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.smartspace.SmartspaceAction;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.service.notification.StatusBarNotification;
import com.android.internal.annotations.VisibleForTesting;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MediaDataManager.kt */
public final class MediaDataManagerKt {
    /* access modifiers changed from: private */
    @NotNull
    public static final String[] ART_URIS = {"android.media.metadata.ALBUM_ART_URI", "android.media.metadata.ART_URI", "android.media.metadata.DISPLAY_ICON_URI"};
    @NotNull
    private static final SmartspaceMediaData EMPTY_SMARTSPACE_MEDIA_DATA = new SmartspaceMediaData("INVALID", false, false, "INVALID", (SmartspaceAction) null, CollectionsKt__CollectionsKt.emptyList(), 0);
    /* access modifiers changed from: private */
    @NotNull
    public static final MediaData LOADING = new MediaData(-1, false, 0, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, CollectionsKt__CollectionsKt.emptyList(), CollectionsKt__CollectionsKt.emptyList(), "INVALID", (MediaSession.Token) null, (PendingIntent) null, (MediaDeviceData) null, true, (Runnable) null, false, false, (String) null, false, (Boolean) null, false, 0, 8323072, (DefaultConstructorMarker) null);

    @VisibleForTesting
    public static /* synthetic */ void getEMPTY_SMARTSPACE_MEDIA_DATA$annotations() {
    }

    @NotNull
    public static final SmartspaceMediaData getEMPTY_SMARTSPACE_MEDIA_DATA() {
        return EMPTY_SMARTSPACE_MEDIA_DATA;
    }

    public static final boolean isMediaNotification(@NotNull StatusBarNotification statusBarNotification) {
        Intrinsics.checkNotNullParameter(statusBarNotification, "sbn");
        if (!statusBarNotification.getNotification().hasMediaSession()) {
            return false;
        }
        Class notificationStyle = statusBarNotification.getNotification().getNotificationStyle();
        if (Notification.DecoratedMediaCustomViewStyle.class.equals(notificationStyle) || Notification.MediaStyle.class.equals(notificationStyle)) {
            return true;
        }
        return false;
    }
}
