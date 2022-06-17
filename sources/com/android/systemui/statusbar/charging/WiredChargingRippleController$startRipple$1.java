package com.android.systemui.statusbar.charging;

import android.view.View;
import org.jetbrains.annotations.Nullable;

/* compiled from: WiredChargingRippleController.kt */
public final class WiredChargingRippleController$startRipple$1 implements View.OnAttachStateChangeListener {
    final /* synthetic */ WiredChargingRippleController this$0;

    public void onViewDetachedFromWindow(@Nullable View view) {
    }

    WiredChargingRippleController$startRipple$1(WiredChargingRippleController wiredChargingRippleController) {
        this.this$0 = wiredChargingRippleController;
    }

    public void onViewAttachedToWindow(@Nullable View view) {
        this.this$0.layoutRipple();
        this.this$0.getRippleView().startRipple(new C1495xfe18c88d(this.this$0));
        this.this$0.getRippleView().removeOnAttachStateChangeListener(this);
    }
}
