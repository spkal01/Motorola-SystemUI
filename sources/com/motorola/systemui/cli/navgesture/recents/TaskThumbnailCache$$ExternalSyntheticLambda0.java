package com.motorola.systemui.cli.navgesture.recents;

import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.function.Consumer;

public final /* synthetic */ class TaskThumbnailCache$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ Task f$0;

    public /* synthetic */ TaskThumbnailCache$$ExternalSyntheticLambda0(Task task) {
        this.f$0 = task;
    }

    public final void accept(Object obj) {
        this.f$0.thumbnail = (ThumbnailData) obj;
    }
}
