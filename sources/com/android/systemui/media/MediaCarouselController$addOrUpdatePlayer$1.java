package com.android.systemui.media;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

/* compiled from: MediaCarouselController.kt */
/* synthetic */ class MediaCarouselController$addOrUpdatePlayer$1 extends FunctionReferenceImpl implements Function0<Unit> {
    MediaCarouselController$addOrUpdatePlayer$1(MediaCarouselController mediaCarouselController) {
        super(0, mediaCarouselController, MediaCarouselController.class, "updateCarouselDimensions", "updateCarouselDimensions()V", 0);
    }

    public final void invoke() {
        ((MediaCarouselController) this.receiver).updateCarouselDimensions();
    }
}
