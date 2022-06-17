package com.motorola.systemui.cli.navgesture.recents;

import android.view.View;
import com.android.systemui.shared.recents.model.ThumbnailData;

public interface ITaskViewAware {
    View asView();

    int getTaskId();

    View getThumbnail();

    ThumbnailData getThumbnailData();
}
