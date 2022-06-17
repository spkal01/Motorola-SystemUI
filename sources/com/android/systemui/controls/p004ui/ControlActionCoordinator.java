package com.android.systemui.controls.p004ui;

import android.content.Context;
import android.service.controls.Control;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinator */
/* compiled from: ControlActionCoordinator.kt */
public interface ControlActionCoordinator {
    void closeDialogs();

    void drag(boolean z);

    void enableActionOnTouch(@NotNull String str);

    void longPress(@NotNull ControlViewHolder controlViewHolder);

    void runPendingAction(@NotNull String str);

    void setActivityContext(@NotNull Context context);

    void setValue(@NotNull ControlViewHolder controlViewHolder, @NotNull String str, float f);

    void toggle(@NotNull ControlViewHolder controlViewHolder, @NotNull String str, boolean z);

    void touch(@NotNull ControlViewHolder controlViewHolder, @NotNull String str, @NotNull Control control);
}
