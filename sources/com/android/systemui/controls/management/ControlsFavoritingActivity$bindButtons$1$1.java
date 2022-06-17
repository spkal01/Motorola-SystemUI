package com.android.systemui.controls.management;

import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.systemui.R$string;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$bindButtons$1$1 implements View.OnClickListener {
    final /* synthetic */ Button $this_apply;
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$bindButtons$1$1(ControlsFavoritingActivity controlsFavoritingActivity, Button button) {
        this.this$0 = controlsFavoritingActivity;
        this.$this_apply = button;
    }

    public final void onClick(View view) {
        View access$getDoneButton$p = this.this$0.doneButton;
        if (access$getDoneButton$p != null) {
            if (access$getDoneButton$p.isEnabled()) {
                Toast.makeText(this.this$0.getApplicationContext(), R$string.controls_favorite_toast_no_changes, 0).show();
            }
            this.this$0.startActivity(new Intent(this.$this_apply.getContext(), ControlsProviderSelectorActivity.class), ActivityOptions.makeSceneTransitionAnimation(this.this$0, new Pair[0]).toBundle());
            this.this$0.animateExitAndFinish();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("doneButton");
        throw null;
    }
}
