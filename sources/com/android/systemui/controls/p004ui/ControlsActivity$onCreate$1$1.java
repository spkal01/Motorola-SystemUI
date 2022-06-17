package com.android.systemui.controls.p004ui;

import android.view.View;
import android.view.WindowInsets;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlsActivity$onCreate$1$1 */
/* compiled from: ControlsActivity.kt */
final class ControlsActivity$onCreate$1$1 implements View.OnApplyWindowInsetsListener {
    public static final ControlsActivity$onCreate$1$1 INSTANCE = new ControlsActivity$onCreate$1$1();

    ControlsActivity$onCreate$1$1() {
    }

    public final WindowInsets onApplyWindowInsets(@NotNull View view, @NotNull WindowInsets windowInsets) {
        Intrinsics.checkNotNullParameter(view, "v");
        Intrinsics.checkNotNullParameter(windowInsets, "insets");
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), windowInsets.getInsets(WindowInsets.Type.systemBars()).bottom);
        return WindowInsets.CONSUMED;
    }
}
