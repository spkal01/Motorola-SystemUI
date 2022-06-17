package com.android.systemui.controls.p004ui;

import android.view.View;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createListView$1$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$createListView$1$1 implements View.OnClickListener {
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$createListView$1$1(ControlsUiControllerImpl controlsUiControllerImpl) {
        this.this$0 = controlsUiControllerImpl;
    }

    public final void onClick(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "$noName_0");
        Runnable access$getOnDismiss$p = this.this$0.onDismiss;
        if (access$getOnDismiss$p != null) {
            access$getOnDismiss$p.run();
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("onDismiss");
            throw null;
        }
    }
}
