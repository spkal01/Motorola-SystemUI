package com.android.systemui.statusbar.policy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: DeviceControlsController.kt */
public interface DeviceControlsController {

    /* compiled from: DeviceControlsController.kt */
    public interface Callback {
        void onControlsUpdate(@Nullable Integer num);
    }

    void removeCallback();

    void setCallback(@NotNull Callback callback);
}
