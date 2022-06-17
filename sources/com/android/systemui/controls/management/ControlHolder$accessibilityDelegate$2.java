package com.android.systemui.controls.management;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

/* compiled from: ControlAdapter.kt */
/* synthetic */ class ControlHolder$accessibilityDelegate$2 extends FunctionReferenceImpl implements Function0<Integer> {
    ControlHolder$accessibilityDelegate$2(ControlHolder controlHolder) {
        super(0, controlHolder, ControlHolder.class, "getLayoutPosition", "getLayoutPosition()I", 0);
    }

    public final int invoke() {
        return ((ControlHolder) this.receiver).getLayoutPosition();
    }
}
