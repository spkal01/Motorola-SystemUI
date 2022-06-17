package com.android.systemui.media;

/* compiled from: MediaCarouselScrollHandler.kt */
final class MediaCarouselScrollHandler$onTouch$1 implements Runnable {
    final /* synthetic */ int $newScrollX;
    final /* synthetic */ MediaCarouselScrollHandler this$0;

    MediaCarouselScrollHandler$onTouch$1(MediaCarouselScrollHandler mediaCarouselScrollHandler, int i) {
        this.this$0 = mediaCarouselScrollHandler;
        this.$newScrollX = i;
    }

    public final void run() {
        this.this$0.scrollView.smoothScrollTo(this.$newScrollX, this.this$0.scrollView.getScrollY());
    }
}
