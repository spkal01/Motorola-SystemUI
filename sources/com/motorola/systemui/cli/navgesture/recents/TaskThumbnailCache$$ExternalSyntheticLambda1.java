package com.motorola.systemui.cli.navgesture.recents;

import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.function.Consumer;

public final /* synthetic */ class TaskThumbnailCache$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ Task f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ TaskThumbnailCache$$ExternalSyntheticLambda1(Task task, Consumer consumer) {
        this.f$0 = task;
        this.f$1 = consumer;
    }

    public final void accept(Object obj) {
        TaskThumbnailCache.lambda$updateThumbnailInBackground$1(this.f$0, this.f$1, (ThumbnailData) obj);
    }
}
