package com.android.systemui.controls.p004ui;

import android.os.VibrationEffect;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$vibrate$1 */
/* compiled from: ControlActionCoordinatorImpl.kt */
final class ControlActionCoordinatorImpl$vibrate$1 implements Runnable {
    final /* synthetic */ VibrationEffect $effect;
    final /* synthetic */ ControlActionCoordinatorImpl this$0;

    ControlActionCoordinatorImpl$vibrate$1(ControlActionCoordinatorImpl controlActionCoordinatorImpl, VibrationEffect vibrationEffect) {
        this.this$0 = controlActionCoordinatorImpl;
        this.$effect = vibrationEffect;
    }

    public final void run() {
        this.this$0.vibrator.vibrate(this.$effect);
    }
}
