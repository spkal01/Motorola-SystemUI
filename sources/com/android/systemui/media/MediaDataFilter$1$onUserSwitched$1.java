package com.android.systemui.media;

/* compiled from: MediaDataFilter.kt */
final class MediaDataFilter$1$onUserSwitched$1 implements Runnable {
    final /* synthetic */ int $newUserId;
    final /* synthetic */ MediaDataFilter this$0;

    MediaDataFilter$1$onUserSwitched$1(MediaDataFilter mediaDataFilter, int i) {
        this.this$0 = mediaDataFilter;
        this.$newUserId = i;
    }

    public final void run() {
        this.this$0.mo14185xa9aae04c(this.$newUserId);
    }
}
