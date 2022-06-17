package com.android.systemui.media;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaData.kt */
public final class MediaData {
    @NotNull
    private final List<MediaAction> actions;
    @NotNull
    private final List<Integer> actionsToShowInCompact;
    private boolean active;
    @Nullable
    private final String app;
    @Nullable
    private final Icon appIcon;
    @Nullable
    private final CharSequence artist;
    @Nullable
    private final Icon artwork;
    private final int backgroundColor;
    @Nullable
    private final PendingIntent clickIntent;
    @Nullable
    private final MediaDeviceData device;
    private boolean hasCheckedForResume;
    private final boolean initialized;
    private final boolean isClearable;
    private boolean isLocalSession;
    @Nullable
    private final Boolean isPlaying;
    private long lastActive;
    @Nullable
    private final String notificationKey;
    @NotNull
    private final String packageName;
    @Nullable
    private Runnable resumeAction;
    private boolean resumption;
    @Nullable
    private final CharSequence song;
    @Nullable
    private final MediaSession.Token token;
    private final int userId;

    public static /* synthetic */ MediaData copy$default(MediaData mediaData, int i, boolean z, int i2, String str, Icon icon, CharSequence charSequence, CharSequence charSequence2, Icon icon2, List list, List list2, String str2, MediaSession.Token token2, PendingIntent pendingIntent, MediaDeviceData mediaDeviceData, boolean z2, Runnable runnable, boolean z3, boolean z4, String str3, boolean z5, Boolean bool, boolean z6, long j, int i3, Object obj) {
        MediaData mediaData2 = mediaData;
        int i4 = i3;
        return mediaData.copy((i4 & 1) != 0 ? mediaData2.userId : i, (i4 & 2) != 0 ? mediaData2.initialized : z, (i4 & 4) != 0 ? mediaData2.backgroundColor : i2, (i4 & 8) != 0 ? mediaData2.app : str, (i4 & 16) != 0 ? mediaData2.appIcon : icon, (i4 & 32) != 0 ? mediaData2.artist : charSequence, (i4 & 64) != 0 ? mediaData2.song : charSequence2, (i4 & 128) != 0 ? mediaData2.artwork : icon2, (i4 & 256) != 0 ? mediaData2.actions : list, (i4 & 512) != 0 ? mediaData2.actionsToShowInCompact : list2, (i4 & 1024) != 0 ? mediaData2.packageName : str2, (i4 & 2048) != 0 ? mediaData2.token : token2, (i4 & 4096) != 0 ? mediaData2.clickIntent : pendingIntent, (i4 & 8192) != 0 ? mediaData2.device : mediaDeviceData, (i4 & 16384) != 0 ? mediaData2.active : z2, (i4 & 32768) != 0 ? mediaData2.resumeAction : runnable, (i4 & 65536) != 0 ? mediaData2.isLocalSession : z3, (i4 & 131072) != 0 ? mediaData2.resumption : z4, (i4 & 262144) != 0 ? mediaData2.notificationKey : str3, (i4 & 524288) != 0 ? mediaData2.hasCheckedForResume : z5, (i4 & 1048576) != 0 ? mediaData2.isPlaying : bool, (i4 & 2097152) != 0 ? mediaData2.isClearable : z6, (i4 & 4194304) != 0 ? mediaData2.lastActive : j);
    }

    @NotNull
    public final MediaData copy(int i, boolean z, int i2, @Nullable String str, @Nullable Icon icon, @Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @Nullable Icon icon2, @NotNull List<MediaAction> list, @NotNull List<Integer> list2, @NotNull String str2, @Nullable MediaSession.Token token2, @Nullable PendingIntent pendingIntent, @Nullable MediaDeviceData mediaDeviceData, boolean z2, @Nullable Runnable runnable, boolean z3, boolean z4, @Nullable String str3, boolean z5, @Nullable Boolean bool, boolean z6, long j) {
        Intrinsics.checkNotNullParameter(list, "actions");
        Intrinsics.checkNotNullParameter(list2, "actionsToShowInCompact");
        Intrinsics.checkNotNullParameter(str2, "packageName");
        return new MediaData(i, z, i2, str, icon, charSequence, charSequence2, icon2, list, list2, str2, token2, pendingIntent, mediaDeviceData, z2, runnable, z3, z4, str3, z5, bool, z6, j);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MediaData)) {
            return false;
        }
        MediaData mediaData = (MediaData) obj;
        return this.userId == mediaData.userId && this.initialized == mediaData.initialized && this.backgroundColor == mediaData.backgroundColor && Intrinsics.areEqual((Object) this.app, (Object) mediaData.app) && Intrinsics.areEqual((Object) this.appIcon, (Object) mediaData.appIcon) && Intrinsics.areEqual((Object) this.artist, (Object) mediaData.artist) && Intrinsics.areEqual((Object) this.song, (Object) mediaData.song) && Intrinsics.areEqual((Object) this.artwork, (Object) mediaData.artwork) && Intrinsics.areEqual((Object) this.actions, (Object) mediaData.actions) && Intrinsics.areEqual((Object) this.actionsToShowInCompact, (Object) mediaData.actionsToShowInCompact) && Intrinsics.areEqual((Object) this.packageName, (Object) mediaData.packageName) && Intrinsics.areEqual((Object) this.token, (Object) mediaData.token) && Intrinsics.areEqual((Object) this.clickIntent, (Object) mediaData.clickIntent) && Intrinsics.areEqual((Object) this.device, (Object) mediaData.device) && this.active == mediaData.active && Intrinsics.areEqual((Object) this.resumeAction, (Object) mediaData.resumeAction) && this.isLocalSession == mediaData.isLocalSession && this.resumption == mediaData.resumption && Intrinsics.areEqual((Object) this.notificationKey, (Object) mediaData.notificationKey) && this.hasCheckedForResume == mediaData.hasCheckedForResume && Intrinsics.areEqual((Object) this.isPlaying, (Object) mediaData.isPlaying) && this.isClearable == mediaData.isClearable && this.lastActive == mediaData.lastActive;
    }

    public int hashCode() {
        int hashCode = Integer.hashCode(this.userId) * 31;
        boolean z = this.initialized;
        boolean z2 = true;
        if (z) {
            z = true;
        }
        int hashCode2 = (((hashCode + (z ? 1 : 0)) * 31) + Integer.hashCode(this.backgroundColor)) * 31;
        String str = this.app;
        int i = 0;
        int hashCode3 = (hashCode2 + (str == null ? 0 : str.hashCode())) * 31;
        Icon icon = this.appIcon;
        int hashCode4 = (hashCode3 + (icon == null ? 0 : icon.hashCode())) * 31;
        CharSequence charSequence = this.artist;
        int hashCode5 = (hashCode4 + (charSequence == null ? 0 : charSequence.hashCode())) * 31;
        CharSequence charSequence2 = this.song;
        int hashCode6 = (hashCode5 + (charSequence2 == null ? 0 : charSequence2.hashCode())) * 31;
        Icon icon2 = this.artwork;
        int hashCode7 = (((((((hashCode6 + (icon2 == null ? 0 : icon2.hashCode())) * 31) + this.actions.hashCode()) * 31) + this.actionsToShowInCompact.hashCode()) * 31) + this.packageName.hashCode()) * 31;
        MediaSession.Token token2 = this.token;
        int hashCode8 = (hashCode7 + (token2 == null ? 0 : token2.hashCode())) * 31;
        PendingIntent pendingIntent = this.clickIntent;
        int hashCode9 = (hashCode8 + (pendingIntent == null ? 0 : pendingIntent.hashCode())) * 31;
        MediaDeviceData mediaDeviceData = this.device;
        int hashCode10 = (hashCode9 + (mediaDeviceData == null ? 0 : mediaDeviceData.hashCode())) * 31;
        boolean z3 = this.active;
        if (z3) {
            z3 = true;
        }
        int i2 = (hashCode10 + (z3 ? 1 : 0)) * 31;
        Runnable runnable = this.resumeAction;
        int hashCode11 = (i2 + (runnable == null ? 0 : runnable.hashCode())) * 31;
        boolean z4 = this.isLocalSession;
        if (z4) {
            z4 = true;
        }
        int i3 = (hashCode11 + (z4 ? 1 : 0)) * 31;
        boolean z5 = this.resumption;
        if (z5) {
            z5 = true;
        }
        int i4 = (i3 + (z5 ? 1 : 0)) * 31;
        String str2 = this.notificationKey;
        int hashCode12 = (i4 + (str2 == null ? 0 : str2.hashCode())) * 31;
        boolean z6 = this.hasCheckedForResume;
        if (z6) {
            z6 = true;
        }
        int i5 = (hashCode12 + (z6 ? 1 : 0)) * 31;
        Boolean bool = this.isPlaying;
        if (bool != null) {
            i = bool.hashCode();
        }
        int i6 = (i5 + i) * 31;
        boolean z7 = this.isClearable;
        if (!z7) {
            z2 = z7;
        }
        return ((i6 + (z2 ? 1 : 0)) * 31) + Long.hashCode(this.lastActive);
    }

    @NotNull
    public String toString() {
        return "MediaData(userId=" + this.userId + ", initialized=" + this.initialized + ", backgroundColor=" + this.backgroundColor + ", app=" + this.app + ", appIcon=" + this.appIcon + ", artist=" + this.artist + ", song=" + this.song + ", artwork=" + this.artwork + ", actions=" + this.actions + ", actionsToShowInCompact=" + this.actionsToShowInCompact + ", packageName=" + this.packageName + ", token=" + this.token + ", clickIntent=" + this.clickIntent + ", device=" + this.device + ", active=" + this.active + ", resumeAction=" + this.resumeAction + ", isLocalSession=" + this.isLocalSession + ", resumption=" + this.resumption + ", notificationKey=" + this.notificationKey + ", hasCheckedForResume=" + this.hasCheckedForResume + ", isPlaying=" + this.isPlaying + ", isClearable=" + this.isClearable + ", lastActive=" + this.lastActive + ')';
    }

    public MediaData(int i, boolean z, int i2, @Nullable String str, @Nullable Icon icon, @Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @Nullable Icon icon2, @NotNull List<MediaAction> list, @NotNull List<Integer> list2, @NotNull String str2, @Nullable MediaSession.Token token2, @Nullable PendingIntent pendingIntent, @Nullable MediaDeviceData mediaDeviceData, boolean z2, @Nullable Runnable runnable, boolean z3, boolean z4, @Nullable String str3, boolean z5, @Nullable Boolean bool, boolean z6, long j) {
        String str4 = str2;
        Intrinsics.checkNotNullParameter(list, "actions");
        Intrinsics.checkNotNullParameter(list2, "actionsToShowInCompact");
        Intrinsics.checkNotNullParameter(str4, "packageName");
        this.userId = i;
        this.initialized = z;
        this.backgroundColor = i2;
        this.app = str;
        this.appIcon = icon;
        this.artist = charSequence;
        this.song = charSequence2;
        this.artwork = icon2;
        this.actions = list;
        this.actionsToShowInCompact = list2;
        this.packageName = str4;
        this.token = token2;
        this.clickIntent = pendingIntent;
        this.device = mediaDeviceData;
        this.active = z2;
        this.resumeAction = runnable;
        this.isLocalSession = z3;
        this.resumption = z4;
        this.notificationKey = str3;
        this.hasCheckedForResume = z5;
        this.isPlaying = bool;
        this.isClearable = z6;
        this.lastActive = j;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ MediaData(int i, boolean z, int i2, String str, Icon icon, CharSequence charSequence, CharSequence charSequence2, Icon icon2, List list, List list2, String str2, MediaSession.Token token2, PendingIntent pendingIntent, MediaDeviceData mediaDeviceData, boolean z2, Runnable runnable, boolean z3, boolean z4, String str3, boolean z5, Boolean bool, boolean z6, long j, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, (i3 & 2) != 0 ? false : z, i2, str, icon, charSequence, charSequence2, icon2, list, list2, str2, token2, pendingIntent, mediaDeviceData, z2, runnable, (i3 & 65536) != 0 ? true : z3, (i3 & 131072) != 0 ? false : z4, (i3 & 262144) != 0 ? null : str3, (i3 & 524288) != 0 ? false : z5, (i3 & 1048576) != 0 ? null : bool, (i3 & 2097152) != 0 ? true : z6, (i3 & 4194304) != 0 ? 0 : j);
    }

    public final int getUserId() {
        return this.userId;
    }

    public final int getBackgroundColor() {
        return this.backgroundColor;
    }

    @Nullable
    public final String getApp() {
        return this.app;
    }

    @Nullable
    public final Icon getAppIcon() {
        return this.appIcon;
    }

    @Nullable
    public final CharSequence getArtist() {
        return this.artist;
    }

    @Nullable
    public final CharSequence getSong() {
        return this.song;
    }

    @Nullable
    public final Icon getArtwork() {
        return this.artwork;
    }

    @NotNull
    public final List<MediaAction> getActions() {
        return this.actions;
    }

    @NotNull
    public final List<Integer> getActionsToShowInCompact() {
        return this.actionsToShowInCompact;
    }

    @NotNull
    public final String getPackageName() {
        return this.packageName;
    }

    @Nullable
    public final MediaSession.Token getToken() {
        return this.token;
    }

    @Nullable
    public final PendingIntent getClickIntent() {
        return this.clickIntent;
    }

    @Nullable
    public final MediaDeviceData getDevice() {
        return this.device;
    }

    public final boolean getActive() {
        return this.active;
    }

    public final void setActive(boolean z) {
        this.active = z;
    }

    @Nullable
    public final Runnable getResumeAction() {
        return this.resumeAction;
    }

    public final void setResumeAction(@Nullable Runnable runnable) {
        this.resumeAction = runnable;
    }

    public final boolean isLocalSession() {
        return this.isLocalSession;
    }

    public final boolean getResumption() {
        return this.resumption;
    }

    public final boolean getHasCheckedForResume() {
        return this.hasCheckedForResume;
    }

    public final void setHasCheckedForResume(boolean z) {
        this.hasCheckedForResume = z;
    }

    @Nullable
    public final Boolean isPlaying() {
        return this.isPlaying;
    }

    public final boolean isClearable() {
        return this.isClearable;
    }

    public final long getLastActive() {
        return this.lastActive;
    }
}
