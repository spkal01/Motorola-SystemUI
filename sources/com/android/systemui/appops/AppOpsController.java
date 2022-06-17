package com.android.systemui.appops;

import java.util.List;

public interface AppOpsController {

    public interface Callback {
        void onActiveStateChanged(int i, int i2, String str, boolean z);
    }

    void addCallback(int[] iArr, Callback callback);

    List<AppOpItem> getActiveAppOps();

    List<AppOpItem> getActiveAppOps(boolean z);

    boolean isMicMuted();

    void removeCallback(int[] iArr, Callback callback);
}
