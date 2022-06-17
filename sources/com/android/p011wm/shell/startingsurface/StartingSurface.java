package com.android.p011wm.shell.startingsurface;

import android.app.TaskInfo;

/* renamed from: com.android.wm.shell.startingsurface.StartingSurface */
public interface StartingSurface {
    IStartingWindow createExternalInterface() {
        return null;
    }

    int getBackgroundColor(TaskInfo taskInfo) {
        return -16777216;
    }
}
