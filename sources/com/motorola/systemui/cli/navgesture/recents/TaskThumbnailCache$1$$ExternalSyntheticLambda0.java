package com.motorola.systemui.cli.navgesture.recents;

import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.motorola.systemui.cli.navgesture.recents.TaskThumbnailCache;
import java.util.function.Consumer;

public final /* synthetic */ class TaskThumbnailCache$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ TaskThumbnailCache.C27201 f$0;
    public final /* synthetic */ Task.TaskKey f$1;
    public final /* synthetic */ ThumbnailData f$2;
    public final /* synthetic */ Consumer f$3;

    public /* synthetic */ TaskThumbnailCache$1$$ExternalSyntheticLambda0(TaskThumbnailCache.C27201 r1, Task.TaskKey taskKey, ThumbnailData thumbnailData, Consumer consumer) {
        this.f$0 = r1;
        this.f$1 = taskKey;
        this.f$2 = thumbnailData;
        this.f$3 = consumer;
    }

    public final void run() {
        this.f$0.lambda$run$0(this.f$1, this.f$2, this.f$3);
    }
}
