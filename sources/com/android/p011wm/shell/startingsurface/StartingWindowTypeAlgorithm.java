package com.android.p011wm.shell.startingsurface;

import android.window.StartingWindowInfo;

/* renamed from: com.android.wm.shell.startingsurface.StartingWindowTypeAlgorithm */
public interface StartingWindowTypeAlgorithm {
    @StartingWindowInfo.StartingWindowType
    int getSuggestedWindowType(StartingWindowInfo startingWindowInfo);
}
