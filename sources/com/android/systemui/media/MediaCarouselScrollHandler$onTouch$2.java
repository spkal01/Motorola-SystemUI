package com.android.systemui.media;

/* compiled from: MediaCarouselScrollHandler.kt */
final class MediaCarouselScrollHandler$onTouch$2 implements Runnable {
    final /* synthetic */ MediaCarouselScrollHandler this$0;

    MediaCarouselScrollHandler$onTouch$2(MediaCarouselScrollHandler mediaCarouselScrollHandler) {
        this.this$0 = mediaCarouselScrollHandler;
    }

    public final void run() {
        this.this$0.dismissCallback.invoke();
    }
}
