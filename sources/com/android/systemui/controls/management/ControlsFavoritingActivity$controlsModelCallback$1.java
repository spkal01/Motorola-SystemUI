package com.android.systemui.controls.management;

import android.view.View;
import com.android.systemui.controls.management.ControlsModel;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
public final class ControlsFavoritingActivity$controlsModelCallback$1 implements ControlsModel.ControlsModelCallback {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$controlsModelCallback$1(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public void onFirstChange() {
        View access$getDoneButton$p = this.this$0.doneButton;
        if (access$getDoneButton$p != null) {
            access$getDoneButton$p.setEnabled(true);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("doneButton");
            throw null;
        }
    }
}
