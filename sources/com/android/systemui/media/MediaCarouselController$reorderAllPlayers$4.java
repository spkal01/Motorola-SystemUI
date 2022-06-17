package com.android.systemui.media;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: MediaCarouselController.kt */
final class MediaCarouselController$reorderAllPlayers$4 extends Lambda implements Function0<Unit> {
    final /* synthetic */ int $activeMediaIndex;
    final /* synthetic */ MediaCarouselController this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    MediaCarouselController$reorderAllPlayers$4(MediaCarouselController mediaCarouselController, int i) {
        super(0);
        this.this$0 = mediaCarouselController;
        this.$activeMediaIndex = i;
    }

    public final void invoke() {
        MediaCarouselScrollHandler.scrollToPlayer$default(this.this$0.getMediaCarouselScrollHandler(), 0, this.$activeMediaIndex, 1, (Object) null);
    }
}
