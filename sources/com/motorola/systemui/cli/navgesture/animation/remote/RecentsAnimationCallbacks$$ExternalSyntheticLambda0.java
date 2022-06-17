package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.recents.model.ThumbnailData;

public final /* synthetic */ class RecentsAnimationCallbacks$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ RecentsAnimationCallbacks f$0;
    public final /* synthetic */ ThumbnailData f$1;

    public /* synthetic */ RecentsAnimationCallbacks$$ExternalSyntheticLambda0(RecentsAnimationCallbacks recentsAnimationCallbacks, ThumbnailData thumbnailData) {
        this.f$0 = recentsAnimationCallbacks;
        this.f$1 = thumbnailData;
    }

    public final void run() {
        this.f$0.lambda$onAnimationCanceled$1(this.f$1);
    }
}
