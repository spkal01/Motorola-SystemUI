package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.content.Context;
import android.service.controls.Control;
import android.view.ViewGroup;
import com.android.systemui.controls.controller.StructureInfo;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlsUiController */
/* compiled from: ControlsUiController.kt */
public interface ControlsUiController {
    @NotNull
    StructureInfo getPreferredStructure(@NotNull List<StructureInfo> list);

    void hide();

    void onActionResponse(@NotNull ComponentName componentName, @NotNull String str, int i);

    void onRefreshState(@NotNull ComponentName componentName, @NotNull List<Control> list);

    void show(@NotNull ViewGroup viewGroup, @NotNull Runnable runnable, @NotNull Context context);
}
