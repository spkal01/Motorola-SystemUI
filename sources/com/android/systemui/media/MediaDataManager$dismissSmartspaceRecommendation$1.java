package com.android.systemui.media;

/* compiled from: MediaDataManager.kt */
final class MediaDataManager$dismissSmartspaceRecommendation$1 implements Runnable {
    final /* synthetic */ MediaDataManager this$0;

    MediaDataManager$dismissSmartspaceRecommendation$1(MediaDataManager mediaDataManager) {
        this.this$0 = mediaDataManager;
    }

    public final void run() {
        MediaDataManager mediaDataManager = this.this$0;
        mediaDataManager.notifySmartspaceMediaDataRemoved(mediaDataManager.getSmartspaceMediaData().getTargetId(), true);
    }
}
