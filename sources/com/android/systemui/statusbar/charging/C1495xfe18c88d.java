package com.android.systemui.statusbar.charging;

/* renamed from: com.android.systemui.statusbar.charging.WiredChargingRippleController$startRipple$1$onViewAttachedToWindow$1 */
/* compiled from: WiredChargingRippleController.kt */
final class C1495xfe18c88d implements Runnable {
    final /* synthetic */ WiredChargingRippleController this$0;

    C1495xfe18c88d(WiredChargingRippleController wiredChargingRippleController) {
        this.this$0 = wiredChargingRippleController;
    }

    public final void run() {
        this.this$0.windowManager.removeView(this.this$0.getRippleView());
    }
}
