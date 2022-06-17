package com.android.systemui.controls.p004ui;

import android.app.PendingIntent;
import android.service.controls.Control;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$longPress$1 */
/* compiled from: ControlActionCoordinatorImpl.kt */
final class ControlActionCoordinatorImpl$longPress$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ ControlViewHolder $cvh;
    final /* synthetic */ ControlActionCoordinatorImpl this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ControlActionCoordinatorImpl$longPress$1(ControlViewHolder controlViewHolder, ControlActionCoordinatorImpl controlActionCoordinatorImpl) {
        super(0);
        this.$cvh = controlViewHolder;
        this.this$0 = controlActionCoordinatorImpl;
    }

    public final void invoke() {
        Control control = this.$cvh.getCws().getControl();
        if (control != null) {
            ControlViewHolder controlViewHolder = this.$cvh;
            ControlActionCoordinatorImpl controlActionCoordinatorImpl = this.this$0;
            controlViewHolder.getLayout().performHapticFeedback(0);
            PendingIntent appIntent = control.getAppIntent();
            Intrinsics.checkNotNullExpressionValue(appIntent, "it.getAppIntent()");
            controlActionCoordinatorImpl.showDetail(controlViewHolder, appIntent);
        }
    }
}
