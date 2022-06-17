package com.android.systemui.media;

import android.app.Notification;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.service.notification.StatusBarNotification;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;

/* compiled from: MediaDataManager.kt */
final class MediaDataManager$loadMediaDataInBg$1 implements Runnable {
    final /* synthetic */ List<MediaAction> $actionIcons;
    final /* synthetic */ Ref$ObjectRef<List<Integer>> $actionsToShowCollapsed;
    final /* synthetic */ String $app;
    final /* synthetic */ Icon $artWorkIcon;
    final /* synthetic */ Ref$ObjectRef<CharSequence> $artist;
    final /* synthetic */ boolean $isLocalSession;
    final /* synthetic */ Boolean $isPlaying;
    final /* synthetic */ String $key;
    final /* synthetic */ long $lastActive;
    final /* synthetic */ Notification $notif;
    final /* synthetic */ String $oldKey;
    final /* synthetic */ StatusBarNotification $sbn;
    final /* synthetic */ Icon $smallIcon;
    final /* synthetic */ Ref$ObjectRef<CharSequence> $song;
    final /* synthetic */ MediaSession.Token $token;
    final /* synthetic */ MediaDataManager this$0;

    MediaDataManager$loadMediaDataInBg$1(MediaDataManager mediaDataManager, String str, String str2, StatusBarNotification statusBarNotification, String str3, Icon icon, Ref$ObjectRef<CharSequence> ref$ObjectRef, Ref$ObjectRef<CharSequence> ref$ObjectRef2, Icon icon2, List<MediaAction> list, Ref$ObjectRef<List<Integer>> ref$ObjectRef3, MediaSession.Token token, Notification notification, boolean z, Boolean bool, long j) {
        this.this$0 = mediaDataManager;
        this.$key = str;
        this.$oldKey = str2;
        this.$sbn = statusBarNotification;
        this.$app = str3;
        this.$smallIcon = icon;
        this.$artist = ref$ObjectRef;
        this.$song = ref$ObjectRef2;
        this.$artWorkIcon = icon2;
        this.$actionIcons = list;
        this.$actionsToShowCollapsed = ref$ObjectRef3;
        this.$token = token;
        this.$notif = notification;
        this.$isLocalSession = z;
        this.$isPlaying = bool;
        this.$lastActive = j;
    }

    public final void run() {
        MediaData mediaData = (MediaData) this.this$0.mediaEntries.get(this.$key);
        Boolean bool = null;
        Runnable resumeAction = mediaData == null ? null : mediaData.getResumeAction();
        MediaData mediaData2 = (MediaData) this.this$0.mediaEntries.get(this.$key);
        if (mediaData2 != null) {
            bool = Boolean.valueOf(mediaData2.getHasCheckedForResume());
        }
        boolean areEqual = Intrinsics.areEqual((Object) bool, (Object) Boolean.TRUE);
        MediaData mediaData3 = (MediaData) this.this$0.mediaEntries.get(this.$key);
        boolean active = mediaData3 == null ? true : mediaData3.getActive();
        MediaDataManager mediaDataManager = this.this$0;
        String str = this.$key;
        String str2 = this.$oldKey;
        String packageName = this.$sbn.getPackageName();
        Intrinsics.checkNotNullExpressionValue(packageName, "sbn.packageName");
        MediaData mediaData4 = r3;
        MediaData mediaData5 = new MediaData(this.$sbn.getNormalizedUserId(), true, this.this$0.bgColor, this.$app, this.$smallIcon, (CharSequence) this.$artist.element, (CharSequence) this.$song.element, this.$artWorkIcon, this.$actionIcons, (List) this.$actionsToShowCollapsed.element, packageName, this.$token, this.$notif.contentIntent, (MediaDeviceData) null, active, resumeAction, this.$isLocalSession, false, this.$key, areEqual, this.$isPlaying, this.$sbn.isClearable(), this.$lastActive, 131072, (DefaultConstructorMarker) null);
        mediaDataManager.onMediaDataLoaded(str, str2, mediaData4);
    }
}
