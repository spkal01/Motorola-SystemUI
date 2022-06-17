package com.android.systemui.controls.management;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlAdapter.kt */
/* synthetic */ class ControlHolder$accessibilityDelegate$1 extends FunctionReferenceImpl implements Function1<Boolean, CharSequence> {
    ControlHolder$accessibilityDelegate$1(ControlHolder controlHolder) {
        super(1, controlHolder, ControlHolder.class, "stateDescription", "stateDescription(Z)Ljava/lang/CharSequence;", 0);
    }

    @Nullable
    public final CharSequence invoke(boolean z) {
        return ((ControlHolder) this.receiver).stateDescription(z);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return invoke(((Boolean) obj).booleanValue());
    }
}
