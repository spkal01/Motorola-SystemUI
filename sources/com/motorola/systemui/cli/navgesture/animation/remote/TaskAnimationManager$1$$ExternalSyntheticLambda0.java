package com.motorola.systemui.cli.navgesture.animation.remote;

import com.android.systemui.shared.recents.model.ThumbnailData;
import com.motorola.systemui.cli.navgesture.animation.remote.TaskAnimationManager;

public final /* synthetic */ class TaskAnimationManager$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ TaskAnimationManager.C27121 f$0;
    public final /* synthetic */ ThumbnailData f$1;

    public /* synthetic */ TaskAnimationManager$1$$ExternalSyntheticLambda0(TaskAnimationManager.C27121 r1, ThumbnailData thumbnailData) {
        this.f$0 = r1;
        this.f$1 = thumbnailData;
    }

    public final void run() {
        this.f$0.lambda$onRecentsAnimationCanceled$0(this.f$1);
    }
}
