package com.motorola.systemui.statusbar.policy;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: ThemedStylusBatteryDrawable.kt */
final class ThemedStylusBatteryDrawable$invalidateRunnable$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ ThemedStylusBatteryDrawable this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ThemedStylusBatteryDrawable$invalidateRunnable$1(ThemedStylusBatteryDrawable themedStylusBatteryDrawable) {
        super(0);
        this.this$0 = themedStylusBatteryDrawable;
    }

    public final void invoke() {
        this.this$0.invalidateSelf();
    }
}
