package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import com.android.systemui.animation.ActivityLaunchAnimator;

/* renamed from: com.android.systemui.qs.tiles.DeviceControlsTile$handleClick$1 */
/* compiled from: DeviceControlsTile.kt */
final class DeviceControlsTile$handleClick$1 implements Runnable {
    final /* synthetic */ ActivityLaunchAnimator.Controller $animationController;
    final /* synthetic */ Intent $intent;
    final /* synthetic */ DeviceControlsTile this$0;

    DeviceControlsTile$handleClick$1(DeviceControlsTile deviceControlsTile, Intent intent, ActivityLaunchAnimator.Controller controller) {
        this.this$0 = deviceControlsTile;
        this.$intent = intent;
        this.$animationController = controller;
    }

    public final void run() {
        if (this.this$0.keyguardStateController.isUnlocked()) {
            this.this$0.mActivityStarter.startActivity(this.$intent, true, this.$animationController);
        } else if (this.this$0.getState().state == 2) {
            this.this$0.mHost.collapsePanels();
            this.this$0.mContext.startActivity(this.$intent);
        } else {
            this.this$0.mActivityStarter.postStartActivityDismissingKeyguard(this.$intent, 0, this.$animationController);
        }
    }
}
