package com.android.systemui.util.wrapper;

import android.os.Build;

public class BuildInfo {
    public boolean isDebuggable() {
        return Build.IS_DEBUGGABLE;
    }
}
