package com.android.systemui.statusbar.phone.ongoingcall;

import android.content.Intent;
import android.view.View;
import com.android.systemui.animation.ActivityLaunchAnimator;

/* compiled from: OngoingCallController.kt */
final class OngoingCallController$updateChip$1$1 implements View.OnClickListener {
    final /* synthetic */ View $backgroundView;
    final /* synthetic */ Intent $intent;
    final /* synthetic */ OngoingCallController this$0;

    OngoingCallController$updateChip$1$1(OngoingCallController ongoingCallController, Intent intent, View view) {
        this.this$0 = ongoingCallController;
        this.$intent = intent;
        this.$backgroundView = view;
    }

    public final void onClick(View view) {
        this.this$0.logger.logChipClicked();
        this.this$0.activityStarter.postStartActivityDismissingKeyguard(this.$intent, 0, ActivityLaunchAnimator.Controller.Companion.fromView(this.$backgroundView, 34));
    }
}
