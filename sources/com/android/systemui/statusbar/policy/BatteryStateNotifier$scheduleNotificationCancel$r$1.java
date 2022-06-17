package com.android.systemui.statusbar.policy;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: BatteryStateNotifier.kt */
final class BatteryStateNotifier$scheduleNotificationCancel$r$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ BatteryStateNotifier this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    BatteryStateNotifier$scheduleNotificationCancel$r$1(BatteryStateNotifier batteryStateNotifier) {
        super(0);
        this.this$0 = batteryStateNotifier;
    }

    public final void invoke() {
        if (!this.this$0.getStateUnknown()) {
            this.this$0.getNoMan().cancel(666);
        }
    }
}
