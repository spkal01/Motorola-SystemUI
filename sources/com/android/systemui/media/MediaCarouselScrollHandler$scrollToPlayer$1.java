package com.android.systemui.media;

import android.view.View;

/* compiled from: MediaCarouselScrollHandler.kt */
final class MediaCarouselScrollHandler$scrollToPlayer$1 implements Runnable {
    final /* synthetic */ View $view;
    final /* synthetic */ MediaCarouselScrollHandler this$0;

    MediaCarouselScrollHandler$scrollToPlayer$1(MediaCarouselScrollHandler mediaCarouselScrollHandler, View view) {
        this.this$0 = mediaCarouselScrollHandler;
        this.$view = view;
    }

    public final void run() {
        this.this$0.scrollView.smoothScrollTo(this.$view.getLeft(), this.this$0.scrollView.getScrollY());
    }
}
