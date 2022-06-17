package com.android.systemui.media;

/* compiled from: RecommendationViewHolder.kt */
final class RecommendationViewHolder$marquee$1 implements Runnable {
    final /* synthetic */ boolean $start;
    final /* synthetic */ RecommendationViewHolder this$0;

    RecommendationViewHolder$marquee$1(RecommendationViewHolder recommendationViewHolder, boolean z) {
        this.this$0 = recommendationViewHolder;
        this.$start = z;
    }

    public final void run() {
        this.this$0.getLongPressText().setSelected(this.$start);
    }
}
