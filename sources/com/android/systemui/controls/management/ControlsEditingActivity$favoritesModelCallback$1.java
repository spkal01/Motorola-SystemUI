package com.android.systemui.controls.management;

import android.view.View;
import android.widget.TextView;
import com.android.systemui.controls.management.FavoritesModel;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsEditingActivity.kt */
public final class ControlsEditingActivity$favoritesModelCallback$1 implements FavoritesModel.FavoritesModelCallback {
    final /* synthetic */ ControlsEditingActivity this$0;

    ControlsEditingActivity$favoritesModelCallback$1(ControlsEditingActivity controlsEditingActivity) {
        this.this$0 = controlsEditingActivity;
    }

    public void onNoneChanged(boolean z) {
        if (z) {
            TextView access$getSubtitle$p = this.this$0.subtitle;
            if (access$getSubtitle$p != null) {
                access$getSubtitle$p.setText(ControlsEditingActivity.EMPTY_TEXT_ID);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("subtitle");
                throw null;
            }
        } else {
            TextView access$getSubtitle$p2 = this.this$0.subtitle;
            if (access$getSubtitle$p2 != null) {
                access$getSubtitle$p2.setText(ControlsEditingActivity.SUBTITLE_ID);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("subtitle");
                throw null;
            }
        }
    }

    public void onFirstChange() {
        View access$getSaveButton$p = this.this$0.saveButton;
        if (access$getSaveButton$p != null) {
            access$getSaveButton$p.setEnabled(true);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("saveButton");
            throw null;
        }
    }
}
